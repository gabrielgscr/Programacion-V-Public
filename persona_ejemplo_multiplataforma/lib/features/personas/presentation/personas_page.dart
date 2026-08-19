import 'dart:async';

import 'package:flutter/material.dart';

import '../../../app/app_theme.dart';
import '../../../app/theme_controller.dart';
import '../domain/persona.dart';
import 'personas_controller.dart';
import 'widgets/persona_card.dart';
import 'widgets/persona_form_sheet.dart';

class PersonasPage extends StatefulWidget {
  const PersonasPage({
    required this.controller,
    required this.themeController,
    super.key,
  });

  final PersonasController controller;
  final ThemeController themeController;

  @override
  State<PersonasPage> createState() => _PersonasPageState();
}

class _PersonasPageState extends State<PersonasPage> {
  late final TextEditingController _searchController;

  @override
  void initState() {
    super.initState();
    _searchController = TextEditingController();
    unawaited(widget.controller.load());
  }

  @override
  void dispose() {
    _searchController.dispose();
    widget.controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: AnimatedBuilder(
          animation: widget.controller,
          builder: (context, _) {
            final controller = widget.controller;
            return RefreshIndicator(
              onRefresh: () => controller.load(page: controller.page),
              child: CustomScrollView(
                physics: const AlwaysScrollableScrollPhysics(),
                slivers: [
                  SliverToBoxAdapter(
                    child: _Header(
                      themeController: widget.themeController,
                      totalCount: controller.totalCount,
                    ),
                  ),
                  SliverToBoxAdapter(
                    child: _SearchBar(
                      controller: _searchController,
                      onChanged: controller.search,
                      onClear: () {
                        _searchController.clear();
                        controller.search('');
                      },
                    ),
                  ),
                  if (controller.isLoading && controller.personas.isEmpty)
                    const SliverFillRemaining(
                      hasScrollBody: false,
                      child: Center(child: CircularProgressIndicator()),
                    )
                  else if (controller.error != null &&
                      controller.personas.isEmpty)
                    SliverFillRemaining(
                      hasScrollBody: false,
                      child: _ErrorState(
                        message: controller.error!,
                        onRetry: controller.load,
                      ),
                    )
                  else if (controller.personas.isEmpty)
                    SliverFillRemaining(
                      hasScrollBody: false,
                      child: _EmptyState(hasQuery: controller.query.isNotEmpty),
                    )
                  else
                    SliverPadding(
                      padding: const EdgeInsets.fromLTRB(16, 8, 16, 8),
                      sliver: SliverList.builder(
                        itemCount: controller.personas.length,
                        itemBuilder: (context, index) {
                          final persona = controller.personas[index];
                          return PersonaCard(
                            persona: persona,
                            onEdit: () => _openEditor(persona),
                            confirmDelete: () => _confirmDelete(persona),
                            onDismissed: () => _delete(persona),
                          );
                        },
                      ),
                    ),
                  if (controller.personas.isNotEmpty)
                    SliverToBoxAdapter(
                      child: _Pagination(
                        page: controller.page,
                        totalPages: controller.totalPages,
                        canGoBack: controller.canGoBack,
                        canGoForward: controller.canGoForward,
                        isLoading: controller.isLoading,
                        onBack: () =>
                            controller.load(page: controller.page - 1),
                        onForward: () =>
                            controller.load(page: controller.page + 1),
                      ),
                    ),
                  const SliverToBoxAdapter(child: SizedBox(height: 96)),
                ],
              ),
            );
          },
        ),
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => _openEditor(),
        icon: const Icon(Icons.person_add_alt_1_rounded),
        label: const Text('Nueva persona'),
      ),
    );
  }

  Future<void> _openEditor([Persona? summary]) async {
    Persona? persona = summary;
    if (summary != null) {
      final details = await widget.controller.getDetails(summary.id);
      if (!mounted) return;
      if (details.persona == null) {
        _showMessage(details.error ?? 'No se pudo cargar la persona.');
        return;
      }
      persona = details.persona;
    }

    if (!mounted) return;
    final result = await showModalBottomSheet<String>(
      context: context,
      isScrollControlled: true,
      useSafeArea: true,
      constraints: const BoxConstraints(maxWidth: 640),
      builder: (_) => PersonaFormSheet(
        persona: persona,
        onSubmit: persona == null
            ? widget.controller.create
            : widget.controller.update,
      ),
    );
    if (mounted && result != null) _showMessage(result);
  }

  Future<bool> _confirmDelete(Persona persona) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        icon: Icon(
          Icons.delete_outline_rounded,
          color: Theme.of(context).colorScheme.error,
        ),
        title: const Text('¿Eliminar persona?'),
        content: Text(
          'Se eliminará a ${persona.nombre}. Esta acción no se puede deshacer.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancelar'),
          ),
          FilledButton(
            style: FilledButton.styleFrom(
              backgroundColor: Theme.of(context).colorScheme.error,
              foregroundColor: Theme.of(context).colorScheme.onError,
            ),
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Eliminar'),
          ),
        ],
      ),
    );
    return confirmed ?? false;
  }

  Future<void> _delete(Persona persona) async {
    final error = await widget.controller.delete(persona);
    if (!mounted) return;
    if (error == null) {
      _showMessage('${persona.nombre} fue eliminado.');
    } else {
      _showMessage(error);
      await widget.controller.load(page: widget.controller.page);
    }
  }

  void _showMessage(String message) {
    ScaffoldMessenger.of(context)
      ..hideCurrentSnackBar()
      ..showSnackBar(SnackBar(content: Text(message)));
  }
}

