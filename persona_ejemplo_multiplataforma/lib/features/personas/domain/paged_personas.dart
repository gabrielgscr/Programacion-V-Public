// Objetivo: modelar la respuesta paginada de personas devuelta por el API.
// Preparado para el curso Programacion V CUC -Cartago
import 'persona.dart';

class PagedPersonas {
  const PagedPersonas({
    required this.items,
    required this.pageNumber,
    required this.pageSize,
    required this.totalCount,
    required this.totalPages,
  });

  final List<Persona> items;
  final int pageNumber;
  final int pageSize;
  final int totalCount;
  final int totalPages;

  factory PagedPersonas.fromJson(Map<String, Object?> json) {
    final rawItems = json['items'] ?? json['Items'];
    return PagedPersonas(
      items: rawItems is List
          ? rawItems
                .whereType<Map<String, Object?>>()
                .map(Persona.fromJson)
                .toList()
          : const [],
      pageNumber: _asInt(json['pageNumber'] ?? json['PageNumber'], 1),
      pageSize: _asInt(json['pageSize'] ?? json['PageSize'], 10),
      totalCount: _asInt(json['totalCount'] ?? json['TotalCount'], 0),
      totalPages: _asInt(json['totalPages'] ?? json['TotalPages'], 0),
    );
  }

  static int _asInt(Object? value, int fallback) {
    if (value is num) return value.toInt();
    return int.tryParse(value?.toString() ?? '') ?? fallback;
  }
}
