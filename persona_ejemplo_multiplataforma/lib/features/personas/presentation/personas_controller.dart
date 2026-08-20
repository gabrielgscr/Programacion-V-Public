import 'dart:async';

import 'package:flutter/foundation.dart';

import '../../../core/network/api_exception.dart';
import '../domain/persona.dart';
import '../domain/persona_repository.dart';

class PersonasController extends ChangeNotifier {
  PersonasController(this._repository);

  final PersonaRepository _repository;
  final int pageSize = 10;

  List<Persona> _personas = const [];
  bool _isLoading = false;
  bool _isSaving = false;
  String? _error;
  String _query = '';
  int _page = 1;
  int _totalPages = 0;
  int _totalCount = 0;
  Timer? _searchDebounce;
  int _requestId = 0;

  List<Persona> get personas => _personas;
  bool get isLoading => _isLoading;
  bool get isSaving => _isSaving;
  String? get error => _error;
  String get query => _query;
  int get page => _page;
  int get totalPages => _totalPages;
  int get totalCount => _totalCount;
  bool get canGoBack => _query.isEmpty && _page > 1;
  bool get canGoForward => _query.isEmpty && _page < _totalPages;

  Future<void> load({int page = 1}) async {
    final requestId = ++_requestId;
    _setLoading(true);
    try {
      if (_query.isEmpty) {
        final result = await _repository.getPage(
          page: page,
          pageSize: pageSize,
        );
        if (requestId != _requestId) return;
        _personas = result.items;
        _page = result.pageNumber;
        _totalPages = result.totalPages;
        _totalCount = result.totalCount;
      } else {
        final all = await _repository.getAll();
        if (requestId != _requestId) return;
        final normalized = _query.toLowerCase();
        _personas = all.where((person) {
          return person.id.toLowerCase().contains(normalized) ||
              person.nombre.toLowerCase().contains(normalized) ||
              person.tipoLabel.toLowerCase().contains(normalized);
        }).toList();
        _page = 1;
        _totalPages = _personas.isEmpty ? 0 : 1;
        _totalCount = _personas.length;
      }
      _error = null;
    } catch (error) {
      if (requestId != _requestId) return;
      _error = _messageFor(error);
    } finally {
      if (requestId == _requestId) _setLoading(false);
    }
  }

  void search(String value) {
    _query = value.trim();
    _searchDebounce?.cancel();
    _searchDebounce = Timer(const Duration(milliseconds: 350), load);
    notifyListeners();
  }

  Future<String?> create(Persona persona) async {
    return _save(() => _repository.create(persona), 'Persona creada');
  }

  Future<({Persona? persona, String? error})> getDetails(String id) async {
    try {
      return (persona: await _repository.getById(id), error: null);
    } catch (error) {
      return (persona: null, error: _messageFor(error));
    }
  }

  Future<String?> update(Persona persona) async {
    return _save(() => _repository.update(persona), 'Cambios guardados');
  }

  Future<String?> _save(
    Future<Persona> Function() operation,
    String successMessage,
  ) async {
    _isSaving = true;
    notifyListeners();
    try {
      await operation();
      await load(page: _page);
      return successMessage;
    } catch (error) {
      return _messageFor(error);
    } finally {
      _isSaving = false;
      notifyListeners();
    }
  }

  Future<String?> delete(Persona persona) async {
    _isSaving = true;
    notifyListeners();
    try {
      await _repository.delete(persona.id);
      final targetPage = _personas.length == 1 && _page > 1 ? _page - 1 : _page;
      await load(page: targetPage);
      return null;
    } catch (error) {
      return _messageFor(error);
    } finally {
      _isSaving = false;
      notifyListeners();
    }
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }

  void _setLoading(bool value) {
    _isLoading = value;
    notifyListeners();
  }

  String _messageFor(Object error) {
    return error is ApiException
        ? error.message
        : 'Ocurrió un error inesperado. Intenta nuevamente.';
  }

  @override
  void dispose() {
    _searchDebounce?.cancel();
    super.dispose();
  }
}
