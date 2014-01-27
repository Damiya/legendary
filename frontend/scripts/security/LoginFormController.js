// Code from https://github.com/angular-app/angular-app

'use strict';

angular.module('security.login.form', ['services.localizedMessages'])
// The LoginFormController provides the behaviour behind a reusable form to allow users to authenticate.
// This controller and its template (login/form.html) are used in a modal dialog box by the security service.
    .controller('LoginFormController', ['$scope', 'security', 'localizedMessages', function ($scope, security, localizedMessages) {
      // The model for this form
      $scope.user = {
        username: null,
        password: null
      };

      $scope.alerts = $scope.alerts || [];

      if (security.getLoginReason()) {
        security.clearLocalToken();
        $scope.alerts.push({
          msg: localizedMessages.get('login.reason.notAuthenticated'),
          type: 'warning'
        });
      }

      $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
      };

      // Attempt to authenticate the user specified in the form's model
      $scope.login = function () {
        // Clear any previous security errors
        $scope.authError = null;

        // Try to login
        security.login($scope.user.username, $scope.user.password).then(function (loggedIn) {
          $scope.alerts = [];
        }, function (response) {
          // If we get here then there was a problem with the login request to the server
          $scope.alerts.push({
            msg: response.data.non_field_errors[0],
            type: 'danger'
          });
        });
      };

      $scope.clearForm = function () {
        $scope.user.username = null;
        $scope.user.password = null;
      };

      $scope.cancelLogin = function () {
        security.cancelLogin();
      };
    }]);
