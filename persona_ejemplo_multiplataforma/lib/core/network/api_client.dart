import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;

import 'api_exception.dart';

class ApiClient {
  ApiClient({
    required String baseUrl,
    http.Client? client,
    this.timeout = const Duration(seconds: 12),
  }) : _baseUri = Uri.parse(baseUrl.replaceFirst(RegExp(r'/$'), '')),
       _client = client ?? http.Client() {
    debugPrint('API base URI: $_baseUri');
  }

  final Uri _baseUri;
  final http.Client _client;
  final Duration timeout;

  Future<Object?> get(String path, {Map<String, Object?>? query}) {
    return _send('GET', path, query: query);
  }

  Future<Object?> post(String path, {required Object body}) {
    return _send('POST', path, body: body);
  }

  Future<Object?> put(String path, {required Object body}) {
    return _send('PUT', path, body: body);
  }

  Future<void> delete(String path) async {
    await _send('DELETE', path);
  }

  Future<Object?> _send(
    String method,
    String path, {
    Map<String, Object?>? query,
    Object? body,
  }) async {
    final uri = _baseUri.replace(
      path: '${_baseUri.path}${path.startsWith('/') ? path : '/$path'}',
      queryParameters: query?.map(
        (key, value) => MapEntry(key, value?.toString()),
      ),
    );
    final request = http.Request(method, uri)
      ..headers.addAll({
        'Accept': 'application/json',
        'Content-Type': 'application/json; charset=utf-8',
      });
    if (body != null) request.body = jsonEncode(body);

    try {
      debugPrint('API request: $method $uri');
      final streamed = await _client.send(request).timeout(timeout);
      final response = await http.Response.fromStream(streamed);
      final decoded = _decode(response.body);
      if (response.statusCode < 200 || response.statusCode >= 300) {
        throw ApiException(
          _errorMessage(decoded, response.statusCode),
          statusCode: response.statusCode,
        );
      }
      return decoded;
    } on TimeoutException catch (error) {
      debugPrint('API timeout for $method $uri: $error');
      throw const ApiException(
        'El servicio tardó demasiado en responder. Intenta nuevamente.',
      );
    } on SocketException catch (error) {
      debugPrint('API connection failed for $method $uri: $error');
      throw const ApiException(
        'No fue posible conectar con el servicio. Verifica que esté activo.',
      );
    } on HandshakeException catch (error) {
      debugPrint('API TLS handshake failed for $method $uri: $error');
      throw const ApiException(
        'No fue posible establecer una conexión segura con el servicio.',
      );
    } on http.ClientException catch (error) {
      debugPrint('API client error for $method $uri: $error');
      throw const ApiException(
        'No fue posible conectar con el servicio. Verifica que esté activo.',
      );
    } catch (error, stackTrace) {
      debugPrint('API unexpected error for $method $uri: $error');
      debugPrintStack(stackTrace: stackTrace);
      throw ApiException(
        'No fue posible conectar con el servicio. Verifica la configuración.',
      );
    }
  }

  Object? _decode(String body) {
    if (body.trim().isEmpty) return null;
    try {
      return jsonDecode(body);
    } on FormatException {
      return body;
    }
  }

  String _errorMessage(Object? body, int statusCode) {
    if (body case {'message': final String message}) return message;
    if (body case {'title': final String title}) return title;
    return switch (statusCode) {
      400 => 'Los datos enviados no son válidos.',
      404 => 'No se encontró el recurso solicitado.',
      409 => 'Ya existe un registro con esos datos.',
      >= 500 => 'El servicio encontró un problema. Intenta más tarde.',
      _ => 'No se pudo completar la operación.',
    };
  }
}
