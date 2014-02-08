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
    .directive('lgdFeaturedVideo', function () {
      return {
        template: '<div class="well well-sm featured-video-container"></div>',
        restrict: 'E',
        scope: {
          video: '='
        },
        replace: true,
        link: function (scope, element, attrs) {
          var youtubeEmbedded = false;

          scope.$watch('video', function (value) {
            if (!youtubeEmbedded && value) {
              var videoId = value.url.split('=')[1];
              element.append('<iframe class="youtube-player" src="https://www.youtube.com/embed/' + videoId + '"' +
                  ' type="text/html" width="425" height="295" id="player" allowfullscreen frameborder="0"></iframe>');
            }
          });
        }
      };
    });
