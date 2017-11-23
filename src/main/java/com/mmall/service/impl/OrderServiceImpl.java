package com.mmall.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.OrderItemMapper;
import com.mmall.dao.OrderMapper;
import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;



@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
	private Logger logger=LoggerFactory.getLogger(OrderServiceImpl.class);
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderItemMapper orderItemMapper;
	
	@Override
	public ServerResponse<Object> pay(Long orderNo, Integer userId, String path) {
		// TODO Auto-generated method stub
		Map<String,String> map= new HashMap<String,String>();		
		if(orderNo==null){
			return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"订单号为空！");
		}
		//根据订单号和用户Id查询是否有此订单
		Order order=orderMapper.selectByOrderNoAndUid(orderNo, userId);
		if(order==null){
			return ServerResponse.createByErrorMessage("用户没有该订单信息！");
		}
		//查询到订单信息，封装生成支付宝订单的对象		
        String outTradeNo =order.getOrderNo().toString();//订单号      
        String subject=new StringBuilder().append("HappyMMall网上订单扫码消费，订单号：").append(outTradeNo).toString();//happymmall订单扫码支付
        String totalAmount = order.getPayment().toString();//订单总金额
        String undiscountableAmount = "0";//不打折金额【比如：酒水饮料的总价】       
        //收款账号【商家支付宝】  该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "tnhumv9205@sandbox.com";
        //订单描述     比如填写"购买商品2件共15.00元"
        String body=new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();
        //商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "happymmall_operator_o1";
        //(必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息
        String storeId = "happymmall_store_s1";
        //支付超时，定义为120分钟
        String timeoutExpress = "120m";
        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");       
        /**
         * 创建商品购买明细list
         */
        //查询数据库   获得该订单购买的商品
        List<OrderItem> orderProductList=orderItemMapper.selectByOrderNoAndUid(orderNo, userId);       
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();//新建一个list用于存放购买明细【这个list需要传给支付宝】
        if(orderProductList.size()<=0){
        	return ServerResponse.createByErrorMessage("异常订单：该订单无购买产品");
        }
        //遍历查询出来的list，封装购买明细list
        for(OrderItem oi:orderProductList){
        	 //创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量
        	 GoodsDetail goods = GoodsDetail.newInstance(oi.getProductId().toString(),
									        			 oi.getProductName(),
									        			 //产品单价记得x100转化为分
									        			 BigDecimalUtil.mul(oi.getCurrentUnitPrice().doubleValue(), new BigDecimal("100").doubleValue()).longValue(), 
									        			 //购买数量
									        			 oi.getQuantity());
        	 goodsDetailList.add(goods);
        }              
        //创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
            .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
            .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
            .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
            .setTimeoutExpress(timeoutExpress)
            .setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
            .setGoodsDetailList(goodsDetailList);
        //发送扫码支付请求，并返回结果
       /* AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
            	logger.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                // 需要修改为运行机器上的路径【二维码存放的路径】
                String filePath = String.format("/Users/sudo/Desktop/qr-%s.png",response.getOutTradeNo());
                logger.info("filePath:" + filePath);
                //ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                break;

            case FAILED:
            	logger.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
            	logger.error("系统异常，预下单状态未知!!!");
                break;

            default:
            	logger.error("不支持的交易状态，交易返回异常!!!");
                break;
        }*/
		return null;
	}
	
}
