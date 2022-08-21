package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 品牌控制器层
 */
@RestController
@RequestMapping(value = "/brand")
public class BrandController {
    @Reference
    private BrandService brandService;
    //查询所有品牌
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    //品牌列表分页
    @RequestMapping(value = "/findPage")
    public PageResult findPage(int page,int rows){
        return brandService.findPage(page,rows);
    }

    //条件查询
    @RequestMapping(value = "/search")
    public PageResult search(@RequestBody TbBrand brand,int page,int rows){
        return brandService.findPage(brand,page,rows);
    }

    //添加品牌
    @RequestMapping(value = "/add")
    public Result add(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    //根据id查询品牌
    @RequestMapping(value = "/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    //修改品牌
    @RequestMapping(value = "/update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    //删除品牌
    @RequestMapping(value = "/delete")
    public Result delete(Long []ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    //下拉列表数据
    @RequestMapping(value = "/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }

}
