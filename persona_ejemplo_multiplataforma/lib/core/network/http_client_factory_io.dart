// Objetivo: crear un cliente HTTP para IO con apoyo de certificados de desarrollo.
// Preparado para el curso Programacion V CUC -Cartago
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:http/io_client.dart';

http.Client? createHttpClient() {
  final httpClient = HttpClient();
  if (kDebugMode) {
    httpClient.badCertificateCallback =
        (X509Certificate certificate, String host, int port) {
          return true;
        };
  }
  return IOClient(httpClient);
}
