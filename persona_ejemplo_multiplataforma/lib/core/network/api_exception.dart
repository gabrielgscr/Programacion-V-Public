// Objetivo: representar fallos de API con un mensaje apto para la interfaz.
// Preparado para el curso Programacion V CUC -Cartago
class ApiException implements Exception {
  const ApiException(this.message, {this.statusCode});

  final String message;
  final int? statusCode;

  @override
  String toString() => message;
}
