'use strict';

angular.module('videostoreApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
