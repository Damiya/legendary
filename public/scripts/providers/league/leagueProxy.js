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
    .factory('leagueProxy', ['$http', '$q', '$log', '$window', 'apiEndpoint',
        function ($http, $q, $log, $window, apiEndpoint) {
            var isConnected = true;
            var deferred = $q.defer();

            var service = {
                isConnected: function () {
                    return isConnected;
                },

                logout: function () {
                    return true;
//                    $http.delete(apiEndpoint + 'league/logout', {tracker: 'loadingTracker'})
//                        .then(function () {
//                            isConnected = false;
//                        },function() {
//                            $log.error('oh dear');
//                        }
//                    );
                },

                deferredLogin: function (username, password) {
                    return true;
//                    $http.post(apiEndpoint + 'league/login', {username: username, password: password}, {tracker: 'loadingTracker'})
//                        .then(function success(response) {
//                            isConnected = true;
//                            deferred.resolve();
//                        }, function error(response) {
//                            deferred.reject(response);
//                        });
//                    return deferred.promise;
                },

                login: function (username, password) {
                    service.deferredLogin(username, password);
                }
            };

            return service;
        }]);