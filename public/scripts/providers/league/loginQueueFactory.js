/*
 * Copyright 2014 Kate von Roeder (katevonroder at gmail dot com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

angular.module('legendary')
    .factory('loginQueueFactory', ['$http', '$q', '$log', '$window', 'apiEndpoint',
      function ($http, $q, $log, $window, apiEndpoint) {
        var deferred = $q.defer();
        var loginToken = $window.sessionStorage.getItem('lol-loginToken');

        var service = {
          getTokens: function () {
            return $window.sessionStorage.getItem('lol-loginToken');
          },

          logout: function () {
            $window.sessionStorage.removeItem('lol-loginToken');
            loginToken = null;
          },

          getLoginToken: function () {
            return deferred.promise;
          },

          deferredLogin: function (username, password) {
            $http.post(apiEndpoint + 'login-queue/authenticate/', {user: username, password: password}, {tracker: 'loadingTracker'})
                .then(function success(response) {
                  var data = response.data;

                  if (data.lqt) {
                    $log.debug('loginQueueFactory: Got a new loginToken. Resolving');
                    var stringifiedToken = JSON.stringify(data.lqt);
                    $window.sessionStorage.putItem('lol-loginToken', stringifiedToken, {expires: 24000});
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

          login: function (username, password) {
            service.deferredLogin(username, password);
          }
        };

        return service;
      }]);