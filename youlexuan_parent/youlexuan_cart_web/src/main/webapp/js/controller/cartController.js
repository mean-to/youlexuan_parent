app.controller('cartController',function ($scope, cartService) {
    //获取购物车数据方法
    $scope.findCartList=function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList=response;
            //计算购物车合计数据
          $scope.totalValue=  cartService.sum($scope.cartList);
        })
    }
    //添加商品到购物车
    $scope.addGoodsToCartList=function (itemId, num) {
        cartService.addGoodsToCartList(itemId,num).success(function (response) {
            if(response.success){
                //刷新购物车列表数据
                $scope.findCartList();
            }else {
                alert(response.message);
            }
        })
    }


})
