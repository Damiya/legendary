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
    .factory('authenticationFactory', ['RestangularFactory', '$q' , '$log', '$window', '$http',
      function (RestangularFactory, $q, $log, $window, $http) {
        var currentUser;

        var removeToken = function () {
          $window.sessionStorage.removeItem('backend-authToken');
        };

        var getAuthToken = function () {
          return $window.sessionStorage.getItem('backend-authToken');
        };

        var setHeaderFromSessionStorage = function () {
          $http.defaults.headers.common['X-Auth-Token'] = $window.sessionStorage.getItem('backend-authToken');
        };

        setHeaderFromSessionStorage();

        var tokensRoute = RestangularFactory.core.one('token');

        var factory = {
          logout: function () {
            tokensRoute.destroy().then(function () {
              removeToken();
              delete $http.defaults.headers.common['X-Auth-Token'];
            });
          },

          getAuthToken: getAuthToken,

          conditionalLogin: function (username, password) {
            var deferred = $q.defer();
            if (getAuthToken()) {
              deferred.resolve();
              return deferred.promise;
            } else {
              return $q.when(factory.login(username, password));
            }
          },

          login: function (username, password) {
            return tokensRoute.create({username: username, password: password}).then(
                function success(token) {
                  $window.sessionStorage.setItem('backend-authToken', token.value);
                  setHeaderFromSessionStorage();
                  $log.debug('Security: login success');
                },
                function error(response) {
                  $log.debug('Security: Invalid credentials');
                }
            );
          }

        };

        return factory;
      }]);