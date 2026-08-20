// Objetivo: centralizar los colores, temas y estilos visuales de la aplicacion.
// Preparado para el curso Programacion V CUC -Cartago
import 'package:flutter/material.dart';

abstract final class AppColors {
  static const cucBlue = Color(0xFF1E376C);
  static const cucRed = Color(0xFFEF4135);
  static const lightBackground = Color(0xFFF5F7FB);
  static const darkBackground = Color(0xFF0D1424);
}

abstract final class AppTheme {
  static ThemeData get light => _build(Brightness.light);
  static ThemeData get dark => _build(Brightness.dark);

  static ThemeData _build(Brightness brightness) {
    final isDark = brightness == Brightness.dark;
    final scheme =
        ColorScheme.fromSeed(
          seedColor: AppColors.cucBlue,
          brightness: brightness,
        ).copyWith(
          primary: isDark ? const Color(0xFFB7C8FF) : AppColors.cucBlue,
          onPrimary: isDark ? const Color(0xFF071638) : Colors.white,
          secondary: isDark ? const Color(0xFFFFB4AC) : AppColors.cucRed,
          onSecondary: isDark ? const Color(0xFF690005) : Colors.white,
          error: isDark ? const Color(0xFFFFB4AB) : const Color(0xFFBA1A1A),
          surface: isDark ? const Color(0xFF151E30) : Colors.white,
        );

    final base = ThemeData(
      useMaterial3: true,
      brightness: brightness,
      colorScheme: scheme,
      scaffoldBackgroundColor: isDark
          ? AppColors.darkBackground
          : AppColors.lightBackground,
    );

    return base.copyWith(
      textTheme: base.textTheme.copyWith(
        headlineMedium: base.textTheme.headlineMedium?.copyWith(
          fontWeight: FontWeight.w800,
          letterSpacing: -0.7,
        ),
        titleLarge: base.textTheme.titleLarge?.copyWith(
          fontWeight: FontWeight.w700,
        ),
      ),
      cardTheme: CardThemeData(
        elevation: 0,
        color: scheme.surface,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: scheme.surface,
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: BorderSide.none,
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: BorderSide(color: scheme.outlineVariant),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(16),
          borderSide: BorderSide(color: scheme.primary, width: 2),
        ),
      ),
      snackBarTheme: SnackBarThemeData(
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
      ),
    );
  }
}
