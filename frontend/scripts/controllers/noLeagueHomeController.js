'use strict';

angular.module('legendary')
    .controller('NoLeagueHomeController', ['$scope', '$http', '$rootScope',
      function ($scope, $http, $rootScope) {
        console.log('NoLeagueHomeController: Success');
//        $scope.alerts = [];
//        $scope.loginToLeague = function () {
//          $state.go('home.landingPage');
//        };
//        $rootScope.$on('$stateChangeError', function (event, toState, toParams, fromState) {
//          if (fromState.name === 'home.noLeague' && toState.name === 'home.landingPage') {
//            $scope.alerts.push({
//              msg: 'Hey, you don\'t have a league account. Your credentials are all wrong.',
//              type: 'danger'
//            });
//          }
//        });
      }]);
