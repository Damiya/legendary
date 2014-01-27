'use strict';

angular.module('services.loginQueue', ['legendary.constants'])
    .factory('loginQueueFactory', ['$http', '$q', 'apiEndpoint', function ($http, $q, apiEndpoint) {
      return {
        authenticate: function (username, password) {
          var deferred = $q.defer();
          $http.post(apiEndpoint + 'login-queue/authenticate/', {user: username, password: password})
              .then(function success(response) {
                var data = response.data;

                if (data.lqt) {
                  deferred.resolve(data.lqt);
                }
              }, function error(response) {
                deferred.reject(response.status);
              });
          return deferred.promise;
        }
      };
    }]);