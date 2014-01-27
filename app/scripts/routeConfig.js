'use strict';

angular.module('legendary.js')
    .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
      $urlRouterProvider.otherwise('/');
      $stateProvider
          .state('home', {
            abstract: true,
            url: '/',
            templateUrl: 'partials/home/index'
          })
          .state('home.landingPage', {
            url: '',
            resolve: {
              currentUser: ['securityAuthorization', function (securityAuthorization) {
                securityAuthorization.requireAuthenticatedUser();
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