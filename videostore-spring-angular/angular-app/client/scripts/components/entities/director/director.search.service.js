'use strict';

angular.module('videostoreApp')
    .factory('DirectorSearch', function ($resource) {
        return $resource('api/_search/directors/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
