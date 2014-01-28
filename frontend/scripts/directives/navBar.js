'use strict';

angular.module('legendary')
    .directive('navbar', function () {
      return {
        templateUrl: 'partials/navBar',
        restrict: 'E',
        controller: ['$scope',  'promiseTracker',
          function ($scope, promiseTracker) {
//            $scope.location = $location;
//
//            $scope.isAuthenticated = security.isAuthenticated;
//            $scope.login = security.showLogin;
//            $scope.logout = security.logout;
//
//            $scope.$watch(function () {
//              return security.currentUser;
//            }, function (currentUser) {
//              $scope.currentUser = currentUser;
//            });

            $scope.loadingTracker = promiseTracker('loadingTracker')
          }]
      };
    });
