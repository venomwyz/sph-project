package com.atguigu.gmall.payment.service.impl;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.payment.service.WxPayService;
import com.atguigu.gmall.payment.util.HttpClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
@Log4j2
@Service
public class WxPayServiceImplImpl implements WxPayService {

    //读取配置文件的字段
    //公众账号ID
    @Value("${weixin.pay.appid}")
    private String appId;
    //商户号
    @Value("${weixin.pay.partner}")
    private String partner;
    //盐
    @Value("${weixin.pay.partnerkey}")
    private String partnerkey;
    //通知地址
    @Value("${weixin.pay.notifyUrl}")
    private String notifyUrl;
    /**
     * 调用微信接口
     * @param body
     * @param orderId
     * @param amount
     * @return
     */

    @Override

    public Map<String, String> toWxPayApi(String body, String orderId, String amount) {
        //数据校验
        if (StringUtils.isEmpty(body) || StringUtils.isEmpty(orderId) || StringUtils.isEmpty(amount)){
            return null;
        }
        //给微信传数据需要的数据
        Map<String, String> toXml = new HashMap<>();
        toXml.put("appid", appId);
        toXml.put("mch_id", partner);
        toXml.put("nonce_str", WXPayUtil.generateNonceStr());
        toXml.put("body", body);
        toXml.put("out_trade_no", orderId);
        toXml.put("total_fee", amount);
        toXml.put("spbill_create_ip", "192.168.200.1");
        toXml.put("notify_url", notifyUrl);
        toXml.put("trade_type", "NATIVE");


        try {
            //将map数据转化为Json生成签名
            String generateSignedXml = WXPayUtil.generateSignedXml(toXml, partnerkey);
            //向微信发送post请求
            String url="https://api.mch.weixin.qq.com/pay/unifiedorder";
            HttpClient httpClient = new HttpClient(url);
            Map<String, String> map = getMap(httpClient, generateSignedXml);
            return map;
        } catch (Exception e) {
            log.error("请求微信获取二维码失败，订单号为："+ orderId);
            log.error("请求微信获取二维码失败，错误原因为："+ e.getMessage());

        }
        return null;
    }
    //提取方法
    private Map<String, String> getMap(HttpClient httpClient, String generateSignedXml) throws Exception {
        //设置xml要的参数
        httpClient.setXmlParam(generateSignedXml);
        //设置https
        httpClient.setHttps(true);
        //传成功之后获取请求结果
        httpClient.post();
        //判断协议状态与业务状态
        String content = httpClient.getContent();
        //获取到返回结果
        Map<String, String> map = WXPayUtil.xmlToMap(content);
        return map;
    }

    /**
     * 获取返回支付结果
     * @param orderId
     * @return
     */
    @Override
    public Map<String, String> resultWxpay(String orderId) {
        //参数校验
        if (StringUtils.isEmpty(orderId)){
            return null;
        }
        //填充数据
        Map<String, String> map = new HashMap<>();
        map.put("appid", appId);
        map.put("mch_id", partner);
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        map.put("out_trade_no", orderId);
        try {
            //将map类型的数据转换为xml，并且生成签名
            String xml = WXPayUtil.generateSignedXml(map, partnerkey);
            //发送post接口
            String url="https://api.mch.weixin.qq.com/pay/orderquery";
            HttpClient httpClient = new HttpClient(url);
            //设置xml参数
            Map<String, String> result = getMap(httpClient, xml);
            return result;

        } catch (Exception e) {
            log.error("请求微信获取二维码失败，订单号为："+ orderId);
            log.error("请求微信获取二维码失败，错误原因为："+ e.getMessage());

        }
        return null;
    }


}
