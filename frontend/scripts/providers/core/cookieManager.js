'use strict';

angular.module('legendary')
    .factory('cookieManager', function () {
      return {
        get: function (key) {
          return Cookies.get(key);
        },
        put: function (key, value, options) {
          return Cookies.set(key, value, options);
        },
        remove: function (key, options) {
          return Cookies.expire(key, options);
        }
      };
    });