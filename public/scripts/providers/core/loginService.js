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
    .factory('loginService', ['$state', '$q', 'loginQueueFactory', 'authenticationFactory',
      function ($state, $q, loginQueueFactory, authenticationFactory) {
        var backendAuthenticated,
            loginQueueAuthenticated;

        var redirect = function (url) {
          url = url || 'home.loginRequired';
          $state.go(url);
        };

        var setAuthStatusFromCookies = function () {
          backendAuthenticated = authenticationFactory.getTokens();
          loginQueueAuthenticated = loginQueueFactory.getTokens();
        };

        var factory = {
          isAuthenticated: function () {
            setAuthStatusFromCookies();
            return backendAuthenticated && loginQueueAuthenticated;
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
            authenticationFactory.logout();
            loginQueueFactory.logout();
            redirect();
          },

          login: function (username, password) {
            var deferred = $q.defer();
            authenticationFactory.conditionalLogin(username, password)
                .then(function success() {
                  backendAuthenticated = true;
                  return loginQueueFactory.deferredLogin(username, password);
                },
                function failure(response) {
                  deferred.reject(response);
                })
                .then(function success() {
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