package com.mmall.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.OrderItemMapper;
import com.mmall.dao.OrderMapper;
import com.mmall.dao.PayInfoMapper;
import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import com.mmall.pojo.PayInfo;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FtpUtil;
import com.mmall.util.PropertiesUtil;



@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
	private Logger logger=LoggerFactory.getLogger(OrderServiceImpl.class);
    private static AlipayTradeService   tradeService;// 支付宝当面付2.0服务 
//    private static AlipayTradeService   tradeWithHBService; // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
//    private static AlipayMonitorService monitorService;// 支付宝交易保障接口服务，供测试接口api使用
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderItemMapper orderItemMapper;
	@Autowired
	private PayInfoMapper payInfoMapper;
	
	/**
	 * 返回二维码
	 */
	@Override
	public ServerResponse<Map<String, String>> pay(Long orderNo, Integer userId, String path) {
		// TODO Auto-generated method stub				
		if(orderNo==null){
			return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"订单号为空！");
		}
		//根据订单号和用户Id查询是否有此订单
		Order order=orderMapper.selectByOrderNoAndUid(orderNo, userId);
		if(order==null){
			return ServerResponse.createByErrorMessage("用户没有该订单信息！");
		}
		//根据订单号和用户id 查询该订单的商品详细
        List<OrderItem> orderProductList=orderItemMapper.selectByOrderNoAndUid(orderNo, userId);  
        if(orderProductList.size()<=0){
        	return ServerResponse.createByErrorMessage("异常订单：该订单无购买产品");
        }
		//查询到订单和订单商品信息，封装生成支付宝订单的对象	
        ServerResponse<Map<String, String>> response=this.aliPay_f2f(orderNo, userId, order, orderProductList, path);
		return response;
	}
	/**
	 * 支付宝回调验证
	 */
	@Override
	public ServerResponse<String> aliCallBackCheck(Map<String,String[]> requestParams){
		/**
		 * 将参数String[]进行转化成String
		 */
		Map<String,String> params=new HashMap<String,String>();
		params=this.paramInit(requestParams);
		/**
		 * 开始验证 1验签  2验单  3验证交易状态  
		 */
		//TODO 验证回调的正确性【简称：验签】，确定是否是支付宝返回的
		try {
			boolean alipayRSA2CheckedV2=AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
			if(!alipayRSA2CheckedV2){
				return ServerResponse.createByErrorMessage("非法请求，验证不通过！订单支付失败！");
			}
		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			logger.error("支付宝验证回调异常！",e);
		}
		/**
		 *  核对各种数据【比如：订单号、交易状态等等】
		 */
		Long orderNo=Long.parseLong(params.get("out_trade_no"));//订单号
		String tradeNo=params.get("trade_no");//交易号【支付宝生成的】
		String tradeStatus=params.get("trade_status");//交易状态
		Order order=orderMapper.selectByOrderNo(orderNo);//获得订单
		//TODO 验证订单
		if(order==null){
			return ServerResponse.createByErrorMessage("订单不存在！支付失败！");
		}else{
			//验证订单状态
			if(order.getStatus()>=Const.OrderStatus.PAIED.getCode()){//该订单已经支付
				return ServerResponse.createBySuccessMessage("支付宝重复调用，订单已经支付完成！");
			}
		}
		//TODO 验证交易状态，并更新订单
		if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
			order.setStatus(Const.OrderStatus.PAIED.getCode());
			order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
			//交易成功，则更新订单状态
			orderMapper.updateByPrimaryKey(order);			
		}		
		//不管支付是否成功，插入支付表中该笔支付数据
		this.insertPayInfo(tradeNo, tradeStatus, order);								
		return ServerResponse.createBySuccessMessage("支付数据写入成功！");
	}
	/**
	 * 查询某个订单状态是否是成功
	 */
	@Override
	public ServerResponse<Boolean> queryOrderPayStatusIsSuccess(Long orderNo, Integer userId) {
		// TODO Auto-generated method stub
		Order order=orderMapper.selectByOrderNoAndUid(orderNo, userId);
		if(order==null){
			return ServerResponse.createByErrorMessage("订单不存在！");
		}
		if(order.getStatus()>=Const.OrderStatus.PAIED.getCode()){//只要大于付款的状态码【不管是已付款还是已发货】，那么就是成功的订单
			return ServerResponse.createBySuccessMessage("订单查询成功,该订单已支付",true);
		}else{
			return ServerResponse.createByErrorMessage("订单查询成功,该订单未支付");
		}		
	}
	
	
	
	
	
	
	
	
	/**
	 * 
	 * @Title: aliPay_f2f
	 * @Description: TODO 传入相关参数，调用支付宝【当面付】接口
	 * @param @param orderNo 订单号
	 * @param @param userId  用户id
	 * @param @param order   订单 
	 * @param @param orderProductList   订单中购买的商品详情列表
	 * @param @param path  请求成功时二维码存放路径
	 * @return void    
	 * @throws
	 */
	private ServerResponse<Map<String, String>> aliPay_f2f(Long orderNo, Integer userId, Order order,List<OrderItem> orderProductList,String path) {
		String outTradeNo =order.getOrderNo().toString();//订单号      
        String subject=new StringBuilder().append("HappyMMall网上订单扫码消费，订单号：").append(outTradeNo).toString();//happymmall订单扫码支付
        String totalAmount = order.getPayment().toString();//订单总金额
        String undiscountableAmount = "0";//不打折金额【比如：酒水饮料的总价】       
        //收款账号【商家支付宝】  该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";
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
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();//新建一个list用于存放购买明细【这个list需要传给支付宝】      
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
            .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
            .setGoodsDetailList(goodsDetailList);
        //加载alipay的资源文件
        Configs.init("AlipaySource.properties");
        //初始化【当面付】服务对象
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
        //tradeService对象调用tradePrecreate方法，发送扫码请求，会返回成功还是失败的状态
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);        
        switch (result.getTradeStatus()) {
            case SUCCESS:
            	logger.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();//得到响应对象【里面含有各种信息，包括二维码信息】
                dumpResponse(response);//打印响应对象
                File folder=new File(path);
                if(!folder.exists()){
                	folder.setWritable(true);//赋予可以操作
                	folder.mkdirs();//创建目录
                }
                //需要修改为运行机器上的路径【二维码存放的路径】
                String qrPath = String.format(path+"/"+"qr-%s.png",response.getOutTradeNo());               
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                /**
                 * 上传至ftp服务器上
                 */
                String qrFileName=String.format("qr-%s.png", response.getOutTradeNo());//文件名
                File targetFile = new File(path,qrFileName);             
				try {
					FtpUtil.uplod("/usr/ftpfile/alipay/qrCode",Lists.newArrayList(targetFile));//路径和文件
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("ftp上传二维码失败",e);
					e.printStackTrace();
				}
				/**
				 * 线上 二维码图片访问地址如下
				 */
				String qrUrl=PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
				Map<String,String> map= new HashMap<String,String>();
				map.put("qrUrl", qrUrl);
                return ServerResponse.createBySuccessMessage("获取二维码成功", map);
            case FAILED:
            	logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!");
            case UNKNOWN:
            	logger.error("系统异常，预下单状态未知!!!");
            	return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!");
            default:
            	logger.error("不支持的交易状态，交易返回异常!!!");
            	return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!");
        }
	}
	//简单打印支付宝的应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                    response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }
    //写入支付记录数据
    private void insertPayInfo(String tradeNo, String tradeStatus, Order order) {
		PayInfo payInfo=new PayInfo();
		payInfo.setUserId(order.getUserId());
		payInfo.setOrderNo(order.getOrderNo());
		payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
		payInfo.setPlatformNumber(tradeNo);//交易号
		payInfo.setPlatformStatus(tradeStatus);//交易状态
		payInfoMapper.insert(payInfo);
	}
    //支付宝回调接口 参数转化
    private Map<String,String> paramInit(Map<String,String[]> requestParams) {		
		Map<String,String> params=new HashMap<String,String>();
		Iterator<String> it=requestParams.keySet().iterator();
		while(it.hasNext()){
			String name=it.next();
			String[] valus=requestParams.get(name);	
			String valueStr="";
			for(int i=0;i<valus.length;i++){				
				StringBuilder sb=new StringBuilder();
				if(i+1!=valus.length&&i<valus.length){
					sb.append(valus[i]).append(",");
				}
				if(i+1==valus.length){
					sb.append(valus[i]);
				}
			    valueStr=sb.toString();			
			}				
			params.put(name, valueStr);
		}
		//注意：验证参数中需要删除此属性！！！【必须在转化之后删除，不然会报错】
		if(params.containsKey("sign_type")){
			params.remove("sign_type");
		}
		return params;
	}
}
