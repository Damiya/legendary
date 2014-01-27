'use strict';

angular.module('services.loginTokenProvider', ['services.loginQueue'])
    .provider('loginTokenProvider', {
      requireLoginToken: ['loginTokenProvider', function (loginTokenProvider) {
        return loginTokenProvider.requireLoginToken();
      }],

      $get: ['loginQueueFactory', '$rootScope', '$q', function (loginQueueService, $rootScope, $q) {
        var factory = {
          promise: null,
          requireLoginToken: function () {
            var deferred = $q.defer();
            loginQueueService.authenticate($rootScope.username, $rootScope.password).then(function () {
              deferred.resolve();
            }, function () {
              deferred.reject();
            });

            factory.promise = deferred.promise;
          }
        };

        return factory;
      }]
    });
