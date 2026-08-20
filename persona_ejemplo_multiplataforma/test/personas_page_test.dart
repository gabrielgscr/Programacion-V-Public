// Objetivo: comprobar la interaccion visual principal de la pantalla de personas.
// Preparado para el curso Programacion V CUC -Cartago
import 'package:flutter_test/flutter_test.dart';
import 'package:persona_ejemplo_multiplataforma/app/app.dart';
import 'package:persona_ejemplo_multiplataforma/app/theme_controller.dart';
import 'package:persona_ejemplo_multiplataforma/features/personas/domain/paged_personas.dart';
import 'package:persona_ejemplo_multiplataforma/features/personas/domain/persona.dart';
import 'package:persona_ejemplo_multiplataforma/features/personas/domain/persona_repository.dart';
import 'package:persona_ejemplo_multiplataforma/features/personas/presentation/personas_controller.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() {
  testWidgets('muestra personas y confirma el gesto de eliminación', (
    tester,
  ) async {
    SharedPreferences.setMockInitialValues({});
    final preferences = await SharedPreferences.getInstance();
    final controller = PersonasController(_WidgetRepository());

    await tester.pumpWidget(
      PersonasApp(
        themeController: ThemeController(preferences),
        personasController: controller,
      ),
    );
    await tester.pumpAndSettle();

    expect(find.text('Personas CUC'), findsOneWidget);
    expect(find.text('Ana Solano'), findsOneWidget);
    expect(find.text('Nueva persona'), findsOneWidget);

    await tester.drag(find.text('Ana Solano'), const Offset(-500, 0));
    await tester.pumpAndSettle();

    expect(find.text('¿Eliminar persona?'), findsOneWidget);
    expect(find.text('Cancelar'), findsOneWidget);
  });
}

class _WidgetRepository implements PersonaRepository {
  static const person = Persona(
    id: '1-1111-1111',
    nombre: 'Ana Solano',
    tipo: 1,
    genero: 'Femenino',
  );

  @override
  Future<Persona> create(Persona persona) async => persona;

  @override
  Future<void> delete(String id) async {}

  @override
  Future<List<Persona>> getAll() async => const [person];

  @override
  Future<Persona> getById(String id) async => person;

  @override
  Future<PagedPersonas> getPage({
    required int page,
    required int pageSize,
  }) async {
    return PagedPersonas(
      items: const [person],
      pageNumber: 1,
      pageSize: pageSize,
      totalCount: 1,
      totalPages: 1,
    );
  }

  @override
  Future<Persona> update(Persona persona) async => persona;
}
