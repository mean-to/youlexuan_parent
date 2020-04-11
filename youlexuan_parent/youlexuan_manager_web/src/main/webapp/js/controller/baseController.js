app.controller('baseController',function ($scope) {
    //json字符串分割，拼接
    $scope.jsonToString=function (jsonStr, key) {
        //1、把json字符串解析成json对象
        var jsonObj= JSON.parse(jsonStr);

        var value="";

        //2、遍历json数组
        for(var i=0;i<jsonObj.length;i++){
            if(i>0){
                value+=",";
            }
            value+=jsonObj[i][key];
        }

        return value;
    }





    //切换页码,请求刷新
    $scope.reloadList=function () {
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage)
    }
    //分页控件设置
    $scope.paginationConf={
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange:function() {
            $scope.reloadList();
        }
    }
    $scope.selectIds = [];//选中的ID
    $scope.updateSelection=function ($event,id) {
        if($event.target.checked){
            $scope.selectIds.push(id);
        }else{
            var idx=$scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx,1);
        }
    }

})
