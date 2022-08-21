app.controller('searchController',function($scope,searchService,$location){
    //搜索
    $scope.search=function(){
        $scope.searchMap.pageNo=1;
        searchService.search( $scope.searchMap ).success(
            function(response){
                $scope.resultMap=response;//搜索返回的结果
            }
        );
    }

    //搜索对象
    $scope.searchMap = {keywords:"",brand:"",category:"",spec:{},price:"",pageNo:1,pageSize:20,sort:"",sortField:""};

    //添加搜索项
    $scope.addSearchItem = function (key,value) {
        if (key=='brand' || key=='category' || key=='price'){ //如果用户点击的是品牌或分类
            $scope.searchMap[key] = value;
        }else { //如果用户点击的是规格
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    }


    //删除搜索项
    $scope.removeSearchItem = function (key,value) {
        if (key=='brand' || key=='category' || key=='price'){ //如果用户点击的是品牌或分类
            $scope.searchMap[key] = '';
        }else { //如果用户点击的是规格
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }

    //分页加载
    $scope.searchpage = function () {
        if ($scope.searchMap.pageNo < $scope.searchMap.totalPages){
            $scope.searchMap.pageNo++; //当前页码+1
            searchService.search($scope.searchMap).success(function (response) {
                //搜索返回的结果,追加下一页内容
                $scope.resultMap.rows = $scope.resultMap.rows.concat(response.rows);
            })
        }
    }

    //按价格排序
    $scope.sortSearch = function (sort,sortField) {
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.search();
    }

    $scope.activeClass = ['active','','','','','']; //激活样式处理
    //设置激活样式
    $scope.setActiveClass = function (index) {
        //将原有的值清空
        $scope.activeClass = ['','','','','',''];//激活样式
        $scope.activeClass[index] = 'active';
    }

    //判断关键字是否包含品牌
    $scope.keywordsIsBrand = function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0) {
                return true;
            }
        }
        return false;
    }

    //加载参数查询
    $scope.loadKeywords = function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        if ($scope.searchMap.keywords!=null && $scope.searchMap.keywords!=''){
            $scope.search();
        }

    }


});