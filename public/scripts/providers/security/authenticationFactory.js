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
    .factory('authenticationFactory', ['$http', '$q', '$state', '$log', 'apiEndpoint', '$window',
        function ($http, $q, $state, $log, apiEndpoint, $window) {
            var authToken,
                csrfToken,
                currentUser;

            var checkTokens = function () {
                authToken = $window.sessionStorage.getItem('backend-authToken');
               // csrfToken = $window.sessionStorage.getItem('backend-csrfToken');

                return authToken; //&& csrfToken;
            };

            var setHttpHeaders = function () {
                if (checkTokens()) {
                    _setHeaders(authToken, csrfToken);
                }
            };

            var removeCookies = function () {
                $window.sessionStorage.removeItem('backend-authToken');
                //$window.sessionStorage.removeItem('backend-csrfToken');
            };

            var _setHeaders = function (auth, csrf) {
                $http.defaults.headers.common['X-AuthToken'] = auth;
             //   $http.defaults.headers.common['X-CSRFToken'] = csrf;
            };

            setHttpHeaders();

            var factory = {
                logout: function () {
                    _setHeaders(null, null);
                    removeCookies();
                    currentUser = null;
                },

                getTokens: function () {
                    return $window.sessionStorage.getItem('backend-authToken');// && $window.sessionStorage.getItem('backend-csrfToken');
                },

                conditionalLogin: function (username, password) {
                    var deferred = $q.defer();
                    if (factory.getTokens()) {
                        deferred.resolve();
                        return deferred.promise;
                    } else {
                        return $q.when(factory.login(username, password));
                    }
                },

                login: function (username, password) {
                    return $http.post(apiEndpoint + 'token/new', {username: username, password: password}, {tracker: 'loadingTracker'}).then(
                        function success(response) {
                            $window.sessionStorage.setItem('backend-authToken', response.data.token);
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