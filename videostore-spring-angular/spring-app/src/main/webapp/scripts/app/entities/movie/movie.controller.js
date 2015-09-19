'use strict';

angular.module('videostoreApp')
    .controller('MovieController', function ($scope, Movie, MovieSearch, ParseLinks) {
        $scope.movies = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Movie.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.movies = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Movie.get({id: id}, function(result) {
                $scope.movie = result;
                $('#deleteMovieConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Movie.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteMovieConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            MovieSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.movies = result;
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
            $scope.movie = {name: null, releasedDate: null, description: null, id: null};
        };
    });
