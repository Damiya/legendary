'use strict';

angular.module('legendary')
    .factory('loginQueueFactory', ['$http', '$q', '$log', 'cookieManager', 'apiEndpoint',
      function ($http, $q, $log, cookieManager, apiEndpoint) {
        var deferred = $q.defer();
        var loginToken = cookieManager.get('lol-loginToken');

        if (loginToken) {
          $log.debug('loginQueueFactory: loginToken exists. Resolving');
          deferred.resolve(loginToken);
        }

        var service = {
          getLoginToken: function () {
            return deferred.promise;
          },

          deferredAuthenticate: function (username, password) {
            $http.post(apiEndpoint + 'login-queue/authenticate/', {user: username, password: password}, {tracker: 'loadingTracker'})
                .then(function success(response) {
                  var data = response.data;

                  if (data.lqt) {
                    $log.debug('loginQueueFactory: Got a new loginToken. Resolving');
                    var stringifiedToken = JSON.stringify(data.lqt);
                    cookieManager.put('lol-loginToken', stringifiedToken, {expires: 24000});
                    loginToken = data.lqt;
                    deferred.resolve(data.lqt);
                  } else {
                    deferred.reject(response);
                  }
                }, function error(response) {
                  deferred.reject(response);
                });
            return deferred.promise;
          },

          authenticate: function (username, password) {
            service.deferredAuthenticate(username, password);
          }
        };

        return service;
      }]);