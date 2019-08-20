package com.atguigu.alipaytest.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.alipaytest.config.AlipayTemplate;
import com.atguigu.alipaytest.vo.PayAsyncVo;
import com.atguigu.alipaytest.vo.PayResultVo;
import com.atguigu.alipaytest.vo.PayVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Controller
public class PayController {

    @ResponseBody
    @PostMapping("/pay")
    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayTemplate.return_url);
        alipayRequest.setNotifyUrl(AlipayTemplate.notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //若想给BizContent 增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
        //alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
        //		+ "\"total_amount\":\""+ total_amount +"\","
        //		+ "\"subject\":\""+ subject +"\","
        //		+ "\"body\":\""+ body +"\","
        //		+ "\"timeout_express\":\"10m\","
        //		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节

        //3、alipayClient将此次支付请求发送给支付宝，支付宝会给我们想要
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }

    @GetMapping("/orderList")
    public String toOrderPage(PayResultVo resultVo, Model model){

        model.addAttribute("result",resultVo);
        return "order";
    }

    /**
     * 支付宝会给我们这个发送一个请求，感知支付状态
     * @return
     *
     * [gmt_create=[2019-06-19 14:07:27]]
     * [charset=[utf-8]]
     * [gmt_payment=[2019-06-19 14:07:35]]
     * [notify_time=[2019-06-19 14:07:37]]
     * [subject=[啦啦啦啦]]
     * [sign=[QPjiMVSMh2dBvEARBYAz5okgWaX72nLUnz6cDoLW+TzZLobCzPbXXJ2dUyBmAqvacxlJsS7DvNBGUUeZv3lVnM4UqPG9uOQTa/h2EoJNwLwix15/wY8xgo1zaatLUCKWobN/UV4XxoKr38q9U7KV4g3TeDfq0zXwkCv584Ygi+mBw4xatoeNF8ZI+mQJuH2zgdbNZJe7Zg1z9AYF8x1ELgl4csQU7fIizWO4aI6b/Btfr7Z3GqeCD3Hd8QHdzhScoI1QOuv0bkjZEex9aJRFaKQONdA25GFPhttfLmU+eXrI1ZxxtZOnlVNb1ZRKD/p8ty0SlHklMWKkLWIivyT5PQ==]]
     * [buyer_id=[2088102178892826]]
     * [body=[哈哈哈哈哈]]
     * [invoice_amount=[999.00]]
     * [version=[1.0]]
     * [notify_id=[2019061900222140736092821000289570]]
     * [fund_bill_list=[[{"amount":"999.00","fundChannel":"ALIPAYACCOUNT"}]]]
     * [notify_type=[trade_status_sync]]
     * [out_trade_no=[5898415646477777]]
     * [total_amount=[999.00]]
     * [trade_status=[TRADE_SUCCESS]]
     * [trade_no=[2019061922001492821000023009]]
     * [auth_app_id=[2016092200568607]]
     * [receipt_amount=[999.00]]
     * [point_amount=[0.00]]
     * [app_id=[2016092200568607]]
     * [buyer_pay_amount=[999.00]]
     * [sign_type=[RSA2]]
     * [seller_id=[2088102176735660]]
     *
     * 异步通知：
     *    支付完成，5s，5mins，10mins，60min,1day,3day,
     *    1、主动查询哪些订单成功了
     *    2、依靠支付宝的异步通知
     *
     *   //
     */
    @ResponseBody
    @RequestMapping("/payAsync")
    public String payStatus(PayAsyncVo vo,HttpServletRequest request) throws UnsupportedEncodingException {
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }



        //验签；
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayTemplate.alipay_public_key, AlipayTemplate.charset, AlipayTemplate.sign_type); //调用SDK验证签名
            //沙箱环境：经常的事情
            System.out.println("验签==》"+signVerified);
            if(signVerified){
                System.out.println("支付宝异步通知调用：结果"+vo);
                //TRADE_SUCCESS:交易成功；支付成功；
                //TRADE_FINISHED:交易完成；支付宝把钱转给商家；
                //vo.getTrade_status()
                if(vo.getTrade_status().equals("TRADE_SUCCESS")){
                    //支付完成，
                    System.out.println("订单："+vo.getOut_trade_no()+"支付完成。。。数据库已经修改");

                }else if(vo.getTrade_status().equals("TRADE_FINISHED")){
                    System.out.println("订单："+vo.getOut_trade_no()+"整笔交易全部完成。。。数据库已经修改");
                }

            }

        } catch (AlipayApiException e) {
            System.out.println("验签失败....");
        }



        //只要订单处理完了，告诉支付宝，订单通知收到了。
        return "success";
    }
}
