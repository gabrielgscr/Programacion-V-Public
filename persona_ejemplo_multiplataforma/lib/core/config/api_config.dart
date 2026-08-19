import 'package:flutter/foundation.dart';

abstract final class ApiConfig {
  static const _configuredUrl = String.fromEnvironment('API_BASE_URL');

  static String get baseUrl {
    if (_configuredUrl.isNotEmpty) return _configuredUrl;
    if (kIsWeb) return 'http://localhost:5200';
    if (defaultTargetPlatform == TargetPlatform.android) {
      return 'http://10.0.2.2:5200';
    }
    return 'http://localhost:5200';
  }
}
