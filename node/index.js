(function (_, Kotlin) {
  'use strict';
  var println = Kotlin.kotlin.io.println_s8jyv4$;
  var Unit = Kotlin.kotlin.Unit;
  function main$lambda() {
    println('Listening on port 3000');
    return Unit;
  }
  function main$lambda_0(f, res, f_0) {
    return res.send('i am beautiful butterfly');
  }
  function main() {
    var express = require('express');
    var app = express();
    app.listen(3000, main$lambda);
    app.get('/', main$lambda_0);
  }
  _.main = main;
  main();
  Kotlin.defineModule('index', _);
  return _;
}(module.exports, require('kotlin')));
