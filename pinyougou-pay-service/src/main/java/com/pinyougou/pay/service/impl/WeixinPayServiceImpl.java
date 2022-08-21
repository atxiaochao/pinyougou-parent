package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import util.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner; //商户id

    @Value("${partnerkey}")
    private String partnerkey;  //秘钥
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1.构建参数
        Map paramMap = new HashMap();
        paramMap.put("appid",appid); //公众账号ID
        paramMap.put("mch_id",partner);//商户号
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        paramMap.put("body","品优购");//商品描述
        paramMap.put("out_trade_no",out_trade_no);//商品订单号
        paramMap.put("total_fee",total_fee);//标价金额
        paramMap.put("spbill_create_ip","127.0.0.1");//终端ip
        paramMap.put("notify_url","http://pay.pinyougou.com/notify.do");//通知地址
        paramMap.put("trade_type","NATIVE");//交易类型

        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

        try {
            //2.发送请求
            String paramXml = WXPayUtil.generateSignedXml(paramMap,partnerkey);

            System.out.println(paramXml);

            HttpClient client = new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(paramXml);
            client.post();

            //3.获取结果
            String resultXml = client.getContent();

            System.out.println(resultXml);

            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);

            Map map = new HashMap();
            map.put("code_url",resultMap.get("code_url"));
            map.put("out_trade_no",out_trade_no);
            map.put("total_fee",total_fee);
            return map;


        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        //1.构建参数
        Map paramMap = new HashMap();
        paramMap.put("appid",appid); //公众账号ID
        paramMap.put("mch_id",partner); //商户号
        paramMap.put("out_trade_no",out_trade_no); //商户订单号
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr()); //随机字符串

        String url = "https://api.mch.weixin.qq.com/pay/orderquery";

        try {
            //2.发送请求
            String paramXml = WXPayUtil.generateSignedXml(paramMap,partnerkey);
            HttpClient client = new HttpClient(url);
            client.setXmlParam(paramXml);
            client.setHttps(true);
            client.post();
            
            //3.获取结果
            String resultXml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);

            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map queryPayStatusWhile(String out_trade_no) {
        Map map = null;
        int x = 0;
        while (true){
            x++;
            if (x>=100){
                break;
            }
            map = queryPayStatus(out_trade_no);
            if (map==null){
                break;
            }
            if ("SUCCESS".equals(map.get("trade_state"))){
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
