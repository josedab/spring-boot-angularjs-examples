'use strict';

angular.module('videostoreApp').controller('DirectorDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Director', 'Movie',
        function($scope, $stateParams, $modalInstance, entity, Director, Movie) {

        $scope.director = entity;
        $scope.movies = Movie.query();
        $scope.load = function(id) {
            Director.get({id : id}, function(result) {
                $scope.director = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('videostoreApp:directorUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.director.id != null) {
                Director.update($scope.director, onSaveFinished);
            } else {
                Director.save($scope.director, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
