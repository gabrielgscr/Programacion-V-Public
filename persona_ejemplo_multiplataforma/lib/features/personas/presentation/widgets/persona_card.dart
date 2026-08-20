// Objetivo: mostrar cada persona como tarjeta interactiva con edicion y borrado.
// Preparado para el curso Programacion V CUC -Cartago
import 'package:flutter/material.dart';

import '../../domain/persona.dart';

class PersonaCard extends StatelessWidget {
  const PersonaCard({
    required this.persona,
    required this.onEdit,
    required this.confirmDelete,
    required this.onDismissed,
    super.key,
  });

  final Persona persona;
  final VoidCallback onEdit;
  final Future<bool> Function() confirmDelete;
  final VoidCallback onDismissed;

  @override
  Widget build(BuildContext context) {
    final colors = Theme.of(context).colorScheme;
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Dismissible(
        key: ValueKey(persona.id),
        direction: DismissDirection.endToStart,
        confirmDismiss: (_) => confirmDelete(),
        onDismissed: (_) => onDismissed(),
        background: Container(
          decoration: BoxDecoration(
            color: colors.error,
            borderRadius: BorderRadius.circular(20),
          ),
          padding: const EdgeInsets.symmetric(horizontal: 24),
          alignment: Alignment.centerRight,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(Icons.delete_outline_rounded, color: colors.onError),
              const SizedBox(height: 4),
              Text(
                'Eliminar',
                style: TextStyle(
                  color: colors.onError,
                  fontWeight: FontWeight.w700,
                ),
              ),
            ],
          ),
        ),
        child: Card(
          margin: EdgeInsets.zero,
          clipBehavior: Clip.antiAlias,
          child: InkWell(
            onTap: onEdit,
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  _Avatar(name: persona.nombre),
                  const SizedBox(width: 14),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          persona.nombre,
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                          style: Theme.of(context).textTheme.titleMedium
                              ?.copyWith(fontWeight: FontWeight.w700),
                        ),
                        const SizedBox(height: 5),
                        Wrap(
                          spacing: 8,
                          runSpacing: 5,
                          children: [
                            _Metadata(
                              icon: Icons.badge_outlined,
                              label: persona.id,
                            ),
                            _Metadata(
                              icon: Icons.work_outline_rounded,
                              label: persona.tipoLabel,
                            ),
                            _Metadata(
                              icon: Icons.person_outline_rounded,
                              label: persona.genero,
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                  IconButton(
                    tooltip: 'Editar persona',
                    onPressed: onEdit,
                    icon: const Icon(Icons.edit_outlined),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class _Avatar extends StatelessWidget {
  const _Avatar({required this.name});

  final String name;

  @override
  Widget build(BuildContext context) {
    final colors = Theme.of(context).colorScheme;
    final initials = name
        .trim()
        .split(RegExp(r'\s+'))
        .where((part) => part.isNotEmpty)
        .take(2)
        .map((part) => part[0].toUpperCase())
        .join();
    return CircleAvatar(
      radius: 25,
      backgroundColor: colors.primaryContainer,
      foregroundColor: colors.onPrimaryContainer,
      child: Text(
        initials.isEmpty ? '?' : initials,
        style: const TextStyle(fontWeight: FontWeight.w800),
      ),
    );
  }
}

class _Metadata extends StatelessWidget {
  const _Metadata({required this.icon, required this.label});

  final IconData icon;
  final String label;

  @override
  Widget build(BuildContext context) {
    final color = Theme.of(context).colorScheme.onSurfaceVariant;
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(icon, size: 15, color: color),
        const SizedBox(width: 4),
        Text(label, style: TextStyle(color: color, fontSize: 13)),
      ],
    );
  }
}