class _Header extends StatelessWidget {
  const _Header({required this.themeController, required this.totalCount});

  final ThemeController themeController;
  final int totalCount;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.fromLTRB(16, 16, 16, 12),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: [AppColors.cucBlue, Color(0xFF31579D)],
        ),
        borderRadius: BorderRadius.circular(28),
        boxShadow: [
          BoxShadow(
            color: AppColors.cucBlue.withValues(alpha: 0.22),
            blurRadius: 24,
            offset: const Offset(0, 10),
          ),
        ],
      ),
      child: Row(
        children: [
          Container(
            width: 52,
            height: 52,
            decoration: BoxDecoration(
              color: Colors.white.withValues(alpha: 0.14),
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: Colors.white.withValues(alpha: 0.22)),
            ),
            child: const Icon(
              Icons.school_rounded,
              color: Colors.white,
              size: 29,
            ),
          ),
          const SizedBox(width: 15),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Personas CUC',
                  style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                    color: Colors.white,
                    fontWeight: FontWeight.w800,
                  ),
                ),
                const SizedBox(height: 3),
                Text(
                  totalCount == 1 ? '1 registro' : '$totalCount registros',
                  style: TextStyle(color: Colors.white.withValues(alpha: 0.78)),
                ),
              ],
            ),
          ),
          PopupMenuButton<ThemeMode>(
            tooltip: 'Cambiar apariencia',
            initialValue: themeController.mode,
            onSelected: themeController.setMode,
            icon: const Icon(Icons.brightness_6_outlined, color: Colors.white),
            itemBuilder: (context) => const [
              PopupMenuItem(
                value: ThemeMode.system,
                child: _ThemeOption(Icons.settings_suggest_outlined, 'Sistema'),
              ),
              PopupMenuItem(
                value: ThemeMode.light,
                child: _ThemeOption(Icons.light_mode_outlined, 'Claro'),
              ),
              PopupMenuItem(
                value: ThemeMode.dark,
                child: _ThemeOption(Icons.dark_mode_outlined, 'Oscuro'),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _ThemeOption extends StatelessWidget {
  const _ThemeOption(this.icon, this.label);

  final IconData icon;
  final String label;

  @override
  Widget build(BuildContext context) {
    return Row(children: [Icon(icon), const SizedBox(width: 12), Text(label)]);
  }
}

class _SearchBar extends StatelessWidget {
  const _SearchBar({
    required this.controller,
    required this.onChanged,
    required this.onClear,
  });

  final TextEditingController controller;
  final ValueChanged<String> onChanged;
  final VoidCallback onClear;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 4, 16, 12),
      child: TextField(
        controller: controller,
        onChanged: onChanged,
        textInputAction: TextInputAction.search,
        decoration: InputDecoration(
          hintText: 'Buscar por nombre, identificación o tipo',
          prefixIcon: const Icon(Icons.search_rounded),
          suffixIcon: controller.text.isEmpty
              ? null
              : IconButton(
                  tooltip: 'Limpiar búsqueda',
                  onPressed: onClear,
                  icon: const Icon(Icons.close_rounded),
                ),
        ),
      ),
    );
  }
}

