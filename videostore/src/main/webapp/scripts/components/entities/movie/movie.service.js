'use strict';

angular.module('videostoreApp')
    .factory('Movie', function ($resource, DateUtils) {
        return $resource('api/movies/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.releasedDate = DateUtils.convertDateTimeFromServer(data.releasedDate);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
