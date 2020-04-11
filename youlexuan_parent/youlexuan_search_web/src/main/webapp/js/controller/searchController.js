app.controller('searchController',function ($scope,$location, searchService) {

    //搜索请求
    $scope.search=function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.list=response.rows;
            $scope.resultMap=response;
        })
    }
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','sortField':'','sort':''};
    //搜索对象
    // 添加搜索项
    $scope.addSearchItem=function(key,value)
    { if(key=='category' || key=='brand'||key=='price'){
        //如果点击的是分类或者是品牌
        $scope.searchMap[key]=value;
    }else{
        $scope.searchMap.spec[key]=value; }
        $scope.search();//搜索
    }
    $scope.removeSearchItem=function(key){
        if(key=="category" || key=="brand"||key=='price'){
            //如果是分类或品牌
            $scope.searchMap[key]="";
        }else{
            //否则是规格
            delete $scope.searchMap.spec[key];
            //移除此属性
            }
        $scope.search();
        }
        $scope.sortSearch=function (sortField,sort) {
            $scope.searchMap.sort=sort;
            $scope.searchMap.sort=sort;
            $scope.search();
        }
        $scope.keywordsIsBrand=function () {
            for(var i=0;i<$scope.resultMap.brandList.length;i++){
                if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                    // 如果包含
                    return true;
                }
                return false;
            }
        }
$scope.loadkeywords=function () {
    $scope.searchMap.keywords=$location.search()['keywords'];
    $scope.search();
}

})
