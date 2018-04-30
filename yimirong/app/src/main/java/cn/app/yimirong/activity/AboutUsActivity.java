package cn.app.yimirong.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.common.Constant;
import cn.app.yimirong.model.bean.ShareInvite;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.utils.ClickCounter;
import cn.app.yimirong.utils.PhoneUtils;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.ToastUtils;
import cn.app.yimirong.view.RoundedImageView;

public class AboutUsActivity extends BaseActivity implements OnClickListener {

	private RoundedImageView iconImage;

	private TextView tvVersion;

	private TextView home_page;

	// 网站
	private LinearLayout llWeb;

	// 了解我们
	private LinearLayout llLiaoJie;

	// 微信服务号
	private LinearLayout llWeiXin;

	// 官方邮箱
	private LinearLayout llMail;

	// 客服电话
	private LinearLayout llPhone;

	// QQ群
	private LinearLayout llQQGroup;

	private android.text.ClipboardManager clip1;

	private android.content.ClipboardManager clip2;

	private ClickCounter clickCounter;

	private String url;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_about_us);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("关于易米融");

		home_page = (TextView) findViewById(R.id.home_page);

		iconImage = (RoundedImageView) findViewById(R.id.activity_about_us_icon);
		iconImage.setOnClickListener(this);

		tvVersion = (TextView) findViewById(R.id.activity_about_us_version);

		llWeb = (LinearLayout) findViewById(R.id.activity_about_us_web);
		llWeb.setOnClickListener(this);

