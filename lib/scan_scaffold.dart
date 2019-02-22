import 'package:flutter/material.dart';
import 'package:flutter_qr/flutter_qr.dart';


class ScanScaffoldPage extends StatelessWidget {
  
  void loadScanView(BuildContext context) {
    FlutterQr.scan().then((value) {
      if (value == null) {
        Navigator.of(context).pop();
      } else {
        Navigator.of(context)
            .push(MaterialPageRoute(
                builder: (BuildContext context){
                  return null;
                }))
            .then((value) {
          loadScanView(context);
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    loadScanView(context);
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.white,
        title: Text('扫描条形码/二维码',
            style: TextStyle(color: Colors.white, fontSize: 16.0)),
        elevation: 0,
        centerTitle: true,
      ),
      body: Container(
        color: Colors.white,
      ),
    );
  }
}
