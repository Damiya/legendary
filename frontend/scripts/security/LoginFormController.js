// Code from https://github.com/angular-app/angular-app

'use strict';

angular.module('security.login.form', ['services.localizedMessages'])
// The LoginFormController provides the behaviour behind a reusable form to allow users to authenticate.
// This controller and its template (login/form.html) are used in a modal dialog box by the security service.
    .controller('LoginFormController', ['$scope', 'security', 'localizedMessages', function ($scope, security, localizedMessages) {
      // The model for this form
      $scope.user = {
        email: null,
        password: null
      };

      $scope.alerts = [];

      if (security.getLoginReason()) {
        var alertMsg = security.isAuthenticated() ?
            localizedMessages.get('login.reason.notAuthorized') :
            localizedMessages.get('login.reason.notAuthenticated');

        $scope.alerts.push({
          msg: alertMsg,
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
        security.login($scope.user.email, $scope.user.password).then(function (loggedIn) {
          if (!loggedIn) {
            // If we get here then the login failed due to bad credentials
            $scope.alerts.push({
              msg: localizedMessages.get('login.error.invalidCredentials'),
              type: 'danger'
            });
          }
        }, function (x) {
          // If we get here then there was a problem with the login request to the server
          $scope.alerts.push({
            msg: localizedMessages.get('login.error.serverError', { exception: x }),
            type: 'danger'
          });
        });


      };

      $scope.clearForm = function () {
        $scope.user.email = null;
        $scope.user.password = null;
      };

      $scope.cancelLogin = function () {
        security.cancelLogin();
      };
    }]);
