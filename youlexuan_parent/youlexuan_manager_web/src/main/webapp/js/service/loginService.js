app.service('loginService',function ($http) {
    this.showLoginName=function () {
        return $http.get('../user/showLoginName.do');
    }
})