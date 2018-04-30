package cn.app.yimirong.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baofoo.sdk.vip.BaofooPayActivity;
import com.bumptech.glide.Glide;

import com.fuiou.mobile.util.AppConfig;
import com.umeng.analytics.MobclickAgent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
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
import cn.app.yimirong.event.custom.PassEvent1;
import cn.app.yimirong.event.custom.UserInfoUpdateEvent;
import cn.app.yimirong.model.base.BaseModel;
import cn.app.yimirong.model.bean.Bank;
import cn.app.yimirong.model.bean.BaseProduct;
import cn.app.yimirong.model.bean.BuyResult;
import cn.app.yimirong.model.bean.DQProduct;
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
import cn.app.yimirong.utils.ToastUtils;
import cn.app.yimirong.view.OnPasswordInputFinish;
import cn.app.yimirong.view.PasswordView;
import cn.app.yimirong.view.PayView;
import cn.app.yimirong.view.PayView.OnInputListener;

public class BuyActivity extends BaseActivity implements OnClickListener ,MyFyPayCallBack{
	private PayPresenter payPresenter;

	public static final int PAYTYPE_ACCOUNT = 1;

	public static final int PAYTYPE_BANK = 2;

	public static final int PAY_RESUEST_CODE = 100;

	// 添加银行卡
	private LinearLayout addBankWrapper;

	// 付款方式
	private LinearLayout payTypeWrapper;

	// 产品名称
	private TextView tvPName;

	// 期限
	private TextView tvQiXian;

	// 年化利率
	private TextView tvLiLv;

	// 起息
	private TextView tvQiXi;

	private TextView balaceTxt;

	private ImageView bankIcon;

	private RelativeLayout rlEdu;

	private TextView tvEdu;

	private RelativeLayout rlWay1;

	private RelativeLayout rlWay2;

	private PopupWindow window;

	// 方式1
	private TextView tvType1;

	// 方式2
	private TextView tvType2;

	// 金额
	private EditText etMoney;
	private double money;
	private ImageButton ibDelete;

	// 方式1
	private CheckBox cbType1;

	// 方式2
	private CheckBox cbType2;

	private String password;

	private boolean isBalance = false;

	private Button btnSubmit;

	private PayView payView;

	protected String pid;

	private int paytype;

	private double moneyLimit = 0;
	private Integer moneyInteger;
	private double startMoney = 0;

	private double leftMoney;

	private int passError = 3;

	private int productType;

	private BaseProduct product;

	private LinearLayout profitWrapper;

	private TextView tvProfit;

	private double yuqiProfit = 0.00f;

	private String passmd5;

	private double shouldPay;

	private boolean isUserCanBuy = false;
	private Editable buyMoney;
	private Bank bank;

	private PayPresenter presenter;
	//富友返回结果验证码
	public String fuiouCode;
	//商户订单号
	public String orderid;
	//富友sdk

	public int single;

	public int singleDay;

