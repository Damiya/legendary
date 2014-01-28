'use strict';

angular.module('legendary')
    .factory('djangoAuthenticationFactory', ['$http', '$q', '$state', '$log', 'apiEndpoint', 'cookieManager',
      function ($http, $q, $state, $log, apiEndpoint, cookieManager) {
        var authToken,
            csrfToken,
            currentUser;

        var checkDjangoTokens = function () {
          authToken = cookieManager.get('django-authToken');
          csrfToken = cookieManager.get('django-csrfToken');

          return authToken && csrfToken;
        };

        var setHttpHeaders = function () {
          if (checkDjangoTokens()) {
            _setHeaders('Token ' + authToken, csrfToken);
          }
        };

        var removeCookies = function () {
          cookieManager.remove('django-authToken');
          cookieManager.remove('django-csrfToken');
        };

        var _setHeaders = function (auth, csrf) {
          $http.defaults.headers.common['Authorization'] = auth;
          $http.defaults.headers.common['X-CSRFToken'] = csrf;
        };

        var factory = {
          logout: function () {
            _setHeaders(null, null);
            removeCookies();
            currentUser = null;
          },

          getCookies: function () {
            return cookieManager.get('django-authToken') && cookieManager.get('django-csrfToken');
          },

          conditionalLogin: function (username, password) {
            var deferred = $q.defer();
            if (factory.getCookies()) {
              deferred.resolve();
              return deferred.promise;
            } else {
              return $q.when(factory.login(username, password));
            }
          },

          login: function (username, password) {
            return $http.post(apiEndpoint + 'api-tokens/', {username: username, password: password}, {tracker: 'loadingTracker'}).then(
                function success(response) {
                  cookieManager.put('django-authToken', response.data.token, { expires: 24000 });
                  cookieManager.put('django-csrfToken', response.headers('x-csrftoken'), { expires: 24000 });
                  currentUser = response.data.user;
                  setHttpHeaders();
                  $log.debug('Security: login success');
                },
                function error(response) {
                  $log.debug('Security: Invalid credentials');
                }
            );
          },

          getCurrentUser: function () {
            var deferred = $q.defer();

            if (currentUser) {
              deferred.resolve(currentUser);
            } else {
              $http.get(apiEndpoint + 'users/current/', {tracker: 'loadingTracker'}).then(
                  function success(response) {
                    $log.debug('Security: GetCurrentUser success');
                    deferred.resolve(response.data);
                  },
                  function error(response) {
                    $log.error(response);
                    deferred.reject();
                  }
              );
            }

            return deferred.promise;
          }

        };

        return factory;
      }]);