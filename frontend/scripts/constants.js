'use strict';
angular.module('legendary.constants', [])
    .constant('I18N.MESSAGES', {
      'login.reason.notAuthenticated': 'Your token has expired. You must log back in.',
      'login.error.invalidCredentials': 'Login failed.  Please check your credentials and try again.',
      'login.error.serverError': 'There was a problem with authenticating: {{exception}}.',
      'logout.successful': 'You have successfully logged out.'
    })
    .constant('apiEndpoint', 'http://localhost:8000/');