app.service('searchService',function ($http) {
    //查询方法
    this.search=function (searchMap) {
      return  $http.post('/search.do',searchMap);
    }
})
