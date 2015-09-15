'use strict';

angular.module('videostoreApp')
    .controller('MovieDetailController', function ($scope, $rootScope, $stateParams, entity, Movie, Director) {
        $scope.movie = entity;
        $scope.load = function (id) {
            Movie.get({id: id}, function(result) {
                $scope.movie = result;
            });
        };
        $rootScope.$on('videostoreApp:movieUpdate', function(event, result) {
            $scope.movie = result;
        });
    });
