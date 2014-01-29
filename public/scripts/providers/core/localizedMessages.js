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