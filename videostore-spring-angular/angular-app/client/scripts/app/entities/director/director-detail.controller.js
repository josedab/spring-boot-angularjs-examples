'use strict';

angular.module('videostoreApp')
    .controller('DirectorDetailController', function ($scope, $rootScope, $stateParams, entity, Director, Movie) {
        $scope.director = entity;
        $scope.load = function (id) {
            Director.get({id: id}, function(result) {
                $scope.director = result;
            });
        };
        $rootScope.$on('videostoreApp:directorUpdate', function(event, result) {
            $scope.director = result;
        });
    });
