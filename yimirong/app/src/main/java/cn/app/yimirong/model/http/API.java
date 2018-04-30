package cn.app.yimirong.model.http;

public class API {

	// API接口
	public static final String API_TEST = "http://api.cmibank.vip/";
//	public static final String API_TEST = "http://api.wcdog.vip/";
	public static final String COOKIE_DOMAIN = ".cmibank.vip";
	public static final String HTTP_COOKIE_DOMAIN = ".cmibank.com";
//	public static final String COOKIE_DOMAIN = ".wcdog.cn";
	public static final String API_HTTP = "http://api.cmibank.com/";

	public static final String API_HTTPS = "https://api.cmibank.com/";

	public static final String BASE_URL = API_HTTPS;

    // 文件接收地址
	public static final String FILE_UPLOAD ="http://upload.cmibank.com";

	//校验渠道
	public static final String CHECK_INSTALL_MARK ="plat_mark/install_mark";

	// 静态资源
	public static final String STATIC_CDN = "http://static.cmibank.com/";

	/**
	 * 证件类型
	 */
	public static final String GET_ID_TYPES = STATIC_CDN + "tpl/zj.json";

	/**
	 * 获取版本号
	 */
//    public static final String GET_VERSION = "system/getVersion";
	public static final String GET_VERSION = "system/getAndroidVersion";

	/**
	 * 获取活动时间
	 */
	public static final String GET_ACTIVITY_TIME = "system/get_activity_time";

	/**
	 * 获取注册赠送体验金
	 * */
	public static final String GET_REGIST_EXP = "system/get_expmoney_amount";


	/**
	 * 银行列表
	 */
	public static final String CITY_LIST = STATIC_CDN + "tpl/city.json";

	/**
	 * 城市列表
	 */
	public static final String BANK_LIST = STATIC_CDN + "tpl/bank.json";

	/**
	 * 新银行列表
	 */
	public static final String NEW_BANK_LIST = STATIC_CDN + "tpl/new_bank.json";

	/**
	 * 银行图标
	 */
	public static final String BANK_ICON_BASE = STATIC_CDN + "icon/bank/";

	/**
	 * 活期取现方式
	 */
	public static final String KH_CASH_WAY = STATIC_CDN + "tpl/qxfs.html";

	/**
	 * 风险投资告知书
	 */
	public static final String FENGXIAN = STATIC_CDN + "tpl/fxts.html";

	/**
	 * 风险投资告知书
	 */
	public static final String MZSM = STATIC_CDN + "tpl/mzsm.html";

	/**
	 * 快乐宝取现方式
	 */
	public static final String KL_CASH_WAY = STATIC_CDN + "tpl/klbqxfs.html";


	/**
	 * 起息时间
	 */
	public static final String START_TIME = STATIC_CDN + "tpl/qxsj.html";

	/**
	 * 用户协议
	 */
	public static final String USER_AGREEMENT = STATIC_CDN
			+ "tpl/UserAgreementRegister.html";

	/**
	 * 法定节假日，调休日
	 */
	public static final String SPECIAL_DAYS = STATIC_CDN
			+ "tpl/specialdays.json";

	/**
	 * 非工作日提现提示
	 */
	public static final String WITHDRAW_TIPS = STATIC_CDN
			+ "tpl/withdraw_tips.json";

	/**
	 * 转出说明
	 */
	public static final String ZCHU_DESC = STATIC_CDN + "tpl/zcsm.html";

	/**
	 * 邀请相关
	 */
	public static final String SHARE_INVITE_DESC = STATIC_CDN
			+ "tpl/invite.json";

	/**
	 * 提现说明
	 */
	public static final String TIXIAN_DESC = STATIC_CDN + "tpl/txsm.html";

	/**
	 * 购买说明
	 */
	public static final String BUY_DESC = STATIC_CDN + "tpl/hqsm.html";

	/**
	 * 公司资质
	 */
	public static final String QUALIFICATION = STATIC_CDN + "tpl/zhengshu.html";

	/**
	 * 常见问题
	 */
	public static final String QUESTION_ANSWER = STATIC_CDN
			+ "json/question.json";

	/**
	 * 体验金说明
	 */
	public static final String EXP_DESC = STATIC_CDN + "tpl/tyjsm.html";

	/**
	 * 了解我们
	 */
	public static final String LIAOJIE_WOMEN = STATIC_CDN + "tpl/ljwm.html";

	/**
	 * 邀请说明
	 */
	public static final String YAO_QING_DESC = STATIC_CDN + "tpl/yqsm.html";

