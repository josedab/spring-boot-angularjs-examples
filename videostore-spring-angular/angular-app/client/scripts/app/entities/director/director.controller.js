'use strict';

angular.module('videostoreApp')
    .controller('DirectorController', function ($scope, Director, DirectorSearch, ParseLinks) {
        $scope.directors = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Director.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.directors = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Director.get({id: id}, function(result) {
                $scope.director = result;
                $('#deleteDirectorConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Director.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteDirectorConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            DirectorSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.directors = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.director = {name: null, birthdate: null, id: null};
        };
    });
