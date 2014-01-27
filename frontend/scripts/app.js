'use strict';

angular.module('legendary.js', [
      'ngCookies',
      'ngResource',
      'ngSanitize',
      'security',
      'services.httpRequestTracker',
      'ui.router',
      'ui.bootstrap'
    ])
    .run(['$cookieStore', function ($cookieStore) {
      $cookieStore.remove('django-authtoken');
      $cookieStore.remove('django-csrftoken');
    }])
    .constant('I18N.MESSAGES', {
      'login.reason.notAuthenticated': 'Your token has expired. You must log back in.',
      'login.error.invalidCredentials': 'Login failed.  Please check your credentials and try again.',
      'login.error.serverError': 'There was a problem with authenticating: {{exception}}.',
      'logout.successful': 'You have successfully logged out.'
    });