'use strict';

angular.module('security.service', [
      'security.retryQueue',    // Keeps track of failed requests that need to be retried once the user logs in
      'security.login.form',         // Contains the login form template and controller,
      'ngCookies',
      'legendary.constants',
      'services.localizedMessages'
    ])

    .factory('security', ['$http', '$q', '$state', 'securityRetryQueue', '$cookieStore', 'apiEndpoint', 'localizedMessages', '$rootScope',
      function ($http, $q, $state, queue, $cookieStore, apiEndpoint, localizedMessages, $rootScope) {
        var authToken,
            csrfToken,
            alerts = [];

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

        function bothTokensPresent() {
          authToken = $cookieStore.get('django-authtoken');
          csrfToken = $cookieStore.get('django-csrftoken');

          return authToken && csrfToken;
        }

        function addAlert(msg, type) {
          clearAlerts();
          alerts.push({
            msg: msg,
            type: type
          });
        }

        function clearAlerts() {
          alerts.length = 0;
        }

        // The public API of the service
        var service = {
          alerts: alerts,

          // Get the first reason for needing a login
          updateAlerts: function () {
            if (queue.retryReason()) {
              addAlert(localizedMessages.get('login.reason.notAuthenticated'), 'warning');
            }
          },

          // Attempt to authenticate a user by the given email and password
          login: function (username, password) {
            var request = $http.post(apiEndpoint + 'api-tokens/', {username: username, password: password});
            // This is gross but we're going to put user and pass in our namespace temporarily so we can reuse them into the login queue
            $rootScope.username = username;
            $rootScope.password = password;
            return request.then(function success(response) {
              clearAlerts();
              $cookieStore.put('django-authtoken', response.data.token);
              $cookieStore.put('django-csrftoken', response.headers('x-csrftoken'));
              service.currentUserId = response.data.user_id;
              service.setLoginToken();
              redirect('home.noLeague');
            }, function error(response) {
              addAlert(localizedMessages.get('login.error.invalidCredentials'), 'danger');
              redirect();
            });
          },

          setLoginToken: function () {
            if (bothTokensPresent()) {
              $http.defaults.headers.common['Authorization'] = 'Token ' + authToken;
              $http.defaults.headers.common['X-CSRFToken'] = csrfToken;
            }
          },

          clearLocalToken: function () {
            $cookieStore.remove('django-authtoken');
            $cookieStore.remove('django-csrftoken');
            $http.defaults.headers.common['Authorization'] = null;
            $http.defaults.headers.common['X-CSRFToken'] = null;
          },

          // Give up trying to login and clear the retry queue
          cancelLogin: function () {
            clearAlerts();
            redirect();
          },

          // Logout the current user and redirect
          logout: function () {
            var request = $http.delete(apiEndpoint + 'api-tokens/' + authToken + '/');
            request.success(function () {
              addAlert(localizedMessages.get('logout.successful'), 'success');
              service.clearLocalToken();
              redirect();
            });
          },

          // Ask the backend to see if a user is already authenticated - this may be from a previous session.
          requestCurrentUser: function () {
            if (service.isAuthenticated()) {
              return $q.when(service.currentUser);
            } else {
              return $http.get(apiEndpoint + 'users/current/').then(function (response) {
                service.currentUser = response.data;
                return service.currentUser;
              }, function (response) {
               console.log(response);
              });
            }
          },

          // Information about the current user
          currentUserId: 0,
          currentUser: null,

          // Is the current user authenticated?
          isAuthenticated: function () {
            return bothTokensPresent();
          }
        };

        return service;
      }]);