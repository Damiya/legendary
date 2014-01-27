'use strict';

angular.module('legendary')
    .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
      $urlRouterProvider.otherwise('/login');
      $stateProvider
          .state('home', {
            abstract: true,
            url: '/',
            templateUrl: 'partials/home/index'
          })
          .state('home.noLeague', {
            url: 'noLeague',
            resolve: {
              currentUser: ['securityAuthorization', function (securityAuthorization) {
                securityAuthorization.requireAuthenticatedUser();
                return securityAuthorization.promise;
              }]
            },
            templateUrl: 'partials/home/noLeague',
            controller: 'NoLeagueHomeController'
          })
          .state('home.landingPage', {
            url: '',
            resolve: {
              currentUser: ['securityAuthorization', function (securityAuthorization) {
                securityAuthorization.requireAuthenticatedUser();
                return securityAuthorization.promise;
              }],
              loginToken: ['loginTokenProvider', function (loginTokenProvider) {
                loginTokenProvider.requireLoginToken();
                return loginTokenProvider.promise;
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