	/**
	 * 产品列表排序
	 */
	public static final String FRAG_SORT = STATIC_CDN + "config/fragsort.json";

	/**
	 * 用户反馈
	 */
	public static final String FEED_BACK = "system/sendfeedback";

	/**
	 * 公司动态
	 */
	public static final String COMPANY_NEWS = "system/getNews";

	/**
	 * 获取服务器时间
	 */
	public static final String SERVER_TIME = "homepage/servertime";

	/**
	 * 获取公告
	 */
	public static final String GET_MESSAGES = "notice";

	/**
	 * 紧急通告
	 */
	public static final String JINJI_NOTICE = "notice/jj_notice";

	/**
	 * 获取系统信息
	 */
	public static final String CHECK_SYSTEM_INFO = "system/checkSystemInfo_android";

	/**
	 * 获取兑换商品
	 * */
	public static final String GET_ONLINE_GOODS_LIST = "goods/getOnlineGoodsList";

	/**
	 * 获取兑换商品
	 * */
	public static final String GET_GOODS_DETAIL = "goods/getGoodsDetail";

	/**
	 * 兑换
	 * */
	public static final String DUIHUAN = "goods/duihuan";

	/**
	 * 获得商品信息
	 * */
	public static final String GET_GOODS_DESC = "goods/getGoodsDesc";

	/**
	 * 获取兑换记录
	 * */
	public static final String GET_DUIHUAN_LIST = "goods/getDuihuanList";


	public static class Product {

		/**
		 * Banner
		 */
		public static final String RECOMMEND_BANNER = "recommend/banner";

		/**
		 * 推荐产品
		 */
		public static final String RECOMMEND_PRODUCT = "recommend";

		/**
		 * 产品列表
		 */
		public static final String PRODUCT_LIST = "homepage";

		/**
		 * 定期产品列表
		 */
		public static final String DQ_PRODUCT_LIST = "homepage/product";

		/**
		 * 快活宝产品列表
		 */
		public static final String KH_PRODUCT_LIST = "homepage/longproduct";

		/**
		 * 快乐宝产品详情
		 */
		public static final String KL_PRODUCT_LIST = "homepage/klproduct";

		/**
		 * 定期产品列表
		 */
		public static final String DQ_PRODUCT_DETAIL = "product/getProductDetail";

		/**
		 * 快活宝产品详情
		 */
		public static final String KH_PRODUCT_DETAIL = "longproduct/getLongProductDetail";


		/**
		 * 快乐宝产品详情
		 */
		public static final String KL_PRODUCT_DETAIL = "klproduct/getKlproductDetail";

		/**
		 * 购买定期
		 */
		public static final String BUY_DQ_PRODUCT = "buy/product";

		/**
		 * 购买快活宝
		 */
		public static final String BUY_KH_PRODUCT = "buy/longproduct";

		/**
		 * 购买快乐宝
		 */
		public static final String BUY_KL_PRODUCT = "buy/klproduct";

		/**
		 * 定期产品购买记录
		 */
		public static final String DQ_BUY_RECORD = "product/getProductBuyInfo";

		/**
		 * 定期产品购买记录
		 */
		public static final String WEB_PRODUCT = "/homepage/gotoDetail";

		/**
		 * 快活宝产品购买记录
		 */
		public static final String KH_BUY_RECORD = "longproduct/getLongproductBuyInfo";

		/**
		 * 快乐宝产品购买记录
		 */
		public static final String KL_BUY_RECORD = "klproduct/getKlproductBuyInfo";

		/**
		 * 定期产品合同信息
		 */
		public static final String DQ_PRODUCT_CONTRACT = "product/getContractObject";

		/**
		 * 快活宝产品合同信息
		 */
		public static final String KH_PRODUCT_CONTRACT = "longproduct/getLongProductContractObject";

		/**
		 * 快乐宝产品合同信息
		 */
		public static final String KL_PRODUCT_CONTRACT = "klproduct/getKlProductContractObject";

	}

	public static class Account {

		/**
		 * 验证账户是否存在
		 */
		public static final String VERIFY_ACCOUNT = "login/verification_account";

		/**
		 * 用户合同
		 */
		public static final String USER_PRODUCT_CONTRACT = "user/getUserProductContract";

		/**
		 * 发送短信验证码
		 */
		public static final String SEND_VERIFY_CODE = "login/send_phone_code";

