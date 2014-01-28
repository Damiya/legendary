'use strict';

angular.module('legendary')
    .factory('djangoAuthenticationFactory', ['$http', '$q', '$state', '$log', 'apiEndpoint', 'cookieManager',
      function ($http, $q, $state, $log, apiEndpoint, cookieManager) {
        var authToken,
            csrfToken,
            currentUser = {};

        var redirect = function (url) {
          url = url || 'home.loginRequired';
          $state.go(url);
        };

        var checkDjangoTokens = function () {
          authToken = cookieManager.get('django-authToken');
          csrfToken = cookieManager.get('django-csrfToken');

          return authToken && csrfToken;
        };

        var setHttpHeaders = function () {
          if (checkDjangoTokens()) {
            $http.defaults.headers.common['Authorization'] = 'Token ' + authToken;
            $http.defaults.headers.common['X-CSRFToken'] = csrfToken;
          }
        };


        var factory = {
          login: function (username, password) {
            return $http.post(apiEndpoint + 'api-tokens/', {username: username, password: password}, {tracker: 'loadingTracker'}).then(
                function success(response) {
                  cookieManager.put('django-authToken', response.data.token, { expires: 24000 });
                  cookieManager.put('django-csrfToken', response.headers('x-csrftoken'), { expires: 24000 });
                  factory.currentUser.id = response.data.user_id;
                  setHttpHeaders();
                  $log.debug('Security: login success');
                  redirect('home.noLeague');
                },
                function error(response) {
                  $log.debug('Security: Invalid credentials');
                  redirect();
                }
            ).then(factory.getCurrentUser);
          },

          isAuthenticated: function () {
            return checkDjangoTokens();
          },

          getCurrentUser: function () {
            var deferred = $q.defer();

            if (currentUser.username) {
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