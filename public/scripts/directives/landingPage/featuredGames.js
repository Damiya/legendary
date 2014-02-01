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
    .directive('lgdFeaturedGame', function () {
      return {
        templateUrl: 'views/partials/landingPage/directives/featuredGame.html',
        restrict: 'E',
        replace: true,
        scope: {
          participants: '='
        },
        link: function (scope, element, attrs) {
          scope.$watch('participants', function (value) {
            if (!value) {
              return;
            }
            var participantCount = value.length || 0;
            scope.redTeam = [];
            scope.blueTeam = [];
            for (var i = 0; i < participantCount; i++) {
              var participant = value[i];
              if (participant.teamId === 200) {
                scope.redTeam.push(participant);
              } else {
                scope.blueTeam.push(participant);
              }
            }
          });
        }
      };
    })
    .directive('lgdFeaturedGameParticipant', function () {
      return {
        template: '' +
            '<div class="featured-game-participant">' +
            '<img ng-src="images/league/champion/icons/{{participant.championId}}_Web_0.jpg" class="champion-icon">' +
            '<strong class="participant-name well well-sm">{{participant.summonerName}}</strong>' +
            '</div>',
        restrict: 'E',
        replace: true
      };
    });
