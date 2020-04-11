 //控制层
app.controller('contentController' ,function($scope,$controller,$location,contentService){

	$controller('baseController',{$scope:$scope});//继承



	//获取指定广告分类对应的广告数据集合
	$scope.findByCategoryId=function () {
		contentService.findByCategoryId(1).success(function (response) {
			$scope.list=response;
        })
    }
    $scope.search=function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }

});
