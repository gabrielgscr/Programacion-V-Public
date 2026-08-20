// Objetivo: resolver la URL base del API segun .env y valores por defecto.
// Preparado para el curso Programacion V CUC -Cartago
import 'package:flutter/foundation.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

abstract final class ApiConfig {
  static String get _configuredUrl => dotenv.env['API_BASE_URL'] ?? '';

  static String get baseUrl {
    if (_configuredUrl.isNotEmpty) return _configuredUrl;
    if (kIsWeb) return 'https://localhost:7231';
    if (defaultTargetPlatform == TargetPlatform.android) {
      return 'https://10.0.2.2:7231';
    }
    return 'https://localhost:7231';
  }
}
