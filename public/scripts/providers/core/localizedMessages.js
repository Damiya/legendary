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
    .factory('localizedMessages', ['$interpolate', 'I18N.MESSAGES', function ($interpolate, i18nmessages) {

      var handleNotFound = function (msg, msgKey) {
        return msg || '?' + msgKey + '?';
      };

      return {
        get: function (msgKey, interpolateParams) {
          var msg = i18nmessages[msgKey];
          if (msg) {
            return $interpolate(msg)(interpolateParams);
          } else {
            return handleNotFound(msg, msgKey);
          }
        }
      };
    }])
    .value('I18N.MESSAGES', {
      'login.reason.notAuthenticated': 'Your token has expired. You must log back in.',
      'login.error.invalidCredentials': 'Login failed.  Please check your credentials and try again.',
      'login.error.serverError': 'There was a problem with authenticating: {{exception}}.',
      'logout.successful': 'You have successfully logged out.'
    });