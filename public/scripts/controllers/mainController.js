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
    .controller('MainController', ['$scope', '$http', '$rootScope', '$log', 'promiseTracker',
      function ($scope, $http, $rootScope, $log, promiseTracker) {
        promiseTracker.register('loadingTracker');

        $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState) {
          $log.debug('Transition completed');
        });
        $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState) {
          $log.debug('Transition started from ' + fromState.name + ' to ' + toState.name);
        });
        $rootScope.$on('$stateChangeError', function (event, toState, toParams, fromState) {
          $log.error('Transition errored');
        });

        console.log('MainController: Instantiated');
      }]);
