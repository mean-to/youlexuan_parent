app.service('loginService',function($http){
    //读取列表数据绑定表单中
    this.showName=function () {
        return $http.get('../user/getName.do');
    }
});