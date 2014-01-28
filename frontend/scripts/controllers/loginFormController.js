'use strict';

angular.module('legendary')
// The LoginFormController provides the behaviour behind a reusable form to allow users to authenticate.
// This controller and its template (login/form.html) are used in a modal dialog box by the security service.
    .controller('LoginFormController', ['$scope', '$http',
      function ($scope, $http) {
        console.log('LoginFormController: Success');
        // The model for this form
        $scope.user = {
          username: null,
          password: null
        };

      }]);
