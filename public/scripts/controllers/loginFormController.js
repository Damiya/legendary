/*
 * Copyright 2014 Kate von Roeder (katevonroder at gmail dot com) - twitter: @itsdamiya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
