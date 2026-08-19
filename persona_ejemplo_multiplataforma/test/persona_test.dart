import 'package:flutter_test/flutter_test.dart';
import 'package:persona_ejemplo_multiplataforma/features/personas/domain/persona.dart';

void main() {
  test('Persona interpreta la respuesta JSON del microservicio', () {
    final persona = Persona.fromJson({
      'personaId': '1-2345',
      'nombre': 'Andrea Gómez',
      'tipo': 3,
      'gender': 'Femenino',
    });

    expect(persona.id, '1-2345');
    expect(persona.tipoLabel, 'Administrativo');
    expect(persona.toJson(), isNot(contains('password')));
  });
}