class _Pagination extends StatelessWidget {
  const _Pagination({
    required this.page,
    required this.totalPages,
    required this.canGoBack,
    required this.canGoForward,
    required this.isLoading,
    required this.onBack,
    required this.onForward,
  });

  final int page;
  final int totalPages;
  final bool canGoBack;
  final bool canGoForward;
  final bool isLoading;
  final VoidCallback onBack;
  final VoidCallback onForward;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          IconButton.outlined(
            tooltip: 'Página anterior',
            onPressed: canGoBack && !isLoading ? onBack : null,
            icon: const Icon(Icons.chevron_left_rounded),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 18),
            child: Text(
              totalPages == 0 ? 'Sin páginas' : 'Página $page de $totalPages',
              style: const TextStyle(fontWeight: FontWeight.w700),
            ),
          ),
          IconButton.outlined(
            tooltip: 'Página siguiente',
            onPressed: canGoForward && !isLoading ? onForward : null,
            icon: const Icon(Icons.chevron_right_rounded),
          ),
        ],
      ),
    );
  }
}

class _EmptyState extends StatelessWidget {
  const _EmptyState({required this.hasQuery});

  final bool hasQuery;

  @override
  Widget build(BuildContext context) {
    return _CenteredState(
      icon: hasQuery ? Icons.search_off_rounded : Icons.people_outline_rounded,
      title: hasQuery ? 'Sin coincidencias' : 'Aún no hay personas',
      message: hasQuery
          ? 'Prueba con otro nombre o identificación.'
          : 'Crea el primer registro usando el botón inferior.',
    );
  }
}

class _ErrorState extends StatelessWidget {
  const _ErrorState({required this.message, required this.onRetry});

  final String message;
  final Future<void> Function({int page}) onRetry;

  @override
  Widget build(BuildContext context) {
    return _CenteredState(
      icon: Icons.cloud_off_rounded,
      title: 'No pudimos conectar',
      message: message,
      action: FilledButton.tonalIcon(
        onPressed: onRetry,
        icon: const Icon(Icons.refresh_rounded),
        label: const Text('Reintentar'),
      ),
    );
  }
}

class _CenteredState extends StatelessWidget {
  const _CenteredState({
    required this.icon,
    required this.title,
    required this.message,
    this.action,
  });

  final IconData icon;
  final String title;
  final String message;
  final Widget? action;

  @override
  Widget build(BuildContext context) {
    final colors = Theme.of(context).colorScheme;
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(icon, size: 56, color: colors.primary),
            const SizedBox(height: 16),
            Text(title, style: Theme.of(context).textTheme.titleLarge),
            const SizedBox(height: 8),
            Text(
              message,
              textAlign: TextAlign.center,
              style: TextStyle(color: colors.onSurfaceVariant),
            ),
            if (action != null) ...[const SizedBox(height: 20), action!],
          ],
        ),
      ),
    );
  }
}
