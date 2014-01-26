'use strict';

angular.module('roflclientJsApp')
    .directive('navbar', function () {
      return {
        templateUrl: 'templates/navbar',
        restrict: 'E',
        controller: ['$scope', '$location', 'security', 'httpRequestTracker',
          function ($scope, $location, security, httpRequestTracker) {
            $scope.location = $location;

            $scope.isAuthenticated = security.isAuthenticated;
            $scope.login = security.showLogin;
            $scope.logout = security.logout;
            $scope.$watch(function () {
              return security.currentUser;
            }, function (currentUser) {
              $scope.currentUser = currentUser;
            });

            $scope.hasPendingRequests = function () {
              return httpRequestTracker.hasPendingRequests();
            };
          }]
      };
    });
