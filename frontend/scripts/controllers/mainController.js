'use strict';

angular.module('legendary')
    .controller('MainController', ['$scope', '$http', '$rootScope', '$log', 'promiseTracker',
      function ($scope, $http, $rootScope, $log, promiseTracker) {
        promiseTracker.register('loadingTracker');

        $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState) {
          $log.debug('Transition completed');
        });
        $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState) {
          $log.debug('Transition started from ' + fromState.name + ' to ' + toState.name);
        });
        $rootScope.$on('$stateChangeError', function (event, toState, toParams, fromState) {
          $log.error('Transition errored');
        });

        console.log('MainController: Instantiated');
      }]);
