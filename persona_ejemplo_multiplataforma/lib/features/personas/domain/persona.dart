// Objetivo: modelar y serializar los datos de una persona del sistema.
// Preparado para el curso Programacion V CUC -Cartago
class Persona {
  const Persona({
    required this.id,
    required this.nombre,
    required this.tipo,
    required this.genero,
    this.password,
  });

  final String id;
  final String nombre;
  final int tipo;
  final String genero;
  final String? password;

  String get tipoLabel => switch (tipo) {
    1 => 'Estudiante',
    2 => 'Docente',
    3 => 'Administrativo',
    _ => 'Tipo $tipo',
  };

  Persona copyWith({
    String? id,
    String? nombre,
    int? tipo,
    String? genero,
    String? password,
  }) {
    return Persona(
      id: id ?? this.id,
      nombre: nombre ?? this.nombre,
      tipo: tipo ?? this.tipo,
      genero: genero ?? this.genero,
      password: password ?? this.password,
    );
  }

  factory Persona.fromJson(Map<String, Object?> json) {
    return Persona(
      id: (json['personaId'] ?? json['PersonaId'] ?? '').toString(),
      nombre: (json['nombre'] ?? json['Nombre'] ?? '').toString(),
      tipo: _asInt(json['tipo'] ?? json['Tipo']),
      genero: (json['gender'] ?? json['Gender'] ?? '').toString(),
    );
  }

  Map<String, Object?> toJson({bool includePassword = false}) {
    return {
      'personaId': id,
      'nombre': nombre,
      'tipo': tipo,
      'gender': genero,
      if (includePassword) 'password': password,
    };
  }

  static int _asInt(Object? value) {
    if (value is num) return value.toInt();
    return int.tryParse(value?.toString() ?? '') ?? 0;
  }
}
