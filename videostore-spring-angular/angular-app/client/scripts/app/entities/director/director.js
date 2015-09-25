'use strict';

angular.module('videostoreApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('director', {
                parent: 'entity',
                url: '/directors',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'videostoreApp.director.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/director/directors.html',
                        controller: 'DirectorController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('director');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('director.detail', {
                parent: 'entity',
                url: '/director/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'videostoreApp.director.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/director/director-detail.html',
                        controller: 'DirectorDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('director');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Director', function($stateParams, Director) {
                        return Director.get({id : $stateParams.id});
                    }]
                }
            })
            .state('director.new', {
                parent: 'director',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/director/director-dialog.html',
                        controller: 'DirectorDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {name: null, birthdate: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('director', null, { reload: true });
                    }, function() {
                        $state.go('director');
                    })
                }]
            })
            .state('director.edit', {
                parent: 'director',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/director/director-dialog.html',
                        controller: 'DirectorDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Director', function(Director) {
                                return Director.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('director', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
