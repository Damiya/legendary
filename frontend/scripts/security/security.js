// Code from https://github.com/angular-app/angular-app

'use strict';

angular.module('security.service', [
      'security.retryQueue',    // Keeps track of failed requests that need to be retried once the user logs in
      'security.login.form'         // Contains the login form template and controller
    ])

    .factory('security', ['$http', '$q', '$state', 'securityRetryQueue', function ($http, $q, $state, queue) {
      function redirect(url) {
        url = url || 'home.loginRequired';
        $state.go(url);
      }

      // Register a handler for when an item is added to the retry queue
      queue.onItemAddedCallbacks.push(function () {
        if (queue.hasMore()) {
          redirect();
        }
      });

      // The public API of the service
      var service = {

        // Get the first reason for needing a login
        getLoginReason: function () {
          return queue.retryReason();
        },

        // Attempt to authenticate a user by the given email and password
        login: function (email, password) {
          var request = $http.post('/login', {email: email, password: password});
          return request.then(function (response) {
            service.currentUser = response.data.user;
            if (service.isAuthenticated()) {
              redirect('home.landingPage');
            }
            return service.isAuthenticated();
          });
        },

        // Give up trying to login and clear the retry queue
        cancelLogin: function () {
          redirect();
        },

        // Logout the current user and redirect
        logout: function () {
          $http.post('/logout').then(function () {
            service.currentUser = null;
            redirect();
          });
        },

        // Ask the backend to see if a user is already authenticated - this may be from a previous session.
        requestCurrentUser: function () {
          if (service.isAuthenticated()) {
            return $q.when(service.currentUser);
          } else {
            return $http.get('/current-user').then(function (response) {
              service.currentUser = response.data.user;
              return service.currentUser;
            });
          }
        },

        // Information about the current user
        currentUser: null,

        // Is the current user authenticated?
        isAuthenticated: function () {
          return !!service.currentUser;
        }
      };

      return service;
    }]);