// Objetivo: definir el contrato de acceso a datos para personas.
// Preparado para el curso Programacion V CUC -Cartago
import 'paged_personas.dart';
import 'persona.dart';

abstract interface class PersonaRepository {
  Future<List<Persona>> getAll();
  Future<PagedPersonas> getPage({required int page, required int pageSize});
  Future<Persona> getById(String id);
  Future<Persona> create(Persona persona);
  Future<Persona> update(Persona persona);
  Future<void> delete(String id);
}
