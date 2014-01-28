'use strict';

angular.module('legendary', [
      'ngResource',
      'ngSanitize',
      'ui.router',
      'ui.bootstrap',
      'ajoslin.promise-tracker'
    ]);
//    .run(['cookieManager', function (cookieManager) {
//      //cookieManager.remove('lol-loginToken');
////      cookieManager.remove('django-authToken');
////      cookieManager.remove('django-csrfToken');
//    }]);