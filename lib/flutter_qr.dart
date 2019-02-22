import 'dart:async';
import 'package:flutter/services.dart';

class FlutterQr {
  static const MethodChannel _channel = const MethodChannel('flutter_qr');
  static Future<String> scan({bool anim = false}) async {
    Map params = {'anim':anim};//anim = false 跳转无动画 true Android 自带动画
    return await _channel.invokeMethod('readQRCode', params);
  }
}