//		llLiaoJie = (LinearLayout) findViewById(R.id.activity_about_us_liaojie);
//		llLiaoJie.setOnClickListener(this);

		llWeiXin = (LinearLayout) findViewById(R.id.activity_about_us_weixin);
		llWeiXin.setOnClickListener(this);

		llMail = (LinearLayout) findViewById(R.id.activity_about_us_mail);
		llMail.setOnClickListener(this);

		llPhone = (LinearLayout) findViewById(R.id.activity_about_us_phone);
		llPhone.setOnClickListener(this);

		llQQGroup = (LinearLayout) findViewById(R.id.activity_about_us_qq);
		llQQGroup.setOnClickListener(this);
	}

	@Override
	public void initData() {
		ShareInvite shareInvite = (ShareInvite) mCache
				.getAsObject("share_invite");
		if (shareInvite!=null && shareInvite.home_page!=null && !shareInvite.home_page.equals("")){
			home_page.setText(shareInvite.home_page);
			url = shareInvite.home_page;
		}else {
			url =  "http://"+getResources().getString(R.string.web_wongcai);
		}

		String versionCode = SystemUtils.getVersionName(context);
		tvVersion.setText("易米融 " + versionCode);
		if (Constant.API_LEVEL < 11) {
			clip1 = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		} else {
			clip2 = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		}
		clickCounter = new ClickCounter(Constant.DEBUG_CLICK_TIMES, 2000);
		clickCounter.setListener(new ClickCounter.OnCountListener() {
			@Override
			public void onCount(int currentTime) {
				// Do Nothing
			}

			@Override
			public void onCountDone() {
				showDebugDialog();
			}
		});
	}

	@Override
	public void onClick(View v) {
		hideInputMethod();
		int id = v.getId();
		switch (id) {
			case R.id.activity_about_us_icon:
				countClick();
				break;

			case R.id.activity_about_us_web:
				toOurWeb();
				break;

//			case R.id.activity_about_us_liaojie:
//				toLiaoJie();
//				break;

			case R.id.activity_about_us_weixin:
				toBarCode();
				break;

			case R.id.activity_about_us_mail:
				showMail();
				break;

			case R.id.activity_about_us_phone:
				showPhone();
				break;

			case R.id.activity_about_us_qq:
				showQQGroup();
				break;

			default:
				break;
		}
	}

	private void countClick() {
		clickCounter.countClick();
	}

	/**
	 * 显示密码输入框
	 */
	private void showDebugDialog() {
		PromptUtils.showDebugDialog(activity, context, "调试模式", "取消", "确认", new PromptUtils.OnDialogClickListener2() {
			@Override
			public void onLeftClick(Dialog dialog) {
				dialog.dismiss();
			}

			@Override
			public void onRightClick(Dialog dialog) {
				EditText etPass = (EditText) dialog.findViewById(R.id.dialog_debug_input);
				LinearLayout wrapper = (LinearLayout) dialog.findViewById(R.id.dialog_debug_wrapper);
				if (etPass.getVisibility() == View.VISIBLE && wrapper.getVisibility() == View.GONE) {
					String debugPass = etPass.getText().toString().trim();
					if (Constant.DEBUG_PASSWORD.equals(debugPass)) {
						//密码正确
						etPass.setVisibility(View.GONE);
						wrapper.setVisibility(View.VISIBLE);
						showDebugMenu(dialog);
					} else {
						ToastUtils.show(context, "调试密码错误!", Toast.LENGTH_SHORT);
					}
				} else {
					dialog.dismiss();
				/*	Http.clearCookies();
					Http.saveCookies(context);*/
//					EventBus.getDefault().post(new DebugChangeEvent());
					logout();

				}
			}
		});
	}

	private void showDebugMenu(Dialog dialog) {
		RadioGroup group = (RadioGroup) dialog.findViewById(R.id.dialog_debug_group);
		RadioButton test = (RadioButton) dialog.findViewById(R.id.dialog_debug_test);
		RadioButton http = (RadioButton) dialog.findViewById(R.id.dialog_debug_http);
		RadioButton https = (RadioButton) dialog.findViewById(R.id.dialog_debug_https);
		switch (App.DEBUG_LEVEL) {
			case App.DEBUG_LEVEL_TEST:
				test.setChecked(true);
				break;

			case App.DEBUG_LEVEL_HTTP:
				http.setChecked(true);
				break;

			case App.DEBUG_LEVEL_HTTPS:
				https.setChecked(true);
				break;
			default:
				break;

		}
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int id) {
				switch (id) {
					case R.id.dialog_debug_test:
						App.DEBUG_LEVEL = App.DEBUG_LEVEL_TEST;
						break;

					case R.id.dialog_debug_http:
						App.DEBUG_LEVEL = App.DEBUG_LEVEL_HTTP;
						break;

					case R.id.dialog_debug_https:
						App.DEBUG_LEVEL = App.DEBUG_LEVEL_HTTPS;
					default:
						break;
				}
				//保存配置
				saveConfig();
			}
		});
	}

	/**
	 * 保存配置
	 */
	private void saveConfig() {
		if (sp != null) {
			sp.edit()
					.putInt("debugLevel", App.DEBUG_LEVEL)
					.commit();
		}
	}

	/**
	 * 了解我们
	 */
	private void toLiaoJie() {
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra("title", "了解我们");
		intent.putExtra("url", API.LIAOJIE_WOMEN);
		startActivity(intent);
	}

	/**
	 * 显示QQ群
	 */
	private void showQQGroup() {
		final String message = getResources().getString(R.string.qq_group);
		PromptUtils.showDialog2(activity, context, "客服QQ群", message, "取消",
				"复制", new PromptUtils.OnDialogClickListener2() {

					@SuppressLint("NewApi")
					@Override
					public void onRightClick(Dialog dialog) {
						if (Constant.API_LEVEL < 11) {
							clip1.setText(message);
						} else {
							clip2.setText(message);
						}
						ToastUtils.show(context, "QQ号码已复制到剪贴板");
					}

					@Override
					public void onLeftClick(Dialog dialog) {
						// 取消
					}
				});
	}

	/**
	 * 打开公司网站
	 */
	private void toOurWeb() {
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra("title", "易米融");
		intent.putExtra("url",
				url);
		startActivity(intent);
	}

	/**
	 * 显示客服电话
	 */
	private void showPhone() {
		final String number = getResources().getString(R.string.service_phone);
		String font = getResources().getString(R.string.service_time);
		PromptUtils.showDialog2(activity, context, number, font, "取消", "拨打", new PromptUtils.OnDialogClickListener2() {
			@Override
			public void onRightClick(Dialog dialog) {
				// 拨打电话
				PhoneUtils.call(context, number);
			}

			@Override
			public void onLeftClick(Dialog dialog) {
				// 取消
			}
		});
	}

	/**
	 * 显示客服邮箱
	 */
	private void showMail() {
		final String message = getResources().getString(R.string.service_mail);
		PromptUtils.showDialog2(activity, context, "客服邮箱", message, "取消", "复制",
				new PromptUtils.OnDialogClickListener2() {
					@SuppressLint("NewApi")
					@Override
					public void onRightClick(Dialog dialog) {
						if (Constant.API_LEVEL < 11) {
							clip1.setText(message);
						} else {
							clip2.setText(message);
						}
						ToastUtils.show(context, "邮箱已复制到剪贴板");
					}

					@Override
					public void onLeftClick(Dialog dialog) {
						// 取消
					}
				});
	}

	/**
	 * 去查看微信服务号二维码
	 */
	private void toBarCode() {
		Intent intent = new Intent(context, BarCodeActivity.class);
		startActivity(intent);
	}

}
