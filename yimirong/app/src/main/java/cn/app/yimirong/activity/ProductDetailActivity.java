package cn.app.yimirong.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baofoo.sdk.vip.BaofooPayActivity;
import com.fuiou.mobile.http.HttpConfig;
import com.umeng.analytics.MobclickAgent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.BaseEvent;
import cn.app.yimirong.event.custom.BuySuccessEvent;
import cn.app.yimirong.event.custom.PassEvent;
import cn.app.yimirong.event.custom.UserInfoUpdateEvent;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.base.BaseModel;
import cn.app.yimirong.model.bean.Bank;
import cn.app.yimirong.model.bean.BaseProduct;
import cn.app.yimirong.model.bean.BuyResult;
import cn.app.yimirong.model.bean.DQProduct;
import cn.app.yimirong.model.bean.ShareInvite;
import cn.app.yimirong.model.bean.UserInfo;
import cn.app.yimirong.model.db.dao.BankDao;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.presenter.MyFyPayCallBack;
import cn.app.yimirong.presenter.impl.PayPresenter;
import cn.app.yimirong.utils.DigestUtils;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;
import cn.app.yimirong.view.Calculator;
import cn.app.yimirong.view.ObservableScrollView;
import cn.app.yimirong.view.OnPasswordInputFinish;
import cn.app.yimirong.view.PasswordView;

public class ProductDetailActivity extends BaseActivity implements
		OnClickListener, SwipeRefreshLayout.OnRefreshListener, ObservableScrollView.ScrollListener,MyFyPayCallBack {

	public static final int PAYTYPE_ACCOUNT = 1;

	public static final int PAYTYPE_BANK = 2;

	public static final int PAY_RESUEST_CODE = 100;

	private int paytype = PAYTYPE_BANK;

	private double moneyLimit = 0;

	public int single;

	private PopupWindow window;

	private String password;

	private Bank bank;

	private int passError = 3;

	private String passmd5;

	private PayPresenter presenter;

	private RelativeLayout rlWrapper2;

	private TextView seling_money,hq_qitou,hq_muji;

	private LinearLayout tvBuy;

	private long reLen = 0;

	private Animation show;

	private Animation hidden;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					reLen--;
					getFormatedTime(reLen);
					handler.removeMessages(1);
					if (reLen > 0) {
						Message message = handler.obtainMessage(1);
						handler.sendMessageDelayed(message, 1000);      // send message
						tvBuy.setOnClickListener(null);
					} else {
						tvBuy.setOnClickListener(ProductDetailActivity.this);
						initData();
					}
					break;
			}
			super.handleMessage(msg);
		}
	};

	private TextView payTitle;

	private LinearLayout hq_detail;

//	// 方式1
//	private TextView tvType1;
//
//	// 方式2
//	private TextView tvType2;
//
//	// 方式1
//	private CheckBox cbType1;
//
//	// 方式2
//	private CheckBox cbType2;

	//项目描述
	public static final String TYPE_OBJ = "obj";

	//资金保障
	public static final String TYPE_CAP = "cap";

	//下拉刷新
	private SwipeRefreshLayout refresher;

	private TextView tvText1;
	private TextView tvText2;
	private TextView howT;

	private TextView tvText3;
	private TextView tvText4;
	private LinearLayout bottomLayout, longproductline;

	private ObservableScrollView scroll;

	// 立即购买
	private RelativeLayout dq_detail,muji_layout,rlWrapper1;

	// 年化利率
	private TextView tvProfit;

	//产品名称
	private TextView tvName;

	private TextView tvRongzi;

	private double leftMoney;

	private double startMoney = 0;

	private boolean isBalance = false;

	private boolean isAddCark = false;

//	private LinearLayout profitWrapper;

//	private TextView tgProfit;

	// 期限
	private TextView tvDays;

	// 起购
	private TextView tvStart;

	/**
	 * ScrollView正在向上滑动
	 */
	public static final int SCROLL_UP = 0x01;

	/**
	 * ScrollView正在向下滑动
	 */
	public static final int SCROLL_DOWN = 0x10;

	//可投
//	private TextView tvInvest;

	private TextView baozhang;


	// 金额
	private EditText etMoney;
	private double money;
	private ImageButton ibDelete;
	private double shouldPay;

	//计时器
//    private Chronometer chTimer;

	// 已购买人数
	private TextView tvBuyNum;
//	private TextView tvEdu;
	private TextView tvProjDesc;
//	private TextView tvSecuInfo;
	private TextView tvQixiInfo,tvDaoqiInfo,buy_text;
	RelativeLayout rlBuyRecord;
