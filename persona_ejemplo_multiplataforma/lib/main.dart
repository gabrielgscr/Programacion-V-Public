import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'app/app.dart';
import 'app/theme_controller.dart';
import 'core/config/api_config.dart';
import 'core/network/api_client.dart';
import 'features/personas/data/http_persona_repository.dart';
import 'features/personas/presentation/personas_controller.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  final preferences = await SharedPreferences.getInstance();
  final themeController = ThemeController(preferences);
  final apiClient = ApiClient(baseUrl: ApiConfig.baseUrl);
  final personasController = PersonasController(
    HttpPersonaRepository(apiClient),
  );

  runApp(
    PersonasApp(
      themeController: themeController,
      personasController: personasController,
    ),
  );
}
