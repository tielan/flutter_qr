import 'dart:async';
import 'package:flutter/services.dart';

class FlutterQr {
  static const MethodChannel _channel = const MethodChannel('flutter_qr');
  static Future<String> scan() async {
    Map params = {};
    return await _channel.invokeMethod('readQRCode', params);
  }
}
