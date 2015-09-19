'use strict';

angular.module('videostoreApp')
    .factory('MovieSearch', function ($resource) {
        return $resource('api/_search/movies/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
