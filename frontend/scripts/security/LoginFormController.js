// Code from https://github.com/angular-app/angular-app

'use strict';

angular.module('security.login.form', [])
// The LoginFormController provides the behaviour behind a reusable form to allow users to authenticate.
// This controller and its template (login/form.html) are used in a modal dialog box by the security service.
    .controller('LoginFormController', ['$scope', 'security', 'loginQueueFactory',
      function ($scope, security, loginQueueFactory) {
        // The model for this form
        $scope.user = {
          username: null,
          password: null
        };

        security.clearLocalToken();

        $scope.alerts = security.alerts || [];

        $scope.closeAlert = function (index) {
          $scope.alerts.splice(index, 1);
        };

        // Attempt to authenticate the user specified in the form's model
        $scope.login = function () {
          // Try to login
          security.login($scope.user.username, $scope.user.password);
        };

        $scope.clearForm = function () {
          $scope.user.username = null;
          $scope.user.password = null;
        };

        $scope.cancelLogin = function () {
          security.cancelLogin();
        };
      }]);
