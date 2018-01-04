package com.mmall.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
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
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.OrderItemMapper;
import com.mmall.dao.OrderMapper;
import com.mmall.dao.PayInfoMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import com.mmall.pojo.PayInfo;
import com.mmall.pojo.Product;
import com.mmall.pojo.Shipping;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FtpUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;



@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
	private Logger logger=LoggerFactory.getLogger(OrderServiceImpl.class);
    private static AlipayTradeService   tradeService;// 支付宝当面付2.0服务 
//    private static AlipayTradeService   tradeWithHBService; // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
//    private static AlipayMonitorService monitorService;// 支付宝交易保障接口服务，供测试接口api使用
    @Autowired
	private CartMapper cartMapper;
    @Autowired
	private ProductMapper productMapper;
    @Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderItemMapper orderItemMapper;
	@Autowired
	private PayInfoMapper payInfoMapper;
	@Autowired
	private ShippingMapper shippingMapper;
	
	
	/**
	 * 创建订单
	 */
	@Override
	public ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId) {
		// TODO Auto-generated method stub		
		//调用getOrderItem方法
		ServerResponse<List<OrderItem>> serverResponse=this.getOrderItems(userId);
		if(serverResponse.isError()){
			return ServerResponse.createByErrorMessage(serverResponse.getMsg());
		}
		//获得订单的明细
		List<OrderItem> orderItems=serverResponse.getData();
		//计算整个订单的总价
		BigDecimal payment=new BigDecimal("0");
		for(OrderItem o:orderItems){			
			payment=BigDecimalUtil.add(payment.doubleValue(), o.getTotalPrice().doubleValue());
		}
		//一、生成订单
		Order order=this.assembleOrder(userId, shippingId, payment);
		if(order==null){
			return ServerResponse.createByErrorMessage("订单号生存错误! 数据写入失败");
		}
		for(OrderItem o:orderItems){
			o.setOrderNo(order.getOrderNo());//设置订单明细的订单号
		}
		//二、批量插入数据orderItems
		try {
			orderItemMapper.batchInsert(orderItems);
			for(OrderItem o:orderItems){
				//三、批减少库存			
				productMapper.reduceStockByPid(o.getProductId(), o.getQuantity());
				//四、清空购物车
				cartMapper.deleteByPidAndUid(o.getProductId(), userId);
			}
			//返回给前端数据 【详细的订单vo】
		} catch (Exception e) {
			// TODO Auto-generated catch block			
			orderMapper.deleteByPrimaryKey(orderMapper.selectByOrderNo(order.getOrderNo()).getId());
			e.printStackTrace();	
			return ServerResponse.createByErrorMessage("创建订单失败！");					
		}		
		OrderVo orderVo=this.assembleOrderVo(order, orderItems);		
		return ServerResponse.createBySuccessMessage("创建订单vo类成功！", orderVo);
	}
	/**
	 * 取消订单
	 */
	@Override
	public ServerResponse<String> cancel(Integer userId, Long orderNo) {
		// TODO Auto-generated method stub
		Order order=orderMapper.selectByOrderNoAndUid(orderNo, userId);
		//验证订单是否存在
		if(order==null){
			return ServerResponse.createByErrorMessage("该用户的此订单不存在，订单号："+orderNo);
		}
		//验证订单状态  是否已付款
		if(order.getStatus()>=Const.OrderStatusEnum.PAIED.getCode()){
			return ServerResponse.createByErrorMessage("该用户的此订单已付款，无法取消！订单号："+orderNo);
		}
		//更新订单状态
		Order temp=new Order();
		temp.setId(order.getId());
		temp.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
		int rowCount=orderMapper.updateByPrimaryKeySelective(temp);
		if(rowCount<=0){
			return ServerResponse.createByErrorMessage("数据库写入异常，订单取消失败！");
		}else{
			//更新库存
			List<OrderItem> orderItems=orderItemMapper.selectByOrderNo(order.getOrderNo());
			for(OrderItem o:orderItems){
				productMapper.addStockByPid(o.getProductId(), o.getQuantity());
			}
			return ServerResponse.createBySuccessMessage("取消订单成功！");			
		}		
	}
	/**
	 * 预览订单中的商品  【需要获得购物车中已勾选的商品】
	 */
	@Override
	public ServerResponse<OrderProductVo> getOrderCartProduct(Integer userId) {
		// TODO Auto-generated method stub
		//调用  获得已勾选商品s
		ServerResponse<List<OrderItem>> response=this.getOrderItems(userId);
		if(response.isError()){
			return ServerResponse.createByErrorMessage(response.getMsg());
		}
		//获得返回结果中的date 订单列表明细项
		List<OrderItem> orderItems=response.getData();
		//组装OrderProductVo对象
		List<OrderItemVo> orderItemVos=new ArrayList<OrderItemVo>();
		BigDecimal productTotalPrice=new BigDecimal("0");
		for(OrderItem o:orderItems){
			productTotalPrice=BigDecimalUtil.add(productTotalPrice.doubleValue(), o.getTotalPrice().doubleValue());
			orderItemVos.add(this.assembleOrderItemVo(o));
		}
		OrderProductVo orderProductVo=new OrderProductVo();
		orderProductVo.setProductTotalPrice(productTotalPrice);
		orderProductVo.setOrderItemVoList(orderItemVos);
		orderProductVo.setImgHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		return ServerResponse.createBySuccessMessage("获得订单预览信息成功！", orderProductVo);
	}
	/**
	 * 订单详情
	 */
	@Override
	public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo) {
		// TODO Auto-generated method stub
		Order order=orderMapper.selectByOrderNoAndUid(orderNo, userId);
		//验证订单是否存在
		if(order==null){
			return ServerResponse.createByErrorMessage("该用户的此订单不存在，订单号："+orderNo);
		}
		List<OrderItem> orderItems=orderItemMapper.selectByOrderNoAndUid(orderNo, userId);
		OrderVo orderVo=this.assembleOrderVo(order, orderItems);		
		return ServerResponse.createBySuccessMessage("获得订单详情成功！", orderVo);
	}
	@Override
	public ServerResponse<OrderVo> manageGetOrderDetail(Long orderNo) {
		// TODO Auto-generated method stub
		Order order=orderMapper.selectByOrderNo(orderNo);
		//验证订单是否存在
		if(order==null){
			return ServerResponse.createByErrorMessage("该用户的此订单不存在，订单号："+orderNo);
		}
		List<OrderItem> orderItems=orderItemMapper.selectByOrderNo(orderNo);
		OrderVo orderVo=this.assembleOrderVo(order, orderItems);		
		return ServerResponse.createBySuccessMessage("获得订单详情成功！", orderVo);
	}
	/**
	 * 顾客查看自己的订单list
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ServerResponse<PageInfo> getOrderList(Integer userId,Integer pageNum,Integer pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum,pageSize);
		List<Order> orders=orderMapper.selectByUid(userId);		
		List<OrderVo> orderVos=new ArrayList<OrderVo>();
		if(orders.size()<=0){
			return ServerResponse.createByErrorMessage("此用户无订单！");
		}
		for(Order o:orders){
			List<OrderItem> orderItems=orderItemMapper.selectByOrderNoAndUid(o.getOrderNo(), userId);
			orderVos.add(this.assembleOrderVo(o, orderItems));
		}
		PageInfo pageInfo=new PageInfo(orders);
		pageInfo.setList(orderVos);
		return ServerResponse.createBySuccessMessage("查询订单成功！", pageInfo);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ServerResponse<PageInfo> manageGetOrderList(Integer pageNum,Integer pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum,pageSize);
		List<Order> orders=orderMapper.selectAll();		
		List<OrderVo> orderVos=new ArrayList<OrderVo>();
		if(orders.size()<=0){
			return ServerResponse.createByErrorMessage("此用户无订单！");
		}
		for(Order o:orders){
			List<OrderItem> orderItems=orderItemMapper.selectByOrderNo(o.getOrderNo());
			orderVos.add(this.assembleOrderVo(o, orderItems));
		}
		PageInfo pageInfo=new PageInfo(orders);
		pageInfo.setList(orderVos);
		return ServerResponse.createBySuccessMessage("查询订单成功！", pageInfo);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ServerResponse<PageInfo> manageSearch(Integer pageNum,Integer pageSize,Long orderNo) {
		// TODO Auto-generated method stub
		List<Order> orders=new ArrayList<Order>();
		List<OrderVo> orderVos=new ArrayList<OrderVo>();
		PageHelper.startPage(pageNum,pageSize);
		Order order=orderMapper.selectByOrderNo(orderNo);				
		if(order==null){
			return ServerResponse.createByErrorMessage("未搜索到该订单！");
		}
		orders.add(order);
		List<OrderItem> orderItems=orderItemMapper.selectByOrderNo(orderNo);
		orderVos.add(this.assembleOrderVo(order, orderItems));		
		PageInfo pageInfo=new PageInfo(orders);
		pageInfo.setList(orderVos);
		return ServerResponse.createBySuccessMessage("搜索订单成功！", pageInfo);
	}
	/**
	 * 发货
	 */
	@Override
	public ServerResponse<String> sendGoods(Long orderNo) {
		// TODO Auto-generated method stub
		Order order=orderMapper.selectByOrderNo(orderNo);
		if(order==null){
			return ServerResponse.createByErrorMessage("发货失败！该订单不存在");
		}
		//校验订单
		if(order.getStatus()!=Const.OrderStatusEnum.PAIED.getCode()){//如果是已付款的订单则进行发货
			return ServerResponse.createByErrorMessage("发货失败！该订单处于非付款状态");
		}		
		order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
		order.setSendTime(new Date());
		int rowCount=orderMapper.updateByPrimaryKeySelective(order);
		if(rowCount<=0){
			return ServerResponse.createByErrorMessage("发货失败！数据库写入错误");
		}else{
			return ServerResponse.createBySuccessMessage("发货成功！");
		}		
	}
	
	/**
	 * 
	 */
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
			if(order.getStatus()>=Const.OrderStatusEnum.PAIED.getCode()){//该订单已经支付
				return ServerResponse.createBySuccessMessage("支付宝重复调用，订单已经支付完成！");
			}
		}
		//TODO 验证交易状态，并更新订单
		if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
			order.setStatus(Const.OrderStatusEnum.PAIED.getCode());
			order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));//支付时间
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
		if(order.getStatus()>=Const.OrderStatusEnum.PAIED.getCode()){//只要大于付款的状态码【不管是已付款还是已发货】，那么就是成功的订单
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
    //获得购物车中已经选中的物品  并包装成OrderItem
    private ServerResponse<List<OrderItem>> getOrderItems(Integer userId) {
    	//获得该用户的购物车勾选中的 信息
    	List<Cart> list=cartMapper.selectCheckedByUid(userId);//获得购物车中已经选中的商品
		if(CollectionUtils.isEmpty(list)){
			return ServerResponse.createByErrorMessage("异常：购物车选中的商品未查询到！");
		}
		//获得订单明细list
		List<OrderItem> orderItems=new ArrayList<OrderItem>();
		for(Cart c:list){
			Product product=productMapper.selectByPrimaryKey(c.getProductId());//获得商品信息
			//验证该商品是否在售
			if(Const.ProductStatusEnum.ON_SALE.getCode()!=product.getStatus()){
				return ServerResponse.createByErrorMessage("购物车中存在勾选已下架的商品！商品名：【"+product.getName()+"】");
			}
			//验证购买数量
			if(c.getQuantity()>product.getStock()){
				return ServerResponse.createByErrorMessage("购物车中存在勾选库存不足的商品！商品名：【"+product.getName()+"】");
			}
			//创建订单明细OrderItem
			OrderItem oi=new OrderItem();
			oi.setUserId(userId);
			oi.setProductId(product.getId());
			oi.setProductName(product.getName());
			oi.setProductImage(product.getMainImage());
			oi.setCurrentUnitPrice(product.getPrice());
			oi.setQuantity(c.getQuantity());
			oi.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), c.getQuantity()));
			orderItems.add(oi);
		}
		return ServerResponse.createBySuccessMessage("创建订单成功!返回订单明细列表", orderItems);
	}
    //生成订单号
    private Long generateOrderNo(){
		long currentTime=System.currentTimeMillis();
    	return currentTime+new Random().nextInt(100);   	
    }
    //生成订单
    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal payment){
    	Order order =new Order();
    	long orderNo=this.generateOrderNo();
    	order.setOrderNo(orderNo);
    	order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());//未付款
    	order.setPostage(0);//邮费
    	order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());//支付方式  在线支付
    	order.setPayment(payment);
    	order.setUserId(userId);
    	order.setShippingId(shippingId);//发货地址id
    	//插入数据库
    	int rowCount=orderMapper.insert(order);
    	if(rowCount>0){
    		return order;
    	}
		return null;
    	
    }
    //返回下单成功数据
    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItems){
    	OrderVo orderVo=new OrderVo();
    	orderVo.setOrderNo(order.getOrderNo());
    	orderVo.setPayment(order.getPayment());
    	orderVo.setPaymentType(order.getPaymentType());
    	orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeof(order.getPaymentType()).getValue());
    	orderVo.setPostage(order.getPostage());
    	orderVo.setStatus(order.getStatus());
    	orderVo.setStatusDesc(Const.OrderStatusEnum.codeof(order.getStatus()).getValue());
    	orderVo.setShippingId(order.getShippingId());
    	Shipping shipping=shippingMapper.selectByPrimaryKey(order.getShippingId());
    	if(shipping!=null){
    		orderVo.setReceiverName(shipping.getReceiverName());
    		ShippingVo shippingVo = assembleShippingVo(shipping);
    		orderVo.setShippingVo(shippingVo);
    	}
    	orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
    	orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
    	orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
    	orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
    	orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
    	orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
    	List<OrderItemVo> orderItemVos=new ArrayList<OrderItemVo>();
    	for(OrderItem o:orderItems){
    		OrderItemVo orderItemVo = assembleOrderItemVo(o);
    		orderItemVos.add(orderItemVo);
    	}
    	orderVo.setOrderItemVoList(orderItemVos);
		return orderVo;
    }
	private ShippingVo assembleShippingVo(Shipping shipping) {
		ShippingVo shippingVo=new ShippingVo();
		shippingVo.setReceiverAddress(shipping.getReceiverAddress());
		shippingVo.setReceiverCity(shipping.getReceiverCity());
		shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
		shippingVo.setReceiverMobile(shipping.getReceiverMobile());
		shippingVo.setReceiverName(shipping.getReceiverName());
		shippingVo.setReceiverPhone(shipping.getReceiverPhone());
		shippingVo.setReceiverProvince(shipping.getReceiverProvince());
		shippingVo.setReceiverZip(shipping.getReceiverZip());
		return shippingVo;
	}
	private OrderItemVo assembleOrderItemVo(OrderItem o) {
		OrderItemVo orderItemVo=new OrderItemVo();
		orderItemVo.setCurrentUnitPrice(o.getCurrentUnitPrice());
		orderItemVo.setOrderNo(o.getOrderNo());
		orderItemVo.setProductId(o.getProductId());
		orderItemVo.setProductImage(o.getProductImage());
		orderItemVo.setProductName(o.getProductName());
		orderItemVo.setQuantity(o.getQuantity());
		orderItemVo.setTotalPrice(o.getTotalPrice());
		orderItemVo.setCreateTime(DateTimeUtil.dateToStr(o.getCreateTime()));
		return orderItemVo;
	}
					
}
