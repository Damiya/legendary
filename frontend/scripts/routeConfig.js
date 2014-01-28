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
            templateUrl: 'partials/home/index'
          })
          .state('home.landingPage', {
            url: '',
            resolve: {
              login: ['loginService', function (loginService) {
                return loginService.requireAuthentication();
              }]
            },
            templateUrl: 'partials/home/landingPage',
            controller: 'HomeController'
          })
          .state('home.loginRequired', {
            url: 'login',
            templateUrl: 'partials/security/loginForm',
            controller: 'LoginFormController'
          });
    }]);