	public int singleMonth;
	private TextView kegoujine;
	private boolean yuebuzu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		payPresenter = new PayPresenter();
	}

	@Override
	protected void onStop() {
		if (payView != null && payView.isShowing) {
			payView.dismiss();
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		presenter.unBind();
		super.onDestroy();
	}

	@Override
	public void loadView() {
		setContentView(R.layout.activity_buy);
		presenter = new PayPresenter();
		presenter.bind(this);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("立即抢购");

		addBankWrapper = (LinearLayout) findViewById(R.id.activity_buy_add_bank_wrapper);
		addBankWrapper.setOnClickListener(this);
		kegoujine = (TextView) findViewById(R.id.kegoujine);
		profitWrapper = (LinearLayout) findViewById(R.id.activity_buy_profit_wrapper);
		tvProfit = (TextView) findViewById(R.id.activity_buy_profit);

		payTypeWrapper = (LinearLayout) findViewById(R.id.activity_buy_pay_way_wrapper);

		balaceTxt = (TextView) findViewById(R.id.activity_buy_payway1_balance);

		bankIcon = (ImageView) findViewById(R.id.bank_img);

		tvPName = (TextView) findViewById(R.id.activity_buy_name);

		tvQiXian = (TextView) findViewById(R.id.activity_buy_days);

		tvLiLv = (TextView) findViewById(R.id.activity_buy_lilv);

		tvQiXi = (TextView) findViewById(R.id.activity_buy_start_day);

		etMoney = (EditText) findViewById(R.id.activity_buy_money);
//		ibDelete = (ImageButton) findViewById(R.id.activity_buy_delete);
//		ibDelete.setOnClickListener(this);
		etMoney.addTextChangedListener(new TextWatcher() {


			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				buyMoney = s;
				if (product != null && "1".equals(product.status)) {
					if (!s.toString().isEmpty()) {

						String substring = s.toString().substring(0, 1);
						if (substring.equals("0")) {
							etMoney.setText("");
							return;
						}
						int moneyMax = new Integer(product.money_max).intValue();

						int buyMoney = new Integer(s.toString().trim()).intValue();

						int canBeInvest = new Double(leftMoney).intValue();

						if (canBeInvest > moneyMax && buyMoney > moneyMax) {
							etMoney.setText(moneyMax + "");
							return;
						} else if (canBeInvest < moneyMax && canBeInvest < buyMoney) {
							etMoney.setText(canBeInvest + "");
							return;
						} else if (buyMoney > moneyMax) {
							etMoney.setText(moneyMax + "");
							return;
						}
					}

					if (s != null && s.length() > 0) {
//						ibDelete.setVisibility(View.VISIBLE);
						double yue = 0;
						if (App.userinfo != null
								&& App.userinfo.balance != null) {
							yue = Double.parseDouble(App.userinfo.balance);
						}
						double tmpmoney = Double.parseDouble(s.toString());
						calcProfit(tmpmoney);
						if (tmpmoney > yue && paytype == PAYTYPE_ACCOUNT) {
							String left = SystemUtils.getDoubleStr(tmpmoney - yue);
							enableSubmitBtn("余额不足,使用银行卡支付");
						} else {
							enableSubmitBtn("提交");
						}
					} else {
//						ibDelete.setVisibility(View.GONE);
						calcProfit(0.00f);
						disableSubmitBtn("提交");
					}
				}
				Selection.setSelection(s,s.length());
			}
		});

		rlEdu = (RelativeLayout) findViewById(R.id.activity_buy_edu_wrapper);

		tvEdu = (TextView) findViewById(R.id.activity_buy_edu);

		TextView tvAddEDu = (TextView) findViewById(R.id.activity_buy_edu_add);
		tvAddEDu.setOnClickListener(this);

		rlWay1 = (RelativeLayout) findViewById(R.id.activity_buy_payway_wrapper1);

		rlWay2 = (RelativeLayout) findViewById(R.id.activity_buy_payway_wrapper2);

		tvType1 = (TextView) findViewById(R.id.activity_buy_payway1);

		tvType2 = (TextView) findViewById(R.id.activity_buy_payway2);

		cbType1 = (CheckBox) findViewById(R.id.activity_buy_cb_way1);
		cbType2 = (CheckBox) findViewById(R.id.activity_buy_cb_way2);
		if (cbType1.isChecked()) {
			paytype = PAYTYPE_ACCOUNT;
		} else {
			paytype = PAYTYPE_BANK;
		}
		cbType1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				if (isChecked) {
					paytype = PAYTYPE_ACCOUNT;
					updateButton();
				} else {
					paytype = PAYTYPE_BANK;
					updateButton();
				}
			}
		});


		btnSubmit = (Button) findViewById(R.id.activity_buy_submit);
		btnSubmit.setOnClickListener(this);

		setProfit();

		disableSubmitBtn("提交");

		initPayType();


		initPayView();

		updatePayType();
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
			yuqiProfit = tmpmoney * days * (income / 100.0) / 365.0;
			setProfit();
		}
	}

	private void setProfit() {
		String str = "<font color='#eb515e'>"
				+ SystemUtils.getDoubleStr(yuqiProfit) + "</font>元";
		tvProfit.setText(Html.fromHtml(str));
	}

	@Override
	public void initData() {
		if (App.userinfo != null) {
			UserInfo userinfo = App.userinfo;
			if (userinfo.identity != null
					&& userinfo.identity.bankid != null) {
				bank = BankDao.getInstance(context)
						.query(userinfo.identity.bankid);
			}
		}
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		if (data != null) {
			pid = data.getString("pid");
			productType = data.getInt("productType", BaseProduct.TYPE_NONE);
			product = (BaseProduct) data.getSerializable("product");
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
			rlEdu.setVisibility(View.VISIBLE);
			if (App.userinfo != null) {
				String str = SystemUtils.getDoubleStr(App.userinfo.canBuyLong);
				tvEdu.setText(str);
			}
		} else if (productType == BaseProduct.TYPE_DQ
				|| productType == BaseProduct.TYPE_KL) {
			setTitleRight(false, null);
			rlEdu.setVisibility(View.GONE);
		} else {
			ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
			finish();
		}
		// 获取产品详情
		loadProductDetail();
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

	/**
	 * 获取产品详情
	 */
	private void loadProductDetail() {
		if (product != null) {
			//先更新界面
			updateView();
		}
		if (pmodel != null) {
			//再加载数据
			showLoading("玩命向钱冲");
			pmodel.getProductDetail(productType, pid, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null && t instanceof BaseProduct) {
						product = (BaseProduct) t;
						updateView();
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					ToastUtils.show(context, msg);
				}
			});
		}
	}

	/**
	 * 更新界面
	 */
	public void updateView() {
		if (product != null) {
			tvPName.setText(product.pname);
			tvLiLv.setText(SystemUtils.getDoubleStr(product.income));
			etMoney.setHint(product.startmoney + "元起投" + " "
					+ product.money_limit + "元累加");
			kegoujine.setText("可投金额 "+leftMoney+"元");
			double allmoney = Double.parseDouble(product.money);
			double sellmoney = Double.parseDouble(product.sellmoney);
			leftMoney = allmoney - sellmoney;

			this.moneyLimit = Double.parseDouble(product.money_limit);
			this.startMoney = Double.parseDouble(product.startmoney);

			if (product instanceof DQProduct) {
				profitWrapper.setVisibility(View.VISIBLE);
				DQProduct p = (DQProduct) product;
				int days = SystemUtils.getDays(p.uistime, p.uietime);
				tvQiXian.setText(days + "天");
				tvQiXi.setText(p.uistime + "起息");
			} else {
				profitWrapper.setVisibility(View.GONE);
				tvQiXian.setText("随时可转");
				tvQiXi.setText("当日起息");
			}

			if (isUserCanBuy) {
				if ("1".equals(product.status)) {
					String moneyStr = etMoney.getText().toString().trim();
					if (StringUtils.isBlank(moneyStr)) {
						disableSubmitBtn("提交");
					} else {
						enableSubmitBtn("提交");
					}

				} else {
					disableSubmitBtn("满额");
				}
			} else {
				disableSubmitBtn("请添加银行卡");
			}
		} else {
			disableSubmitBtn("提交");
		}
	}

	@Override
	public void updateData(Bundle data) {
		super.updateData(data);
        buyProduct();
	}

	/**
	 * 禁用提交按钮
	 */
	private void disableSubmitBtn(String text) {
		btnSubmit.setText(text);
		btnSubmit.setClickable(false);
		btnSubmit.setBackgroundResource(R.drawable.shape_bg_dark_gray_normal);
	}

	/**
	 * 开启提交按钮
	 */
	private void enableSubmitBtn(String text) {
		if ("余额不足,使用银行卡支付".equals(text)) {
			yuebuzu = true;
		} else {
			yuebuzu = false;
		}
		btnSubmit.setText(text);
		btnSubmit.setClickable(true);
		btnSubmit.setBackgroundResource(R.drawable.selector_bg_red_btn);
	}

	/**
	 * 初始化支付方式
	 */
	private void initPayType() {
		rlWay1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cbType1.setChecked(true);
				cbType2.setChecked(false);
			/*	if (!isBalance) {
					paytype = PAYTYPE_BANK;
				} else {
					paytype = PAYTYPE_ACCOUNT;
				}*/
//				updateButton();
			}
		});

		rlWay2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cbType1.setChecked(false);
				cbType2.setChecked(true);
		/*		if (!isBalance) {
					paytype = PAYTYPE_ACCOUNT;
				} else {
					paytype = PAYTYPE_BANK;
				}*/
