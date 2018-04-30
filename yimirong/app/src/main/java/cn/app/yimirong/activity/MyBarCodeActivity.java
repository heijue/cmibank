package cn.app.yimirong.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.ShareBoardConfig;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.bean.LoginData;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.utils.BitmapUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.ToastUtils;

public class MyBarCodeActivity extends BaseActivity {

	private ImageView ivBarCode;

	private RelativeLayout rlIcon;

	private ImageView ivIcon;

	private TextView tvName;

	private TextView tvAccount,inviteBtn;

	private String code;



	@Override
	public void loadView() {
		setContentView(R.layout.activity_my_barcode);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("我的二维码");
		ivBarCode = (ImageView) findViewById(R.id.activity_my_barcode_image);

		rlIcon = (RelativeLayout) findViewById(R.id.activity_my_barcode_icon_wrapper);

		ivIcon = (ImageView) findViewById(R.id.activity_my_barcode_icon);

		tvName = (TextView) findViewById(R.id.activity_my_barcode_name);

		tvAccount = (TextView) findViewById(R.id.activity_my_barcode_account);

		inviteBtn = (TextView) findViewById(R.id.activity_invite_erweima);
	}

	@Override
	public void initData() {
		code = App.code;
		if (code == null) {
			LoginData loginData = DataMgr.getInstance(appContext)
					.restoreLoginData();
			code = loginData.code;
		}
		final Bitmap qrcode;
		if (App.DEBUG_LEVEL == App.DEBUG_LEVEL_TEST) {
			qrcode = BitmapUtils.createQRImage(context, API.API_TEST
					+ "invite_page?code=" + code);
		} else {
			qrcode = BitmapUtils.createQRImage(context, API.API_HTTP
					+ "invite_page?code=" + code);
		}

		ivBarCode.setImageBitmap(qrcode);
		setUserInfo();
		inviteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!StringUtils.isBlank(code)) {

					UMImage web = new UMImage(MyBarCodeActivity.this,qrcode);
					ShareBoardConfig config = new ShareBoardConfig();
					config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_NONE);
					new ShareAction(activity)
							.setDisplayList(SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
							.withMedia(web)
							.setCallback(umShareListener)
							.open(config);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

	}

	private UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onStart(SHARE_MEDIA platform) {
		}
		@Override
		public void onResult(SHARE_MEDIA platform) {

			Toast.makeText(MyBarCodeActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			if (t.getMessage().contains("没有安装应用")) {
				switch (platform) {
					case WEIXIN:
						ToastUtils.show(MyBarCodeActivity.this,"您没有安装微信");
						break;
					case QQ:
						ToastUtils.show(MyBarCodeActivity.this,"您没有安装QQ");
						break;
					case QZONE:
						ToastUtils.show(MyBarCodeActivity.this,"您没有安装QZONE");
						break;
					case WEIXIN_CIRCLE:
						ToastUtils.show(MyBarCodeActivity.this,"您没有安装微信");
						break;
				}
			} else {
//				Toast.makeText(MyBarCodeActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
		}
	};

	private void setUserInfo() {
		if (App.account != null) {
			tvAccount.setText(StringUtils.getSecretAccount(App.account));
		}

		if (App.userinfo != null && App.userinfo.identity != null
				&& App.userinfo.identity.realName != null) {
			setAccountVerified(true);
		} else {
			setAccountVerified(false);
		}
	}

	private void setAccountVerified(boolean isverified) {
		if (isverified) {
			tvName.setVisibility(View.VISIBLE);
			if (App.userinfo != null && App.userinfo.identity != null) {
				tvName.setText(App.userinfo.identity.realName);
			}
			ivIcon.setVisibility(View.VISIBLE);
		} else {
			tvName.setVisibility(View.GONE);
			ivIcon.setVisibility(View.VISIBLE);
		}
	}

}
