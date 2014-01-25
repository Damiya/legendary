'use strict';
var index = require('./security');

/**
 * Application routes
 */
module.exports = function (app) {
  // All other routes to use Angular routing in app/scripts/app.js

  app.get('/templates/*', index.templates);

};