package cn.app.yimirong.model.http;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.PreferencesCookieStore;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.SetCookie;

import java.io.File;
import java.util.List;

import cn.app.yimirong.App;
import cn.app.yimirong.log.Logger;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.base.BaseModel;

public class Http {

	public static final HttpRequest.HttpMethod POST = HttpRequest.HttpMethod.POST;

	public static final HttpRequest.HttpMethod GET = HttpRequest.HttpMethod.GET;

	private static final String TAG = "Http";

	public static HttpUtils httpUtils;

	private static PreferencesCookieStore cookieStore;

	public static void init(Context context) {
		Logger.getInstance().d(TAG, "init");
		if (httpUtils == null) {
			Log.d(TAG, "httpUtils is null");
			httpUtils = new HttpUtils(1000 * 20);
		} else {
			Log.d(TAG, "httpUtils is not null");
		}
		// 配置超时
		httpUtils.configSoTimeout(1000 * 20);
		httpUtils.configTimeout(1000 * 20);
		// 配置线程池
		httpUtils.configRequestThreadPoolSize(10);
		// 配置缓存
		httpUtils.configDefaultHttpCacheExpiry(0);
		// 配置SSL
		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		socketFactory.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
		httpUtils.configSSLSocketFactory(socketFactory);
		configCookies(context);
	}

