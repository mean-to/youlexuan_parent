//创建自定义模块,引入分页组件
var app=angular.module('youlexuan',[]);
/*
$sce服务写成过滤器*/
app.filter('trustHtml',['$sce',function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}])