package cn.app.yimirong.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baofoo.sdk.vip.BaofooPayActivity;
import com.fuiou.mobile.FyPay;
import com.lidroid.xutils.BitmapUtils;
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
import cn.app.yimirong.event.custom.PassEvent;
import cn.app.yimirong.event.custom.UserInfoEvent;
import cn.app.yimirong.model.bean.Bank;
import cn.app.yimirong.model.bean.UserCD;
import cn.app.yimirong.model.bean.UserInfo;
import cn.app.yimirong.model.db.dao.BankDao;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.presenter.MyFyPayCallBack;
import cn.app.yimirong.presenter.impl.PayPresenter;
import cn.app.yimirong.utils.DigestUtils;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.ToastUtils;
import cn.app.yimirong.view.OnPasswordInputFinish;
import cn.app.yimirong.view.PasswordView;
import cn.app.yimirong.view.PayView;

/**
 * 充值
 *
 * @author android
 */
public final class PayActivity extends BaseActivity implements MyFyPayCallBack{

	private ImageView ivBankIcon;
	private TextView tvPayCD;
	private TextView tvBankName;
	private EditText etMoneyNum;
	private Button btnOK;

	private TextView tvSingle;

	// 充值金额
	private double money;

	private PayView payview;

	private int cd = 0;

	private boolean isBuy = false;

	private double payMoney;

	private String passmd5;

	private Bank bank;

	private PayPresenter presenter;


	public String fuiouCode;

	public String orderid;

	private int single;

	private PopupWindow window;

	private String password;

	private String cid = null;

	private String ptid = null;

