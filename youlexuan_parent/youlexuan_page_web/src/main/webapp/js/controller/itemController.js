app.controller('itemController',function ($scope) {
    //当用户点击 + 或者 - 调整购买数量
    $scope.addNum=function (value) {
        $scope.num=$scope.num+value;

        //判断num不能小于等于0
        if($scope.num<=0){
            $scope.num=1;
        }
    }

    //定义一个json对象，保存用户选中的规格和规格选项
    $scope.specificationItems={};

    //用户点击对应规格选项，保存规格和规格选项
    $scope.selectSpecification=function (name, value) {
        //｛"网络":"移动4G"｝
        $scope.specificationItems[name]=value;
        searchSku();
    }
    //判断指定规格和规格选项是否被选中
    $scope.isSelected=function (name, value) {
        if($scope.specificationItems[name]==value){
            return true;
        }else {
            return false;
        }
    }

    //加载sku数据 第一个
    $scope.loadSku=function () {
      $scope.sku=  skuList[0];
      //把第一个sku商品规格和规格选项赋值给保存用户选中的规格和规格选项
        $scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
    }

    //比对2个json对象，看是否相等
    matchObject=function (map1, map2) {
        //1、从map1向map2进行逐个比对
        for(var k in map1){
            if(map1[k]!=map2[k]){
                return false;
            }
        }
        //2、从map2向map1逐个比对
        for(var k in map2){
            if(map2[k]!=map1[k]){
                return false;
            }
        }

        return true;
    }

    //查询sku列表，和用户选中规格进行比对
    searchSku=function () {

        //遍历sku集合
        for(var i=0;i<skuList.length;i++){
            if(matchObject(skuList[i].spec,$scope.specificationItems)){
                //返回当前sku节点
               $scope.sku= skuList[i];

               return;
            }
        }

        $scope.sku={"title":"----","price":0.0};

    }

    //加入购物车
    $scope.addToCart=function () {
        alert("加入购物车成功");
    }

})
