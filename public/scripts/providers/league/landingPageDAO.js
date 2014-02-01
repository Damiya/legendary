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
    .factory('landingPageDAO', ['$rootScope', 'RestangularFactory', '$window', function ($rootScope, RestangularFactory, $window) {
      var factory = {
        gameList: null,

        nextGameListUpdate: null,

        getLandingPageContent: function () {
          return RestangularFactory.league.one('landingPage').get().then(function (response) {
            $window.sessionStorage.setItem('landingPageContent', JSON.stringify(response.originalElement));

            return response.originalElement;
          });
        },

        getGameList: function () {
          var now = new Date().getTime();

          if (factory.nextGameListUpdate <= now) {
            return RestangularFactory.league.one('featuredGames').get().then(function (response) {
              factory.gameList = response.gameList;
              factory.nextGameListUpdate = now + response.clientRefreshInterval;

              return response.gameList;
            });
          } else {
            return factory.gameList;
          }
        }
      };

      return factory;
    }]);
