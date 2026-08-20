// Objetivo: adaptar las operaciones HTTP del API al contrato del repositorio.
// Preparado para el curso Programacion V CUC -Cartago
import '../../../core/network/api_client.dart';
import '../../../core/network/api_exception.dart';
import '../domain/paged_personas.dart';
import '../domain/persona.dart';
import '../domain/persona_repository.dart';

class HttpPersonaRepository implements PersonaRepository {
  HttpPersonaRepository(this._client);

  static const _path = '/api/Persona';
  final ApiClient _client;

  @override
  Future<List<Persona>> getAll() async {
    final response = await _client.get('$_path/');
    if (response is! List) throw _invalidResponse();
    return response
        .whereType<Map<String, Object?>>()
        .map(Persona.fromJson)
        .toList();
  }

  @override
  Future<PagedPersonas> getPage({
    required int page,
    required int pageSize,
  }) async {
    final response = await _client.get(
      '$_path/page',
      query: {'pageNumber': page, 'pageSize': pageSize},
    );
    if (response is! Map<String, Object?>) throw _invalidResponse();
    return PagedPersonas.fromJson(response);
  }

  @override
  Future<Persona> getById(String id) async {
    final response = await _client.get('$_path/${Uri.encodeComponent(id)}');
    if (response is! Map<String, Object?>) throw _invalidResponse();
    return Persona.fromJson(response);
  }

  @override
  Future<Persona> create(Persona persona) async {
    final response = await _client.post(
      '$_path/',
      body: persona.toJson(includePassword: true),
    );
    if (response is! Map<String, Object?>) throw _invalidResponse();
    return Persona.fromJson(response);
  }

  @override
  Future<Persona> update(Persona persona) async {
    final response = await _client.put(
      '$_path/${Uri.encodeComponent(persona.id)}',
      body: persona.toJson(),
    );
    if (response is! Map<String, Object?>) throw _invalidResponse();
    return Persona.fromJson(response);
  }

  @override
  Future<void> delete(String id) {
    return _client.delete('$_path/${Uri.encodeComponent(id)}');
  }

  ApiException _invalidResponse() {
    return const ApiException('El servicio devolvió una respuesta inesperada.');
  }
}
