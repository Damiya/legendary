'use strict';

angular.module('roflclientJsApp', [
      'ngCookies',
      'ngResource',
      'ngSanitize',
      'security',
      'services.httpRequestTracker',
      'ui.router',
      'ui.bootstrap'
    ])
    .run(['security', function (security) {
      security.requestCurrentUser();
    }])
    .constant('I18N.MESSAGES', {
      'login.reason.notAuthenticated': 'You must log in first.',
      'login.error.invalidCredentials': 'Login failed.  Please check your credentials and try again.',
      'login.error.serverError': 'There was a problem with authenticating: {{exception}}.'
    });