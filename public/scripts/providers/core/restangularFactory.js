/*
 * Copyright 2014 Kate von Roeder (katevonroder at gmail dot com)
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
    .factory('RestangularFactory', ['Restangular', '$window', function (Restangular, $window) {
      var restangular = Restangular.withConfig(function (RestangularConfigurer) {
        RestangularConfigurer.setDefaultHttpFields({tracker: 'loadingTracker'});
        RestangularConfigurer.setBaseUrl('/api/');

        RestangularConfigurer.setResponseExtractor(function (response) {
          var newResponse = response;
          if (angular.isArray(response)) {
            angular.forEach(newResponse, function (value, key) {
              newResponse[key].originalElement = angular.copy(value);
            });
          } else {
            newResponse.originalElement = angular.copy(response);
          }

          return newResponse;
        });

        RestangularConfigurer.addElementTransformer('token', function (tokens) {

          tokens.addRestangularMethod('create', 'post', 'create');

          tokens.addRestangularMethod('destroy', 'remove', 'destroy');
          return tokens;
        });
      });

      return {
        league: restangular.all('league'),
        core: restangular
      };
    }]);
