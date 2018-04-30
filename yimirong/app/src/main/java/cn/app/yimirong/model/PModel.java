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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import cn.app.yimirong.model.base.BaseModel;
import cn.app.yimirong.model.bean.Banner;
import cn.app.yimirong.model.bean.BaseProduct;
import cn.app.yimirong.model.bean.BuyRecord;
import cn.app.yimirong.model.bean.BuyResult;
import cn.app.yimirong.model.bean.DQProduct;
import cn.app.yimirong.model.bean.KHProduct;
import cn.app.yimirong.model.bean.KLProduct;
import cn.app.yimirong.model.bean.ProductList;
import cn.app.yimirong.model.bean.ProductMore;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.model.http.Http;
import cn.app.yimirong.model.http.IResponseHandler;
import cn.app.yimirong.utils.JSONUtils;
import cn.app.yimirong.utils.SystemUtils;

public class PModel extends BaseModel {

	private static final String TAG = "PModel";

	private static PModel model;

	private PModel(Context context) {
		super(context);
	}

	public synchronized static PModel getInstance(Context context) {
		if (model == null) {
			model = new PModel(context);
		}
		return model;
	}

	/**
	 * 获取推荐页Banner列表
	 *
	 * @param handler 响应处理
	 */
	public void getBannerList(final IResponseHandler handler) {
		Http.get(API.Product.RECOMMEND_BANNER, null, new RequestCallBack<String>() {
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
					JSONObject jsonData = jsonObj.optJSONObject("data");
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					if (error == 0) {
						List<Banner> list = new ArrayList<>();
						JSONObject jsonBanner = null;
						if (jsonData != null) {
							jsonBanner = jsonData.optJSONObject("banner");
						}
						if (jsonBanner != null) {
							Iterator<String> keys = jsonBanner.keys();
							while (keys != null && keys.hasNext()) {
								String key = keys.next();
								JSONObject json = jsonBanner.optJSONObject(key);
								Banner banner = JSONUtils.parseJsonString(Banner.class,
										json.toString());
								int index = Integer.parseInt(key);
								banner.index = index;
								list.add(banner);
							}
							Collections.sort(list,
									new Comparator<Banner>() {

										@Override
										public int compare(Banner o1,
														   Banner o2) {
											if (o1.index > o2.index) {
												return 1;
											}

											if (o1.index < o2.index) {
												return -1;
											}

											return 0;
										}
									});
						}

						if (handler != null) {
							handler.onSuccess(response.result, list);
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
	 * 加载精品推荐的产品数据
	 *
	 * @param handler
	 */
	public void getRecomProduct(final IResponseHandler handler) {
		Http.get(API.Product.RECOMMEND_PRODUCT, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						BaseProduct product = null;
						if (jsonData != null) {
							JSONObject jsonRecom = jsonData.optJSONObject("recommend");
							if (jsonRecom != null) {
								String type = jsonRecom.optString("longproduct", null);
								if ("1".equals(type)) {
									//活期
									product = JSONUtils.parseJsonString(KHProduct.class, jsonRecom.toString());
								} else {
									//定期
									product = JSONUtils.parseJsonString(DQProduct.class, jsonRecom.toString());
								}
							}
						}
						if (handler != null) {
							handler.onSuccess(response.result, product);
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
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}
		});
	}


	/**
	 * 加载详细的产品数据
	 *
	 * @param handler
	 */
	public void getProductDetail(final int ptype,
								 String pid,
								 final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("pid", pid);
		String api;
		if (ptype == BaseProduct.TYPE_KH) {
			logger.i(TAG, "快活宝产品详情");
			api = API.Product.KH_PRODUCT_DETAIL;
		} else if (ptype == BaseProduct.TYPE_DQ) {
			logger.i(TAG, "产品详情");
			api = API.Product.DQ_PRODUCT_DETAIL;
		} else if (ptype == BaseProduct.TYPE_KL) {
			logger.i(TAG, "快乐宝产品详情");
			api = API.Product.KL_PRODUCT_DETAIL;
		} else {
			return;
		}
		Http.post(api, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						BaseProduct detail = null;
						if (jsonData != null) {
							JSONObject jsonDetail = jsonData.optJSONObject("detail");
							if (jsonDetail != null) {
								if (ptype == BaseProduct.TYPE_DQ) {
									detail = JSONUtils.parseJsonString(DQProduct.class, jsonDetail.toString());
								} else if (ptype == BaseProduct.TYPE_KH) {
									detail = JSONUtils.parseJsonString(KHProduct.class, jsonDetail.toString());
								} else if (ptype == BaseProduct.TYPE_KL) {
									detail = JSONUtils.parseJsonString(KLProduct.class, jsonDetail.toString());
								} else ;
							}
						}
						if (handler != null) {
							handler.onSuccess(response.result, detail);
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
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}
		});
	}

	/**
	 * 购买产品
	 *
	 * @param pid     产品ID
	 * @param money   金额
	 * @param paytype 购买方式
	 * @param handler
	 */
	public void buyProduct(int ptype,
						   String pid,
						   double money,
						   int paytype,
						   String tpwd,
						   String cid,
						   final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("pid", pid);
		params.addBodyParameter("money", SystemUtils.getDoubleStr(money));
		params.addBodyParameter("paytype", paytype + "");
		params.addBodyParameter("tpwd", tpwd);
		params.addBodyParameter("cid", cid);
		String api;
		if (ptype == BaseProduct.TYPE_KH) {
			logger.i(TAG, "购买快活宝");
			api = API.Product.BUY_KH_PRODUCT;
		} else if (ptype == BaseProduct.TYPE_DQ) {
			logger.i(TAG, "购买产品");
			api = API.Product.BUY_DQ_PRODUCT;
		} else if (ptype == BaseProduct.TYPE_KL) {
			logger.i(TAG, "购买快乐宝");
			api = API.Product.BUY_KL_PRODUCT;
		} else {
			return;
		}
		Http.post(api, params, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null && e != null) {
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
						BuyResult result = null;
						if (jsonData != null) {
							result = JSONUtils.parseJsonString(
									BuyResult.class,
									jsonData.toString());
						}

						if (result != null) {
							result.activity = jsonObj.optBoolean("activity", false);
						}

						if (handler != null) {
							handler.onSuccess(response.result, result);
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
	 * 获取产品列表
	 *
	 * @param handler
	 */
	@Deprecated
	private void getProductList(final IResponseHandler handler) {
		Http.get(API.Product.PRODUCT_LIST, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						ProductList pl = new ProductList();
						pl.product = new ArrayList<>();
						pl.longproduct = new ArrayList<>();
						JSONArray jsonProduct = null;
						JSONArray jsonLongProduct = null;
						if (jsonData != null) {
							jsonProduct = jsonData
									.optJSONArray("product");
							jsonLongProduct = jsonData
									.optJSONArray("longproduct");
						}

						if (jsonProduct != null
								&& jsonProduct.length() > 0) {
							for (int i = 0; i < jsonProduct.length(); i++) {
								JSONObject json = jsonProduct.optJSONObject(i);
								DQProduct p = JSONUtils.parseJsonString(
										DQProduct.class,
										json.toString());
								pl.product.add(p);
							}
						}

						if (jsonLongProduct != null
								&& jsonLongProduct.length() > 0) {
							for (int i = 0; i < jsonLongProduct
									.length(); i++) {
								JSONObject json = jsonLongProduct.optJSONObject(i);
								KHProduct p = JSONUtils.parseJsonString(
										KHProduct.class,
										json.toString());
								pl.longproduct.add(p);
							}
						}

						if (handler != null) {
							handler.onSuccess(response.result, pl);
						}

					} else {
						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}

				} catch (JSONException e) {
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
	 * 获取快活宝产品列表
	 *
	 * @param handler
	 */
	public void getKHProducts(final IResponseHandler handler) {
		Http.get(API.Product.KH_PRODUCT_LIST, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String response = responseInfo.result;
				try {
					JSONObject jsonObj = new JSONObject(response);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						ProductMore pm = new ProductMore();
						pm.product = new ArrayList<>();
						pm.sellout = new ArrayList<>();
						pm.defaultIncome = jsonObj.optString("defaultIncome");
						if (jsonData != null) {
							JSONArray jsonProduct = jsonData.optJSONArray("longproduct");
							JSONArray jsonSellout = jsonData.optJSONArray("sellout");
							if (jsonProduct != null) {
								int len = jsonProduct.length();
								for (int i = 0; i < len; i++) {
									JSONObject json = jsonProduct.optJSONObject(i);
									if (json != null) {
										BaseProduct p = JSONUtils.parseJsonString(KHProduct.class, json.toString());
										p.state = BaseProduct.STATE_SELLING;
										pm.product.add(p);
									}
								}
							}

							if (jsonSellout != null) {
								int len = jsonSellout.length();
								for (int i = 0; i < len; i++) {
									JSONObject json = jsonSellout.optJSONObject(i);
									if (json != null) {
										BaseProduct p = JSONUtils.parseJsonString(KHProduct.class, json.toString());
										p.state = BaseProduct.STATE_SELLOUT;
										pm.sellout.add(p);
									}
								}
							}
						}
						if (handler != null && response!=null) {
							handler.onSuccess(response, pm);
						}
					} else {
						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}
				} catch (JSONException e) {
					if (handler != null) {
						handler.onFailure("-1", SYSTEM_ERROR);
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				if (handler != null && error != null) {
					handler.onFailure(error.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});
	}

	/**
	 * 获取定期产品列表
	 *
	 * @param handler
	 */
	public void getDQProducts(final IResponseHandler handler) {
		Http.get(API.Product.DQ_PRODUCT_LIST, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String response = responseInfo.result;
				try {
					JSONObject jsonObj = new JSONObject(response);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						ProductMore pm = new ProductMore();
						pm.product = new ArrayList<>();
						pm.complete = new ArrayList<>();
						pm.sellout = new ArrayList<>();
						if (jsonData != null) {
							JSONArray jsonProduct = jsonData.optJSONArray("product");
							JSONArray jsonComplete = jsonData.optJSONArray("complete");
							JSONArray jsonSellout = jsonData.optJSONArray("sellout");
							if (jsonProduct != null) {
								int len = jsonProduct.length();
								for (int i = 0; i < len; i++) {
									JSONObject json = jsonProduct.optJSONObject(i);
									if (json != null) {
										BaseProduct p = JSONUtils.parseJsonString(DQProduct.class, json.toString());
										p.state = BaseProduct.STATE_SELLING;
										pm.product.add(p);
									}
								}
							}

							if (jsonComplete != null) {
								int len = jsonComplete.length();
								for (int i = 0; i < len; i++) {
									JSONObject json = jsonComplete.optJSONObject(i);
									if (json != null) {
										BaseProduct p = JSONUtils.parseJsonString(DQProduct.class, json.toString());
										p.state = BaseProduct.STATE_COMPLETE;
										pm.complete.add(p);
									}
								}
							}

							if (jsonSellout != null) {
								int len = jsonSellout.length();
								for (int i = 0; i < len; i++) {
									JSONObject json = jsonSellout.optJSONObject(i);
									if (json != null) {
										BaseProduct p = JSONUtils.parseJsonString(DQProduct.class, json.toString());
										p.state = BaseProduct.STATE_SELLOUT;
										pm.sellout.add(p);
									}
								}
							}
						}
						if (handler != null && pm != null && response != null) {
							handler.onSuccess(response, pm);
						}
					} else {
						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}
				} catch (JSONException e) {
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
	 * webView跳转立即购买
	 */
	public void getLJbuyPriduct(String ptid, final String type, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("ptid", ptid);
		params.addBodyParameter("type", type);

		Http.post(API.Product.WEB_PRODUCT, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						BaseProduct product = null;
						if (type.equals("product")) {
							//定期
							product = JSONUtils.parseJsonString(DQProduct.class, jsonData.toString());
						} else {
							//活期
							product = JSONUtils.parseJsonString(KHProduct.class, jsonData.toString());
						}
						if (handler != null) {
							handler.onSuccess(response.result, product);
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
			public void onFailure(HttpException error, String msg) {
				if (handler != null) {
					handler.onFailure(error.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}
		});
	}


	/**
	 * 获取快乐宝产品列表
	 *
	 * @param handler
	 */
	public void getKLProducts(final IResponseHandler handler) {
		Http.get(API.Product.KL_PRODUCT_LIST, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String response = responseInfo.result;
				try {
					JSONObject jsonObj = new JSONObject(response);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						ProductMore pm = new ProductMore();
						pm.product = new ArrayList<>();
						pm.sellout = new ArrayList<>();
						if (jsonData != null) {
							JSONArray jsonProduct = jsonData.optJSONArray("klproduct");
							JSONArray jsonSellout = jsonData.optJSONArray("sellout");
							if (jsonProduct != null) {
								int len = jsonProduct.length();
								for (int i = 0; i < len; i++) {
									JSONObject json = jsonProduct.optJSONObject(i);
									if (json != null) {
										BaseProduct p = JSONUtils.parseJsonString(KLProduct.class, json.toString());
										p.state = BaseProduct.STATE_SELLING;
										pm.product.add(p);
									}
								}
							}

							if (jsonSellout != null) {
								int len = jsonSellout.length();
								for (int i = 0; i < len; i++) {
									JSONObject json = jsonSellout.optJSONObject(i);
									if (json != null) {
										BaseProduct p = JSONUtils.parseJsonString(KLProduct.class, json.toString());
										p.state = BaseProduct.STATE_SELLOUT;
										pm.sellout.add(p);
									}
								}
							}
						}
						if (handler != null) {
							handler.onSuccess(response, pm);
						}
					} else {
						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}


				} catch (JSONException e) {
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
	 * 获取更多产品列表
	 *
	 * @param handler
	 */
	@Deprecated
	private void getMoreProducts(final int ptype, final IResponseHandler handler) {
		String api;
		if (ptype == BaseProduct.TYPE_KH) {
			api = API.Product.KH_PRODUCT_LIST;
		} else {
			api = API.Product.DQ_PRODUCT_LIST;
		}
		Http.get(api, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						ProductMore more = new ProductMore();
						more.product = new ArrayList<>();
						more.complete = new ArrayList<>();
						more.sellout = new ArrayList<>();

						JSONArray jsonProduct = null;
						JSONArray jsonComplete = null;
						JSONArray jsonSellout = null;

						if (jsonData != null) {
							if (ptype == BaseProduct.TYPE_DQ) {
								jsonProduct = jsonData.optJSONArray("product");
							} else {
								jsonProduct = jsonData.optJSONArray("longproduct");
							}
							if (ptype == BaseProduct.TYPE_DQ) {
								jsonComplete = jsonData.optJSONArray("complete");
							}
							jsonSellout = jsonData.optJSONArray("sellout");
						}

						if (jsonComplete != null && jsonComplete.length() > 0) {
							for (int i = 0; i < jsonComplete.length(); i++) {
								JSONObject json = jsonComplete.optJSONObject(i);
								if (ptype == BaseProduct.TYPE_DQ) {
									DQProduct p = JSONUtils.parseJsonString(
											DQProduct.class,
											json.toString());
									p.state = BaseProduct.STATE_COMPLETE;
									more.complete.add(p);
								} else {
									KHProduct p = JSONUtils.parseJsonString(
											KHProduct.class,
											json.toString());
									p.state = BaseProduct.STATE_COMPLETE;
									more.complete.add(p);
								}
							}
						}

						if (jsonProduct != null && jsonProduct.length() > 0) {
							for (int i = 0; i < jsonProduct.length(); i++) {
								JSONObject json = jsonProduct.optJSONObject(i);
								if (ptype == BaseProduct.TYPE_DQ) {
									DQProduct p = JSONUtils.parseJsonString(
											DQProduct.class,
											json.toString());
									p.state = BaseProduct.STATE_SELLING;
									more.product.add(p);
								} else {
									KHProduct p = JSONUtils.parseJsonString(
											KHProduct.class,
											json.toString());
									p.state = BaseProduct.STATE_SELLING;
									more.product.add(p);
								}
							}
						}

						if (jsonSellout != null && jsonSellout.length() > 0) {
							for (int i = 0; i < jsonSellout.length(); i++) {
								JSONObject json = jsonSellout.optJSONObject(i);
								if (ptype == BaseProduct.TYPE_DQ) {
									DQProduct p = JSONUtils.parseJsonString(
											DQProduct.class,
											json.toString());
									p.state = BaseProduct.STATE_SELLOUT;
									more.sellout.add(p);
								} else {
									KHProduct p = JSONUtils.parseJsonString(
											KHProduct.class,
											json.toString());
									p.state = BaseProduct.STATE_SELLOUT;
									more.sellout.add(p);
								}
							}
						}

						// 返回结果
						if (handler != null) {
							handler.onSuccess(response.result, more);
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
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});
	}

	/**
	 * 获取产品的购买记录
	 *
	 * @param pid
	 * @param handler
	 */
	public void getProductBuyInfo(int ptype, String pid,
								  final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("pid", pid);

		String api;
		if (ptype == BaseProduct.TYPE_KH) {
			logger.i(TAG, "快活宝购买记录");
			api = API.Product.KH_BUY_RECORD;
		} else if (ptype == BaseProduct.TYPE_DQ) {
			logger.i(TAG, "产品购买记录");
			api = API.Product.DQ_BUY_RECORD;
		} else if (ptype == BaseProduct.TYPE_KL) {
			logger.i(TAG, "快乐宝购买记录");
			api = API.Product.KL_BUY_RECORD;
		} else {
			return;
		}
		Http.post(api, params, new RequestCallBack<String>() {
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
						List<BuyRecord> recordList = new ArrayList<>();
						if (jsonData != null) {
							JSONArray jsonList = jsonData.optJSONArray("list");
							int len = jsonList.length();
							if (len > 0) {
								for (int i = 0; i < len; i++) {
									JSONObject json = jsonList.optJSONObject(i);
									String account = json.optString("account");
									String money = json.optString("money");
									String ctime = json.optString("ctime");

									BuyRecord record = new BuyRecord();
									record.account = account;
									record.money = money;
									record.ctime = ctime;
									recordList.add(record);
								}
							}
						}

						if (handler != null) {
							handler.onSuccess(response.result,
									recordList);
						}

					} else {
						if (handler != null) {
							handler.onFailure("-1", msg);
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
	 * 获取产品合同信息
	 */
	public void getProductContract(int ptype, String cid, String type,
								   final IResponseHandler handler) {

		RequestParams params = new RequestParams();
		params.addBodyParameter("cid", cid);
		params.addBodyParameter("type", type);

		String api;
		if (ptype == BaseProduct.TYPE_KH) {
			api = API.Product.KH_PRODUCT_CONTRACT;
		} else if (ptype == BaseProduct.TYPE_DQ) {
			api = API.Product.DQ_PRODUCT_CONTRACT;
		} else if (ptype == BaseProduct.TYPE_KL) {
			api = API.Product.KL_PRODUCT_CONTRACT;
		} else {
			return;
		}
		Http.post(api, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				if (handler != null) {
					handler.onSuccess(response.result, null);
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
	 * 获取产品合同信息
	 */
	public void getProductContract2(String pid,
								   final IResponseHandler handler) {

		RequestParams params = new RequestParams();
		params.addBodyParameter("pid", pid);

		Http.post(API.Account.USER_PRODUCT_CONTRACT, params, new RequestCallBack<String>() {
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


	public void getProductContract3(String bid,
									final IResponseHandler handler) {

		RequestParams params = new RequestParams();
		params.addBodyParameter("bid", bid);

		Http.post(API.Account.USER_PRODUCT_CONTRACT, params, new RequestCallBack<String>() {
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


}
