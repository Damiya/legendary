'use strict';

angular.module('legendary')
    .factory('loginService', ['$state', '$q', 'loginQueueFactory', 'djangoAuthenticationFactory',
      function ($state, $q, loginQueueFactory, djangoAuthenticationFactory) {
        var djangoAuthenticated,
            loginQueueAuthenticated;

        var redirect = function (url) {
          url = url || 'home.loginRequired';
          $state.go(url);
        };

        var setAuthStatusFromCookies = function () {
          djangoAuthenticated = djangoAuthenticationFactory.getCookies();
          loginQueueAuthenticated = loginQueueFactory.getCookies();
        };

        var factory = {
          isAuthenticated: function () {
            setAuthStatusFromCookies();
            return djangoAuthenticated && loginQueueAuthenticated;
          },

          requireAuthentication: function () {
            var deferred = $q.defer();

            if (factory.isAuthenticated()) {
              deferred.resolve();
            } else {
              deferred.reject();
            }

            return deferred.promise;
          },

          logout: function () {
            djangoAuthenticationFactory.logout();
            loginQueueFactory.logout();
            redirect();
          },

          login: function (username, password) {
            var deferred = $q.defer();
            djangoAuthenticationFactory.conditionalLogin(username, password).then(
                function success() {
                  djangoAuthenticated = true;
                  return loginQueueFactory.deferredLogin(username, password);
                },
                function failure(response) {
                  deferred.reject(response);
                }).then(
                function success() {
                  loginQueueAuthenticated = true;
                  deferred.resolve();
                  redirect('home.landingPage');
                },
                function failure(response) {
                  deferred.reject(response);
                });

            return deferred.promise;
          }
        };
        return factory;
      }]);