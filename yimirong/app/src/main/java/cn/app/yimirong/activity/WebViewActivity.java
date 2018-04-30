package cn.app.yimirong.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgentJSInterface;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.bean.BaseProduct;
import cn.app.yimirong.model.bean.LoginData;
import cn.app.yimirong.model.bean.ShareInvite;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.ToastUtils;

public class WebViewActivity extends BaseActivity {

	private WebView webView;

	private String title;

	private boolean isPush;


	private Bitmap bmp;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_webview);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		titleBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isPush) {
					toMain();
				} else {
					finish();
				}
			}
		});

		webView = (WebView) findViewById(R.id.activity_webview_webview);
		new MobclickAgentJSInterface(this, webView);
		configWebView();
	}

	/**
	 * 配置webview
	 */
	protected void configWebView() {
//        webView.setVerticalScrollbarOverlay(true); //指定的垂直滚动条有叠加样式
//
//        WebSettings settings = webView.getSettings();
//
//        settings.setUseWideViewPort(true);//设定支持viewport
//
//        settings.setLoadWithOverviewMode(true);
//
////        settings.setBuiltInZoomControls(true);
//
//        settings.setSupportZoom(true);//设定支持缩放

		webView.getSettings().setJavaScriptEnabled(true);
		webView.setVerticalScrollbarOverlay(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.setWebViewClient(new WebViewClient() {


			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (!TextUtils.isEmpty(url) && url.contains("toproduectxx")) {
					view.stopLoading();
					return false;
				}
				if (!TextUtils.isEmpty(url) && url.contains("bannarInvitexx")) {
					view.stopLoading();
					return false;
				}
				view.loadUrl(url);
				return true;
			}

			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
				return super.shouldInterceptRequest(view, request);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				if (!TextUtils.isEmpty(url) && url.contains("toproduectxx")) {
					String one = url.substring(7, url.length());
					String[] names = one.split("\\/");
					String[] pnames = names[2].split("\\&");
					final String type = pnames[0].substring(pnames[0].indexOf("=") + 1, pnames[0].length());
					final String ptid = pnames[1].substring(pnames[1].indexOf("=") + 1, pnames[1].length());
					getProduct(type, ptid);
					return;
				}

				if (!TextUtils.isEmpty(url) && url.contains("bannarInvitexx")) {

						if (App.isLogin){
							String code = App.code;
							String shareTile;
							String shareContent;

							if (code == null) {
								LoginData loginData = DataMgr.getInstance(appContext)
										.restoreLoginData();
								code = loginData.code;
								App.code = code;
							}

							ShareInvite shareInvite = (ShareInvite) mCache
									.getAsObject("share_invite");
							if (shareInvite != null) {
								shareTile = shareInvite.share_title;
								shareContent = shareInvite.share_content;
							} else {
								// 邀请文本 默认值
								shareTile = "易米融";
								shareContent = "我正在使用易米融理财，是个靠谱的理财平台，推荐试一下。";
							}
							if (!StringUtils.isBlank(code)) {

								Resources res = getResources();
								bmp = BitmapFactory.decodeResource(res, R.mipmap.icon);

								UMWeb web = new UMWeb(API.API_HTTP + API.API_HTTP + "invite_page?code=" + code);
								web.setThumb(new UMImage(context,bmp));
								web.setTitle(shareTile);
								web.setDescription(shareContent);

								new ShareAction(activity)
										.setDisplayList(SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
										.withMedia(web)
										.setCallback(umShareListener)
										.open();

							}
						}else {
							Intent intent = new Intent(context, LoginActivity.class);
							startActivity(intent);
						}

					return;
				}
				super.onPageStarted(view, url, favicon);
				showLoading("玩命向钱冲");
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				closeLoading();
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

			Toast.makeText(WebViewActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			Toast.makeText(WebViewActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
		}
	};

	private void getProduct(String type, String ptid) {
		showLoading("跳转中");
		if (pmodel != null) {
			pmodel.getLJbuyPriduct(ptid, type, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof BaseProduct) {
						BaseProduct product = (BaseProduct) t;
						toProductDetail(product);
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					ToastUtils.show(context, msg, Toast.LENGTH_SHORT);
					closeLoading();
				}
			});
		}
	}

	/**
	 * 去产品详情
	 *
	 * @param product
	 */
	private void toProductDetail(BaseProduct product) {
		if (product != null) {
			boolean canBuy = true;
			product.state = Integer.parseInt(product.status);
			boolean isSellOut = product.state != BaseProduct.STATE_SELLING;
			if (isSellOut) {
				canBuy = false;
			}
			Intent intent = new Intent(context, ProductDetailActivity.class);
			Bundle data = new Bundle();
			data.putSerializable("product", product);
			data.putInt("productType", product.ptype);
			data.putInt("productState", product.state);
			data.putString("title", product.pname);
			data.putString("pid", product.pid);
			data.putBoolean("canbuy", canBuy);
			data.putBoolean("issellout", isSellOut);
			data.putBoolean("isyugao", product.isYuGao());
			intent.putExtras(data);
			startActivity(intent);
			closeLoading();
		}
	}

	@Override
	public void initData() {

		Intent intent = getIntent();
		isPush = intent.getBooleanExtra("isPush", false);
		title = intent.getStringExtra("title");
		String pid = intent.getStringExtra("pid");
		String bid = intent.getStringExtra("bid");
		if (!StringUtils.isBlank(title)) {
			setTitleText(title);
		}

		String url = intent.getStringExtra("url");
		if (!StringUtils.isBlank(url)) {
			LoginData loginData = DataMgr.getInstance(app).restoreLoginData();
			if (loginData != null) {
				if (loginData.uid != null && !url.contains("www.yimirong.com")) {
					url = url + "?uid=" + loginData.uid;
				}
			}
			webView.loadUrl(url);
		} else {
			if (pid == null && bid == null) {
				String cid = intent.getStringExtra("cid");
				String type = intent.getStringExtra("type");
				int ptype = intent.getIntExtra("ptype", BaseProduct.TYPE_DQ);

				if (!StringUtils.isBlank(type) && !StringUtils.isBlank(cid)) {
					if (pmodel != null) {
						pmodel.getProductContract(ptype, cid, type,
								new ResponseHandler() {
									@Override
									public <T> void onSuccess(String response, T t) {
										super.onSuccess(response, t);
										webView.loadData(response,
												"text/html;charset=utf-8", null);
									}
								});
					}
				}
			}else{
				if (pid!=null) {
					if (pmodel != null) {
						pmodel.getProductContract2(pid, new ResponseHandler() {
							@Override
							public <T> void onSuccess(String response, T t) {
								super.onSuccess(response, t);
								if (response.contains("error")) {
									PromptUtils.showDialog3(context, "您的操作过于频繁！", new PromptUtils.OnDialogClickListener1() {
										@Override
										public void onClick(Dialog dialog) {
											dialog.dismiss();
											finish();
										}
									});
								} else {
									webView.loadData(response,
											"text/html;charset=utf-8", null);
								}
							}
						});
					}
				}

				if (bid!=null){
					if (pmodel != null) {
						pmodel.getProductContract3(bid, new ResponseHandler() {
							@Override
							public <T> void onSuccess(String response, T t) {
								super.onSuccess(response, t);
								if (response.contains("error")) {
									PromptUtils.showDialog3(context, "您的操作过于频繁，十秒以后再试！", new PromptUtils.OnDialogClickListener1() {
										@Override
										public void onClick(Dialog dialog) {
											dialog.dismiss();
											finish();
										}
									});
								} else {
									webView.loadData(response,
											"text/html;charset=utf-8", null);
								}
							}
						});
					}
				}

			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(title == null ? this.getClass()
				.getSimpleName() : title);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if(bmp!=null){
			bmp.isRecycled();
			bmp=null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(title == null ? this.getClass().getSimpleName()
				: title);
		MobclickAgent.onPause(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isPush) {
				toMain();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void toMain() {
		Intent intent = new Intent(context, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.alpha_in_200, R.anim.alpha_out_200);
		finish();
	}
}
