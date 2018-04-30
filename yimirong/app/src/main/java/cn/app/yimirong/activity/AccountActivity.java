package cn.app.yimirong.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.DebugChangeEvent;
import cn.app.yimirong.event.custom.SetNewPassEvent;
import cn.app.yimirong.event.custom.UserInfoUpdateEvent;
import cn.app.yimirong.model.base.BaseModel;
import cn.app.yimirong.model.bean.UserInfo;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.ToastUtils;

public class AccountActivity extends BaseActivity implements OnClickListener {

	private TextView tvVerified;

	private TextView tvBankCard;

	private TextView tvPayPass,tvAccount;

	private CheckBox switchBtn;

	private LinearLayout llGesture;

	private boolean isBankAdded = false;

	private boolean isIdentified = false;

	private YWIMKit mIMKit;

	private OnCheckedChangeListener listener;
	private RelativeLayout rlAccount;

	@Override
	public void loadView() {
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_account);
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("我的账户");
		String userid = App.userinfo.top_uid;
//		if (!userid.isEmpty()) {
////			mIMKit = YWAPI.getIMKitInstance(userid, "");
//		}
		rlAccount = (RelativeLayout) findViewById(R.id.activity_account_name_wrapper);
		rlAccount.setOnClickListener(this);
		tvAccount = (TextView) findViewById(R.id.activity_account_phone);
		if (App.isLogin && !StringUtils.isBlank(App.account)) {
			tvAccount.setText(StringUtils.getSecretAccount(App.account));
		}
		tvVerified = (TextView) findViewById(R.id.activity_account_isverified);
		tvBankCard = (TextView) findViewById(R.id.activity_account_bankcard);
		tvBankCard.setOnClickListener(this);
		TextView tvLoginPass = (TextView) findViewById(R.id.activity_account_login_pass);
		tvLoginPass.setOnClickListener(this);

		tvPayPass = (TextView) findViewById(R.id.activity_account_pay_pass);
		tvPayPass.setOnClickListener(this);

		RelativeLayout rlShouShi = (RelativeLayout) findViewById(R.id.activity_account_shoushi_wrapper);
		rlShouShi.setOnClickListener(this);

		llGesture = (LinearLayout) findViewById(R.id.activity_account_gesture_wrapper);
		llGesture.setOnClickListener(this);

		switchBtn = (CheckBox) findViewById(R.id.activity_account_shoushi_switch);

		RelativeLayout rlQRCode = (RelativeLayout) findViewById(R.id.activity_account_myqrcode_wrapper);
		rlQRCode.setOnClickListener(this);

		TextView btnLogout = (TextView) findViewById(R.id.activity_account_logout);
		btnLogout.setOnClickListener(this);

