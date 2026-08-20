// Objetivo: presentar el formulario inferior para crear y editar personas.
// Preparado para el curso Programacion V CUC -Cartago
import 'package:flutter/material.dart';

import '../../domain/persona.dart';

typedef PersonaSubmit = Future<String?> Function(Persona persona);

class PersonaFormSheet extends StatefulWidget {
  const PersonaFormSheet({required this.onSubmit, this.persona, super.key});

  final Persona? persona;
  final PersonaSubmit onSubmit;

  @override
  State<PersonaFormSheet> createState() => _PersonaFormSheetState();
}

class _PersonaFormSheetState extends State<PersonaFormSheet> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _idController;
  late final TextEditingController _nameController;
  late final TextEditingController _genderController;
  late final TextEditingController _passwordController;
  late int _type;
  bool _saving = false;
  bool _hidePassword = true;

  bool get _isEditing => widget.persona != null;

  @override
  void initState() {
    super.initState();
    final persona = widget.persona;
    _idController = TextEditingController(text: persona?.id);
    _nameController = TextEditingController(text: persona?.nombre);
    _genderController = TextEditingController(text: persona?.genero);
    _passwordController = TextEditingController();
    _type = persona?.tipo ?? 1;
  }

  @override
  void dispose() {
    _idController.dispose();
    _nameController.dispose();
    _genderController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final media = MediaQuery.of(context);
    return SafeArea(
      child: Padding(
        padding: EdgeInsets.fromLTRB(20, 12, 20, media.viewInsets.bottom + 20),
        child: SingleChildScrollView(
          child: Form(
            key: _formKey,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Center(
                  child: Container(
                    width: 42,
                    height: 4,
                    decoration: BoxDecoration(
                      color: Theme.of(context).colorScheme.outlineVariant,
                      borderRadius: BorderRadius.circular(10),
                    ),
                  ),
                ),
                const SizedBox(height: 20),
                Text(
                  _isEditing ? 'Editar persona' : 'Nueva persona',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const SizedBox(height: 6),
                Text(
                  _isEditing
                      ? 'Actualiza la información del registro.'
                      : 'Completa los datos para crear el registro.',
                  style: TextStyle(
                    color: Theme.of(context).colorScheme.onSurfaceVariant,
                  ),
                ),
                const SizedBox(height: 22),
                TextFormField(
                  controller: _idController,
                  enabled: !_isEditing,
                  textInputAction: TextInputAction.next,
                  decoration: const InputDecoration(
                    labelText: 'Identificación',
                    prefixIcon: Icon(Icons.badge_outlined),
                  ),
                  validator: (value) =>
                      _required(value, 'Ingresa la identificación'),
                ),
                const SizedBox(height: 14),
                TextFormField(
                  controller: _nameController,
                  textCapitalization: TextCapitalization.words,
                  textInputAction: TextInputAction.next,
                  decoration: const InputDecoration(
                    labelText: 'Nombre completo',
                    prefixIcon: Icon(Icons.person_outline_rounded),
                  ),
                  validator: (value) {
                    final required = _required(value, 'Ingresa el nombre');
                    if (required != null) return required;
                    if (value!.trim().length < 5) {
                      return 'El nombre debe tener al menos 5 caracteres';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 14),
                DropdownButtonFormField<int>(
                  initialValue: _type,
                  decoration: const InputDecoration(
                    labelText: 'Tipo de persona',
                    prefixIcon: Icon(Icons.work_outline_rounded),
                  ),
                  items: const [
                    DropdownMenuItem(value: 1, child: Text('Estudiante')),
                    DropdownMenuItem(value: 2, child: Text('Docente')),
                    DropdownMenuItem(value: 3, child: Text('Administrativo')),
                  ],
                  onChanged: (value) => _type = value ?? 1,
                ),
                const SizedBox(height: 14),
                TextFormField(
                  controller: _genderController,
                  textCapitalization: TextCapitalization.sentences,
                  textInputAction: _isEditing
                      ? TextInputAction.done
                      : TextInputAction.next,
                  decoration: const InputDecoration(
                    labelText: 'Género',
                    prefixIcon: Icon(Icons.diversity_1_outlined),
                  ),
                  validator: (value) {
                    final required = _required(value, 'Ingresa el género');
                    if (required != null) return required;
                    if (value!.trim().length > 10) {
                      return 'Usa un máximo de 10 caracteres';
                    }
                    return null;
                  },
                ),
                if (!_isEditing) ...[
                  const SizedBox(height: 14),
                  TextFormField(
                    controller: _passwordController,
                    obscureText: _hidePassword,
                    textInputAction: TextInputAction.done,
                    decoration: InputDecoration(
                      labelText: 'Contraseña',
                      prefixIcon: const Icon(Icons.lock_outline_rounded),
                      suffixIcon: IconButton(
                        tooltip: _hidePassword ? 'Mostrar' : 'Ocultar',
                        onPressed: () => setState(() {
                          _hidePassword = !_hidePassword;
                        }),
                        icon: Icon(
                          _hidePassword
                              ? Icons.visibility_outlined
                              : Icons.visibility_off_outlined,
                        ),
                      ),
                    ),
                    validator: (value) {
                      final required = _required(
                        value,
                        'Ingresa una contraseña',
                      );
                      if (required != null) return required;
                      if (value!.length < 8) {
                        return 'Usa al menos 8 caracteres';
                      }
                      if (value.length > 100) {
                        return 'Usa un máximo de 100 caracteres';
                      }
                      return null;
                    },
                    onFieldSubmitted: (_) => _submit(),
                  ),
                ],
                const SizedBox(height: 24),
                FilledButton.icon(
                  onPressed: _saving ? null : _submit,
                  icon: _saving
                      ? const SizedBox.square(
                          dimension: 18,
                          child: CircularProgressIndicator(strokeWidth: 2),
                        )
                      : Icon(
                          _isEditing ? Icons.save_outlined : Icons.add_rounded,
                        ),
                  label: Text(_isEditing ? 'Guardar cambios' : 'Crear persona'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  String? _required(String? value, String message) {
    return value == null || value.trim().isEmpty ? message : null;
  }

  Future<void> _submit() async {
    if (_saving || !_formKey.currentState!.validate()) return;
    setState(() => _saving = true);
    final persona = Persona(
      id: _idController.text.trim(),
      nombre: _nameController.text.trim(),
      tipo: _type,
      genero: _genderController.text.trim(),
      password: _isEditing ? null : _passwordController.text,
    );
    final result = await widget.onSubmit(persona);
    if (!mounted) return;
    setState(() => _saving = false);
    if (result == 'Persona creada' || result == 'Cambios guardados') {
      Navigator.pop(context, result);
      return;
    }
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(result ?? 'No se pudo guardar la persona.')),
    );
  }
}
