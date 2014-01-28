'use strict';

angular.module('legendary')
// The LoginFormController provides the behaviour behind a reusable form to allow users to authenticate.
// This controller and its template (login/form.html) are used in a modal dialog box by the security service.
    .controller('LoginFormController', ['$scope', 'loginService',
      function ($scope, loginService) {
        console.log('LoginFormController: Instantiated');
        // The model for this form
        $scope.user = {
          username: null,
          password: null
        };

        $scope.login = function () {
          loginService.login($scope.user.username, $scope.user.password);
        };

      }]);
