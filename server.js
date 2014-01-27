'use strict';

var express = require('express'),
    passport = require('passport');

/**
 * Main application file
 */

// Default node environment to development
process.env.NODE_ENV = process.env.NODE_ENV || 'development';

// Application Config
var config = require('./lib/config/config');

var app = express();
app.use(passport.initialize());                             // Initialize PassportJS
app.use(passport.session());

// Express settings
require('./lib/config/express')(app);

// Routing
require('./lib/routes/general').addRoutes(app);


// Start server
app.listen(config.port, function () {
  console.log('Express server listening on port %d in %s mode', config.port, app.get('env'));
});

// Expose app
module.exports = app;