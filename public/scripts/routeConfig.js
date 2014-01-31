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
    .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
      $urlRouterProvider.when('/', ['$state', 'loginService', function ($state, loginService) {
        if (!loginService.isAuthenticated()) {
          $state.go('home.loginRequired');
        } else {
          $state.go('home.landingPage');
        }
      }]);

      // Todo: We should just generate a bunch of these states from arrays since there's so much boilerplate
      $urlRouterProvider.otherwise('/login');
      $stateProvider
          .state('home', {
            abstract: true,
            url: '/',
            templateUrl: 'views/partials/abstract.html'
          })
          .state('home.landingPage', {
            url: '',
            resolve: {
              login: ['loginService', function (loginService) {
                return loginService.requireAuthentication();
              }]
            },
            templateUrl: 'views/partials/home/landingPage.html',
            controller: 'HomeController'
          })
          .state('home.loginRequired', {
            url: 'login',
            templateUrl: 'views/partials/security/loginForm.html',
            controller: 'LoginFormController'
          })
          .state('profile', {
            url: '/profile',
            abstract: true,
            templateUrl: 'views/partials/abstract.html'
          })
          .state('profile.overview', {
            url: '',
            resolve: {
              login: ['loginService', function (loginService) {
                return loginService.requireAuthentication();
              }]
            },
            templateUrl: 'views/partials/profile/overview.html',
            controller: 'ProfileOverviewController'
          })
          .state('profile.runes', {
            url: '/runes',
            resolve: {
              login: ['loginService', function (loginService) {
                return loginService.requireAuthentication();
              }]
            },
            templateUrl: 'views/partials/profile/runes.html',
            controller: 'RunesController'
          })
          .state('profile.masteries', {
            url: '/masteries',
            resolve: {
              login: ['loginService', function (loginService) {
                return loginService.requireAuthentication();
              }]
            },
            templateUrl: 'views/partials/profile/masteries.html',
            controller: 'MasteriesController'
          }).state('profile.leagues', {
            url: '/leagues',
            resolve: {
              login: ['loginService', function (loginService) {
                return loginService.requireAuthentication();
              }]
            },
            templateUrl: 'views/partials/profile/leagues.html',
            controller: 'LeaguesController'
          });

    }]);