// Objetivo: persistir y notificar el modo de tema seleccionado por el usuario.
// Preparado para el curso Programacion V CUC -Cartago
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ThemeController extends ChangeNotifier {
  ThemeController(this._preferences)
    : _mode = _readMode(_preferences.getString(_preferenceKey));

  static const _preferenceKey = 'theme_mode';
  final SharedPreferences _preferences;
  ThemeMode _mode;

  ThemeMode get mode => _mode;

  Future<void> setMode(ThemeMode mode) async {
    if (_mode == mode) return;
    _mode = mode;
    notifyListeners();
    await _preferences.setString(_preferenceKey, mode.name);
  }

  static ThemeMode _readMode(String? value) {
    return ThemeMode.values.firstWhere(
      (mode) => mode.name == value,
      orElse: () => ThemeMode.system,
    );
  }
}
