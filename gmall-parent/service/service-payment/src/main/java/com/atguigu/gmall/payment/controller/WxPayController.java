package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.payment.service.WxPayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 第三方调用的控制层
 */
@RestController
@RequestMapping("wx/pay")
public class WxPayController {
    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 获取二维码连接
     * @param body
     * @param orderId
     * @param amount
     * @return
     */
    @GetMapping("getUrl")
    public Map<String, String> getUrl(@RequestParam String body,
                         @RequestParam String orderId,
                         @RequestParam String amount){
        Map<String, String> map = wxPayService.toWxPayApi(body, orderId, amount);
        return map;
    }

    /**
     * 获取支付结果
     * @param orderId
     * @return
     */
    @GetMapping("getPayResult")
    public Result getPayResult(String orderId){
        Map<String, String> map = wxPayService.resultWxpay(orderId);
        return Result.ok(map);

    }

    @RequestMapping("/notify/callback")
    public String getWxApi(HttpServletRequest request) throws Exception {
//        //获取通知的内容,因为给过来的是数据流
//        ServletInputStream inputStream = request.getInputStream();
//        //读取输入流，将输入流输出
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        //定义字节
//        byte[] bytes=new byte[1024];
//        //定义长度
//        int len = 0;
//        //读取
//        while ((len = inputStream.read(bytes))!=-1){
//            outputStream.write(bytes,0,len);
//        }
//        //将输出流转换为字节码
//        byte[] bytes1 = outputStream.toByteArray();
//        //转换为字符串
//        String s = new String(bytes);
//        //将数据转换为map
//        Map<String, String> map1 = WXPayUtil.xmlToMap(s);
//        //将map转换为json
//        System.out.println(JSONObject.toJSON(map1));
//        //关闭资源
//        inputStream.close();
//        outputStream.close();

        //模拟信息发送
        String resultString = "{\"transaction_id\":\"4200001756202303102067428075\",\"nonce_str\":\"05113d5eb9b242f6b364d410667e52bb\",\"bank_type\":\"OTHERS\",\"openid\":\"oHwsHuHfkjkoXo8gfTP8NyrJt3B0\",\"sign\":\"C915563CE5BD1FE840429C6B39C3F39D\",\"fee_type\":\"CNY\",\"mch_id\":\"1558950191\",\"cash_fee\":\"1\",\"out_trade_no\":\"47\",\"appid\":\"wx74862e0dfcf69954\",\"total_fee\":\"1\",\"trade_type\":\"NATIVE\",\"result_code\":\"SUCCESS\",\"time_end\":\"20230310102812\",\"payway\":\"WX\",\"is_subscribe\":\"N\",\"return_code\":\"SUCCESS\"}";
        //用mq接收信息
        rabbitTemplate.convertAndSend("result_exchange","resultWx",resultString);

        Map<String, String> map = new HashMap<>();
        map.put("return_code","SUCCESS");
        map.put("return_msg","OK");
        //返回
        return WXPayUtil.mapToXml(map);

    }
}
