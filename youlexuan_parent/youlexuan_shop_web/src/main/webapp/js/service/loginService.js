app.service('loginService',function ($http){
    this.findLoginName=function () {
        return $http.get('../seller/findLoginName.do')
    }
})