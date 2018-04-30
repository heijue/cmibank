package cn.app.yimirong.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.app.yimirong.R;
import cn.app.yimirong.model.http.API;

public class InviteDescActivity extends AppCompatActivity {

	@Bind(R.id.activity_invite_desc_webview)
	WebView mWebview;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_desc);
		ButterKnife.bind(this);
		configWebView();
	}

	/**
	 * 配置webview
	 */
	protected void configWebView() {
		mWebview.setBackgroundResource(R.drawable.shape_bg_dialog_white_15);
//
		mWebview.setBackgroundColor(Color.TRANSPARENT);
		mWebview.getSettings().setJavaScriptEnabled(true);
		mWebview.getSettings().setSupportZoom(true);
		mWebview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		mWebview.getSettings().setUseWideViewPort(true);
		mWebview.getSettings().setLoadWithOverviewMode(true);
		mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mWebview.loadUrl(API.YAO_QING_DESC);
	}

	@OnClick(R.id.activity_invite_desc_btn)
	public void onClick() {
		finish();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.alpha_in_50, R.anim.alpha_out_50);
	}
}
