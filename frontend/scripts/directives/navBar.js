'use strict';

angular.module('legendary')
    .directive('navbar', function () {
      return {
        templateUrl: 'partials/navBar',
        restrict: 'E',
        controller: ['$scope',  'promiseTracker', 'loginService',
          function ($scope, promiseTracker, loginService) {
            $scope.isAuthenticated = loginService.isAuthenticated;

            $scope.logout = loginService.logout;

            $scope.loadingTracker = promiseTracker('loadingTracker');
          }]
      };
    });
