'use strict';

angular.module('legendary', [
      'ngCookies',
      'ngResource',
      'ngSanitize',
      'security',
      'services.httpRequestTracker',
      'services.loginTokenProvider',
      'legendary.constants',
      'ui.router',
      'ui.bootstrap'
    ])
    .run(['$cookieStore', function ($cookieStore) {
      $cookieStore.remove('django-authtoken');
      $cookieStore.remove('django-csrftoken');
    }]);