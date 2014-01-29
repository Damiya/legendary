/*
 * Copyright 2014 Kate von Roeder (katevonroder at gmail dot com) - twitter: @itsdamiya
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
    .factory('djangoAuthenticationFactory', ['$http', '$q', '$state', '$log', 'apiEndpoint', 'cookieManager',
      function ($http, $q, $state, $log, apiEndpoint, cookieManager) {
        var authToken,
            csrfToken,
            currentUser;

        var checkDjangoTokens = function () {
          authToken = cookieManager.get('django-authToken');
          csrfToken = cookieManager.get('django-csrfToken');

          return authToken && csrfToken;
        };

        var setHttpHeaders = function () {
          if (checkDjangoTokens()) {
            _setHeaders('Token ' + authToken, csrfToken);
          }
        };

        var removeCookies = function () {
          cookieManager.remove('django-authToken');
          cookieManager.remove('django-csrfToken');
        };

        var _setHeaders = function (auth, csrf) {
          $http.defaults.headers.common['Authorization'] = auth;
          $http.defaults.headers.common['X-CSRFToken'] = csrf;
        };

        var factory = {
          logout: function () {
            _setHeaders(null, null);
            removeCookies();
            currentUser = null;
          },

          getCookies: function () {
            return cookieManager.get('django-authToken') && cookieManager.get('django-csrfToken');
          },

          conditionalLogin: function (username, password) {
            var deferred = $q.defer();
            if (factory.getCookies()) {
              deferred.resolve();
              return deferred.promise;
            } else {
              return $q.when(factory.login(username, password));
            }
          },

          login: function (username, password) {
            return $http.post(apiEndpoint + 'api-tokens/', {username: username, password: password}, {tracker: 'loadingTracker'}).then(
                function success(response) {
                  cookieManager.put('django-authToken', response.data.token, { expires: 24000 });
                  cookieManager.put('django-csrfToken', response.headers('x-csrftoken'), { expires: 24000 });
                  currentUser = response.data.user;
                  setHttpHeaders();
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