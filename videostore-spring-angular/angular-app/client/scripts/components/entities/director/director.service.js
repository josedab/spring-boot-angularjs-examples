'use strict';

angular.module('videostoreApp')
    .factory('Director', function ($resource, DateUtils) {
        return $resource('api/directors/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.birthdate = DateUtils.convertDateTimeFromServer(data.birthdate);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
