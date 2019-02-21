import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter_qr/flutter_qr.dart';


void main() {
  runApp(new MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      title: 'QRCode Reader Demo',
      home: new MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyHomePage> {
  Future<String> _barcodeString;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('QRCode Reader Example'),
      ),
      body: Center(
          child: FutureBuilder<String>(
              future: _barcodeString,
              builder: (BuildContext context, AsyncSnapshot<String> snapshot) {
                return Text(snapshot.data != null ? snapshot.data : '');
              })),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          setState(() {
            _barcodeString = FlutterQr().scan();
          });
        },
        tooltip: 'Reader the QRCode',
        child: Icon(Icons.add_a_photo),
      ),
    );
  }
}
