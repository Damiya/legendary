'use strict';

var path = require('path');

exports.addRoutes = function (app) {
  function renderTemplate(req, res) {
    var stripped = req.url.split('.')[0];
    var requestedView = path.join('./', stripped);
    res.render(requestedView, function (err, html) {
      if (err) {
        res.send(404);
      } else {
        res.send(html);
      }
    });
  }

  app.get('/', function (req, res) {
    res.render('index');
  });
  app.get('/partials/*', renderTemplate);
};