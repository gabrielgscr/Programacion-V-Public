import 'package:flutter/material.dart';

import '../features/personas/presentation/personas_page.dart';
import '../features/personas/presentation/personas_controller.dart';
import 'app_theme.dart';
import 'theme_controller.dart';

class PersonasApp extends StatelessWidget {
  const PersonasApp({
    required this.themeController,
    required this.personasController,
    super.key,
  });

  final ThemeController themeController;
  final PersonasController personasController;

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: themeController,
      builder: (context, _) {
        return MaterialApp(
          debugShowCheckedModeBanner: false,
          title: 'Personas CUC',
          theme: AppTheme.light,
          darkTheme: AppTheme.dark,
          themeMode: themeController.mode,
          home: PersonasPage(
            controller: personasController,
            themeController: themeController,
          ),
        );
      },
    );
  }
}
