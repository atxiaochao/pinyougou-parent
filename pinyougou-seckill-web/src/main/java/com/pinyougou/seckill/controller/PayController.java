package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference(timeout = 1000*60*5)
    private WeixinPayService weixinPayService;

    @Reference
    private SeckillOrderService seckillOrderService;


    @RequestMapping("/createNative")
    public Map createNative(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //从缓存提取paylog
        //TbPayLog payLog = orderService.searchPayLogFromRedis(userId);
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
        if (seckillOrder!=null){
            long fen = (long) (seckillOrder.getMoney().doubleValue()*100); //金额转换为分
            return weixinPayService.createNative(seckillOrder.getId()+"",fen+"");
        }else {
            return new HashMap();
        }

    }


    /**
     * 查询订单支付状态
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map = weixinPayService.queryPayStatusWhile(out_trade_no);
        if (map==null){
            return new Result(false,"执行出错");
        }else {
            if ("SUCCESS".equals(map.get("trade_state"))){
                //更改订单状态
                seckillOrderService.saveOrderFromRedisToDb(userId,Long.parseLong(out_trade_no),(String)map.get("transaction_id"));
                return new Result(true,"支付成功");
            }else {
                return new Result(false,"超时");
            }
        }
    }
}
