'use strict';

angular.module('security.service', [
      'security.retryQueue',    // Keeps track of failed requests that need to be retried once the user logs in
      'security.login.form',         // Contains the login form template and controller,
      'ngCookies'
    ])

    .factory('security', ['$http', '$q', '$state', 'securityRetryQueue', '$cookieStore', '$rootScope',
      function ($http, $q, $state, queue, $cookieStore, $rootScope) {
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
          login: function (username, password) {
            var request = $http.post('http://localhost:8000/api-tokens/', {username: username, password: password});
            return request.then(function (response) {
              $cookieStore.put('djangotoken', response.data.token);
              service.currentUserId = response.data.user_id;
              service.setLoginToken();
              redirect('home.landingPage');
              $rootScope.$emit('loggedIn');
            });
          },

          setLoginToken: function () {
            var token = $cookieStore.get('djangotoken');
            if (token) {
              $http.defaults.headers.common['Authorization'] = 'Token ' + $cookieStore.get('djangotoken');
            }
          },

          clearLocalToken: function () {
            $cookieStore.remove('djangotoken');
            $http.defaults.headers.common['Authorization'] = null;
          },

          // Give up trying to login and clear the retry queue
          cancelLogin: function () {
            redirect();
          },

          // Logout the current user and redirect
          logout: function () {
            queue.cancelAll();

            var request = $http.delete('http://localhost:8000/api-tokens/' + $cookieStore.get('djangotoken') + '/');
            request.then(function () {
              $rootScope.$emit('loggedOut');
              service.clearLocalToken();
              redirect();
            });
          },

          // Ask the backend to see if a user is already authenticated - this may be from a previous session.
          requestCurrentUser: function () {
            if (service.isAuthenticated()) {
              return $q.when(service.currentUser);
            } else {
              return $http.get('http://localhost:8000/users/current/')
                  .then(function (response) {
                    service.currentUser = response.data;
                    return service.currentUser;
                  });
            }
          },

          // Information about the current user
          currentUserId: 0,
          currentUser: null,

          // Is the current user authenticated?
          isAuthenticated: function () {
            return $cookieStore.get('djangotoken');
          }
        };

        return service;
      }]);