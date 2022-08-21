package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 品牌服务层实现
 */

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;

    //查询所有品牌
    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    //品牌列表分页
    @Override
    public PageResult findPage(int PageNum, int PageSize) {
        PageHelper.startPage(PageNum,PageSize);
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }

    //条件查询
    @Override
    public PageResult findPage(TbBrand brand, int PageNum, int PageSize) {
        PageHelper.startPage(PageNum,PageSize);
        TbBrandExample example = new TbBrandExample(); //封装查询条件
        TbBrandExample.Criteria criteria = example.createCriteria(); //构建查询条件的类
        //名称模糊查询
        if (brand != null){
            if (brand.getName()!=null && brand.getName().length()>0){ //如果有名称的条件
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if (brand.getFirstChar()!=null && brand.getFirstChar().length()>0){ //如果有首字母的条件
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }

    //增加品牌
    @Override
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }

    //根据id查询品牌
    @Override
    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    //修改品牌
    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    //删除品牌
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    //下拉列表数据
    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }


}
