package cn.app.yimirong.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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

import com.lidroid.xutils.BitmapUtils;
import com.umeng.analytics.MobclickAgent;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Map;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.CashEvent;
import cn.app.yimirong.event.custom.ZChuEvent;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.base.BaseModel;
import cn.app.yimirong.model.bean.UserCD;
import cn.app.yimirong.model.bean.UserInfo;
import cn.app.yimirong.model.bean.VersionData;
import cn.app.yimirong.model.bean.WithDrawTips;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.DigestUtils;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;
import cn.app.yimirong.view.OnPasswordInputFinish;
import cn.app.yimirong.view.PasswordView;
import cn.app.yimirong.view.PayView;

public final class CashActivity extends BaseActivity {

	private ImageView ivBankIcon;
	private TextView tvBankName;
	private TextView tvMoneyYue;
	private EditText etMoney;
	private TextView tvCashCD;
	private Button btnOK;
	private long time;

	private PopupWindow window;

	private String password;

	// 提现金额
	private double money;

	private PayView payview;

	private int freecd = 0;

	private VersionData versionData;

	private WithDrawTips tips;

	private UserCD usercd;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_cash);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("提现");
		setTitleRight(true, new OnRightClickListener() {

			@Override
			public void onClick() {
				toCashShuoMing();
			}
		});
		setRightText("提现说明");

		tvBankName = (TextView) findViewById(R.id.activity_tixian_bank_name);
		tvMoneyYue = (TextView) findViewById(R.id.activity_tixian_money_yue);
		ivBankIcon = (ImageView) findViewById(R.id.activity_tixian_bank_icon);
		btnOK = (Button) findViewById(R.id.activity_tixian_btn_ok);
		assert btnOK != null;
		btnOK.setBackground(getResources().getDrawable(R.drawable.shape_bg_dark_gray_normal));
		etMoney = (EditText) findViewById(R.id.activity_tixian_money_num);
		etMoney.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
			}

			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void afterTextChanged(Editable s) {
				int cd = 0;
				if (usercd != null) {
					cd = usercd.withDraw;
				}
				if (cd <= 0) {
					btnOK.setClickable(false);
					btnOK.setTextColor(Color.parseColor("#999999"));
					btnOK.setBackground(getResources().getDrawable(R.drawable.shape_bg_dark_gray_normal));
					return;
				}
				String tmpStr = s.toString();
				double allasset = SystemUtils.getDouble(App.userinfo.balance);
				double ss = SystemUtils.getDouble(tmpStr);

				if (tmpStr.length() > 0) {
					btnOK.setBackground(getResources().getDrawable(R.drawable.shape_bg_red_btn_normal));
					btnOK.setClickable(true);
				} else {
					btnOK.setBackground(getResources().getDrawable(R.drawable.shape_bg_dark_gray_normal));
					btnOK.setClickable(false);
				}
				if (ss > allasset || tmpStr.length() > App.userinfo.balance.length()) {
					etMoney.setText(allasset + "");
					etMoney.setSelection(String.valueOf(allasset).length());
				}
				int dotPos = tmpStr.indexOf(".");
				if (dotPos <= 0) {
					return;
				}
				if (tmpStr.length() - dotPos - 1 > 2) {
					// 小数点后位数大于2，删除多余位数
					s.delete(dotPos + 3, dotPos + 4);
				}
			}
		});
		tvCashCD = (TextView) findViewById(R.id.activity_cash_cd);

		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (new Double(etMoney.getText().toString().trim()) < 2) {
					PromptUtils.showDialog3(context, "提现不能低于2元!", new PromptUtils.OnDialogClickListener1() {
						@Override
						public void onClick(Dialog dialog) {
							dialog.dismiss();
						}
					});
					return;
				} else {
					submit();
				}
			}
		});
		btnOK.setClickable(false);

	}

	@Override
	public void initData() {
		versionData = DataMgr.getInstance(appContext).restoreVersionData();
		if (versionData.withdraw_tips) {
			loadTips();
		}
		setBankIcon();
		loadUserCD();
	}

	/**
	 * 获取提现tips
	 */
	private void loadTips() {
		tips = DataMgr.getInstance(appContext).restoreWithDrawTips();
		if (cmodel != null) {
			cmodel.getWithDrawTips(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof WithDrawTips) {
						tips = (WithDrawTips) t;
						DataMgr.getInstance(appContext).saveWithDrawTips(
								tips);
					}
				}
			});
		}
	}


	/**
	 * 获取用户取现次数
	 */
	private void loadUserCD() {
		showLoading("玩命向钱冲");
		if (amodel != null) {
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

	private void updateView(UserCD chargecd) {
		if (chargecd != null) {
			int cd = chargecd.withDraw;
			freecd = chargecd.free_withDraw;
			String cashcd;
			if (cd <= 0) {
				// 提现次数用完
				cashcd = "今日提现次数全部用完，不可提现";
				tvCashCD.setVisibility(View.GONE);
				tvMoneyYue.setText(cashcd);
//				tvCashTime.setVisibility(View.GONE);
			} else {
				if (chargecd.withDrawVersion > 0) {
					if (freecd > 0) {
						// 可以免费提现
						cashcd = "本月免费提现次数剩余" + "<font color='#ff4747'>" + freecd + "</font>次；今日还可以提现<font color='#ff4747'>" + cd
								+ "</font>次，本次提现免收手续费";
					} else {
						// 不可以免费提现
						cashcd = "本月免费提现次数剩余" + "<font color='#ff4747'>" + freecd + "</font>次；今日还可以提现<font color='#ff4747'>" + cd
								+ "</font>次，本次提现收取手续费" + versionData.withdraw_sxf
								+ "元";
					}
				}else {
					if (freecd > 0) {
						// 可以免费提现
						cashcd = "今日还可以提现<font color='#ff4747'>" + cd
								+ "</font>次，本次提现免收手续费";
					} else {
						// 不可以免费提现
						cashcd = "今日还可以提现<font color='#ff4747'>" + cd
								+ "</font>次，本次提现收取手续费" + versionData.withdraw_sxf
								+ "元";
					}
				}
				tvCashCD.setVisibility(View.VISIBLE);
				tvCashCD.setText(Html.fromHtml(cashcd));
			}
			double yue = 0;
			if (App.userinfo != null && App.userinfo.balance != null) {
				yue = Double.parseDouble(App.userinfo.balance);
			}

			if (cd <= 0 || yue <= 0) {
				disableBtn();
			} else {
				enableBtn();
			}
		}
	}

	private void enableBtn() {
		btnOK.setClickable(true);
	}

	private void disableBtn() {
		btnOK.setClickable(false);
	}

	@Override
	protected void onStop() {
		if (payview != null && payview.isShowing) {
			payview.dismiss();
		}
		super.onStop();
	}

	/**
	 * 设置银行图标
	 */
	private void setBankIcon() {
		UserInfo userinfo = App.userinfo;
		if (userinfo != null && userinfo.identity != null) {
			tvMoneyYue.setText("本次最多转出￥" + userinfo.balance);
			tvBankName.setText(userinfo.identity.nameCard);
			String url = API.BANK_ICON_BASE + userinfo.identity.bankid + ".jpg";
			BitmapUtils bitmapUtils = new BitmapUtils(context);
			bitmapUtils.configDefaultLoadFailedImage(R.drawable.yinhangbeijing);
			bitmapUtils.display(ivBankIcon, url);
			int h = TimeUtils.getServerHour();
			String time2 = StringUtils.getDaoZhangTime(h);
//			btnOK.setText(time2 + "，确认提现");
		}
	}

	/**
	 * 提交
	 */
	private void submit() {
		hideInputMethod();
		if (!getInput()) {
			return;
		}

		if (versionData.withdraw_tips) {
			showTips();
		} else {
			continueCash();
		}
	}

	private void showTips() {
		if (tips != null) {
			PromptUtils.showDialog4(activity, context, "提示", tips.text,
					tips.cancel_btn, tips.true_btn,
					new PromptUtils.OnDialogClickListener2() {
						@Override
						public void onLeftClick(Dialog dialog) {
							dialog.dismiss();
						}

						@Override
						public void onRightClick(Dialog dialog) {
							dialog.dismiss();
							continueCash();
						}
					}, false);
		}
	}

	private void continueCash() {
		if (freecd > 0) {
			String msg = "亲，每笔提现第三方支付公司需扣除￥2.00手续费，本次由易米融为您支付！";
			PromptUtils.showCashDialog(activity, context, money, money, 0, msg, "取消", "确定", new PromptUtils.OnDialogClickListener2() {
				@Override
				public void onLeftClick(Dialog dialog) {
					dialog.dismiss();
				}

				@Override
				public void onRightClick(Dialog dialog) {
					dialog.dismiss();
					showPopwindow();
				}
			});
		} else {
			// 不可免费提现
			showCashInfo();
		}
	}

	/**
	 * 显示提现手续费信息
	 */
	private void showCashInfo() {
		double sx = (double) versionData.withdraw_sxf;
		String message = "亲，因每笔提现第三方支付公司需要扣取￥" + sx + "手续费，我们建议您多赚一点再提现！";
		String leftBtn = "取消";
		String rightBtn = "继续提现";
		DecimalFormat df = new DecimalFormat("######0.00");
		double realMoney = Double.parseDouble(df.format( money - versionData.withdraw_sxf));
		PromptUtils.showCashDialog(activity, context, money, realMoney, versionData.withdraw_sxf, message, leftBtn, rightBtn, new PromptUtils.OnDialogClickListener2() {
			@Override
			public void onLeftClick(Dialog dialog) {
				dialog.dismiss();
			}

			@Override
			public void onRightClick(Dialog dialog) {
				dialog.dismiss();
				showPopwindow();
			}
		});
	}


	/**
	 * 获取用户输入并校验
	 *
	 * @return
	 */
	private boolean getInput() {
		String moneyStr = etMoney.getText().toString().trim();
		if (StringUtils.isBlank(moneyStr)) {
			ToastUtils.show(context, "请输入提现金额");
			return false;
		} else {
			try {
				money = Double.parseDouble(moneyStr);
			} catch (Exception e) {
				ToastUtils.show(context, "输入金额不正确");
				etMoney.setText("");
				return false;
			}

			if (money <= 0) {
				ToastUtils.show(context, "输入金额不正确");
				etMoney.setText("");
				return false;
			}

			if (freecd <= 0 && money <= versionData.withdraw_sxf) {
				ToastUtils.show(context, "提现手续费" + versionData.withdraw_sxf
						+ "元,输入金额应大于" + versionData.withdraw_sxf + "元");
				etMoney.setText("");
				return false;
			}

			UserInfo userinfo = App.userinfo;
			if (userinfo != null) {
				if (money > SystemUtils.getDouble(userinfo.balance)) {
					ToastUtils.show(context, "提现金额超过账户余额");
					etMoney.setText("");
					return false;
				}
			} else {
				ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
				finish();
				return false;
			}
		}
		return true;
	}

	private void getCash(String password) {
		if (amodel != null) {
			String passmd5 = DigestUtils.md5(password).toUpperCase(Locale.CHINA);
			amodel.cash(money, passmd5, null, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					Map<String, String> map = (Map<String, String>) t;
					time = Long.parseLong(map.get("time"));
					cashSuccess();
					MobclickAgent.onEvent(context, "cashSuccess");
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					if ("4021".equals(errorCode)) {
						// 密码错误
						showPassError(msg);
					}else if ("-1".equals(errorCode)){
						time = TimeUtils.getServerTime();
						cashSuccess();
						EventBus.getDefault().post(new ZChuEvent());
					}else {
						PromptUtils.showDialog3(context, msg, new PromptUtils.OnDialogClickListener1() {
							@Override
							public void onClick(Dialog dialog) {
								dialog.dismiss();
							}
						});
					}
					loadUserCD();
					MobclickAgent.onEvent(context, "cashFailure");
				}
			});
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
						submit();
					}

					@Override
					public void onRightClick(Dialog dialog) {
						toFindPass();
					}
				}, false);
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
	 * 显示成功结果
	 */
	private void cashSuccess() {
		EventBus.getDefault().post(new CashEvent());
		btnOK.setClickable(false);
		final Dialog dialog = PromptUtils.showSuccessDialog(this, context,
				"提现申请已提交");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
			}
		}, 1000);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				toResult();
			}
		}, 1200);
	}

	/**
	 * 去提现结果
	 */
	private void toResult() {
		Intent intent = new Intent(context, CashResultActivity.class);
		intent.putExtra("money", money + "");
		intent.putExtra("type", 0);
		intent.putExtra("time", time);
		startActivity(intent);
		finish();
	}

	/**
	 * 去提现说明
	 */
	private void toCashHistory() {
		Intent intent = new Intent(context, HQZCHistoryActivity.class);
		startActivity(intent);
	}

	/**
	 * 去提现说明
	 */
	private void toCashShuoMing() {
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra("title", "提现说明");
		intent.putExtra("url", API.TIXIAN_DESC);
		startActivity(intent);
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


		// 设置popWindow的显示和消失动画
		window.setAnimationStyle(R.style.mypopwindow_anim_style);
		WindowManager.LayoutParams params = this.getWindow().getAttributes();
		params.alpha = 0.7f;
		this.getWindow().setAttributes(params);
		// 在底部显示
		window.showAtLocation(activity.findViewById(R.id.activity_tixian_btn_ok),
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
						// 密码输入完毕
						closePopupWindow();
						showLoading("正在处理");
						// 提现
						getCash(password);
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

}
