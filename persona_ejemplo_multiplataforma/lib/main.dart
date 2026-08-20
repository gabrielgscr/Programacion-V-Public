import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'app/app.dart';
import 'app/theme_controller.dart';
import 'core/config/api_config.dart';
import 'core/network/api_client.dart';
import 'core/network/http_client_factory.dart';
import 'features/personas/data/http_persona_repository.dart';
import 'features/personas/presentation/personas_controller.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  try {
    await dotenv.load(fileName: '.env');
  } catch (_) {
    // Keep the built-in fallback values when the file is missing.
  }

  final preferences = await SharedPreferences.getInstance();
  final themeController = ThemeController(preferences);
  final apiClient = ApiClient(
    baseUrl: ApiConfig.baseUrl,
    client: createHttpClient(),
  );
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
