package cn.app.yimirong.model;

import android.content.Context;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.app.yimirong.model.base.BaseModel;
import cn.app.yimirong.model.bean.ActivityTime;
import cn.app.yimirong.model.bean.Bank;
import cn.app.yimirong.model.bean.FragSort;
import cn.app.yimirong.model.bean.JinJiNotice;
import cn.app.yimirong.model.bean.Message;
import cn.app.yimirong.model.bean.OnlineGoods;
import cn.app.yimirong.model.bean.Province;
import cn.app.yimirong.model.bean.Questions;
import cn.app.yimirong.model.bean.Quota;
import cn.app.yimirong.model.bean.ShareInvite;
import cn.app.yimirong.model.bean.SpecialDays;
import cn.app.yimirong.model.bean.SystemInfo;
import cn.app.yimirong.model.bean.VersionData;
import cn.app.yimirong.model.bean.WithDrawTips;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.model.http.Http;
import cn.app.yimirong.model.http.IResponseHandler;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.model.inter.CheckInstallMarkCallBack;
import cn.app.yimirong.utils.JSONUtils;
import cn.app.yimirong.utils.TimeUtils;


/**
 * Created by android on 2015/10/28.
 */
public class CModel extends BaseModel {
	private static final String TAG = "CModel";

	private static CModel model;

	private CModel(Context context) {
		super(context);
	}

	public static synchronized CModel getInstance(Context context) {
		if (model == null) {
			model = new CModel(context);
		}
		return model;
	}

