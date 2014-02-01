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

// Overlay from: https://github.com/filearts/plunker_www

angular.module('legendary')
    .directive('lgdOverlay', ['promiseTracker', function (promiseTracker) {
      return {
        template: '' +
            '<div ng-show="loadingTracker.active()" class="overlay">' +
            '<p class="message">Loading...</p>' +
            '</div>',
        restrict: 'E',
        replace: true,
        link: function (scope, elem, attrs) {
          scope.loadingTracker = promiseTracker('loadingTracker');
        }
      };
    }]);
