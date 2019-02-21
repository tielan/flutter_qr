import 'dart:async';
import 'package:flutter/services.dart';

class FlutterQr {
  static const MethodChannel _channel = const MethodChannel('flutter_qr');
  Future<String> scan() async {
    Map params = <String, dynamic>{};
    return await _channel.invokeMethod('readQRCode', params);
  }
}