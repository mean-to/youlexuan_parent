app.controller('indexController',function ($scope,$controller,loginService) {
    $controller('baseController',{$scope:$scope});
    $scope.showName=function () {
        loginService.showName().success(
            function (response) {
                $scope.loginName=response.loginName;
            }
        )
    }

})