		/**
		 * 发送短信验证码
		 */
			public static final String SEND_JYT_CODE = "login/send_pay_code";

		/**
		 * 注册
		 */
		public static final String REGISTER = "login/reguser";

		/**
		 * 注册验证码
		 */
		public static final String REGISTER_TEST = "login/validate_phone_code";

		/**
		 * 重置密码验证码
		 */
		public static final String RESETPASS_TEST = "login/validate_loginpwd";

		/**
		 * 重置支付密码验证码
		 */
		public static final String RESETPAYPASS_TEST = "account/validateTpwd_code";

		/**
		 * 登录
		 */
		public static final String LOGIN = "login/login";

		/**
		 * 设置交易密码
		 */
		public static final String SET_PAY_PASS = "account/setTpwd";

		/**
		 * 获取用户信息
		 */
		public static final String GET_USER_INFO = "user/getUserInfo";

		/**
		 * 获取用户账户余额
		 */
		public static final String GET_USER_BALANCE = "user/userBlance";

		/**
		 * 获取短信验证码
		 */
		public static final String GET_MSG_CODE = "yee/getMsgCode";

		/**
		 * 提交支付失败数据
		 */
		public static final String POST_PAY_ERROR = "log/save_pay_error_log";

		/**
		 * 提现
		 */
		public static final String CASH = "yee/withDraw";

		/**
		 * 验证交易密码
		 */
		public static final String JYT_VERFIFY = "yee/verfifytpwd";

		/**
		 * 用户收益
		 */
		public static final String USER_PROFIT_INFO = "user/userProfitInfo";

		/**
		 * 用户收益
		 */
		public static final String USER_NOTICE_MESSAGE = "user/getUserNoticeList";

		/**
		 * 用户快活宝数据
		 */
		public static final String USER_HQ_MONEY = "user/userLongProductInfo";

		/**
		 * 用户购买的产品
		 */
		public static final String USER_PRODUCT = "user/userproduct";

		/**
		 * 用户总的累计收益明细
		 */
		public static final String ALL_PROFIT_LIST = "user/getUserProfitDetailList";


		/**
		 * 用户定期累计收益明细
		 */
		public static final String DQ_PROFIT_LIST = "user/getUserProductProfitDetailList";

		/**
		 * 用户快活宝累计收益明细
		 */
		public static final String HQ_PROFIT_LIST = "user/getUserLongProductProfitDetailList";

		/**
		 * 用户快乐宝累计收益明细
		 */
		public static final String KL_PROFIT_LIST = "user/getUserKlProductProfitDetailList";

		/**
		 * 用户操作记录
		 */
		public static final String ACTION_LOG = "user/userActionLog";

		/**
		 * 快活宝转到余额
		 */
		public static final String LONG_MONEY_TO_BALANCE = "yee/longmoneyToBalance";

		/**
		 * 重置登录密码--第一步 发送验证码
		 */
		public static final String RESET_LOGIN_PASS_STEP_1 = "login/findLoginPwd_step1";

		/**
		 * 重置登录密码--第二步 提交验证码和密码
		 */
		public static final String RESET_LOGIN_PASS_STEP_2 = "login/findLoginPwd_step2";

		/**
		 * 重置交易密码--第一步 发送短信验证码
		 */
		public static final String RESET_PAY_PASS_STEP_1 = "account/findTpwd_step1";

		/**
		 * 重置交易密码--第二步 发送短信验证码
		 */
		public static final String RESET_PAY_PASS_STEP_2 = "account/findTpwd_step2";

		/**
		 * 获取用户取现、充值、转出的次数
		 */
		public static final String GET_USER_CD = "cd/getUserCd";

		/**
		 * 我邀请的交易过的人
		 */
		public static final String MY_INVITE_BUY = "invite/my_invite_buy";

		/**
		 * 我邀请的人
		 */
		public static final String MY_INVITE = "invite/my_invite";

		/**
		 * 我邀请的交易奖励
		 */
		public static final String GET_USER_INVITE_REWARD = "invite/get_user_inviterward";

		/**
		 * 获取用户推送的标签
		 */
		public static final String GET_USER_PUSH_TAG = "user/user_push_tag";

		/**
		 * 获取体验金余额
		 */
		public static final String GET_EXPMONEY_BALANCE = "expmoney/getExpmoneyBalance";

		/**
		 * 获取体验金日志
		 */
		public static final String GET_EXPMONEY_LOG = "expmoney/getExpmoneyLog";

		/**
		 * 体验金购买
		 */
		public static final String USE_EXPMONEY_BUY = "expmoney/buy";

