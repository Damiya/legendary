'use strict';

var passport = require('passport'),
    LocalStrategy = require('passport-local').Strategy;

var filterUser = function (user) {
  if (user) {
    return {
      user: {
        id: user.id,
        username: user.username,
        email: user.email,
        firstName: user.firstName,
        lastName: user.lastName
      }
    };
  } else {
    return { user: null };
  }
};

var security = {
  users: [
    { id: 1, username: 'bob', password: 'secret', email: 'bob@example.com', firstName: 'Billy', lastName: 'Bob' },
    { id: 2, username: 'joe', password: 'birthday', email: 'joe@example.com' }
  ],

  initialize: function () {

    passport.serializeUser(function (user, done) {
      done(null, user.id);
    });

    passport.deserializeUser(function (id, done) {
      security.findById(id, function (err, user) {
        done(err, user);
      });
    });

    passport.use(new LocalStrategy(
        function (username, password, done) {
          security.findByUsername(username, function (err, user) {
            if (err) {
              return done(err);
            }
            if (!user) {
              return done(null, false, { message: 'Unknown user ' + username });
            }
            if (user.password !== password) {
              return done(null, false, { message: 'Invalid password' });
            }
            return done(null, user);
          });
        }
    ));
  },

  findById: function (id, fn) {
    var idx = id - 1;
    if (security.users[idx]) {
      fn(null, security.users[idx]);
    } else {
      fn(new Error('User ' + id + ' does not exist'));
    }
  },

  findByUsername: function (email, fn) {
    for (var i = 0, len = security.users.length; i < len; i++) {
      var user = security.users[i];
      if (user.username === email) {
        return fn(null, user);
      }
    }
    return fn(null, null);
  },

  authenticationRequired: function (req, res, next) {
    console.log('authRequired');
    if (req.isAuthenticated()) {
      next();
    } else {
      res.json(401, filterUser(req.user));
    }
  },
  sendCurrentUser: function (req, res) {
    res.json(200, filterUser(req.user));
    res.end();
  },
  login: function (req, res, next) {
    function authenticationFailed(err, user) {
      if (err) {
        return next(err);
      }
      if (!user) {
        return res.json(filterUser(user));
      }
      req.logIn(user, function (err) {
        if (err) {
          return next(err);
        }
        return res.json(filterUser(user));
      });
    }

    return passport.authenticate('local', authenticationFailed)(req, res, next);
  },
  logout: function (req, res) {
    req.logout();
    res.send(204);
  }
};

module.exports = security;