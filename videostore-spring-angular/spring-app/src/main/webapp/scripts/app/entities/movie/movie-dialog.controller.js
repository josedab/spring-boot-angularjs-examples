'use strict';

angular.module('videostoreApp').controller('MovieDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Movie', 'Director',
        function($scope, $stateParams, $modalInstance, entity, Movie, Director) {

        $scope.movie = entity;
        $scope.directors = Director.query();
        $scope.load = function(id) {
            Movie.get({id : id}, function(result) {
                $scope.movie = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('videostoreApp:movieUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.movie.id != null) {
                Movie.update($scope.movie, onSaveFinished);
            } else {
                Movie.save($scope.movie, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
