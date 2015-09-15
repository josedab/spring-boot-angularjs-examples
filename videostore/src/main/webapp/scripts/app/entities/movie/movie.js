'use strict';

angular.module('videostoreApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('movie', {
                parent: 'entity',
                url: '/movies',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'videostoreApp.movie.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/movie/movies.html',
                        controller: 'MovieController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('movie');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('movie.detail', {
                parent: 'entity',
                url: '/movie/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'videostoreApp.movie.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/movie/movie-detail.html',
                        controller: 'MovieDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('movie');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Movie', function($stateParams, Movie) {
                        return Movie.get({id : $stateParams.id});
                    }]
                }
            })
            .state('movie.new', {
                parent: 'movie',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/movie/movie-dialog.html',
                        controller: 'MovieDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {name: null, releasedDate: null, description: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('movie', null, { reload: true });
                    }, function() {
                        $state.go('movie');
                    })
                }]
            })
            .state('movie.edit', {
                parent: 'movie',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/movie/movie-dialog.html',
                        controller: 'MovieDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Movie', function(Movie) {
                                return Movie.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('movie', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
