'use strict';

angular.module('legendary')
    .controller('HomeController', ['$scope', '$http', '$state', 'loginToken',
      function ($scope, $http, $state, loginQueueToken) {
        console.log('yea ok');
        $scope.doAuth = function () {
          $state.go('home.landingPage');
        };
      }]);