		listener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
				boolean checked = isChecked;
				if (checked) {
					// 开启手势密码
					toSetLockPass();
				} else {
					// 关闭手势密码
					toCloseLockPass();
				}
			}
		};
	}

	@Override
	public void initData() {
		showUserInfo();
	}

	/**
	 * 身份信息验证
	 */
	protected void checkVerified() {
		if (App.isLogin) {
			if (App.userinfo != null) {
				UserInfo userinfo = App.userinfo;
				if (userinfo.identity != null) {
					setAccountVerified(true);
				} else {
					setAccountVerified(false);
				}
			} else {
				ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
				finish();
			}
		} else {
			ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
			logout();
		}
	}

	private void setAccountVerified(boolean isverified) {
		isIdentified = isverified;
		if (isverified) {
			if (App.userinfo != null && App.userinfo.identity != null
					&& App.userinfo.identity.realName != null) {
			}
			String xing = App.userinfo.identity.realName.substring(0, 1);
			String userName = App.userinfo.identity.realName.replace(xing, "*");
			char[] chars = App.userinfo.identity.idCard.toCharArray();
			StringBuilder sb = getNencryptedIDCard(chars);
			tvVerified.setText(userName+"("+sb.toString()+")");
			tvVerified.setTextColor(Color.BLACK);
			tvVerified.setCompoundDrawables(null,null,null,null
			);

		} else {
			tvVerified.setVisibility(View.VISIBLE);
		}
	}

	@NonNull
	private StringBuilder getNencryptedIDCard(char[] chars) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < chars.length; i++) {
            if (i < 3) {
                sb.append(chars[i]);
            } else if (i < chars.length - 3) {
                sb.append("*");
            } else {
                sb.append(chars[i]);
            }

        }
		return sb;
	}

	@Override
	protected void onResume() {
		// 手势密码是否已设置
		String lockPass = sp.getString("lockPass", null);
		boolean islockSet = lockPass != null ? true : false;
		switchBtn.setOnCheckedChangeListener(null);
		switchBtn.setChecked(islockSet);
		if (islockSet) {
			// 手势密码已设置
			llGesture.setVisibility(View.VISIBLE);
		} else {
			// 手势密码未设置
			llGesture.setVisibility(View.GONE);
		}
		switchBtn.setOnCheckedChangeListener(listener);
		super.onResume();
	}

	/**
	 * 获取用户信息
	 */
	private void showUserInfo() {
		updateView();
		checkVerified();
	}

	/**
	 * 更新界面
	 */
	protected void updateView() {
		if (App.userinfo != null) {
			//用户信息已加载
			UserInfo userinfo = App.userinfo;
			if (userinfo.identity != null) {
				//已绑卡
				tvBankCard.setText("银行卡信息");
				isBankAdded = true;
				if (userinfo.identity.tpwd) {
					//已设置交易密码
					tvPayPass.setText("重置支付密码");
				} else {
					//未设置交易密码
					tvPayPass.setText("设置支付密码");
				}
			} else {
				//未绑卡
				tvBankCard.setText("银行卡管理");
				isBankAdded = false;
				tvPayPass.setText("设置支付密码");
			}
		} else {
			//用户信息未加载
			ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
		}
	}

	/**
	 * 登出
	 */
	public void loginOut_Sample() {
		if (mIMKit == null) {
			return;
		}


		// openIM SDK提供的登录服务
		IYWLoginService mLoginService = mIMKit.getLoginService();
		mLoginService.logout(new IWxCallback() {

			@Override
			public void onSuccess(Object... arg0) {

			}

			@Override
			public void onProgress(int arg0) {

			}

			@Override
			public void onError(int arg0, String arg1) {

			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.activity_account_name_wrapper:
				toPersonInfo();
				break;

			case R.id.activity_account_bankcard:
				if (isBankAdded) {
					toBankCardInfo();
				} else {
					toAddBankCard();
				}
				break;
			case R.id.activity_account_login_pass:
				toSetLoginPass();
				break;

			case R.id.activity_account_pay_pass:
				handleSetPayPass();
				break;

			case R.id.activity_account_shoushi_wrapper:
				break;

			case R.id.activity_account_gesture_wrapper:
				toAlterLockPass();
				break;

			case R.id.activity_account_myqrcode_wrapper:
				toMyQRCode();
				break;

			case R.id.activity_account_logout:
				logout();
				break;

			default:
				break;
		}
	}

	/**
	 * 去我的二维码
	 */
	private void toMyQRCode() {
		Intent intent = new Intent(context, MyBarCodeActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 重置登录密码
	 */
	private void toSetLoginPass() {
		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_chongzhi_info);
		Button btnCanel = (Button) dialog.findViewById(R.id.dialog_chongzhi_info_cancel);
		Button btnOK = (Button) dialog.findViewById(R.id.dialog_chongzhi_info_ok);
		btnCanel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				Intent intent = new Intent(context, ResetLoginActivity.class);
				intent.putExtra("account", App.account);
				String ss = App.account + " ";
				String start = ss.substring(0, 3);
				String middel = ss.substring(3, 7);
				String end = ss.substring(7, 11);
				String phoneNum = start + " " + middel + " " + end;
				intent.putExtra("accountText", phoneNum);
				startActivity(intent);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * 设置支付密码
	 */
	private void handleSetPayPass() {
		if (isBankAdded) {
			// 银行卡已添加
			// 判断是否设置支付密码
			if (App.userinfo != null) {
				UserInfo userinfo = App.userinfo;
				if (userinfo.identity != null) {
					//银行卡已绑定
					if (userinfo.identity.tpwd) {
						// 支付密码已设置
						// 重置支付密码
						toResetPayPass();
					} else {
						// 支付密码未设置
						// 设置支付密码
						toSetPayPass();
					}
				} else {
					//银行卡未绑定
					ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
					finish();
				}
			} else {
				ToastUtils.show(context, BaseModel.SYSTEM_ERROR);
				finish();
			}

		} else {
			showAddCard();
		}
	}

	/**
	 * 提示添加银行卡
	 */
	private void showAddCard() {
		final Dialog dialog = new Dialog(context, R.style.DialogStyle);
		dialog.setContentView(R.layout.dialog_chongzhi_info);
		TextView tvTitle = (TextView) dialog.findViewById(R.id.dialog_text);
		tvTitle.setText("您未绑定银行卡，请先绑定银行卡");
		Button btnCanel = (Button) dialog.findViewById(R.id.dialog_chongzhi_info_cancel);
		Button btnOK = (Button) dialog.findViewById(R.id.dialog_chongzhi_info_ok);
		btnCanel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				toBindBankCard();
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * 去设置支付密码
	 */
	private void toSetPayPass() {
		Intent intent = new Intent(context, SetPayPassActivity.class);
		startActivity(intent);
	}

	/**
	 * 去重置支付密码
	 */
	private void toResetPayPass() {
		Intent intent = new Intent(context, IdentifyActivity.class);
		intent.putExtra("isResetPayPass", true);
		startActivity(intent);
	}



	/**
	 * 去银行卡信息
	 */
	private void toBankCardInfo() {
		Intent intent = new Intent(context, BankInfoActivity.class);
		startActivity(intent);
	}

	/**
	 * 去个人信息
	 */
	private void toPersonInfo() {
		if (isIdentified) {
			rlAccount.setOnClickListener(null);
		} else {
			showAddCard();
		}
	}

	/**
	 * 去设置手势密码
	 */
	private void toSetLockPass() {
		Intent intent = new Intent(context, SetLockActivity.class);
		startActivity(intent);
	}

	/**
	 * 去修改手势密码
	 */
	private void toAlterLockPass() {
		Intent intent = new Intent(context, UnLockActivity.class);
		// 修改手势密码的标志
		intent.putExtra("action", UnLockActivity.ACTION_ALTER_LOCK);
		startActivity(intent);
	}

	/**
	 * 去关闭手势密码
	 */
	protected void toCloseLockPass() {
		Intent intent = new Intent(context, UnLockActivity.class);
		// 修改手势密码的标志
		intent.putExtra("action", UnLockActivity.ACTION_CLOSE_LOCK);
		startActivity(intent);
	}

	/**
	 * 去银行卡管理界面
	 */
	private void toBindBankCard() {
		Class claz = BindActivity.class;
		Intent intent = new Intent(context, claz);
		startActivity(intent);
	}

	/**
	 * 去添加银行卡添加界面
	 */
	private void toAddBankCard() {
		Intent intent = new Intent(context, AddCardActivity.class);
		startActivity(intent);
	}

	/**
	 * 用户修改了密码，该Activity要结束掉
	 *
	 * @param event
	 */
	public void onEventMainThread(SetNewPassEvent event) {
		finish();
	}


	/**
	 * 用户数据更新了，重新显示
	 *
	 * @param event
	 */
	public void onEventMainThread(UserInfoUpdateEvent event) {
		if (isCreated) {
			showUserInfo();
		}
	}

}
