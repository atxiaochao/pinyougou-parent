package com.pinyougou.sellergoods.service;


import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 品牌业务接口
 */
public interface BrandService {

    /**
     * 查询品牌列表
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 分页查询
     * @param PageNum
     * @param PageSize
     * @return
     */
    public PageResult findPage(int PageNum,int PageSize);

    /**
     * 条件查询
     * @param brand
     * @param PageSize
     * @return

    */

    public PageResult findPage(TbBrand brand,int PageNum,int PageSize);

    /**
     * 增加品牌
     * @param brand
     */
    public void add(TbBrand brand);

    //根据id查询品牌
    TbBrand findOne(Long id);

    //修改品牌
    void update(TbBrand brand);

    //删除品牌
    void delete(Long[] ids);

    //下拉列表数据
    public List<Map> selectOptionList();
}
