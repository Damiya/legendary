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
      $urlRouterProvider.when('/', ['$state', 'loginService', function($state, loginService) {
        if (!loginService.isAuthenticated()) {
          $state.go('home.loginRequred');
        } else {
          $state.go('home.landingPage');
        }
      }]);
      $urlRouterProvider.otherwise('/login');
      $stateProvider
          .state('home', {
            abstract: true,
            url: '/',
            templateUrl: 'views/partials/home/index.html'
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
          });
    }]);