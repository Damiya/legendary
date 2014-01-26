'use strict';

angular.module('legendary.js')
    .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
      $urlRouterProvider.otherwise('/');
      $stateProvider
          .state('home', {
            abstract: true,
            url: '/',
            templateUrl: 'templates/home/index'
          })
          .state('home.landingPage', {
            url: '',
            resolve: {
              currentUser: ['securityAuthorization', function (securityAuthorization) {
                securityAuthorization.requireAuthenticatedUser();
              }]
            },
            templateUrl: 'templates/home/landingPage',
            controller: 'HomeController'
          })
          .state('home.loginRequired', {
            url: 'login',
            templateUrl: 'templates/security/loginForm',
            controller: 'LoginFormController'
          });
    }]);