	private UserCD usercd;
	private String TAG = "PayActivity";
	@Override
	public void loadView() {
		setContentView(R.layout.activity_pay);
		EventBus.getDefault().register(this);
		presenter = new PayPresenter();
		presenter.bind(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		presenter.unBind();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("充值");

        ivBankIcon = (ImageView) findViewById(R.id.activity_pay_bank_icon);
		tvBankName = (TextView) findViewById(R.id.activity_pay_bank_name);
		etMoneyNum = (EditText) findViewById(R.id.activity_pay_money_num);
//        SpannableString ss = new SpannableString("请输入金额");//定义hint的值
//        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(17,true);//设置字体大小 true表示单位是sp
//        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        etMoneyNum.setHint(new SpannedString(ss));
//        etMoneyNum.setHintTextColor(Color.parseColor("#cccccc"));
		etMoneyNum.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				cd = 0;
				if (usercd != null) {
					cd = usercd.pay;
				}
					if (cd <= 0) {
						disableSubmitBtn();
						return;
					}else {
						enableSubmitBtn();
					}

				String tmpStr = s.toString();
				int dotPos = tmpStr.indexOf(".");
				if (s.length() == 0) {
					btnOK.setClickable(false);
				} else {
					btnOK.setClickable(true);
				}
				if (dotPos <= 0) {
					return;
				}
				if (tmpStr.length() - dotPos - 1 > 2) {
					// 小数点后位数大于2，删除多余位数
					s.delete(dotPos + 3, dotPos + 4);
				}
			}
		});
		tvPayCD = (TextView) findViewById(R.id.activity_pay_cd);
		tvSingle = (TextView) findViewById(R.id.activity_pay_single);
		btnOK = (Button) findViewById(R.id.activity_pay_btn_ok);
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submit();
			}
		});
		disableSubmitBtn();
        initPayView();
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		isBuy = intent.getBooleanExtra("isBuy", false);
		payMoney = intent.getDoubleExtra("payMoney", -1f);
		if (payMoney != -1f) {
			etMoneyNum.setText(payMoney + "");
		}
		setBankInfo();
		loadUserCD();
	}

	@Override
	protected void onStop() {
		if (payview != null && payview.isShowing) {
			payview.dismiss();
		}
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (window != null && window.isShowing()) {
				window.dismiss();
				window = null;
				WindowManager.LayoutParams params = this.getWindow().getAttributes();
				params.alpha = 1f;
				this.getWindow().setAttributes(params);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}


	protected void initPayView() {
		payview = new PayView(context);
		payview.setOnInputListener(new PayView.OnInputListener() {
			@Override
			public void onInputDone(String password) {
				// 密码输入完毕
				inputDone(password);
			}
		});
	}

	/**
	 * 支付密码输入完毕
	 *
	 * @param password
	 */
	protected void inputDone(String password) {
		passmd5 = DigestUtils.md5(password).toUpperCase(Locale.CHINA);
//        logger.i("TAG","    "+passmd5);
		// 支付
		pay();
	}

	private void pay() {
		if (presenter != null) {
			WindowManager windowManager = getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			presenter.pay(display);
			presenter.pay(bank, money, passmd5, cid, ptid, this , PayActivity.this);
		}
	}

	/**
	 * 获取用户充值次数
	 */
	private void loadUserCD() {
		if (amodel != null) {
			showLoading("玩命向钱冲");
			amodel.getUserCD(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null && t instanceof UserCD) {
						usercd = (UserCD) t;
						updateView(usercd);
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
	 *
	 * @param usercd
	 */
	protected void updateView(UserCD usercd) {
		if (usercd != null) {
			tvPayCD.setText(usercd.pay + "");
			cd = usercd.pay;
			if (cd <= 0) {
				disableSubmitBtn();
			}
		}
	}

	@Override
	public void updateData(Bundle data) {
		super.updateData(data);
		paySuccess();
	}

	/**
	 * 使能按钮
	 */
	protected void enableSubmitBtn() {
		btnOK.setClickable(true);
        btnOK.setBackgroundResource(R.drawable.selector_bg_red_btn);

	}

	/**
	 * 禁用按钮
	 */
	protected void disableSubmitBtn() {
		btnOK.setClickable(false);
        btnOK.setBackgroundResource(R.drawable.shape_bg_dark_gray_normal);
	}

	/**
	 * 设置银行卡信息
	 */
	protected void setBankInfo() {
		UserInfo userinfo = null;
		userinfo = App.userinfo;
		if (userinfo != null && userinfo.identity != null) {
			tvBankName.setText(userinfo.identity.nameCard);
			String url = API.BANK_ICON_BASE + userinfo.identity.bankid + ".jpg";
			bank = BankDao.getInstance(context)
					.query(userinfo.identity.bankid);
			tvSingle.setText(StringUtils.getSingleInfo(bank));
            BitmapUtils bitmapUtils = new BitmapUtils(context);
            bitmapUtils.configDefaultLoadFailedImage(R.drawable.yinhangbeijing);
            bitmapUtils.display(ivBankIcon, url);
		}
	}

	/**
	 * 显示成功
	 */
	public void paySuccess() {
		btnOK.setClickable(false);
		EventBus.getDefault().post(new UserInfoEvent());
		final Dialog dialog = PromptUtils.showSuccessDialog(
				PayActivity.this, context, "充值成功");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
			}
		}, 1000);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (isBuy) {
					Intent data = new Intent();
					data.putExtra("payMoney", money + "");
					data.putExtra("passmd5", passmd5);
					setResult(100, data);
					finish();
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							toResult();
						}
					});
				}
			}
		}, 1200);
	}

	/**
	 * 提交
	 */
	private void submit() {
		hideInputMethod();

		if (!getInput()) {
			return;
		}
		UserInfo userinfo = App.userinfo;
		if (userinfo != null && userinfo.identity != null) {
//            payview.setMoney(money);
//            payview.setBankName(userinfo.identity.nameCard);
//            payview.setBankIcon(API.BANK_ICON_BASE + userinfo.identity.bankid
//                    + ".jpg");
//            payview.show(getWindow().getDecorView());
			showPopwindow();
		}
	}

	/**
	 * 获取输入
	 *
	 * @return
	 */
	private boolean getInput() {
		String moneyStr = etMoneyNum.getText().toString().trim();
		if (StringUtils.isBlank(moneyStr)) {
			ToastUtils.show(context, "请输入充值金额");
			return false;
		} else {
			try {
				money = Double.parseDouble(moneyStr);
			} catch (Exception e) {
				etMoneyNum.setText("");
				return false;
			}

			if (money <= 0) {
				ToastUtils.show(context, "请输入充值金额");
				etMoneyNum.setText("");
				return false;
			} else {
				if (money < 1) {
					ToastUtils.show(context, "充值金额必须大于1");
					etMoneyNum.setText("");
					return false;
				}
				if (money < 2 && bank.name.equals("招商银行") && bank.plat.equals("fuiou")) {
					ToastUtils.show(this, "招商银行卡支付必须不少于2元");
					return false;
				}
				if (presenter.getPlatList() != null && presenter.getPlatList().size() > 0 && !presenter.getPlatList().contains(bank.plat)) {
					single = BankDao.getInstance(context).getPlatSingle(presenter.getPlatList().get(0), bank.code);
				} else {
					single = BankDao.getInstance(context).getPlatSingle(bank.plat, bank.code);
				}
				if (bank != null) {
					if (money > single) {
						showQuotaInfo();
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 显示限额信息
	 */
	private void showQuotaInfo() {
		String message = bank.name + "单笔限额" + bank.single;
		PromptUtils.showDialog1(activity, context, "提示", message, "确定",
				new PromptUtils.OnDialogClickListener1() {
					@Override
					public void onClick(Dialog dialog) {
					}
				});
	}

	/**
	 * 去结果详情
	 */
	protected void toResult() {
		Intent intent = new Intent(context, PayResultActivity.class);
		intent.putExtra("money", money);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PayPresenter.BAOFOO_REQUEST_CODE) {
			String result = "-1";
			String msg;
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
				logger.i(TAG, "baofoo result:" + result + " msg:" + msg);
			}
			if ("1".equals(result)) {
				// 支付成功
				toResult();
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
					// 支付失败或订单取消
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
			//易宝注册成功
			if (data != null) {
				boolean isSuccess = data.getBooleanExtra("isSuccess", false);
				if (isSuccess) {
					pay();
				}
			}
		} else ;
	}

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


					System.out.println("节点=" + book.getNodeName() + "\ttext="
							+ book.getFirstChild().getNodeValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		window.showAtLocation(activity.findViewById(R.id.activity_pay_btn_ok),
				Gravity.BOTTOM, 0, 0);
		final PasswordView pass = (PasswordView) view.findViewById(R.id.payview);
		pass.setOnFinishInput(new OnPasswordInputFinish() {
			@Override
			public void inputFinish() {
//						closePopupWindow();
						password = pass.getStrPassword();
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								// 输入完成
								closePopupWindow();
								passmd5 = DigestUtils.md5(password).toUpperCase(Locale.CHINA);
//        logger.i("TAG","    "+passmd5);
								// 支付
								pay();
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
	 * 去找回密码
	 */
	private void toFindPass() {
		Intent intent = new Intent(context, IdentifyActivity.class);
		intent.putExtra("isResetPayPass", true);
		startActivity(intent);
	}

	/**
	 * 处理密码错误
	 */
	public final void showPassError(String msg) {
//        passError--;
//        if (passError > 0) {

		PromptUtils.showDialog4(activity, context, "", msg, "重新输入",
				"找回密码", new PromptUtils.OnDialogClickListener2() {
					@Override
					public void onLeftClick(Dialog dialog) {
						submit();
					}

					@Override
					public void onRightClick(Dialog dialog) {
						toFindPass();
					}
				}, false);
//        } else {
//            App.isPayLocked = true;
//            showPassLocked();
//        }
	}

	public void onEventMainThread(PassEvent event) {
		showPassError(event.msg);
	}

	@Override
	public void requestSuccess(String paramString) {
		Log.i(TAG, "requestSuccess: "+paramString);
		if (paramString.contains("成功")) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					toResult();
				}
			},500);
		} else {
			if (paramString.contains("余额不足")) {
				PromptUtils.showDialog1(this, context, "提示：", "余额不足", "确定", new PromptUtils.OnDialogClickListener1() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
					}
				});
			} else {
				PromptUtils.showDialog1(this,context,"提示：","支付失败","确定",new PromptUtils.OnDialogClickListener1(){
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
}
