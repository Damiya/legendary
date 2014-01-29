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
            templateUrl: 'assets/views/partials/home/index.html'
          })
          .state('home.landingPage', {
            url: '',
            resolve: {
              login: ['loginService', function (loginService) {
                return loginService.requireAuthentication();
              }]
            },
            templateUrl: 'assets/views/partials/home/landingPage.html',
            controller: 'HomeController'
          })
          .state('home.loginRequired', {
            url: 'login',
            templateUrl: 'assets/views/partials/security/loginForm.html',
            controller: 'LoginFormController'
          });
    }]);