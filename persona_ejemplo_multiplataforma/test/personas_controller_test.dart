import 'package:flutter_test/flutter_test.dart';
import 'package:persona_ejemplo_multiplataforma/features/personas/domain/paged_personas.dart';
import 'package:persona_ejemplo_multiplataforma/features/personas/domain/persona.dart';
import 'package:persona_ejemplo_multiplataforma/features/personas/domain/persona_repository.dart';
import 'package:persona_ejemplo_multiplataforma/features/personas/presentation/personas_controller.dart';

void main() {
  group('PersonasController', () {
    test('carga una página y expone la paginación', () async {
      final repository = _FakePersonaRepository();
      final controller = PersonasController(repository);

      await controller.load();

      expect(controller.personas, hasLength(2));
      expect(controller.totalCount, 12);
      expect(controller.totalPages, 2);
      expect(controller.canGoForward, isTrue);
      expect(controller.error, isNull);
    });

    test('crea y vuelve a cargar los registros', () async {
      final repository = _FakePersonaRepository();
      final controller = PersonasController(repository);
      const persona = Persona(
        id: '3',
        nombre: 'María Mora',
        tipo: 1,
        genero: 'Femenino',
        password: 'segura123',
      );

      final result = await controller.create(persona);

      expect(result, 'Persona creada');
      expect(repository.created, persona);
      expect(repository.pageRequests, 1);
    });
  });
}

class _FakePersonaRepository implements PersonaRepository {
  Persona? created;
  int pageRequests = 0;

  final people = const [
    Persona(id: '1', nombre: 'Ana Solano', tipo: 1, genero: 'Femenino'),
    Persona(id: '2', nombre: 'Luis Mora', tipo: 2, genero: 'Masculino'),
  ];

  @override
  Future<Persona> create(Persona persona) async {
    created = persona;
    return persona;
  }

  @override
  Future<void> delete(String id) async {}

  @override
  Future<List<Persona>> getAll() async => people;

  @override
  Future<Persona> getById(String id) async {
    return people.firstWhere((person) => person.id == id);
  }

  @override
  Future<PagedPersonas> getPage({
    required int page,
    required int pageSize,
  }) async {
    pageRequests++;
    return PagedPersonas(
      items: people,
      pageNumber: page,
      pageSize: pageSize,
      totalCount: 12,
      totalPages: 2,
    );
  }

  @override
  Future<Persona> update(Persona persona) async => persona;
}