//				updateButton();
			}
		});

		cbType1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cbType1.setChecked(true);
				cbType2.setChecked(false);
/*				if (!isBalance) {
					paytype = PAYTYPE_BANK;
				} else {
					paytype = PAYTYPE_ACCOUNT;
				}*/
//				updateButton();
			}
		});

		cbType2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cbType2.setChecked(true);
				cbType1.setChecked(false);
	/*			if (!isBalance) {
					paytype = PAYTYPE_ACCOUNT;
				} else {
					paytype = PAYTYPE_BANK;
				}*/
//				updateButton();
			}
		});

		cbType1.setChecked(true);
		cbType2.setChecked(false);
	}

	/**
	 * 更新按钮
	 */
	private void updateButton() {
		if (product != null) {
			if ("1".equals(product.status)) {
				String moneyStr = etMoney.getText().toString().trim();
				if (StringUtils.isBlank(moneyStr)) {
					disableSubmitBtn("提交");
				} else {
					double yue = 0.00f;
					if (App.userinfo != null && App.userinfo.balance != null) {
						yue = Double.parseDouble(App.userinfo.balance);
					}
					double tmpmoney = Double.parseDouble(moneyStr.toString());
					if (tmpmoney > yue && paytype == PAYTYPE_ACCOUNT) {
						String left = SystemUtils.getDoubleStr(tmpmoney - yue);
//						enableSubmitBtn("余额不足,请充值" + left + "元");
						enableSubmitBtn("余额不足,使用银行卡支付");

					} else {
						enableSubmitBtn("提交");
					}
				}
			} else {
				disableSubmitBtn("满额");
			}
		} else {
			disableSubmitBtn("提交");
		}
	}

	/**
	 * 初始化支付界面
	 */
	private void initPayView() {
		payView = new PayView(context);
		payView.setOnInputListener(new OnInputListener() {
			@Override
			public void onInputDone(String password) {
				// 密码输入完毕
				inputDone(password);
			}
		});
	}


	/**
	 * 返回订单相关信息(手动退出)
	 */
	@Override
	protected void onStart() {

//        Log.i("TAG",
//                "应答码："
//                        + AppConfig.getData(this,
//                        AppConfig.RSP_CODE));
//        Log.i("TAG",
//                "描述："
//                        + AppConfig.getData(this,
//                        AppConfig.RSP_DESC));
//        /**
//         * 请求发送成功的返回数据 发起支付
//         */
//        Log.i("TAG",
//                "发送成功请求的返回数据："
//                        + AppConfig.getData(this,
//                        AppConfig.RSP_SDK_DATA));

		if (!TextUtils.isEmpty(AppConfig.getData(this,
				AppConfig.RSP_SDK_DATA)) && !AppConfig.getData(this,
				AppConfig.RSP_SDK_DATA).equals("数据被篡改")) {

			String data = AppConfig.getData(this,
					AppConfig.RSP_SDK_DATA);
			String gab = data.substring(data.indexOf("?") - 2, data.indexOf("R") - 1);
			int gabLen = gab.length();
			int dataLen = data.length();
			String xml = data.substring(gabLen, dataLen - 1);

			Log.i(TAG, "onStart: xmlList" + xml);
			parse(xml, fuiouCode, orderid);


			if (fuiouCode != null && fuiouCode.equals("0000")) {
				presenter.queryfResult(orderid);
			} else if (fuiouCode != null && fuiouCode.equals("200017")) {
				presenter.showPayError("银行卡账户余额不足");
			} else if (fuiouCode != null && fuiouCode.equals("10SM")) {
				presenter.showPayError("超过金额限制");
			} else if (fuiouCode != null && fuiouCode.equals("10FC")) {
//                ToastUtils.show(this, "充值失败");
				presenter.showPayError("支付失败");
			} else {
				Log.i(TAG, "onStart: fuiouCode" + fuiouCode);
				presenter.showPayError("支付失败");
			}

		}
		reset();
		super.onStart();
	}

	/**
	 * 清除数据
	 */
	private void reset() {
		AppConfig.setData(this, AppConfig.RSP_CODE, "");
		AppConfig.setData(this, AppConfig.RSP_DESC, "");
		AppConfig.setData(this, AppConfig.RSP_SDK_DATA, "");
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
//            pay();
		} else {
			// 如果是账户余额支付，直接购买
//            buyProduct();
		}
	}