	// 配置Cookies
	private static void configCookies(Context context) {
		Logger.getInstance().d(TAG, "configCookies");
		if (cookieStore == null) {
			cookieStore = new PreferencesCookieStore(context);
		}
		// 获取保存的Cookies
		List<Cookie> cookies = DataMgr.getInstance(context)
				.restoreCookies();
		if (cookies == null || cookies.isEmpty()) {
			Logger.getInstance().d(TAG, "cookies is empty!");
		} else {
			Logger.getInstance().d(TAG, "cookies is not empty!");
		}

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie != null) {
					Logger.getInstance().d(TAG,
							"configCookies:" + cookie.toString());
					// 将数据库中得Cookie添加到CookieStore中
					cookieStore.addCookie(cookie);
				}
			}
		}
		// 配置CookieStore
		if (httpUtils != null) {
			httpUtils.configCookieStore(cookieStore);
		}
	}

	/**
	 * 获取并保存Cookies
	 */
	public static void saveCookies(Context context) {
		Logger.getInstance().d(TAG, "saveCookies");
		// 获取CookieStore中的Cookies
		if (cookieStore == null) {
			cookieStore = new PreferencesCookieStore(context);
		}
		List<Cookie> cookies = cookieStore.getCookies();
		if (cookies == null || cookies.isEmpty()) {
			Logger.getInstance().d(TAG, "cookies is empty");
		} else {
			Logger.getInstance().d(TAG, "cookies is not empty");
		}

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie != null) {
					Logger.getInstance().d(TAG,
							"saveCookies:" + cookie.toString());
					if (cookie instanceof SetCookie) {
						SetCookie setCookie = (SetCookie) cookie;
						// 重新设置Domain，二级域名
						if (App.DEBUG_LEVEL == App.DEBUG_LEVEL_TEST) {
							setCookie.setDomain(API.COOKIE_DOMAIN);
						} else {
							setCookie.setDomain(API.HTTP_COOKIE_DOMAIN);
						}
					}
					cookieStore.addCookie(cookie);
				}
			}
		}

		// Cookies保存到数据库
		boolean result = DataMgr.getInstance(context).saveCookies(cookies);
		Logger.getInstance().d(TAG, "saveCookies result:" + result);
	}

	/**
	 * 清除Cookies
	 */
	public static void clearCookies() {
		if (cookieStore != null) {
			cookieStore.clear();
		}
	}

	/**
	 * GET请求
	 *
	 * @param api
	 * @param params
	 * @param callBack
	 */
	public static void get(String api, RequestParams params,
							RequestCallBack callBack) {
		String url = buildUrl(api);
		if (url == null) {
			return;
		}

		if (httpUtils != null) {
			Log.d(TAG, "httpUtils is not null");
			httpUtils.send(GET, url, params, callBack);
		} else {
			Log.d(TAG, "httpUtils is null");
			HttpException exception = new HttpException(0,
					BaseModel.SYSTEM_ERROR);
			callBack.onFailure(exception, BaseModel.SYSTEM_ERROR);
		}
	}
	public static void get(String url , RequestCallBack callBack) {

		if (url == null) {
			return;
		}
		if (httpUtils != null) {
			Log.d(TAG, "httpUtils is not null");
			httpUtils.send(GET, url, null, callBack);
		} else {
			Log.d(TAG, "httpUtils is null");
			HttpException exception = new HttpException(0,
					BaseModel.SYSTEM_ERROR);
			callBack.onFailure(exception, BaseModel.SYSTEM_ERROR);
		}
	}

	/**
	 * @param base
	 * @param api
	 * @param params
	 * @param callBack
	 */
	public static void get(@NonNull String base, String api, RequestParams params, RequestCallBack callBack) {
		String url = base + api;
		if (httpUtils != null) {
			Log.d(TAG, "httpUtils is not null");
			httpUtils.send(GET, url, params, callBack);
		} else {
			Log.d(TAG, "httpUtils is null");
			HttpException exception = new HttpException(0,
					BaseModel.SYSTEM_ERROR);
			callBack.onFailure(exception, BaseModel.SYSTEM_ERROR);
		}
	}

	/**
	 * GET请求静态资源
	 *
	 * @param url
	 * @param params
	 * @param callBack
	 */
	public static void getStatic(String url, RequestParams params,
								 RequestCallBack callBack) {
		if (url == null) {
			return;
		}

		if (httpUtils != null) {
			Log.d(TAG, "httpUtils is not null");
			httpUtils.send(GET, url, params, callBack);
		} else {
			Log.d(TAG, "httpUtils is null");
			HttpException exception = new HttpException(0,
					BaseModel.SYSTEM_ERROR);
			callBack.onFailure(exception, BaseModel.SYSTEM_ERROR);
		}
	}

	/**
	 * POST请求
	 *
	 * @param api
	 * @param params
	 * @param callBack
	 */
	public static void post(String api, RequestParams params,
							RequestCallBack callBack) {
		String url = buildUrl(api);
		if (url == null) {
			return;
		}

		if (httpUtils != null) {
			Log.d(TAG, "httpUtils is not null");
			httpUtils.send(POST, url, params, callBack);
		} else {
			Log.d(TAG, "httpUtils is null");
			HttpException exception = new HttpException(0,
					BaseModel.SYSTEM_ERROR);
			callBack.onFailure(exception, BaseModel.SYSTEM_ERROR);
		}
	}

	/**
	 * @param base
	 * @param api
	 * @param params
	 * @param callBack
	 */
	public static void post(@NonNull String base, String api, RequestParams params, RequestCallBack callBack) {
		String url = base + api;
		if (httpUtils != null) {
			Log.d(TAG, "httpUtils is not null");
			httpUtils.send(POST, url, params, callBack);
		} else {
			Log.d(TAG, "httpUtils is null");
			HttpException exception = new HttpException(0,
					BaseModel.SYSTEM_ERROR);
			callBack.onFailure(exception, BaseModel.SYSTEM_ERROR);
		}
	}

	/**
	 * 文件下载
	 *
	 * @param url
	 * @param target
	 * @param callBack
	 */
	public static void download(String url, String target, RequestCallBack<File> callBack) {
		if (httpUtils != null) {
			httpUtils.download(url, target, callBack);
		}
	}

	/**
	 * 构造请求URL
	 *
	 * @return
	 */
	private static String buildUrl(String api) {
		if (api == null) {
			return null;
		}
		String base = null;
		// 判断调试级别
		switch (App.DEBUG_LEVEL) {
			case App.DEBUG_LEVEL_TEST:
				// 测试环境
				base = API.API_TEST;
				break;
			case App.DEBUG_LEVEL_HTTP:
				// 生产环境-http
				base = API.API_HTTP;
				break;
			case App.DEBUG_LEVEL_HTTPS:
				// 生产环境-https
				base = API.API_HTTPS;
				break;
			default:
				base = API.API_HTTPS;
				break;
		}
		return base + api;
	}
}
