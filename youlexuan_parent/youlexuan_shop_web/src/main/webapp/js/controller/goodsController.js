 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location ,goodsService,uploadService,itemCatService,typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        var id=$location.search()['id'];//获取参数值
        if(id==null){
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //向富文本编辑器添加商品介绍
                editor.html($scope.entity.tbGoodsDesc.introduction);
                //将图片列表由字符串转化为json集合对象，显示图片列表
                $scope.entity.tbGoodsDesc.itemImages=JSON.parse($scope.entity.tbGoodsDesc.itemImages);
                //读取商品扩展属性,处理覆盖以后
               $scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems)
                //读取规格属性
                $scope.entity.tbGoodsDesc.specificationItems=JSON.parse($scope.entity.tbGoodsDesc.specificationItems);
               //sku列表规格规格选项列转换
                for (var i=0;i<$scope.entity.itemList.length;i++){
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        );
    }
    //根据规格名称和规格选项名称返回是否勾选
    $scope.checkAttributeValue=function(specName,optionName){
    var items=$scope.entity.tbGoodsDesc.specificationItems;
    var object=$scope.searchObjectByKey(items,'attributeName',specName);
    if (object==null){
        return false;
    } else {
        if(object.attributeValue.indexOf(optionName)>0){
            return true;
        }else {
            return false}
    }
    }



    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        $scope.entity.tbGoodsDesc.introduction=editor.html();
        if ($scope.entity.tbGoods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    location.href="/admin/goods.html";
                } else {
                    alert(response.message);
                }
            }
        );
    }
    $scope.add = function () {
        $scope.entity.tbGoodsDesc.introduction = editor.html();
        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {
                    /*保存成功后请空*/
                    $scope.entity = {tbGoodsDesc: {itemImages: [], specificationItems: []}}
                    editor.html('');
                } else {
                    alert(response.message)
                }
            }
        )
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }
    //上传图片
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    $scope.image_entity.url = response.message;
                } else {
                    alert(response.message);
                }
            }
        )
    }
   /* $scope.entity = {tbGoods: {}, tbGoodsDesc: {itemImages: []}}*/
    //图片列表
    $scope.add_image_entity = function () {
        $scope.entity.tbGoodsDesc.itemImages.push($scope.image_entity);
    }
    $scope.remove_image_entity = function (index) {
        $scope.entity.tbGoodsDesc.itemImages.splice(index, 1);
    }
    //一级分类选择框
    $scope.selectItemCatList = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;
            }
        )
    }
    //读取二级分类,$watch方法用于监控某个变量的值，当被监控的值发生变化，就自动执行相应的函数。
    $scope.$watch('entity.tbGoods.category1Id', function (newValue, oldValue) {
        //判断一级分类有选择具体分类值，再去获取二级分类
        if (newValue) {
            //根据选择的值，查询二级分类
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat2List = response;
                }
            )

        }


    })

//读取三级分类
    $scope.$watch('entity.tbGoods.category2Id', function (newValue, oldValue) {
        //判断二级分类有选择具体分类值，再去获取三级分类
        if (newValue) {
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCat3List = response;
                }
            )
        }

    })

//三级分类选择后 读取模板id
    $scope.$watch("entity.tbGoods.category3Id", function (newValue, oldValue) {
        //获取模板Id
        if (newValue) {
            itemCatService.findOne(newValue).success(
                function (response) {
                    $scope.entity.tbGoods.typeTemplateId = response.typeId;
                }
            )
        }

    })
    //模板Id选择后 更新模板对象
    $scope.$watch('entity.tbGoods.typeTemplateId', function (newValue, oldValue) {
        if (newValue) {
            typeTemplateService.findOne(newValue).success(
                function (response) {
                    $scope.typeTemplate = response;//获取类型模板
                    //品牌列表
                    $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
                    //扩展属性
                    //为了不被覆盖findone扩展属性 判断用户有没有传递id值
                    if ($location.search()['id']==null){
                        $scope.entity.tbGoodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                    }


                }
            )
        }
        ;
        //查询规格列表
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList = response;

            }
        )

    })

    $scope.entity = {tbGoodsDesc: {itemImages: [], specificationItems: []}};
    $scope.updateSpecAttribute = function ($event, name, value) {
        //规格选项，看指定规格是否存在 name规格名称 value规格选项
        var object = $scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems, 'attributeName', name);
        if (object != null) {
            //判断复选框选中状态
            if ($event.target.checked) {
                //
                // 复选框选中，把相应的规格选项值插入当前规格对应规格选项数组
                object.attributeValue.push(value);
            } else {
                //复选框取消勾选
                object.attributeValue.splice(object.attributeValue.indexOf(value), 1);
                //如果规格选项取消了，将此条记录移除
                if (object.attributeValue.length == 0) {
                    $scope.entity.tbGoodsDesc.specificationItems.splice($scope.entity.tbGoodsDesc.specificationItems.indexOf(object), 1);
                }
            }
        } else {
            //首次选中某个规格，添加规格及其对应选中的规格值
            $scope.entity.tbGoodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]});
        }
        //
    }
    //设置商品状态
    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];
    //设置商品分类，数字改为字体
    $scope.itemCatList = [];
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.itemCatList[response[i].id] = response[i].name;
                }
            }
        )

    }
    $scope.createItemList = function () {
        //spec存储sku对应的规格
        $scope.entity.itemList = [{spec:{}, price: 0, num: 999, status: '0', isDefault: '0'}];
        //定义变量items指向用户选中规格集合
        var items = $scope.entity.tbGoodsDesc.specificationItems;
        for (var i = 0; i < items.length;i++) {
            //编写增加sku规格方法addColumn 参数1：sku规格列表 参数2：sku规格名称 参数3：sku规格选项
			$scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
        }
    }
addColumn=function (list,attributeName,attributeValues) {
	var newList=[];
	//遍历sku规格列表
	 for(var i=0;i<list.length;i++){
	 	//读取每行sku数据赋值给oldRow
	 	var oldRow=list[i];
	 	//遍历规格选项
		 for(var j=0;j<attributeValues.length;j++){
		 	//深克隆当前行sku数据为newRow，就是赋值一份新的oldRow数据
			 var newRow=JSON.parse(JSON.stringify(oldRow));
			 //在新扩展的列，给列赋值，相当于左边根据key取值右边是值
			 newRow.spec[attributeName]=attributeValues[j];
			 //保存数据仅newList
			 newList.push(newRow);
		 }
	 }
	 return newList;
}

});	