//	private RelativeLayout rlEdu;
	//富友返回结果验证码
	public String fuiouCode;
	//商户订单号
	public String orderid;
	//富友sdk

	private BaseProduct product;
	private String pid;
	private int productType;
	private int productState;
	private boolean canbuy = false;
	private boolean issellout = false;
	private boolean isyugao = false;
	private boolean isUserCanBuy = false;
	private double yuqiProfit = 0.00f;

	private String cid = null;

	private int position = -1;

	private double sendmoney = 0.00;

	private double minmoney = 0.00;

	private long server;

	private DataMgr vInfo;

	private int canuserbuy = 1;
	private View line2;
	private View line3;
	private TextView qitou;
	private RelativeLayout activityDetailProductName;
	private TextView zjbzDetail;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		//实例化富友sdk
		HttpConfig.getInstance().setRelease(true);
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		presenter.unBind();
		handler.removeMessages(1);
		super.onDestroy();
	}

	@Override
	public void updateData(Bundle data) {
		super.updateData(data);
		buyProduct();
	}

	@SuppressWarnings("ResourceType")
	@Override
	public void loadView() {
		setContentView(R.layout.activity_product_detail);
		presenter = new PayPresenter();
		presenter.bind(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		refreshDone();
	}

	@Override
	public void saveInstance(Bundle outState) {
		super.saveInstance(outState);
		outState.putInt("productType", productType);
		outState.putString("pid", pid);
	}

	@Override
	public void restoreInstance(Bundle savedState) {
		super.restoreInstance(savedState);
		productType = savedState.getInt("productType");
		pid = savedState.getString("pid");
	}

	@Override
	public void initView() {
		show = AnimationUtils.loadAnimation(context, R.anim.popshow_anim);
		hidden = AnimationUtils.loadAnimation(context, R.anim.pophidden_anim);
		setTitleBack(true);
		setTitleText("投资详情");
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		if (data != null) {
			product = (BaseProduct) data.getSerializable("product");
			pid = data.getString("pid");
			productType = data.getInt("productType", BaseProduct.TYPE_DQ);
			productState = data.getInt("productState", BaseProduct.STATE_SELLOUT);
			isyugao = data.getBoolean("isyugao", false);
			canbuy = data.getBoolean("canbuy", false);
			issellout = data.getBoolean("issellout", false);
			canuserbuy = data.getInt("canbuyuser", 1);
			setTitleText(product.pname);
		}

		scroll = (ObservableScrollView) findViewById(R.id.detail_scroll);
		activityDetailProductName = (RelativeLayout) findViewById(R.id.activity_detail_product_name);
		scroll.setScrollListener(this);
//
		findViewById(R.id.mzsm).setOnClickListener(this);
		findViewById(R.id.fxts).setOnClickListener(this);
		baozhang = (TextView) findViewById(R.id.baozhang_text);

		hq_detail = (LinearLayout) findViewById(R.id.hq_detail_layout);
		dq_detail = (RelativeLayout) findViewById(R.id.dq_detail_layout);
		muji_layout = (RelativeLayout) findViewById(R.id.activity_detail_product_rongzi);

		refresher = (SwipeRefreshLayout) findViewById(R.id.activity_detail_refresher);
		assert refresher != null;
		refresher.setOnRefreshListener(this);
		int[] schemes = getResources().getIntArray(R.array.google_colors);
		refresher.setColorSchemeColors(schemes);
		bottomLayout = (LinearLayout) findViewById(R.id.activity_detail_bottom_wrapper);
		qitou = (TextView) findViewById(R.id.activity_product_name_tv1);

		hq_qitou = (TextView) findViewById(R.id.hq_qitou);
		hq_muji = (TextView) findViewById(R.id.hq_selling);
		tvQixiInfo = (TextView) findViewById(R.id.qixi_time_info);
		tvDaoqiInfo = (TextView) findViewById(R.id.daoqi_time_info);
		seling_money = (TextView) findViewById(R.id.seling_money);
		buy_text = (TextView) findViewById(R.id.activity_detail_buy_text);
		tvProfit = (TextView) findViewById(R.id.activity_detail_profit);
		tvBuyNum = (TextView) findViewById(R.id.activity_detail_buy_num);
		tvName = (TextView) findViewById(R.id.activity_detail_name);
		tvRongzi = (TextView) findViewById(R.id.activity_detail_rongziname);
		tvDays = (TextView) findViewById(R.id.activity_detail_days);
		line2 = findViewById(R.id.line2);
		line3 = findViewById(R.id.line3);
		rlWrapper1 = (RelativeLayout) findViewById(R.id.activity_detail_payinfo_wrapper);
		assert rlWrapper1 != null;
		rlWrapper1.setOnClickListener(this);
		tvText1 = (TextView) findViewById(R.id.activity_detail_tv1);
		tvText2 = (TextView) findViewById(R.id.activity_detail_tv2);

		rlWrapper2 = (RelativeLayout) findViewById(R.id.activity_detail_huodong_wrapper);
		assert rlWrapper2 != null;
		rlWrapper2.setOnClickListener(this);
		tvText3 = (TextView) findViewById(R.id.activity_detail_tv3);
		tvText4 = (TextView) findViewById(R.id.activity_detail_tv4);

		rlBuyRecord = (RelativeLayout) findViewById(R.id.activity_detail_buyrecord_wrapper);
		assert rlBuyRecord != null;
		rlBuyRecord.setOnClickListener(this);

		RelativeLayout rlProjDesc = (RelativeLayout) findViewById(R.id.activity_detail_xmms);
		assert rlProjDesc != null;
		rlProjDesc.setOnClickListener(this);


		RelativeLayout rlSecuInfo = (RelativeLayout) findViewById(R.id.activity_detail_zjbz);
		assert rlSecuInfo != null;
		rlSecuInfo.setOnClickListener(this);

		ImageButton btnCalc = (ImageButton) findViewById(R.id.activity_detail_calc);
		assert btnCalc != null;
		btnCalc.setOnClickListener(this);

		tvBuy = (LinearLayout) findViewById(R.id.activity_detail_buy);
		assert tvBuy != null;
		tvBuy.setOnClickListener(this);

		tvProjDesc = (TextView) findViewById(R.id.activity_detail_tv_xmms);

		zjbzDetail = (TextView) findViewById(R.id.zjbz_detail);

		updatePayType();

	}

	/**
	 * 显示添加银行卡
	 */
	private void showAddBank() {
//		payClass.setVisibility(View.GONE);
//		productTitle.setVisibility(View.GONE);
	}


	/**
	 * 隐藏添加银行卡
	 */
	private void hideAddBank() {
//		payClass.setVisibility(View.VISIBLE);
//		productTitle.setVisibility(View.VISIBLE);
	}


	/**
	 * 用户信息已更新
	 *
	 * @param event
	 */
	public void onEventMainThread(UserInfoUpdateEvent event) {
		updatePayType();
	}

	/**
	 * 更新付款方式和账户余额
	 */
	private void updatePayType() {
		if (App.userinfo != null) {
			UserInfo userinfo = App.userinfo;
			if (userinfo.identity != null
					&& !StringUtils.isBlank(userinfo.identity.bankid)) {
				hideAddBank();
				sortPayType();
				enableSubmitBtn("立即购买");
				isUserCanBuy = true;
				isAddCark = false;
			} else {
				showAddBank();
				enableSubmitBtn("请添加银行卡");
				isUserCanBuy = false;
				isAddCark = true;
			}
		} else {
			ToastUtils.show(context, "请先登录");
		}
	}

	/**
	 * 禁用提交按钮
	 */
	private void disableSubmitBtn(String text) {
		buy_text.setText(text);
		tvBuy.setEnabled(false);
	}

	/**
	 * 开启提交按钮
	 */
	private void enableSubmitBtn(String text) {
		buy_text.setText(text);
		tvBuy.setEnabled(true);
	}


	/**
	 * 更新按钮
	 */
	private void updateButton() {
		if (product != null) {
			String status;
			if ("1".equals(product.status)) {
				if (isAddCark && App.isLogin) {
					enableSubmitBtn("请添加银行卡");
					return;
				}
				if (canbuy) {
					isyugao = product.isYuGao();
					reLen = product.getLeftSeconds();
					if (isyugao) {
						if (!StringUtils.isBlank(product.online_time)) {
							// 获取预售时间
							String time2 = getSellText();
						/*	buy_text.setText(time2 + "" +
									"发售");*/
							tvBuy.setClickable(false);
						} else {
						/*	buy_text.setText("即将发售");*/
							tvBuy.setClickable(false);
						}
					}

				}

			} else if (product.state == BaseProduct.STATE_COMPLETE) {
				rlBuyRecord.setVisibility(View.GONE);
				if ("2".equals(product.repayment_status)) {
					status = "已还款";
				}else if ("0".equals(product.repayment_status)){
					status = "满额";
				}else {
					status = "还款中";
				}
				tvBuy.setClickable(false);
				buy_text.setText(status);
			} else {
				disableSubmitBtn("满额");
				rlBuyRecord.setVisibility(View.GONE);
			}

		} else {
			disableSubmitBtn("立即购买");
		}

		if (canuserbuy!=1 && product.state == BaseProduct.STATE_SELLING){
			if (canuserbuy == 3){
				if (product.operation_tag != null && product.operation_tag.length()>0) {
					buy_text.setText(product.operation_tag);
				}else {
					buy_text.setText("老用户才能购买");
				}
			}

			if (canuserbuy == 2){
				if (product.operation_tag != null && product.operation_tag.length()>0) {
					buy_text.setText(product.operation_tag);
				}else {
					buy_text.setText("新手标");
				}
			}
		}

	}

	/**
	 * 处理输入完毕
	 *
	 * @param password
	 */
	private void inputDone(String password) {
		this.passmd5 = DigestUtils.md5(password).toUpperCase(Locale.CHINA);
		if (paytype == PAYTYPE_BANK) {
			// 如果付款方式是银行卡，先充值，再购买
			pay();
		} else {
			// 如果是账户余额支付，直接购买
			buyProduct();
		}
	}

	/**
	 * 先充值
	 */
	private void pay() {
		double m = money;
		if (presenter != null) {
			WindowManager windowManager = getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			presenter.pay(display);
			presenter.pay(bank, m, passmd5, cid, product.ptid,this,this);

		}
	}

	private void yuePay() {
		double y = Double.parseDouble(App.userinfo.balance) * 100;
		int yue = (int) y;
		int m = (int) money * 100;
		if (presenter != null) {

			WindowManager windowManager = getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			presenter.pay(display);

				if (money * 100 > yue) {
					m = m - yue;
					presenter.pay(bank, (double) m / 100, passmd5, cid, product.ptid,this , this);
				} else {
					buyProduct();
				}

		}
	}


	/**
	 * 购买产品
	 */
	public void buyProduct() {
		cid = null;
		if (pmodel != null) {
			showLoading("正在处理");
			pmodel.buyProduct(productType, pid, money, paytype, passmd5, cid, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null && t instanceof BuyResult) {
						BuyResult result = (BuyResult) t;
						buySuccess(result);
						if (App.userinfo != null) {
							App.userinfo.balance = result.balance;
						}
						if(productType==BaseProduct.TYPE_KH){
							EventBus.getDefault().post(new BaseEvent());
						}
						MobclickAgent.onEvent(context, "buySuccess");
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					Map<String, String> map = new HashMap<String, String>();
					map.put("uid", App.loginData.uid);
					map.put("reason", msg);
					MobclickAgent.onEvent(context, "buyFailure", map);
					if ("4021".equals(errorCode)) {
						// 密码错误
						showPassError(msg);
					} else {
						PromptUtils.showDialog3(context, msg, new PromptUtils.OnDialogClickListener1() {
							@Override
							public void onClick(Dialog dialog) {
								dialog.dismiss();
							}
						});
					}
				}
			});
		}
	}

	/**
	 * 处理支付成功
	 */
	private void buySuccess(final BuyResult result) {
		tvBuy.setClickable(false);
		final Dialog dialog = PromptUtils.showSuccessDialog(this, context,
				"购买成功");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// 付款成功，跳转到购买结果
				toBuyResult(result);
			}
		}, 1200);
	}

	/**
	 * 去购买结果
	 */
	private void toBuyResult(BuyResult result) {
		if (result != null && product != null) {
			// 跳转到购买结果
			Intent intent = new Intent(context, BuyDetailActivity.class);
			intent.putExtra("pname", product.pname);
			intent.putExtra("money", result.cost);
			intent.putExtra("paytype", paytype == PAYTYPE_ACCOUNT ? "账户余额支付"
					: "银行卡支付");
			intent.putExtra("trxid", result.trxid);
			Bundle bundle = new Bundle();
			bundle.putSerializable("result", result);
			intent.putExtras(bundle);
			EventBus.getDefault().post(new BaseEvent());
			startActivity(intent);
		}
	}

	/**
	 * 处理密码错误
	 */
	public final void showPassError(String msg) {

		PromptUtils.showDialog4(activity, context, "", msg, "重新输入",
				"找回密码", new PromptUtils.OnDialogClickListener2() {
					@Override
					public void onLeftClick(Dialog dialog) {
						showPopwindow();
					}

					@Override
					public void onRightClick(Dialog dialog) {
						toFindPass();
					}
				}, false);
	}

	/**
	 * 余额不足，去充值
	 *
	 * @param
	 */
	private void toPay(double shouldPay) {
		Intent intent = new Intent(context, PayActivity.class);
		intent.putExtra("isBuy", true);
		intent.putExtra("payMoney", shouldPay);
		startActivityForResult(intent, PAY_RESUEST_CODE);
	}


	/**
	 * 提交，显示支付界面
	 */
	private void submit() {
		if (!App.isLogin) {
			ToastUtils.show(context, "请先登录");
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra("isBuy", true);
			intent.putExtra("origin", this.getClass().getSimpleName());
			startActivityForResult(intent,233);
			return;
		}
		hideInputMethod();

		if (App.userinfo != null) {
			UserInfo userinfo = App.userinfo;
			if (userinfo.identity != null) {
				// 已绑卡
				if (userinfo.identity.tpwd) {
					// 已设置交易密码
					// 校验账户余额

					if (paytype == PAYTYPE_BANK) {
						// 银行卡支付
						if (bank != null) {
							if (money < 2 && bank.name.equals("招商银行") && bank.plat.equals("fuiou")) {
								ToastUtils.show(this, "招商银行卡支付必须不少于2元");
								return;
							}
							if (presenter.getPlatList() != null && presenter.getPlatList().size() > 0 && !presenter.getPlatList().contains(bank.plat)) {
								single = BankDao.getInstance(context).getPlatSingle(presenter.getPlatList().get(0), bank.code);
							} else {
								single = BankDao.getInstance(context).getPlatSingle(bank.plat, bank.code);
							}

							if (money > single) {
								// 超过单笔限额
								showQuotaInfo();
								return;
							}
						}
					}
					detailPopwindow();

				} else {
					// 未设置交易密码，提示去设置交易密码
					showSetTpwd();
				}
			}
		} else {
			ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
			finish();
		}
	}

	/**
	 * 提示设置交易密码
	 */
	private void showSetTpwd() {
		PromptUtils.showDialog1(activity, context, "提示", "未设置支付密码，请设置支付密码",
				"确定", new PromptUtils.OnDialogClickListener1() {
					@Override
					public void onClick(Dialog dialog) {
						Intent intent = new Intent(context,
								SetPayPassActivity.class);
						startActivity(intent);
					}
				}, true);
	}

	/**
	 * 提示超过单笔限额
	 */
	private void showQuotaInfo() {
		String message = bank.name + "单笔限额" + single;
		PromptUtils.showDialog1(activity, context, "提示", message, "确定",
				new PromptUtils.OnDialogClickListener1() {
					@Override
					public void onClick(Dialog dialog) {
					}
				}, true);
	}

	/**
	 * 密码被锁定
	 */
	private void showPassLocked() {
		String msg = "支付密码已锁定，3小时后再试";
		PromptUtils.showDialog2(activity, context, "提示", msg, "取消", "找回密码",
				new PromptUtils.OnDialogClickListener2() {
					@Override
					public void onLeftClick(Dialog dialog) {
					}

					@Override
					public void onRightClick(Dialog dialog) {
						toFindPass();
					}
				});
	}

	/**
	 * 去找回密码
	 */
	private void toFindPass() {
		Intent intent = new Intent(context, IdentifyActivity.class);
		intent.putExtra("isResetPayPass", true);
		startActivity(intent);
	}

	@Override
	public void initData() {
		// 获取产品详情
		ShareInvite shareInvite = (ShareInvite) mCache
				.getAsObject("share_invite");
		if (shareInvite!=null && shareInvite.Third_party_payment!=null && !shareInvite.Third_party_payment.equals("")){
			baozhang.setText(shareInvite.Third_party_payment);
		}
		onRefresh();
		if (product != null) {
			updateView();
		}
		if (App.userinfo != null) {
			UserInfo userinfo = App.userinfo;
			if (userinfo.identity != null
					&& userinfo.identity.bankid != null) {
				bank = BankDao.getInstance(context)
						.query(userinfo.identity.bankid);
			}
		}
		if (productType == BaseProduct.TYPE_KH) {
			// 快活宝
			setTitleRight(true, new OnRightClickListener() {
				@Override
				public void onClick() {
					toBuyShuoMing();
				}
			});
			setRightText("购买说明");
		} else if (productType == BaseProduct.TYPE_DQ
				|| productType == BaseProduct.TYPE_KL) {
			setTitleRight(false, null);
		} else {
			ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
			finish();
		}
		if (isUserCanBuy) {
			if ("1".equals(product.status)) {
				isAddCark = false;
			} else {
				disableSubmitBtn("满额");
			}
		} else {
			enableSubmitBtn("请添加银行卡");
			isAddCark = true;
		}
		updateButton();
	}

	/**
	 * 去购买说明
	 */
	private void toBuyShuoMing() {
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra("title", "购买说明");
		intent.putExtra("url", API.BUY_DESC);
		startActivity(intent);
	}


	private void sortPayType() {
		if (App.userinfo != null && App.userinfo.balance != null) {
			double userBalance = (double) Double
					.parseDouble(App.userinfo.balance);
			if (userBalance > 0) {
				// 用户余额大于0，账户余额支付放在上面
				isBalance = true;
				paytype = PAYTYPE_ACCOUNT;
			} else {
				// 用户余额为0，银行支付放在上面
				isBalance = false;
				paytype = PAYTYPE_BANK;
			}
		}
	}


	/**
	 * 计算收益
	 *
	 * @param tmpmoney
	 */
	private void calcProfit(double tmpmoney) {
		if (product != null && product instanceof DQProduct) {
			DQProduct p = (DQProduct) product;
			int days = SystemUtils.getDays(p.uistime, p.uietime);
			double income = Double.parseDouble(p.income);
			yuqiProfit = getDouble(tmpmoney * days * (income / 100.0) / 365.0,2);
//			setProfit();
		}
	}

	/**
	 * a为一个带有未知位小数的实数
	 * 对其取b位小数
	 * @param a
	 * @param b
	 * @return
	 */
	static double getDouble(double a,int b){
		int x=0;
		int y=1;
		for(int i=0;i<b;i++){
			y=y*10;
		}
		System.out.println(y);
		x=(int)(a*y);
		System.out.println("x="+x);
		return (double)x/y;
	}


	/**
	 * 加载产品详情
	 */
	private void loadProductDetail() {
		if (pmodel != null) {
			pmodel.getProductDetail(productType, pid, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, final T t) {
					super.onSuccess(response, t);
					refreshDone();
					if (t != null && t instanceof BaseProduct) {
						product = (BaseProduct) t;
						product.state = productState;
						updateView();
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					refreshDone();
					ToastUtils.show(context, msg);
				}
			});
		}
	}

	/**
	 * 更新界面
	 */
	private void updateView() {

		double allmoney = SystemUtils.getDouble(product.money);
		double sellmoney = SystemUtils.getDouble(product.sellmoney);
		tvRongzi.setText(SystemUtils.getDoubleStr(allmoney));
		leftMoney = allmoney - sellmoney;
		this.moneyLimit = Double.parseDouble(product.money_limit);
		this.startMoney = Double.parseDouble(product.startmoney);
		//区分定期和活期
		if (productType == BaseProduct.TYPE_KH
				|| productType == BaseProduct.TYPE_KL) {
			tvText3.setText("起息时间");
			tvText4.setText("当日起息");
			hq_detail.setVisibility(View.VISIBLE);
			dq_detail.setVisibility(View.GONE);
			hq_muji.setText(SystemUtils.getDoubleStr(allmoney));
			hq_qitou.setText(product.startmoney);
			muji_layout.setVisibility(View.GONE);
			line2.setVisibility(View.GONE);
			rlWrapper1.setVisibility(View.GONE);
			line3.setVisibility(View.GONE);
			qitou.setText("取款方式");
			tvName.setText("随存随取");
			activityDetailProductName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(context, WebViewActivity.class);
					intent.putExtra("title", "取现方式");
					intent.putExtra("url", API.ZCHU_DESC);
					startActivity(intent);
				}
			});
		} else {
			//定期
			activityDetailProductName.setOnClickListener(null);
			hq_detail.setVisibility(View.GONE);
			dq_detail.setVisibility(View.VISIBLE);
			DQProduct p = (DQProduct) product;
			if (StringUtils.isBlank(p.activity_url)) {
				rlWrapper2.setVisibility(View.GONE);

			} else {
				rlWrapper2.setVisibility(View.VISIBLE);
			}
			int days = SystemUtils.getDays(p.uistime, p.uietime);
			tvDays.setText(days + "天");

			tvText1.setText("还款信息");
			tvText2.setText("到期日还款至账户余额");
			tvText3.setText("活动说明");
			tvText4.setText(product.standard_text);
			long onlineTime = TimeUtils.getTimeInSecondsFromString(p.uistime,
					TimeUtils.DATE_FORMAT_DATE);
			long end = TimeUtils.getTimeInSecondsFromString(p.uietime,
					TimeUtils.DATE_FORMAT_DATE);
			String time1 = TimeUtils.getTimeFromSeconds(onlineTime,
					TimeUtils.DATE_FORMAT_DATE_DOT);
			String time2 = TimeUtils.getTimeFromSeconds(end+86400,
					TimeUtils.DATE_FORMAT_DATE_DOT);
			tvQixiInfo.setText(time1);
			tvDaoqiInfo.setText(time2);
			qitou.setText("起投金额");
			tvName.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
			tvName.setText(product.startmoney + "元");
		}

		double money = Double.parseDouble(product.money);
		if (sellmoney >= money
				|| !"1".equals(product.status)) {
			canbuy = false;
		}

		tvProfit.setText(SystemUtils.getDoubleStr(product.income));


		seling_money.setText("(剩余" + SystemUtils.getDoubleStr(leftMoney) + "元)");



		tvBuyNum.setText("已购买" + product.buyUserNumer + "人");

		tvProjDesc.setText(product.object_overview);

		zjbzDetail.setText(product.capital_overview);

		if (product.state == BaseProduct.STATE_SELLING) {
			if (canbuy) {
				isyugao = product.isYuGao();
				reLen = product.getLeftSeconds();
				if (isyugao) {
					if (!StringUtils.isBlank(product.online_time)) {
						// 获取预售时间
						String time2 = getSellText();
//						buy_text.setText(time2 + "发售");
						if (product.getLeftSeconds() > 0) {
							Message message = handler.obtainMessage(1);
							handler.sendMessageDelayed(message, 1000);
						} else {
							buy_text.setText("立即购买");
							canbuy = true;
							tvBuy.setClickable(true);
						}
					} else {
						buy_text.setText("即将发售");
						tvBuy.setClickable(false);
					}
				} else {
					if (App.userinfo != null) {
						UserInfo userinfo = App.userinfo;
						if (userinfo.identity != null
								&& !StringUtils.isBlank(userinfo.identity.bankid)) {
							hideAddBank();
						}
					}
					if (isAddCark && App.isLogin) {
						enableSubmitBtn("请添加银行卡");
						return;
					}
					tvBuy.setClickable(true);
					enableSubmitBtn("立即购买");
				}
			} else {
				seling_money.setText("(剩余0元)");
				disableSubmitBtn("满额");
			}
		} else {
			String status;
			if (product.state == BaseProduct.STATE_COMPLETE) {
				rlBuyRecord.setVisibility(View.GONE);
				if ("2".equals(product.repayment_status)) {
					status = "已还款";
				}else if ("0".equals(product.repayment_status)){
					status = "满额";
				} else {
					status = "还款中";
				}
			} else {
				rlBuyRecord.setVisibility(View.GONE);
				status = "满额";
			}
			tvBuy.setClickable(false);
			disableSubmitBtn(status);
			seling_money.setText("(剩余0元)");
		}
	}

	private void getFormatedTime(long seconds) {
		int hh = (int) (seconds / 3600);
		int mm = (int) ((seconds % 3600) / 60);
		int ss = (int) ((seconds % 3600) % 60);
		String h = hh < 10 ? ("0" + hh) : hh + "";
		String m =mm < 10 ? ("0" + mm) : mm + "";
		String s = ss < 10 ? ("0" + ss) : ss + "";
		buy_text.setText("("+h+":"+m+":"+s+"后开抢)");
	}

	/**
	 * 获取预售文字
	 *
	 * @return
	 */
	private String getSellText() {
		int days = TimeUtils.getDaysFromOnlineTime(product.online_time);
		String time1 = product.online_time.substring(11);
		String time2;
		if (days == 1) {
			time2 = "次日" + time1;
		} else {
			time2 = time1;
		}
		return time2;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {

			case R.id.activity_detail_payinfo_wrapper:
				if (productType == BaseProduct.TYPE_DQ) {
					toPayInfo();
				} else {
					toCashWay();
				}
				break;

			case R.id.activity_detail_huodong_wrapper:
				if (productType == BaseProduct.TYPE_DQ) {
					toHuoDongDetail();
				} else {
					toStartProfit();
				}
				break;

			case R.id.activity_detail_buyrecord_wrapper:
				toBuyRecord();
				break;

			case R.id.activity_detail_xmms:
				toProjDesc();
				break;

			case R.id.activity_detail_zjbz:
				toSecuInfo();
				break;

			case R.id.activity_detail_calc:
				toCalc();
				break;

			case R.id.activity_detail_buy:
				if (isAddCark && App.isLogin) {
					toBindBank();
				} else {
					toBuy();
				}
				break;

			case R.id.fxts :
				toWebInfo(getResources().getString(R.string.fxts),API.FENGXIAN);
				break;
			case R.id.mzsm :
				toWebInfo(getResources().getString(R.string.mzsm),API.MZSM);
				break;
			default:
				break;
		}
	}

	private void toWebInfo(String destination,String url) {
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra("title", destination);
		intent.putExtra("url", url);
		startActivity(intent);
	}

	/**
	 * 去添加银行卡
	 */
	private void toBindBank() {
		Intent intent = new Intent(context, BindActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (window != null && window.isShowing()) {
				window.dismiss();
				window = null;
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}


	/**
	 * 打开计算器
	 */
	private void toCalc() {
		final Calculator calc = new Calculator(context, product.ptype);
		int days = 0;
		if (product instanceof DQProduct) {
			DQProduct p = (DQProduct) product;
			days = SystemUtils.getDays(p.uistime, p.uietime);
		}
		calc.setDays(days);
		calc.setIncome(Float.parseFloat(product.income));
		calc.btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				calc.dialog.dismiss();
				hideInputMethod();
			}
		});
		Window dialogWindow=calc.dialog.getWindow();
		dialogWindow.setGravity(Gravity.BOTTOM);
		calc.showCalculator();
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = calc.dialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		calc.dialog.getWindow().setAttributes(lp);
	}

	/**
	 * 打开资金保障页面
	 */
	private void toSecuInfo() {
		if (product == null) {
			return;
		}
		Intent intent = new Intent(context, WebViewActivity.class);
		String title = "多重保障";
		intent.putExtra("title", title);
		intent.putExtra("cid", product.cid);
		intent.putExtra("type", TYPE_CAP);
		intent.putExtra("ptype", productType);
		startActivity(intent);
	}

	/**
	 * 打开项目描述页面
	 */
	private void toProjDesc() {
		if (product == null) {
			return;
		}
		Intent intent = new Intent(context, WebViewActivity.class);
		String title = "项目描述";
		intent.putExtra("title", title);
		intent.putExtra("cid", product.cid);
		intent.putExtra("type", TYPE_OBJ);
		intent.putExtra("ptype", productType);
		startActivity(intent);
	}

	/**
	 * 打开还款信息
	 */
	private void toPayInfo() {
		if (product == null) {
			return;
		}
		Intent intent = new Intent(context, PayInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("product", product);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 打开取现方式
	 */
	private void toCashWay() {
		Intent intent = new Intent(context, WebViewActivity.class);
		String title = "取现方式";
		intent.putExtra("title", title);
		String url = API.KH_CASH_WAY;
		intent.putExtra("url", url);
		startActivity(intent);
	}

	/**
	 * 打开起息时间
	 */
	private void toStartProfit() {
		Intent intent = new Intent(context, WebViewActivity.class);
		String title = "收益计算日";
		intent.putExtra("title", title);
		intent.putExtra("url", API.START_TIME);
		startActivity(intent);
	}

	/**
	 * 打开活动详情
	 */
	private void toHuoDongDetail() {
		DQProduct p = null;
		if (productType == BaseProduct.TYPE_DQ) {
			p = (DQProduct) product;
		}
		if (p == null) {
			return;
		}

		if (StringUtils.isBlank(p.activity_url)) {
			return;
		}

		if (p.text_url.length()==0){
			return;
		}

		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra("url", p.text_url);
		intent.putExtra("title", p.text_text);
		startActivity(intent);
	}

	/**
	 * 打开购买记录
	 */
	private void toBuyRecord() {
		Intent intent = new Intent(context, BuyRecordActivity.class);
		intent.putExtra("pid", product.pid);
		intent.putExtra("type", product.ptype);
		startActivity(intent);
	}

	/**
	 * 去购买
	 */
	private void toBuy() {
		if (!App.isLogin) {
			ToastUtils.show(context, "请先登录");
			Intent intent = new Intent(this, PhoneActivity.class);
			intent.putExtra("isBuy", true);
			intent.putExtra("origin", this.getClass().getSimpleName());
			startActivity(intent);
		} else {
			if (product != null && !product.isYuGao() && canbuy) {
				Intent intent = new Intent(context, BuyActivity.class);
				Bundle data = new Bundle();
				data.putSerializable("product", product);
				data.putString("pid", product.pid);
				data.putInt("productType", product.ptype);
				intent.putExtras(data);
				startActivity(intent);
			}
		}
	}

	@Override
	public void onRefresh() {
		loadProductDetail();
		if (product.getLeftSeconds() > 0) {
			Message message = handler.obtainMessage(1);
			handler.sendMessageDelayed(message, 1000);
		}
	}

	/**
	 * 刷新完毕
	 */
	private void refreshDone() {
		closeLoading();
		if (refresher != null) {
			refresher.setRefreshing(false);
		}
	}

	/**
	 * 注册或登录成功，去购买
	 *
	 * @param event
	 */
	public void onEventMainThread(BaseEvent event) {
		if (event != null) {
			if (this.getClass().getSimpleName().equals(event.origin)) {
				toBuy();
			}
		}
	}

	@Override
	public void scrollOritention(int oritention) {
		switch (oritention) {
			case SCROLL_DOWN:
				if (!bottomLayout.isShown()) {
					bottomLayout.startAnimation(show);
					bottomLayout.setVisibility(View.VISIBLE);
				}
				break;
			case SCROLL_UP:
				if (bottomLayout.isShown()) {
					bottomLayout.startAnimation(hidden);
					bottomLayout.setVisibility(View.GONE);
				}
				break;
		}
	}

	private void detailPopwindow() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.buy_detail_pop, null);

		window = new PopupWindow(view,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);

		window.setFocusable(true);

		window.setAnimationStyle(R.style.mypopwindow_anim_style);
		WindowManager.LayoutParams params = this.getWindow().getAttributes();
		params.alpha = 0.7f;
		this.getWindow().setAttributes(params);
		// 在底部显示
		window.showAtLocation(activity.findViewById(R.id.activity_detail_buy),
				Gravity.BOTTOM, 0, 0);
		ImageView cancel = (ImageView) view.findViewById(R.id.pop_cancel);
		TextView pname = (TextView) view.findViewById(R.id.detail_pop_name);
		TextView account = (TextView) view.findViewById(R.id.detail_pop_account);
		TextView blannce = (TextView) view.findViewById(R.id.detail_pop_blanncey);
		TextView paytype1 = (TextView) view.findViewById(R.id.detail_pop_paytype);
		TextView money1 = (TextView) view.findViewById(R.id.detail_pop_money);
		TextView sendmonry1 = (TextView) view.findViewById(R.id.detail_pop_sendmoney);
		TextView buymoney1 = (TextView) view.findViewById(R.id.detail_pop_buymoney);
		TextView buyok1 = (TextView) view.findViewById(R.id.detail_pop_ok);
		TextView xieyi = (TextView) view.findViewById(R.id.server_xieyi);
		TextView text = (TextView) view.findViewById(R.id.server_text);
		RelativeLayout yuelayout = (RelativeLayout) view.findViewById(R.id.detail_pop_yuelayout);

		if (productType == BaseProduct.TYPE_DQ){
			text.setVisibility(View.VISIBLE);
			xieyi.setVisibility(View.VISIBLE);
		}else {
			text.setVisibility(View.GONE);
			xieyi.setVisibility(View.GONE);
		}

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closePopupWindow();
			}
		});
		if (product != null) {
			double y = Double.parseDouble(App.userinfo.balance) * 100;
			int yue = (int) y;
			int m = (int) money * 100;
			pname.setText(product.pname);
			String phone = StringUtils.getSecretAccount(App.account);
			account.setText(phone);
			paytype1.setText(paytype == PAYTYPE_ACCOUNT ? "账户余额" + "(" + App.userinfo.balance + ")"
					: App.userinfo.identity.bankname + "(尾号" + App.userinfo.identity.cardno + ")");
			money1.setText(money + "元");
			//显示使用余额
			if (paytype == PAYTYPE_ACCOUNT) {
				yuelayout.setVisibility(View.VISIBLE);
				//判断金额
					if (money * 100 >= yue) {
						buymoney1.setText(((money * 100 - yue) / 100) + "元");
						blannce.setText("-" + (double) yue / 100 + "元");
					} else {
						buymoney1.setText("0.00元");
						blannce.setText("-" + money + "元");
					}

			} else {
				yuelayout.setVisibility(View.GONE);
				//判断金额
				buymoney1.setText(money + "元");
			}

			xieyi.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, WebViewActivity.class);
					intent.putExtra("title", "相关协议");
					intent.putExtra("pid", product.pid);
					startActivity(intent);
				}
			});

			buyok1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					closePopupWindow();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
					showPopwindow();