		/**
		 * 新体验金获取
		 * */

		public static final String GET_NEWEXPMONEY_LOG = "user/getUserExpmoneyList";

		/**
		 * 使用新体验金
		 * */
		public static final String USE_NEWEXPMONEY= "expmoney/tiyan";

		/**
		 * 用户体验金信息
		 */
		public static final String GET_EXPMONEY_INFO = "expmoney/getUserExpMoneyInfo";

		/**
		 * 获取体验金收益
		 */
//		public static final String GET_EXPMONEY_PROFIT = "user/getUserExpProductProfitDetailList";
		public static final String GET_EXPMONEY_PROFIT = "user/getUserExpMoneyProfitDetailList";

		/**
		 * 活期宝七日万份收益
		 */
		public static final String KH_MONEY_PROFIT = "user/longmoney_seven";

	}

	/**
	 * 易宝支付
	 */
	public static class YeePay {
		/**
		 * 易宝-注册检查
		 */
		public static final String YEE_BIND_CHECK = "yee/yee_bind_check";

		/**
		 * 易宝注册
		 */
		public static final String REGIST_YEE = "yee/regist_yee";

		/**
		 * 易宝注册确认
		 */
		public static final String REGIST_YEE_CHECK = "yee/regist_yee_check";

		/**
		 * 易宝支付
		 */
		public static final String YEE_PAY = "yee/pay";

		/**
		 * 易宝支付查询充值结果
		 */
		public static final String QUERY_PAY_RESULT = "yee/queryOrder";

		/**
		 * 易宝支付 开户
		 */
		@Deprecated
		public static final String YEE_REGIST = "yee/regist";

		/**
		 * 易宝支付 开户-短信确认
		 */
		@Deprecated
		public static final String YEE_REGIST_CHECK = "yee/regist_check";
	}

	/**
	 * 易宝托管
	 *
	 * @author android
	 */
	@Deprecated
	public static class YeeTG {
		/**
		 * 注册
		 */
		public static final String TO_REGISTER = "tg_yee/register";

		/**
		 * 充值
		 */
		public static final String TO_RECHARGE = "tg_yee/toRecharge";

		/**
		 * 购买
		 */
		public static final String BUY_PRODUCT = "tg_yee/BuyProduct";

		/**
		 * 自动转账授权
		 */
		public static final String AUTHORIZE_AUTO_TRANSFER = "tg_yee/toAuthorizeAutoTransfer";

		/**
		 * 提现
		 */
		public static final String TO_WITHDRAW = "tg_yee/toWithdraw";

		/**
		 * 用户托管账户余额
		 */
		public static final String TG_BALANCE = "tg_yee/get_tg_balance";
	}

	/**
	 * 连连支付相关API
	 */
	public static class LLPay {
		// 构造支付订单
		public static final String BUILD_ORDER = "llpay/bulidPaySign";

		// 查询订单
		public static final String QUERY_ORDER = "llpay/queryOrder";

		// 绑卡
		public static final String LLPAY_REGIST = "llpay/llpay_regist";
	}


	/**
	 * 富友支付
	 */
	public static class FUYOUPay {

		/**
		 * 充值
		 */
		public static final String PAY = "fuioupay/pay";
        //fuyou改为fuiou
		/**
		 * 查询
		 */
		public static final String QUERY_ORDER = "fuioupay/queryOrder";

		/**
		 * 查询提现
		 */
		public static final String QUERY_WITH_DRAW_ARRIVE = "fuioupay/queryWithDrawArrive";
	}

	/**
	 * 金运通支付
	 */
	public static class JYTPay {
		/**
		 * 绑卡
		 */
		@Deprecated
		public static final String BIND = "jytpay/bind";

		/**
		 * 充值
		 */
		@Deprecated
		public static final String PAY_OLD = "jytpay/pay";

		/**
		 * 查询
		 */
		@Deprecated
		public static final String QUERY = "jytpay/queryOrder";

		/**
		 * 取现
		 */
		@Deprecated
		public static final String WITHDRAW = "jytpay/withdraw";

		/**
		 * 充值
		 */
		public static final String PAY = "yee/directPayment";
	}

	/**
	 * 宝付支付
	 */
	public static class BaoFooPay {
		public static final String BAOFOO_PAY_READY = "baofoopay/pay_ready";
	}
	//银行开户地址
	public static final String BANK_OF_DEPOSIT = "http://static.cmibank.vip/json/cityid.json";
}
