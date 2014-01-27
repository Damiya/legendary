// Code from https://github.com/angular-app/angular-app

'use strict';

angular.module('security.authorization', ['security.service'])

// This service provides guard methods to support AngularJS routes.
// You can add them as resolves to routes to require authorization levels
// before allowing a route change to complete
    .provider('securityAuthorization', {

      requireAuthenticatedUser: ['securityAuthorization', function (securityAuthorization) {
        return securityAuthorization.requireAuthenticatedUser();
      }],

      $get: ['security', 'securityRetryQueue', '$q', function (security, queue, $q) {
        var service = {
          promise: null,
          // Require that there is an authenticated user
          // (use this in a route resolve to prevent non-authenticated users from entering that route)
          requireAuthenticatedUser: function () {
            var deferred = $q.defer();
            security.requestCurrentUser().then(function () {
              if (!security.isAuthenticated()) {
                queue.pushRetryFn('unauthenticated-client', service.requireAuthenticatedUser).then(function () {
                  deferred.resolve();
                }, function () {
                  deferred.reject();
                });
              } else {
                deferred.resolve();
              }
            });

            service.promise = deferred.promise
          }
        };

        return service;
      }]
    });