//                        }
//                    },500);
				}
			});
		}
		window.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams params = getWindow().getAttributes();
				params.alpha = 1f;
				getWindow().setAttributes(params);
			}
		});

	}

	/**
	 * 显示popupWindow
	 */
	private void showPopwindow() {
		// 利用layoutInflater获得View
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.payview_pop, null);

		// 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()

		window = new PopupWindow(view,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);

		// 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
		window.setFocusable(true);


		// 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        window.setBackgroundDrawable(dw);


		// 设置popWindow的显示和消失动画
		window.setAnimationStyle(R.style.mypopwindow_anim_style);
		WindowManager.LayoutParams params = this.getWindow().getAttributes();
		params.alpha = 0.7f;
		this.getWindow().setAttributes(params);
		// 在底部显示
		window.showAtLocation(activity.findViewById(R.id.activity_detail_buy),
				Gravity.BOTTOM, 0, 0);
		final PasswordView pass = (PasswordView) view.findViewById(R.id.payview);
		pass.setOnFinishInput(new OnPasswordInputFinish() {
			@Override
			public void inputFinish() {
//				closePopupWindow();
				password = pass.getStrPassword();
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						// 输入完成
						closePopupWindow();
						passmd5 = DigestUtils.md5(password).toUpperCase(Locale.CHINA);
						if (paytype == PAYTYPE_BANK) {
							// 如果付款方式是银行卡，先充值，再购买
							pay();
						} else {
							// 如果是账户余额支付，直接购买
							yuePay();
						}
					}
				}, 200);
			}
		});

		pass.getCancelImageView().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closePopupWindow();
			}
		});
		//popWindow消失监听方法
		window.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams params = getWindow().getAttributes();
				params.alpha = 1f;
				getWindow().setAttributes(params);
			}
		});

	}


	/**
	 * 关闭窗口
	 */
	private void closePopupWindow() {
		if (window != null && window.isShowing()) {
			window.dismiss();
			window = null;
		}
	}

	/**
	 * 余额不足，充值回调
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PAY_RESUEST_CODE) {
			if (data != null) {
				this.passmd5 = data.getStringExtra("passmd5");
				String payMoneyStr = data.getStringExtra("payMoney");
				double payMoney = SystemUtils.getDouble(payMoneyStr);
				handlePay(payMoney);
			}
		} else if (requestCode == PayPresenter.BAOFOO_REQUEST_CODE) {
			String result = "-1";
			String msg = "";
			if (data == null || data.getExtras() == null) {
				msg = "支付已被取消";
				Map<String, String> map = new HashMap<>();
				map.put("uid", App.loginData.uid);
				map.put("pay", "baofoo");
				map.put("retCode", result);
				map.put("retMsg", msg);
				MobclickAgent.onEvent(context, "payError", map);
			} else {
				Bundle bundle = data.getExtras();
				// -1:失败 0:取消 1:成功 10:处理中
				result = bundle.getString(BaofooPayActivity.PAY_RESULT);
				msg = bundle.getString(BaofooPayActivity.PAY_MESSAGE);
			}
			if ("1".equals(result)) {
				// 支付成功
				handlePay(money);
				Map<String, String> map = new HashMap<>();
				map.put("pay", "baofoo");
				MobclickAgent.onEvent(context, "paySuccess", map);
			} else {
				ToastUtils.show(context, msg);
				// 提交错误
				if (presenter != null) {
					String orderid = presenter.getTradeNo();
					presenter.postPayError(orderid, result, msg);
				}
				if ("-1".equals(result)) {
					// 支付失败
//                    if (presenter != null) {
//                        if(!TextUtils.isEmpty(msg)&&msg.contains("支付已被取消")){
//                        }else {
					presenter.showPayError(msg);
//                        }
//                    }
					Map<String, String> map = new HashMap<>();
					map.put("uid", App.loginData.uid);
					map.put("pay", "baofoo");
					map.put("retCode", result);
					map.put("retMsg", msg);
					MobclickAgent.onEvent(context, "payError", map);
				} else if ("0".equals(result)) {
//                    订单取消
					presenter.showPayError(msg);
					Map<String, String> map = new HashMap<>();
					map.put("uid", App.loginData.uid);
					map.put("pay", "baofoo");
					map.put("retCode", result);
					map.put("retMsg", msg);
					MobclickAgent.onEvent(context, "payError", map);
				}
			}
		} else if (requestCode == SmsCodeActivity.ACTION_REGIST_YEE_CHECK) {
			if (data != null) {
				boolean isSuccess = data.getBooleanExtra("isSuccess", false);
				if (isSuccess) {
					pay();
				}
			}
		}else if (requestCode == 233) {
//			if (!etMoney.getText().toString().trim().equals("")	&&App.isLogin) {
//				submit();
//			}
		} else ;
	}

	/**
	 * 处理充值完成
	 */
	private void handlePay(double payMoney) {
		if (payMoney >= shouldPay) {
			if (!activity.isFinishing()) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						showLoading("正在处理");
						buyProduct();
					}
				}, 500);
			}
		} else {
			updateButton();
		}
	}

	/**
	 * 购买成功，退出Activity
	 *
	 * @param event
	 */
	public void onEventMainThread(BuySuccessEvent event) {
		finish();
	}

	//解析富友返回xml
	public void parse(String protocolXML, String code, String order) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder
					.parse(new InputSource(new StringReader(protocolXML)));

			Element root = doc.getDocumentElement();
			NodeList books = root.getChildNodes();
			if (books != null) {
				for (int i = 0; i < books.getLength(); i++) {
					Node book = books.item(i);
					if (i == 1) {
						fuiouCode = book.getFirstChild().getNodeValue() + "";

					}
					if (i == 3) {
						orderid = book.getFirstChild().getNodeValue() + "";
					}


				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		initData();
		presenter.bind(this);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				updateView();
				updateButton();
			}
		}, 300);
	}


	public void onEventMainThread(PassEvent event) {
		showPassError(event.msg);
	}


	@Override
	public void requestSuccess(String paramString) {

	}

	@Override
	public void requestFailed(String paramString) {

	}
}
