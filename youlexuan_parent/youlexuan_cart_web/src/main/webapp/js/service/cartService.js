app.service('cartService',function ($http) {
    //获取购物车数据
    this.findCartList=function () {
      return  $http.get('cart/findCartList.do');
    }
    //调用添加商品到购物车
    this.addGoodsToCartList=function (itemId, num) {
     return   $http.get('cart/addGoodsToCartList.do?itemId='+itemId+"&num="+num);
    }




    //编写汇总方法，计算当前购物车所有的商品总数量和总金额
    this.sum=function (cartList) {
        //定义一个对象，存储汇总信息
        var totalValue={totalNum:0,totalFee:0.00};
        //遍历购物车集合
        for(var i=0;i<cartList.length;i++){
            //定义商家购物车变量
            var cart=cartList[i];
            //遍历商家购物车明细
            for(var j=0;j<cart.orderItemList.length;j++){
                var orderItem=cart.orderItemList[j];
                //计算购买数量
                totalValue.totalNum+=orderItem.num;
                //计算合计金额
                totalValue.totalFee+=orderItem.totalFee;
            }
        }

        return totalValue;
    }
})
