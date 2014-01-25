'use strict';

angular.module('app')
    .controller('NavbarCtrl', ['$scope', '$location', 'security', 'httpRequestTracker',
      function ($scope, $location, security, httpRequestTracker) {
        $scope.location = $location;

        $scope.isAuthenticated = security.isAuthenticated;
        $scope.isAdmin = security.isAdmin;

        $scope.home = function () {
          if (security.isAuthenticated()) {
            $location.path('/dashboard');
          } else {
            $location.path('/projectsinfo');
          }
        };

        $scope.hasPendingRequests = function () {
          return httpRequestTracker.hasPendingRequests();
        };
      }]);