	/**
	 * 获取银行列表
	 *
	 * @param handler
	 */
	public void getBankList(final IResponseHandler handler) {
		logger.i("TAG", API.NEW_BANK_LIST);
		Http.getStatic(API.NEW_BANK_LIST, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					// 解析json数据
					List<Bank> list = new ArrayList<>();
					JSONObject jsonObj = new JSONObject(response.result);
					Iterator<String> keys = jsonObj.keys();
					while (keys.hasNext()) {
						String key = keys.next();
						JSONObject jsonBank = jsonObj
								.getJSONObject(key);
						Bank bank = new Bank();
						bank.code = jsonBank.optString("code");
						bank.name = jsonBank.optString("name");
						bank.url = API.BANK_ICON_BASE + key + ".jpg";
						bank.single = jsonBank.optInt("single", 0);
						bank.singleDay = jsonBank.optInt("singleDay", 0);
						bank.singelMonth = jsonBank.optInt("singleMonth", 0);
						if (!jsonBank.isNull("ishide")) {
							bank.isShow = false;
						}
						bank.plat = jsonBank.optString("plat", "llpay");

						JSONObject jsonQuota = jsonBank.optJSONObject("quota");
						Iterator<String> quotaKeys = jsonQuota.keys();
						List<Quota> quotas = new ArrayList<Quota>();
						while (quotaKeys.hasNext()) {
							String plat = quotaKeys.next();
							JSONObject jsonPlat = jsonQuota.optJSONObject(plat);
							if (jsonPlat != null) {
								Quota quota = new Quota();
								quota.plat = plat;
								quota.bankCode = key;
								quota.single = jsonPlat.optInt("single", 0);
								quota.singleDay = jsonPlat.optInt("singleDay", 0);
								quota.singleMonth = jsonPlat.optInt("singleMonth", 0);
								quotas.add(quota);
							}
						}
						bank.quotas = quotas;
						list.add(bank);
					}
					if (handler != null) {
						handler.onSuccess(response.result, list);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}
			}

			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}
		});
	}

	/**
	 * 获取银行开户地地址
	 * @param handler
	 */
	public static void getBankOfDeposit(final IResponseHandler handler) {

		Http.get(API.BANK_OF_DEPOSIT , new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
					List<Province> list = new ArrayList<Province>();
                   JSONArray jsonArray = new JSONArray(responseInfo.result);
                    for (int i = 0; i < jsonArray.length(); i++) {
						Province province = new Province();
						province.list = new LinkedHashMap<String, String>();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						String name = jsonObject.getString("name");
						province.name = name;
						JSONObject city = jsonObject.getJSONObject("city");
						Iterator<String> keys = city.keys();
						while (keys.hasNext()) {
							String cityName = keys.next();
							String cityid = (String) city.get(cityName);
							province.list.put(cityName,cityid);
						}
						list.add(province);
					}
					handler.onSuccess(responseInfo.result,list);
                }catch (JSONException e) {
                    e.printStackTrace();
                    if (handler != null) {
                        handler.onFailure("-1", SYSTEM_ERROR);
                    }
                }

			}

			@Override
			public void onFailure(HttpException e, String s) {
				if (handler != null) {
					handler.onFailure(e.toString(), s);
				}
			}
		});
	}
	/**
	 * 获取城市列表
	 *
	 * @param handler
	 */
	@Deprecated
	public void getCityList(final IResponseHandler handler) {
		Http.getStatic(API.CITY_LIST, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					// 开始解析
					JSONArray jsonArray = new JSONArray(response.result);
					List<Province> list = new ArrayList<>();
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray
								.optJSONObject(i);
						Province province = new Province();
						province.name = jsonObject.optString("name");
						province.code = jsonObject.optString("code");
						// 城市列表
						JSONObject jsonCities = jsonObject
								.optJSONObject("list");
						// 城市列表转成map
						Map<String, String> map = new HashMap<>();
						Iterator<String> keys = jsonCities.keys();
						while (keys.hasNext()) {
							// 获取键值
							String key = keys.next();
							// 存入map
							map.put(key, jsonCities.optString(key));
						}
						province.list = map;
						list.add(province);
					}
					if (handler != null) {
						handler.onSuccess(response.result, list);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1",
								SYSTEM_ERROR);
					}
				}
			}

			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}

		});
	}

	/**
	 * 用户反馈
	 *
	 * @param phone
	 * @param content
	 * @param handler
	 */
	public void sendFeedBack(String phone, String content,
							 final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", phone);
		params.addBodyParameter("content", content);
		Http.post(API.FEED_BACK, params, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					if (error == 0) {
						if (handler != null) {
							handler.onSuccess(response.result, null);
						}
					} else {
						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}
			}
		});
	}

	/**
	 * 获取服务器时间
	 *
	 * @param handler
	 */
	public void getServerTime(final IResponseHandler handler) {
		Http.get(API.SERVER_TIME, null, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						long time = System.currentTimeMillis() / 1000;
						if (jsonData != null) {
							time = jsonData.optLong("servertime", time);
						}

						if (handler != null) {
							handler.onSuccess(response.result, time);
						}

					} else {
						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}
			}
		});
	}

	/**
	 * 获取公告
	 *
	 * @param handler
	 */
	public void getMessages(int page, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("page", page + "");
		Http.post(API.GET_MESSAGES, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String message = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						JSONArray jsonList = null;
						if (jsonData != null) {
							jsonList = jsonData.optJSONArray("list");
						}
						List<Message> list = new ArrayList<>();
						if (jsonList != null) {
							int length = jsonList.length();
							for (int i = 0; i < length; i++) {
								JSONObject json = jsonList
										.optJSONObject(i);
								Message msg = JSONUtils
										.parseJsonString(Message.class,
												json.toString());
								list.add(msg);
							}
						}

						if (handler != null) {
							handler.onSuccess(response.result, list);
						}

					} else {
						if (handler != null) {
							handler.onFailure(error + "", message);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}
			}

			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}
		});
	}

	/**
	 * 获取紧急公告
	 *
	 * @param handler
	 */
	public void getJinJiNotice(final IResponseHandler handler) {
		Http.get(API.JINJI_NOTICE, null, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						JSONObject jsonNotice = null;
						if (jsonData != null) {
							jsonNotice = jsonData.optJSONObject("notice");
						}

						JinJiNotice notice = null;
						if (jsonNotice != null) {
							notice = JSONUtils.parseJsonString(
									JinJiNotice.class,
									jsonNotice.toString());
						}

						if (handler != null) {
							handler.onSuccess(response.result, notice);
						}

					} else {
						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}
			}
		});
	}

	/**
	 * 检查系统信息
	 *
	 * @param version 当前版本 1.0.2
	 * @param plat    渠道名称 例如：360、myapp、mi
	 * @param os      操作系统 例如：android android4.0 android4.2.2 android4.4.2
	 * @param handler
	 */
	public void getSystemInfo(String version, String plat, String os,
							  int type, final ResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("version", version);
		params.addBodyParameter("plat", plat);
		params.addBodyParameter("os", os);
		params.addBodyParameter("type", type + "");
		Http.post(API.CHECK_SYSTEM_INFO, params, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						SystemInfo systemInfo = null;
						if (jsonData != null) {
							systemInfo = JSONUtils.parseJsonString(
									SystemInfo.class,
									jsonData.toString());
						}
						if (handler != null) {
							handler.onSuccess(response.result, systemInfo);
						}
					} else {
						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}
			}
		});
	}

	/**
	 * 获取放假和调班
	 *
	 * @param handler
	 */
	public void getSpecialDays(final IResponseHandler handler) {
		Http.getStatic(API.SPECIAL_DAYS, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				String json = response.result;
				SpecialDays sd = TimeUtils.parseSpecialDays(json);
				if (handler != null) {
					handler.onSuccess(json, sd);
				}
			}

			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}
		});
	}

	/**
	 * 获取邀请好友相关数据
	 *
	 * @param handler
	 */
	public void getShareInviteDesc(final IResponseHandler handler) {
		Http.getStatic(API.SHARE_INVITE_DESC, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);

					ShareInvite shareInvite = JSONUtils
							.parseJsonString(ShareInvite.class,
									jsonObj.toString());
					if (shareInvite != null) {
						if (handler != null) {
							handler.onSuccess(response.result,
									shareInvite);
						}
					} else {
						if (handler != null) {
							handler.onFailure("-1", SYSTEM_ERROR);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}
			}

			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}
		});
	}

	/**
	 * 获取常见问题
	 *
	 * @param handler
	 */
	public void getQuestions(final IResponseHandler handler) {
		Http.getStatic(API.QUESTION_ANSWER, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					JSONArray jsonArray = jsonObj.optJSONArray("list");
					List<Questions> list = new ArrayList<>();
					if (jsonArray != null) {
						int len = jsonArray.length();
						for (int i = 0; i < len; i++) {
							JSONObject json = jsonArray
									.optJSONObject(i);
							Questions q = JSONUtils.parseJsonString(
									Questions.class, json.toString());
							list.add(q);
						}
					}

					if (handler != null) {
						handler.onSuccess(response.result, list);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}
			}

			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}
		});
	}

	/**
	 * Android获取版本
	 */
	public void getVersion(final IResponseHandler handler) {
		Http.get(API.GET_VERSION, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					String msg = jsonObj.optString("msg", null);
					if (error == 0) {
						if (jsonData != null) {
							VersionData data = new VersionData();
							data.version = jsonData.optString("version");
							data.paytype = jsonData.optString("paytype");
							data.pay_qudao = jsonData.optString("pay_qudao");
							data.withdraw_txt = jsonData.optString("withdraw_txt");
							data.withdraw_sxf = jsonData.optInt("withdraw_sxf");
							data.withdraw_tips = jsonData.optBoolean("withdraw_tips");
							data.yee_amount_limit = jsonData.optDouble("yee_amount_limit", 0.00d);
							List<String> pay_list = new ArrayList<String>();
							JSONArray jsonArray = jsonData.optJSONArray("pay_list");
							if (jsonArray != null) {
								for (int i = 0; i < jsonArray.length(); i++) {
									pay_list.add(jsonArray.optString(i));
								}
							}
							data.pay_list = pay_list;

							if (handler != null) {
								handler.onSuccess(response.result, data);
							}
						}
					} else {
						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}
			}

			@Override
			public void onFailure(HttpException e, String msg) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});
	}

	/**
	 * 获取证件类型列表
	 *
	 * @param handler
	 */
	public void getIDTypes(final ResponseHandler handler) {
		Http.getStatic(API.GET_ID_TYPES, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				Map<String, String> map = new HashMap<String, String>();
				try {
					JSONArray jsonArray = new JSONArray(response.result);
					int len = jsonArray.length();
					for (int i = 0; i < len; i++) {
						JSONObject jsonObj = (JSONObject) jsonArray.get(i);
						String id = jsonObj.optString("id");
						String name = jsonObj.optString("name");
						map.put(name, id);
					}
					if (handler != null) {
						handler.onSuccess(response.result, map);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}
			}

			@Override
			public void onFailure(HttpException e, String msg) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});
	}

	public void getFragSort(final IResponseHandler handler) {
		Http.getStatic(API.FRAG_SORT, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					FragSort fragSort = new FragSort();
					fragSort.kh = jsonObj.optInt("kh", 2);
					fragSort.dq = jsonObj.optInt("dq", 1);
					fragSort.kl = jsonObj.optInt("kl", 0);
					if (handler != null) {
						handler.onSuccess(response.result, fragSort);
					}
				} catch (JSONException e) {
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}
			}

			@Override
			public void onFailure(HttpException e, String msg) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});
	}


	public void getRegistEXP(final IResponseHandler handler) {
		Http.get(API.GET_REGIST_EXP, null, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				JSONObject jsonObj = null;
				try {
					jsonObj = new JSONObject(responseInfo.result);
					int error = jsonObj.optInt("error", -1);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						String amount = jsonData.optString("amount");
						if (handler != null && amount!=null) {
							handler.onSuccess(responseInfo.result, amount);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				if (handler != null) {
					handler.onFailure(error.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});

	}


	/**
	 * 获取兑换记录
	 * */
	public void getDuihuanList(int page,final IResponseHandler handler){

		RequestParams params = new RequestParams();
		params.addBodyParameter("page",page+"");
		Http.post(API.GET_DUIHUAN_LIST, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				JSONObject jsonObj = null;
				try {
					jsonObj = new JSONObject(responseInfo.result);
					int error = jsonObj.optInt("error",-1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONArray jsonData = jsonObj.optJSONArray("data");
					if (error == 0){
						List<OnlineGoods> list = new ArrayList<OnlineGoods>();
						if (jsonData != null){
							int len = jsonData.length();
							for (int i = 0; i < len; i++) {
								JSONObject json = jsonData
										.optJSONObject(i);
								OnlineGoods log = JSONUtils
										.parseJsonString(
												OnlineGoods.class,
												json.toString());
								list.add(log);
							}
						}
						if (handler != null) {
							handler.onSuccess(responseInfo.result, list);
						}
					}else {
						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				if (handler != null) {
					handler.onFailure(error.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});

	}

	/**
	 * 获得商品信息
	 * */
	public void getGoodsDesc(String id,final  IResponseHandler handler){

		RequestParams params = new RequestParams();
		params.addBodyParameter("id",id);
		Http.post(API.GET_GOODS_DESC, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				if (handler != null) {
					handler.onSuccess(responseInfo.result, null);
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				if (handler != null) {
					handler.onFailure(error.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}
		});

	}

	/**
	 * 兑换
	 * */
	public void duihuan(String id,int count,final IResponseHandler handler){

		RequestParams params = new RequestParams();
		params.addBodyParameter("id",id);
		params.addBodyParameter("count",count + "");
		Http.post(API.DUIHUAN, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				JSONObject jsonObj = null;
				try {
					jsonObj = new JSONObject(responseInfo.result);
					int error = jsonObj.optInt("error",-1);
					String msg = jsonObj.optString("msg");
					if (error == 0){
						if (handler != null) {
							handler.onSuccess(responseInfo.result, msg);
						}
					}else {
						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				if (handler != null) {
					handler.onFailure(error.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}
		});
	}

	/**
	 * 获取商品详情
	 * */
	public void getGoodsDetail(String id,final IResponseHandler handler){

		RequestParams params = new RequestParams();
		params.addBodyParameter("id",id);
		Http.post(API.GET_GOODS_DETAIL, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					JSONObject jsonObj = new JSONObject(responseInfo.result);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					int error = jsonObj.optInt("error",-1);
					if (error == 0){
						OnlineGoods goods = new OnlineGoods();
						if (jsonData != null){
							goods.id = jsonData.optString("id");
							goods.jifeng = jsonData.optString("jifeng");
							goods.stock = jsonData.optString("stock");
							goods.sold = jsonData.optString("sold");
							goods.ctime = jsonData.optString("ctime");
							goods.img = jsonData.optString("img");
							goods.name = jsonData.optString("name");
							goods.money = jsonData.optString("money");
							goods.rank = jsonData.optString("rank");
							goods.yuanjifeng = jsonData.optString("yuanjifeng");
						}
						if (handler != null) {
							handler.onSuccess(responseInfo.result, goods);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				if (handler != null) {
					handler.onFailure(error.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}
		});

	}


	/**
	 * 获取体验金活动的起始时间
	 *
	 * @param handler
	 */
	public void getActivityTime(final IResponseHandler handler) {
		Http.get(API.GET_ACTIVITY_TIME, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String message = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						Iterator<String> keys = jsonData.keys();
						if (keys != null && keys.hasNext()) {
							String key = keys.next();
							ActivityTime at = new ActivityTime();
							at.type = Integer.parseInt(key);
							JSONObject jsonKey = jsonData.optJSONObject(key);
							at.startTime = jsonKey.optString("starttime", null);
							at.endTime = jsonKey.optString("endtime", null);
							at.expmoney = jsonKey.optInt("expmoney", 0);
							if (handler != null) {
								handler.onSuccess(response.result, at);
							}
						}
					} else {
						if (handler != null) {
							handler.onFailure(error + "", message);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}
			}

			@Override
			public void onFailure(HttpException e, String msg) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});
	}

	public void getWithDrawTips(final IResponseHandler handler) {
		Http.getStatic(API.WITHDRAW_TIPS, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				WithDrawTips tips = JSONUtils.parseJsonString(WithDrawTips.class, response.result);
				if (handler != null) {
					handler.onSuccess(response.result, tips);
				}
			}

			@Override
			public void onFailure(HttpException e, String msg) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});
	}

    public void checkInstallMark(String uid, String android, String channel, final CheckInstallMarkCallBack checkInstallMarkCallBack) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("device_num",uid);
		params.addBodyParameter("device_type",android);
		params.addBodyParameter("qudao",channel);
		Http.post(API.CHECK_INSTALL_MARK, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					JSONObject jsonObj = new JSONObject(responseInfo.result);
					int error = jsonObj.optInt("error", -1);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						String qudao = jsonData.optString("qudao");
						checkInstallMarkCallBack.onSucceed(qudao);
					} else {
						checkInstallMarkCallBack.onFailure();
					}

				} catch (JSONException e) {
					e.printStackTrace();
					checkInstallMarkCallBack.onFailure();
				}
			}
			@Override
			public void onFailure(HttpException e, String s) {
				checkInstallMarkCallBack.onFailure();
			}
		});
    }
}
