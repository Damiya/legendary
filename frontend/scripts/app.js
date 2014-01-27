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
    .run(['security', '$http', '$cookieStore', function (security, $http, $cookieStore) {
      $cookieStore.remove('djangotoken');
      $http.get('http://localhost:8000/get-csrf-token/')
          .success(function (data, status, headers, config) {
            $http.defaults.headers.common['x-csrftoken'] = data.token;
          });
    }])
    .constant('I18N.MESSAGES', {
      'login.reason.notAuthenticated': 'Your token has expired. You must log back in.',
      'login.error.invalidCredentials': 'Login failed.  Please check your credentials and try again.',
      'login.error.serverError': 'There was a problem with authenticating: {{exception}}.',
      'logout.successful': 'You have successfully logged out.'
    });