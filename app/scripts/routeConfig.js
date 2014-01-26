'use strict';

angular.module('app')
    .config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {
      $urlRouterProvider.otherwise('/');
      $stateProvider
          .state('home', {
            url: '/',
            abstract: true
          })
          .state('home.loggedIn', {
            templateUrl: 'templates/main',
            controller: 'MainCtrl',
            url: '/index'
          })
          .state('home.loggedOut', {
            template: 'Please login',
            url: '/login'
          });
//      $routeProvider
//          .when('/', {
//            templateUrl: 'partials/main',
//            controller: 'MainCtrl'
//          })
//          .otherwise({
//            redirectTo: '/'
//          });
//
//      $locationProvider.html5Mode(true);
    }]);