//控制层 
app.controller('goodsController', function ($scope, $controller, goodsService, uploadService, itemCatService, typeTemplateService) {

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
    $scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //增加
    $scope.add = function () {
        $scope.entity.goodsDesc.introduction = editor.html(); //商品介绍
        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {
                    alert(response.message);
                    //重新查询 
                    $scope.entity = {};
                    editor.html(""); //清空富文本编辑器
                }
            }
        );
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

    $scope.image_entity = {}; //图片实体
    //
    $scope.uploadImage = function () {
        uploadService.upload().success(function (response) {
            if (response.error == 0) {
                $scope.image_entity.url = response.url;
            }
        })
    }

    $scope.entity = {goodsDesc: {itemImages: [], specificationItems: []}};  //商品实体
    //添加图片到列表
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }


    //删除列表图片
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }

    //查询商品一级分类列表
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(function (response) {
            $scope.itemCat1List = response;
        })
    }

    //查询商品二级分类列表
    $scope.$watch("entity.goods.category1Id", function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat2List = response;
        })
    })

    //查询商品三级分类列表
    $scope.$watch("entity.goods.category2Id", function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat3List = response;
        })
    })

    //查询模板id
    $scope.$watch("entity.goods.category3Id", function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(function (response) {
            $scope.entity.goods.typeTemplateId = response.typeId; //模板id
        })

    })

    //查询品牌列表
    $scope.$watch("entity.goods.typeTemplateId", function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(function (response) {
            $scope.typeTemplate = response;
            $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds); //品牌列表由字符串转换为对象
            $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);//扩展属性
        })
        typeTemplateService.findSpecList(newValue).success(function (response) {
            $scope.specList = response;

        })
    })

    //更新选中的规格
    $scope.updateSpecItems = function ($event, name, value) {
        var object = $scope.searchObjectByKey(
            $scope.entity.goodsDesc.specificationItems, 'name', name);
        if (object != null) {
            if ($event.target.checked) {
                object.values.push(value);
            } else {//取消勾选				object.values.splice( object.values.indexOf(value ) ,1);//移除选项
                //如果选项都取消了，将此条记录移除
                object.values.splice(object.values.indexOf(value), 1);
                if (object.values.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice(
                        $scope.entity.goodsDesc.specificationItems.indexOf(object), 1);
                }
            }
        } else { //没有此规格，添加规格记录
            $scope.entity.goodsDesc.specificationItems.push(
                {"name": name, "values": [value]});
        }
    }

    //创建SKU列表
    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: "1", isDefault: "0"}];
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColum($scope.entity.itemList, items[i].name, items[i].values);
        }
    }

    addColum = function (list, name, values) {
        var newList = [];
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < values.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow)); //深克隆
                newRow.spec[name] = values[j];
                newList.push(newRow);
            }
        }

        return newList;

    }

});	
