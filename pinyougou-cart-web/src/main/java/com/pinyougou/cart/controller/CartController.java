package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import entity.LoginResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @RequestMapping("/addGoodsToCartList")
    public LoginResult addGoodsToCartList(@RequestBody List<Cart> cartList, Long itemId, Integer num) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("登录用户名：" + username);

        if (username.equals("anonymousUser")) { //未登录
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            return new LoginResult(true, "", cartList);

        } else { //已经登录
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            cartList_redis = cartService.addGoodsToCartList(cartList_redis, itemId, num);
            cartService.saveCartListToRedis(username, cartList_redis);
            return new LoginResult(true, username, cartList_redis);
        }

    }


    /**
     * 查询购物车列表
     *
     * @param cartList
     * @return
     */
    @RequestMapping("/findCartList")
    public LoginResult findCartList(@RequestBody List<Cart> cartList) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.findCartListFromRedis(username);
        if (username.equals("anonymousUser")) { //没登录
            System.out.println("weidenglu,bendi");
            return new LoginResult(true, "", cartList);
        } else {  //登录
            System.out.println("yidenglu,hebinredis");
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            if (cartList.size() > 0) {
                cartList_redis = cartService.mergeCartList(cartList_redis, cartList); //合并
                cartService.saveCartListToRedis(username, cartList_redis); //存储到redis
            }


            return new LoginResult(true, username, cartList_redis);
        }
    }
}
