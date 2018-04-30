package cn.app.yimirong.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.app.yimirong.R;
import cn.app.yimirong.model.http.API;

public class ExpDescActivity extends AppCompatActivity {


	@Bind(R.id.activity_exp_desc_webview)
	WebView mWebview;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exp_desc);
		ButterKnife.bind(this);
		configWebView();
	}

	/**
	 * 配置webview
	 */
	protected void configWebView() {
		mWebview.getSettings().setJavaScriptEnabled(true);
		mWebview.getSettings().setSupportZoom(true);
		mWebview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		mWebview.getSettings().setUseWideViewPort(true);
		mWebview.getSettings().setLoadWithOverviewMode(true);
		mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebview.loadUrl(API.EXP_DESC);
	}

	@OnClick(R.id.activity_exp_desc_btn)
	public void onClick() {
		finish();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.alpha_in_50, R.anim.alpha_out_50);
	}
}
