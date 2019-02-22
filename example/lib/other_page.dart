import 'package:flutter/material.dart';

class OtherPage extends StatelessWidget {
  final String value;
  OtherPage(this.value);
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('第三个页面',
            style: TextStyle(color: Colors.black, fontSize: 16.0)),
        elevation: 0,
        centerTitle: true,
      ),
      body: Container(
        color: Colors.white,
        child: Center(
          child: Text('$value'),
        ),
      ),
    );
  }
}
