'use strict';

angular.module('legendary')
    .factory('loginService', ['$q', 'loginQueueFactory', 'djangoAuthenticationFactory', function ($q, loginQueueFactory, djangoAuthenticationFactory) {
      return {
        login: function (username, password) {
          var deferred = $q.defer();
          djangoAuthenticationFactory.authenticate(username, password).then(
              loginQueueFactory.authenticate(username, password),
              function failure(response) {
                deferred.reject(response);
              }).then(
              function success() {
                deferred.resolve();
              },
              function failure(response) {
                deferred.reject(response);
              });

          return deferred.promise;
        }
      };
    }]);