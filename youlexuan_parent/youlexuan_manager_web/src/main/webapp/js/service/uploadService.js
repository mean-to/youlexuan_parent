app.service("uploadService",function ($http) {
    //上传文件处理方法
    this.uploadFile=function () {
        //把要上传的图片文件，进行封装
        var formDate=new FormData();
        formDate.append("file",file.files[0]);
        //发出请求
        return $http({
            method:'post',
            url:"../content/upload.do",
            data:formDate,
            headers:{'content-Type':undefined},
            transformRequest: angular.identity
        });
    }
})