//    /**
//     * 先充值
//     */
//    private void pay() {
//        if (presenter != null) {
//            presenter.pay(bank, money, passmd5 ,this);
//        }
//    }

	/**
	 * 购买产品
	 */
	public void buyProduct() {
		if (yuebuzu) {
			bankPay();return;
		}
		if (pmodel != null) {
			showLoading("正在处理");
			pmodel.buyProduct(productType, pid, money, paytype, passmd5, null, new ResponseHandler() {
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
						showPassError();
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
	 * 处理密码错误
	 */
	private void showPassError() {
		passError--;
		if (passError > 0) {
			String msg = "密码错误，您还有" + passError + "次输入机会";
			PromptUtils.showDialog2(activity, context, "提示", msg, "重新输入",
					"找回密码", new PromptUtils.OnDialogClickListener2() {
						@Override
						public void onLeftClick(Dialog dialog) {
							submit();
						}

						@Override
						public void onRightClick(Dialog dialog) {
							toFindPass();
						}
					});
		} else {
			App.isPayLocked = true;
			showPassLocked();
		}
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
				etMoney.setEnabled(true);
				disableSubmitBtn("提交");
				isUserCanBuy = true;
			} else {
				showAddBank();
				etMoney.setEnabled(false);
				disableSubmitBtn("请添加银行卡");
				isUserCanBuy = false;
			}
		} else {
			ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
			finish();
		}
	}

	private void sortPayType() {
		if (App.userinfo != null && App.userinfo.balance != null) {
			double userBalance = (double) Double
					.parseDouble(App.userinfo.balance);
			balaceTxt.setText(App.userinfo.balance + "元");
			tvType2.setText(App.userinfo.identity.nameCard);
			String url = API.BANK_ICON_BASE + App.userinfo.identity.bankid + ".jpg";
			Glide.with(context)
					.load(url)
					.into(bankIcon);
			if (userBalance > 0) {
				// 用户余额大于0，账户余额支付放在上面
				isBalance = true;
//				paytype = PAYTYPE_ACCOUNT;
				cbType1.setChecked(true);
				cbType2.setChecked(false);
			} else {
				// 用户余额为0，银行支付放在上面
				isBalance = false;
//				paytype = PAYTYPE_BANK;
				cbType1.setChecked(false);
				cbType2.setChecked(true);
			}
		}
	}

	/**
	 * 显示添加银行卡
	 */
	private void showAddBank() {
		payTypeWrapper.setVisibility(View.GONE);
		addBankWrapper.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏添加银行卡
	 */
	private void hideAddBank() {
		addBankWrapper.setVisibility(View.GONE);
		payTypeWrapper.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (payView != null && payView.isShowing) {
				payView.dismiss();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
//			case R.id.activity_buy_delete:
//				etMoney.setText("");
//				break;
			case R.id.activity_buy_submit:
				submit();
				break;

			case R.id.activity_buy_add_bank_wrapper:
				toBindBank();
				break;

			case R.id.activity_buy_edu_add:
				toAddEdu();
				break;

			default:
				break;
		}
	}

	/**
	 * 提升额度
	 */
	private void toAddEdu() {
		toBuyShuoMing();
	}

	/**
	 * 去添加银行卡
	 */
	private void toBindBank() {
		Intent intent = new Intent(context, BindActivity.class);
		startActivity(intent);
	}

	/**
	 * 获取用户输入
	 *
	 * @return
	 */
	private boolean getInput() {
		String moneyStr = etMoney.getText().toString().trim();
		if (StringUtils.isBlank(moneyStr)) {
			ToastUtils.show(context, "请输入购买金额");
			return false;

		} else {
			try {
				money = Double.parseDouble(moneyStr);
			} catch (NumberFormatException e) {
				ToastUtils.show(context, "请输入购买金额");
				etMoney.setText("");
				return false;
			}

			if (money < startMoney) {
				ToastUtils.show(context, "起投金额：" + startMoney + "元");
				return false;
			}

			if (money < 1) {
				ToastUtils.show(context, "购买金额必须大于1");
				return false;
			}
			if (moneyLimit != 0) {
				if ((int) (money - startMoney) % (int) moneyLimit != 0) {
					ToastUtils.show(context, "累加金额:" + moneyLimit + "元");
					return false;
				}
			}

			if (money > leftMoney) {
				ToastUtils.show(context, "产品可购余额不足");
				return false;
			}

			if (productType == BaseProduct.TYPE_KH) {
				UserInfo userinfo = App.userinfo;
				if (userinfo != null) {
					if (money > userinfo.canBuyLong) {
						ToastUtils.show(context, "购买额度不足，请先购买产品提升额度");
						return false;
					}
				} else {
					ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
					finish();
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 提交，显示支付界面
	 */
	private void submit() {
		hideInputMethod();
		if (!getInput()) {
			return;
		}
		if (App.isPayLocked) {
			// 密码被锁定
			showPassLocked();
			return;
		}

		if (App.userinfo != null) {
			UserInfo userinfo = App.userinfo;
			if (userinfo.identity != null) {
				// 已绑卡
				if (userinfo.identity.tpwd) {
					// 已设置交易密码
					// 校验账户余额
				/*	double yue = Double.parseDouble(App.userinfo.balance);
					if (paytype == PAYTYPE_ACCOUNT && money > yue) {
						// 账户余额支付，余额不足，去充值
						shouldPay = SystemUtils.getDouble(money - yue);
						bankPay();
						return;
					}*/
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
						} else {
							showPopwindow(paytype);
						}
					}
					showPopwindow(paytype);

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
	 * 余额不足，去充值
	 *
	 * @param
	 */
	private void toPay(double shouldPay) {
	/*	Intent intent = new Intent(context, PayActivity.class);
		intent.putExtra("isBuy", true);
		intent.putExtra("payMoney", shouldPay);
		startActivityForResult(intent, PAY_RESUEST_CODE);*/
//	    bankPay();
	}

	/**
	 * 处理支付成功
	 */
	private void buySuccess(final BuyResult result) {
		btnSubmit.setClickable(false);
		final Dialog dialog = PromptUtils.showSuccessDialog(this, context,
				"购买成功");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
			}
		}, 1000);
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
			Intent intent = new Intent(context, BuyResultActivity.class);
			intent.putExtra("pname", product.pname);
			intent.putExtra("money", result.cost);
			intent.putExtra("paytype", paytype == PAYTYPE_ACCOUNT ? "账户余额支付"
					: "银行卡支付");
			intent.putExtra("trxid", result.trxid);
			Bundle bundle = new Bundle();
			bundle.putSerializable("result", result);
			intent.putExtras(bundle);
			startActivity(intent);
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
//                    pay();
				}
			}
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
//                        buyProduct();
					}
				}, 500);
			}
		} else {
			updateButton();
		}
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

	private void showPopwindow(final int paytype) {

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
		window.showAtLocation(activity.findViewById(R.id.activity_buy_submit),
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
							bankPay();
//							ToastUtils.show(context,paytype+"");
						} else {
							buyProduct();
//							ToastUtils.show(context,paytype+"");
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
//pmodel.buyProduct(productType, pid, money, paytype, passmd5, null, new ResponseHandler()
	private void bankPay() {
        if (presenter != null) {
			if (bank!=null&&money>0&&!passmd5.isEmpty()) {
				WindowManager windowManager = getWindowManager();
				Display display = windowManager.getDefaultDisplay();
				presenter.pay(display);
				presenter.pay(bank, money, passmd5, null, null, this , BuyActivity.this);
			}
        }
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

    @Override
    public void requestSuccess(String paramString) {
		if (paramString.contains("成功")) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					buyProduct();
				}
			}, 1000);
		} else {
			if (paramString.contains("余额不足")) {
				PromptUtils.showDialog1(this, context, "提示：", "余额不足", "确定", new PromptUtils.OnDialogClickListener1() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
					}
				});
			} else {
				PromptUtils.showDialog1(this, context, "提示：", "支付失败", "确定", new PromptUtils.OnDialogClickListener1() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
					}
				});
			}

		}
	}

    @Override
    public void requestFailed(String paramString) {
		Log.i(TAG, "requestSuccess: "+paramString);
		PromptUtils.showDialog1(this,context,"提示：","支付失败","确定",new PromptUtils.OnDialogClickListener1(){
			@Override
			public void onClick(Dialog dialog) {
				dialog.dismiss();
			}
		});
    }
	public void onEventMainThread(PassEvent1 event) {
		showPassError();
	}
}
