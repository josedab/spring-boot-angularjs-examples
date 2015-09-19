'use strict';

angular.module('videostoreApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


