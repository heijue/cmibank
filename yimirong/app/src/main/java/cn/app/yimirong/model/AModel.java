package cn.app.yimirong.model;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.app.yimirong.App;
import cn.app.yimirong.activity.ProfitListActivity;
import cn.app.yimirong.common.Constant;
import cn.app.yimirong.model.base.BaseModel;
import cn.app.yimirong.model.bean.ActionLog;
import cn.app.yimirong.model.bean.ComNews;
import cn.app.yimirong.model.bean.ExpMoney;
import cn.app.yimirong.model.bean.ExpMoneyBuy;
import cn.app.yimirong.model.bean.ExpMoneyLog;
import cn.app.yimirong.model.bean.HQProfit;
import cn.app.yimirong.model.bean.InviteFriend;
import cn.app.yimirong.model.bean.LoginData;
import cn.app.yimirong.model.bean.NewExp;
import cn.app.yimirong.model.bean.Notice;
import cn.app.yimirong.model.bean.PayResult;
import cn.app.yimirong.model.bean.ProfitDate;
import cn.app.yimirong.model.bean.ProfitDetail;
import cn.app.yimirong.model.bean.ProfitInfo;
import cn.app.yimirong.model.bean.SevenProfit;
import cn.app.yimirong.model.bean.UserCD;
import cn.app.yimirong.model.bean.UserDQMoney;
import cn.app.yimirong.model.bean.UserHQMoney;
import cn.app.yimirong.model.bean.UserInfo;
import cn.app.yimirong.model.bean.UserProduct;
import cn.app.yimirong.model.bean.UserProduct.SingleProduct;
import cn.app.yimirong.model.bean.UserProfit;
import cn.app.yimirong.model.bean.YaoQingAward;
import cn.app.yimirong.model.bean.YongJinAward;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.model.http.Http;
import cn.app.yimirong.model.http.IResponseHandler;
import cn.app.yimirong.model.http.IqueryWithDrawArriveCallBack;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.JSONUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.tongdun.android.shell.FMAgent;

public class AModel extends BaseModel {

	private static final String TAG = "AModel";

	private static AModel model;

	public synchronized static AModel getInstance(Context context) {
		if (model == null) {
			model = new AModel(context);
		}
		return model;
	}

	private AModel(Context context) {
		super(context);
	}

	/**
	 * 验证账号是否存在
	 *
	 * @param account
	 * @param handler
	 */
	public void verifyAccount(String account, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("account", account);
		Http.post(API.Account.VERIFY_ACCOUNT, params,
				new RequestCallBack<String>() {
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
	 * 发送金运通支付验证
	 *
	 * @param handler
	 */
	public void verifyJYTcode(String money, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("money", money);
		Http.post(API.Account.SEND_JYT_CODE, params,
				new RequestCallBack<String>() {
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
	 * 金运通验证交易密码
	 *
	 * @param handler
	 */
	public void verifyJYTpayPassword(String tpwd, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("tpwd", tpwd);
		Http.post(API.Account.JYT_VERFIFY, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> response) {
						try {
							JSONObject jsonObj = new JSONObject(response.result);
							int error = jsonObj.optInt("error", -1);
							String msg = jsonObj.optString("msg", SYSTEM_ERROR);
							if (error == 0) {
								if (handler != null && !response.result.isEmpty()) {
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
	 * 验证账号是否存在
	 *
	 * @param handler
	 */
	public void sendVerifyCode(String phone, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("phone", phone);
		Http.post(API.Account.SEND_VERIFY_CODE, params,
				new RequestCallBack<String>() {
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
	 * 注册验证码
	 */
	public void registerTest(String account, String verifycode, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("account", account);
		params.addBodyParameter("mobileVerify", verifycode);
		Http.post(API.Account.REGISTER_TEST, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					JSONObject jsonObj = new JSONObject(responseInfo.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					if (error == 0) {
						//成功
						if (handler != null) {
							handler.onSuccess(responseInfo.result, error);
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
					handler.onFailure(error.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});
	}

	/**
	 * 重置密码验证码
	 */
	public void resetrTest(String account, String verifycode, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("account", account);
		params.addBodyParameter("code", verifycode);
		Http.post(API.Account.RESETPASS_TEST, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					JSONObject jsonObj = new JSONObject(responseInfo.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					if (error == 0) {
						//成功
						if (handler != null) {
							handler.onSuccess(responseInfo.result, error);
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
					handler.onFailure(error.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});
	}


	/**
	 * 重置支付密码验证码
	 */
	public void resetPayTest(String verifycode, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("code", verifycode);
		Http.post(API.Account.RESETPAYPASS_TEST, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					JSONObject jsonObj = new JSONObject(responseInfo.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					if (error == 0) {
						//成功
						if (handler != null) {
							handler.onSuccess(responseInfo.result, msg);
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
					handler.onFailure(error.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});
	}


	/**
	 * 注册
	 *
	 * @param account    手机号
	 * @param password1  密码
	 * @param password2  重复密码
	 * @param verifycode 验证码
	 * @param handler
	 */
	public void register(String plat, String account, String password1,
						 String password2, String verifycode, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		String blackBox = FMAgent.onEvent(context);
		params.addBodyParameter("device", Constant.OS);
		params.addBodyParameter("plat", plat);
		params.addBodyParameter("account", account);
		params.addBodyParameter("password1", password1);
		params.addBodyParameter("password2", password2);
		params.addBodyParameter("blackbox", blackBox);
		params.addBodyParameter("mobileVerify", verifycode);
		Http.post(API.Account.REGISTER, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						// 登录成功，保存Cookies
						Http.saveCookies(context);
						LoginData loginData = null;
						if (jsonData != null) {
							loginData = JSONUtils.parseJsonString(
									LoginData.class, jsonData.toString());
						}
						if (handler != null) {
							handler.onSuccess(response.result, loginData);
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
	 * 登录
	 *
	 * @param account
	 * @param password
	 * @param handler
	 */
	public void login(String account, String password,
					  final IResponseHandler handler) {

		RequestParams params = new RequestParams();
		String blackBox = FMAgent.onEvent(context);
		params.addBodyParameter("device", Constant.OS);
		params.addBodyParameter("account", account);
		params.addBodyParameter("password", password);
		params.addBodyParameter("blackbox", blackBox);
		Http.post(API.Account.LOGIN, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						// 登录成功，保存Cookies
						Http.saveCookies(context);
						LoginData loginData = null;
						if (jsonData != null) {
							loginData = JSONUtils.parseJsonString(
									LoginData.class, jsonData.toString());
						}

						if (handler != null) {
							handler.onSuccess(response.result, loginData);
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
	 * 易宝支付开户
	 *
	 * @param bankCode
	 * @param cardno
	 * @param idcardno
	 * @param name
	 * @param phone
	 * @param handler
	 */
	@Deprecated
	public void yeeRegist(String bankCode, String cardno, String idcardno,
						  String name, String phone, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("bankid", bankCode);
		params.addBodyParameter("cardno", cardno);
		params.addBodyParameter("idcardno", idcardno);
		params.addBodyParameter("name", name);
		params.addBodyParameter("phone", phone);

		Http.post(API.YeePay.YEE_REGIST, params,
				new RequestCallBack<String>() {
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
								String requestid = null;
								if (jsonData != null) {
									requestid = jsonData.optString("requestid");
								}
								if (handler != null) {
									handler.onSuccess(response.result,
											requestid);
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
	 * 易宝支付开户短信校验
	 *
	 * @param requestid
	 * @param validatecode
	 * @param handler
	 */
	@Deprecated
	public void yeeRegistCheck(String requestid, String validatecode,
							   String bankcode, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("bankid", bankcode);
		params.addBodyParameter("requestid", requestid);
		params.addBodyParameter("validatecode", validatecode);
		Http.post(API.YeePay.YEE_REGIST_CHECK, params,
				new RequestCallBack<String>() {
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
	 * 
	 * 设置交易密码
	 *
	 * @param password
	 * @param handler
	 */
	public void setPayPassword(String password, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("tpwd", password);
		Http.post(API.Account.SET_PAY_PASS, params,
				new RequestCallBack<String>() {
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
	 * 获取用户信息
	 *
	 * @param handler
	 */
	public void getUserInfo(final IResponseHandler handler) {
		Http.get(API.Account.GET_USER_INFO, null,
				new RequestCallBack<String>() {
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
							int error = jsonObj.getInt("error");
							if (error == 0) {
								UserInfo userinfo = new UserInfo();
								JSONObject jsonData = jsonObj
										.optJSONObject("data");
								if (jsonData != null) {
									userinfo.balance = jsonData.optString(
											"balance", "0.00");
									userinfo.serverTime = jsonData.optLong(
											"server_time",
											System.currentTimeMillis() / 1000);
									userinfo.canBuyLong = (double) jsonData
											.optDouble("canbuyLongProduct", 0f);
									userinfo.top_uid = jsonData.optString("top_uid");
									userinfo.top_pwd = jsonData.optString("top_pwd");
									userinfo.qiandao = jsonData.optInt("qiandao",0);
									JSONObject jsonIdentity = jsonData
											.optJSONObject("identity");
									if (jsonIdentity != null) {
										String bankId = jsonIdentity
												.optString("bankid");
										String bankName = jsonIdentity
												.optString("bankname");

										String cardnoTop = jsonIdentity
												.optString("cardno_top");

										String cardNo = jsonIdentity
												.optString("cardno");
										String idCard = jsonIdentity
												.optString("idCard");

										String realName = jsonIdentity
												.optString("realname");

										String isnew = jsonIdentity
												.optString("isnew");

										boolean tpwd = jsonIdentity
												.optBoolean("tpwd");

										UserInfo.Identity identity = userinfo.new Identity();

										identity.bankid = bankId;
										identity.bankname = bankName;
										identity.nameCard = bankName + "(尾号"
												+ cardNo + ")";
										identity.cardnoTop = cardnoTop;
										identity.cardno = cardNo;
										identity.idCard = idCard;
										identity.realName = realName;
										identity.tpwd = tpwd;
										identity.isnew = isnew;

										userinfo.identity = identity;
									}
								}
								if (handler != null) {
									handler.onSuccess(response.result, userinfo);
								}

							} else {

								if (handler != null) {
									String msg = jsonObj.getString("msg");
									handler.onFailure(error + "", msg);
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
							if (handler != null) {
								handler.onSuccess(response.result, SYSTEM_ERROR);
							}
						}
					}
				});
	}

	/**
	 * 获取用户账户余额
	 *
	 * @param handler
	 */
	public void getUserBalance(final IResponseHandler handler) {
		Http.get(API.Account.GET_USER_BALANCE, null,
				new RequestCallBack<String>() {
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
							int error = -1;
							String msg = SYSTEM_ERROR;
							JSONObject jsonData = null;
							if (jsonObj != null) {
								error = jsonObj.optInt("error", -1);
								msg = jsonObj.optString("msg", null);
								if (!jsonObj.isNull("data")
										&& jsonObj.get("data") instanceof JSONObject) {
									jsonData = jsonObj.optJSONObject("data");
								}
							}

							if (error == 0) {
								String balance = "0.00";
								if (jsonData != null) {
									balance = jsonData.optString("balance",
											"0.00");
								}
								if (handler != null && balance != null && response.result != null) {
									handler.onSuccess(response.result, balance);
								}
							} else {
								if (handler != null) {
									handler.onFailure(error + "", msg);
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
							if (handler != null) {
								handler.onFailure(-1 + "", SYSTEM_ERROR);
							}
						}
					}
				});
	}

	/**
	 * 绑卡
	 *
	 * @param bankCode
	 * @param cardno
	 * @param zj
	 * @param idcardno
	 * @param name
	 * @param phone
	 * @param handler
	 */
	public void bindCard(String bankCode, String cardno, String zj,
						 String idcardno, String name, String phone, String verifyCode,String palt,
						 final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		String blackBox = FMAgent.onEvent(context);
		params.addBodyParameter("bankid", bankCode);
		params.addBodyParameter("cardno", cardno);
		params.addBodyParameter("zj", zj);
		params.addBodyParameter("idcardno", idcardno);
		params.addBodyParameter("name", name);
		params.addBodyParameter("phone", phone);
		params.addBodyParameter("blackbox", blackBox);
		params.addBodyParameter("validatecode", verifyCode);
//		params.addBodyParameter("cityid", cityid);
		params.addBodyParameter("palt",palt);
		Http.post(API.LLPay.LLPAY_REGIST, params,
				new RequestCallBack<String>() {
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
	 * 易宝注册检查
	 *
	 * @param handler
	 */
	public void yeeBindCheck(final IResponseHandler handler) {
		Http.get(API.YeePay.YEE_BIND_CHECK, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						if (handler != null) {
							int status = 0;
							if (jsonData != null) {
								status = jsonData.optInt("status", 0);
							}
							handler.onSuccess(response.result, status == 1 ? true : false);
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
					handler.onFailure(e.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}
		});
	}

	/**
	 * 易宝注册
	 *
	 * @param handler
	 */
	public void yeeRegist(final IResponseHandler handler) {
		Http.get(API.YeePay.REGIST_YEE, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						String requestid = null;
						if (jsonData != null) {
							requestid = jsonData.optString("requestid", null);
						}
						if (handler != null) {
							handler.onSuccess(response.result,
									requestid);
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
			public void onFailure(HttpException e, String s) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}
		});
	}

	/**
	 * 易宝注册确认
	 *
	 * @param requestid
	 * @param validatecode
	 * @param handler
	 */
	public void yeeRegistCheck(String requestid, String validatecode,
							   final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("requestid", requestid);
		params.addBodyParameter("validatecode", validatecode);
		Http.post(API.YeePay.REGIST_YEE_CHECK, params, new RequestCallBack<String>() {
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

			@Override
			public void onFailure(HttpException e, String s) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "",
							NETWORK_ERROR);
				}
			}
		});
	}

	/**
	 * 易宝支付
	 *
	 * @param money
	 * @param password
	 * @param handler
	 */
	public void yeePay(double money, String password, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("amount", SystemUtils.getDoubleStr(money));
		params.addBodyParameter("tpwd", password);
		Http.post(API.YeePay.YEE_PAY, params, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", NETWORK_ERROR);
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
						String orderid = null;
						if (jsonData != null) {
							orderid = jsonData.optString("orderid");
						}
						if (handler != null) {
							handler.onSuccess(response.result, orderid);
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
	 * 查询充值结果
	 */
	public void queryYeePayResult(String orderid, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("orderid", orderid);
		Http.post(API.YeePay.QUERY_PAY_RESULT, params, new RequestCallBack<String>() {

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
						PayResult result = null;
						if (jsonData != null) {
							result = JSONUtils.parseJsonString(
									PayResult.class,
									jsonData.toString());
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
	 * 查询充值结果
	 */
	public void queryLLPayResult(String dtOrder, String noOrder,
								 final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("dt_order", dtOrder);
		params.addBodyParameter("orderid", noOrder);
		Http.post(API.LLPay.QUERY_ORDER, params, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", NETWORK_ERROR);
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
						PayResult result = null;
						if (jsonData != null) {
							result = JSONUtils.parseJsonString(
									PayResult.class, jsonData.toString());
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
	 * 金运通绑卡
	 *
	 * @param bankCode
	 * @param cardno
	 * @param idcardno
	 * @param name
	 * @param phone
	 * @param msgcode
	 * @param money
	 * @param handler
	 */
	@Deprecated
	public void jytBind(String idType, String bankCode, String cardno,
						String idcardno, String name, String phone, String msgcode,
						double money, final ResponseHandler handler) {

		RequestParams params = new RequestParams();
		params.addBodyParameter("bankid", bankCode);
		params.addBodyParameter("cardno", cardno);
		params.addBodyParameter("idcardno", idcardno);
		params.addBodyParameter("name", name);
		params.addBodyParameter("phone", phone);
		params.addBodyParameter("validatecode", msgcode);
		params.addBodyParameter("amount", money + "");
		params.addBodyParameter("zj", idType);

		Http.post(API.JYTPay.BIND, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					if (error == 0) {
						String orderid = null;
						if (jsonData != null) {
							orderid = jsonData.optString("orderid", null);
						}
						if (handler != null) {
							handler.onSuccess(response.result, orderid);
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
	 * 查询富友充值结果
	 */
	public void queryFuiouPayResult(String orderid, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("orderid", orderid);
		Http.post(API.FUYOUPay.QUERY_ORDER, params, new RequestCallBack<String>() {

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
						PayResult result = null;
						if (jsonData != null) {
							result = JSONUtils.parseJsonString(
									PayResult.class,
									jsonData.toString());
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
	 * 富友支付
	 *
	 * @param money
	 * @param password
	 * @param handler
	 */
	public void fuyou(double money, String password, String cid, String ptid, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("money_order", SystemUtils.getDoubleStr(money));
		params.addBodyParameter("tpwd", password);
		params.addBodyParameter("cid", cid);
		params.addBodyParameter("ptid", ptid);
		params.addBodyParameter("platform","android");
		Log.i("TAG", "富友处理中");
		Http.post(API.FUYOUPay.PAY, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						String orderid = null;
						if (jsonData != null) {
							orderid = jsonData.optString("orderid");
						}
						if (handler != null) {
							handler.onSuccess(response.result, orderid);
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
			public void onFailure(HttpException e, String s) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});
	}
	public void fuyou(double money, String password, String cid, String ptid,String ordernumber, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("money_order", SystemUtils.getDoubleStr(money));
		params.addBodyParameter("tpwd", password);
		params.addBodyParameter("cid", cid);
		params.addBodyParameter("ptid", ptid);
		params.addBodyParameter("platform","android");
		params.addBodyParameter("orderid",ordernumber);
		Log.i("TAG", "富友处理中");
		Http.post(API.FUYOUPay.PAY, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						String orderid = null;
						if (jsonData != null) {
							orderid = jsonData.optString("orderid");
						}
						if (handler != null) {
							handler.onSuccess(response.result, orderid);
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
			public void onFailure(HttpException e, String s) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});
	}

	/**
	 * 提现状态查询
	 * @param ordernumber
	 * @return
	 */
	public void fuyou(String ordernumber,String startdt,String enddt, final IqueryWithDrawArriveCallBack queryWithDrawArriveCallBack) {

		RequestParams params = new RequestParams();
		params.addBodyParameter("orderid",ordernumber);
		params.addBodyParameter("startdt",startdt);
		params.addBodyParameter("enddt",enddt);
		Log.i("TAG", "富友处理中");
		Http.post(API.FUYOUPay.QUERY_WITH_DRAW_ARRIVE, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					String code = jsonObj.optString("code");
					String reason = jsonObj.optString("reason");
					String data = jsonObj.optString("data");
					queryWithDrawArriveCallBack.requestCode(code,reason,data);

				} catch (JSONException e) {
					e.printStackTrace();
					queryWithDrawArriveCallBack.requestCode("-1",null,null);
				}
			}

			@Override
			public void onFailure(HttpException e, String s) {
				queryWithDrawArriveCallBack.requestCode("-1",null,null);
			}
		});
	}



	/**
	 * 金运通支付
	 *
	 * @param money
	 * @param password
	 * @param handler
	 */
	public void jytPay(double money, String password, String cid, String ptid,String code, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("amount", SystemUtils.getDoubleStr(money));
		params.addBodyParameter("tpwd", password);
		params.addBodyParameter("cid", cid);
		params.addBodyParameter("ptid", ptid);
		params.addBodyParameter("code", code);
		Http.post(API.JYTPay.PAY, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						String orderid = null;
						if (jsonData != null) {
							orderid = jsonData.optString("orderid");
						}
						if (handler != null) {
							handler.onSuccess(response.result, orderid);
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
	 * 连连支付buildPaySign
	 *
	 * @param name
	 * @param idcard
	 * @param card
	 * @param money
	 * @param handler
	 */
	public void llpayBuildPaySign(String name, String idcard, String bankid,
								  String card, String money, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("acct_name", name);
		params.addBodyParameter("id_no", idcard);
		params.addBodyParameter("money_order", money);
		params.addBodyParameter("card_no", card);
		params.addBodyParameter("bankid", bankid);
		Http.post(API.LLPay.BUILD_ORDER, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					if (error == 0) {
						if (handler != null) {
							handler.onSuccess(
									response.result,
									jsonData == null ? null : jsonData
											.toString());
						}
					} else {
						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure(-1 + "", SYSTEM_ERROR);
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
	 * 连连支付getPaySign
	 *
	 * @param money   充值金额
	 * @param pwd     密码
	 * @param handler
	 */
	public void llpayGetPaySign(double money, String pwd,
								final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("pwd", pwd);
		params.addBodyParameter("money_order", SystemUtils.getDoubleStr(money));
		Http.post(API.LLPay.BUILD_ORDER, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					if (error == 0) {
						if (handler != null) {
							handler.onSuccess(
									response.result,
									jsonData == null ? null : jsonData
											.toString());
						}
					} else {
						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
					if (handler != null) {
						handler.onFailure(-1 + "", SYSTEM_ERROR);
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
	 * 宝付支付获取订单
	 *
	 * @param money
	 * @param pwd
	 * @param handler
	 */
	public void baofooPayReady(double money, String pwd, String cid, String ptid,
							   final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("tpwd", pwd);
		params.addBodyParameter("money", SystemUtils.getDoubleStr(money));
		params.addBodyParameter("cid", cid);
		params.addBodyParameter("ptid", ptid);
		Http.post(API.BaoFooPay.BAOFOO_PAY_READY, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> response) {
						try {
							JSONObject jsonObj = new JSONObject(response.result);
							int error = jsonObj.optInt("error", -1);
							JSONObject jsonData = jsonObj.optJSONObject("data");
							String msg = jsonObj.optString("msg", SYSTEM_ERROR);
							if (error == 0) {
								if (handler != null) {
									handler.onSuccess(response.result, jsonData);
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
					public void onFailure(HttpException e, String msg) {
						if (handler != null) {
							handler.onFailure(e.getExceptionCode() + "",
									NETWORK_ERROR);
						}
					}
				});
	}

	/**
	 * 提交支付失败
	 *
	 * @param orderid
	 * @param retCode
	 * @param retMsg
	 * @param handler
	 */
	public void postPayError(String orderid, String retCode, String retMsg,
							 final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("uid",
				App.loginData != null ? App.loginData.uid : "");
		params.addBodyParameter("orderid", orderid);
		params.addBodyParameter("ret_code", retCode);
		params.addBodyParameter("ret_msg", retMsg);
		Http.post(API.Account.POST_PAY_ERROR, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> response) {
						if (handler != null) {
							handler.onSuccess(response.result, null);
						}
					}

					@Override
					public void onFailure(HttpException e, String msg) {
						if (handler != null) {
							handler.onFailure(e.getExceptionCode() + "",
									NETWORK_ERROR);
						}
					}
				});
	}

	/**
	 * 提现
	 *
	 * @param money
	 * @param password
	 * @param userlogid
	 * @param handler
	 */
	public void cash(double money, String password, String userlogid,
					 final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("amount", money+"");
		params.addBodyParameter("tpwd", password);
		params.addBodyParameter("userlogid", userlogid);
		Http.post(API.Account.CASH, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						String balance = null;
						String time = null;
						Map<String, String> map = new HashMap<String, String>();
						if (jsonData != null) {
							balance = jsonData.optString("balance");
							time = jsonData.getString("time");
							map.put("balance", balance);
							map.put("time", time);
						}
						if (handler != null) {
							handler.onSuccess(response.result, map);
						}
					} else {
						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}

				} catch (JSONException e) {
					if (handler != null) {
						handler.onFailure("-1", "银行处理中，预计下一个工作日18:00前到账");
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
	 * 获取用户收益数据
	 *
	 * @param handler
	 */
	public void getUserProfitInfo(final IResponseHandler handler) {
		Http.get(API.Account.USER_PROFIT_INFO, null,
				new RequestCallBack<String>() {
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
							int error = jsonObj.getInt("error");
							if (error == 0) {
								JSONObject jsonData = jsonObj
										.getJSONObject("data");
								int buyNum = jsonData.optInt("buynum", 0);
								String countProfit = jsonData.optString(
										"countProfit", "0.00");
								String yesterdayProfit = jsonData.optString(
										"yesterday_profit", "0.00");
								String longmoney = jsonData.optString(
										"longmoney", "0.00");
								String longmoneyCountprofit = jsonData
										.optString("longmoneyCountprofit",
												"0.00");
								String klmoney = jsonData.optString("klmoney", "0.00");
								String klmoneyCountProfit = jsonData.optString("klmoneyCountprofit", "0.00");
								String invite = jsonData.optString("invite",
										"0.00");
								String tansaction = jsonData.optString(
										"transaction", "0.00");
								String expmoney = jsonData.optString(
										"expmoney", "0.00");
								String expmoneyCountprofit = jsonData
										.optString("expmoneyTotalprofit",
												"0.00");
								String expmoneyYesterdayprofit = jsonData
										.optString("new_expmoney_yesterday_profit",
												"0.00");
								String expMoneyCurrentProfit = jsonData
										.optString("expmoney_current_profit", "0.00");

								UserProfit mUserProfit = new UserProfit();
								mUserProfit.buyNum = buyNum;
								mUserProfit.countProfit = SystemUtils
										.getDouble(countProfit);
								mUserProfit.yesterdayProfit = SystemUtils
										.getDouble(yesterdayProfit);
								mUserProfit.longmoney = SystemUtils
										.getDouble(longmoney);
								mUserProfit.longmoneyCountprofit = SystemUtils
										.getDouble(longmoneyCountprofit);
								mUserProfit.klmoney = SystemUtils.getDouble(klmoney);
								mUserProfit.klmoneyCountProfit = SystemUtils.getDouble(klmoneyCountProfit);
								mUserProfit.invite = SystemUtils
										.getDouble(invite);

								mUserProfit.transaction = SystemUtils
										.getDouble(tansaction);

								mUserProfit.expmoney = SystemUtils
										.getDouble(expmoney);

								mUserProfit.expmoneyCountprofit = SystemUtils
										.getDouble(expmoneyCountprofit);

								mUserProfit.expmoneyYesterdayprofit = SystemUtils
										.getDouble(expmoneyYesterdayprofit);

								mUserProfit.expmoneyCurrentProfit = SystemUtils
										.getDouble(expMoneyCurrentProfit);

								if (handler != null) {
									handler.onSuccess(response.result,
											mUserProfit);
								}

							} else {
								if (handler != null) {
									handler.onFailure(error + "",
											jsonObj.getString("msg"));
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

	/*
	 * 获取用户定期总额：投资金额+累计收益
     */
	public static void getUserPeridic(final IResponseHandler handler) {
		Http.get(API.Account.USER_PRODUCT, null, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", NETWORK_ERROR);
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
						UserDQMoney mUserDQMoney = new UserDQMoney();
						if (jsonData != null) {
							mUserDQMoney.countmoney = (double) jsonData
									.optDouble("countmoney", 0d);
							mUserDQMoney.countprofit = (double) jsonData
									.optDouble("countprofit", 0d);
						}
						mUserDQMoney.dqmoney = mUserDQMoney.countmoney;
						if (handler != null && response.result!=null) {
							handler.onSuccess(response.result, mUserDQMoney);
						}

					} else {

						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 获取用户购买过的产品
	 *
	 * @param handler
	 */
	public void getUserProduct(final IResponseHandler handler) {
		Http.get(API.Account.USER_PRODUCT, null, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", NETWORK_ERROR);
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
						List<UserProduct> list = new ArrayList<UserProduct>();
						JSONObject jsonProducts = null;
						if (jsonData != null) {
							jsonProducts = jsonData.optJSONObject("product");
						}

						Iterator<String> keys = null;
						if (jsonProducts != null) {
							keys = jsonProducts.keys();
						}

						while (keys != null && keys.hasNext()) {
							String key = keys.next();
							JSONObject jsonProduct = jsonProducts
									.optJSONObject(key);
							String str = jsonProduct.toString();

							UserProduct userProduct = JSONUtils
									.parseJsonString(UserProduct.class, str);

							List<SingleProduct> ls = userProduct.product_list;

							long buytime = 0;
							if (ls != null && ls.size() > 0) {
								String buytimeStr = ls.get(0).buytime;
								if (!StringUtils.isBlank(buytimeStr)) {
									buytime = Long.parseLong(buytimeStr);
								}
							}
							userProduct.buytime = buytime;
							list.add(userProduct);
						}

						Collections.sort(list, new Comparator<UserProduct>() {

							@Override
							public int compare(UserProduct o1, UserProduct o2) {
								if (o1.buytime < o2.buytime) {
									return 1;
								}

								if (o1.buytime > o2.buytime) {
									return -1;
								}

								return 0;
							}
						});

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
				}
			}
		});
	}

	/**
	 * 获取用户累计收益明细
	 */
	public void getUserProfitList(final int profitType, int type, int page,
								  int days, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		// post参数
		params.addBodyParameter("page", page + "");
		if (days == 7 || days == 30) {
			params.addBodyParameter("days", days + "");
		}

		if (type != -1) {
			params.addBodyParameter("type", type + "");
		}

		String api;
		if (profitType == ProfitListActivity.PROTIT_TYPE_ALL) {
			logger.i(TAG, "用户总的累计收益明细");
			api = API.Account.ALL_PROFIT_LIST;

		} else if (profitType == ProfitListActivity.PROTIT_TYPE_HQ) {
			logger.i(TAG, "用户快活宝累计收益明细");
			api = API.Account.HQ_PROFIT_LIST;

		} else {
			logger.i(TAG, "用户产品累计收益明细");
			api = API.Account.DQ_PROFIT_LIST;
		}
		Http.post(api, params, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", NETWORK_ERROR);
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
						List<ProfitDate> profitList = new ArrayList<>();
						JSONObject jsonProduct = null;
						JSONObject jsonLongProduct = null;
						JSONObject jsonExpProduct = null;

						Iterator<String> productKeys = null;
						Iterator<String> longProductKeys = null;
						Iterator<String> expProductKeys = null;

						if (jsonData != null) {
							if (profitType == ProfitListActivity.PROTIT_TYPE_ALL) {
								// 总收益列表
								// 定期
								jsonProduct = jsonData.optJSONObject("product");
								if (jsonProduct != null) {
									productKeys = jsonProduct.keys();
								}

								// 活期
								jsonLongProduct = jsonData.optJSONObject("longProduct");
								if (jsonLongProduct != null) {
									longProductKeys = jsonLongProduct.keys();
								}

								// 体验金
								jsonExpProduct = jsonData.optJSONObject("expmoney");
								if (jsonExpProduct != null) {
									expProductKeys = jsonExpProduct.keys();
								}


							} else if (profitType == ProfitListActivity.PROTIT_TYPE_DQ) {
								// 定期收益列表
								jsonProduct = jsonData.optJSONObject("product");
								if (jsonProduct != null) {
									productKeys = jsonProduct.keys();
								}

							} else {
								// 活期收益列表
								jsonLongProduct = jsonData.optJSONObject("profit_list");
								if (jsonLongProduct != null) {
									longProductKeys = jsonLongProduct.keys();
								}
							}
						}

						// 先解析定期的累计收益
						while (productKeys != null && productKeys.hasNext()) {
							// 获取键名 2015-06-10
							String date = productKeys.next();
							ProfitDate profitDate = new ProfitDate();
							profitDate.date = date;
							// 根据键名获取键值
							JSONObject jsonDate = jsonProduct.optJSONObject(date);

							List<ProfitDetail> profitDetailList = new ArrayList<>();

							Iterator<String> keys = jsonDate.keys();

							double sum = 0f;

							while (keys != null && keys.hasNext()) {

								String profitItemNum = keys.next();

								JSONObject jsonProfitDetail = jsonDate
										.optJSONObject(profitItemNum);

								double profit = jsonProfitDetail
										.optDouble("profit");
								String pname = jsonProfitDetail
										.optString("pname");

								String income = jsonProfitDetail
										.optString("income");

								String money = jsonProfitDetail
										.optString("money");

								String detailDate = jsonProfitDetail
										.optString("date");

								ProfitDetail detail = new ProfitDetail();

								detail.profit = profit;
								detail.pname = pname;
								detail.income = income;
								detail.date = detailDate;
								detail.money = money;

								sum += profit;
								profitDetailList.add(detail);
							}

							profitDate.profit = sum;
							profitDate.profitDetail = profitDetailList;
							profitList.add(profitDate);
						}

						// 再解析快活宝的累计收益
						while (longProductKeys != null
								&& longProductKeys.hasNext()) {
							String date = longProductKeys.next();
							ProfitDate profitDate = new ProfitDate();
							profitDate.date = date;
							JSONObject jsonDate = jsonLongProduct.optJSONObject(date);
							List<ProfitDetail> profitDetailList = new ArrayList<>();
							Iterator<String> keys = jsonDate.keys();
							double sum = 0f;
							while (keys != null && keys.hasNext()) {
								String profitItemNum = keys.next();
								JSONObject jsonProfitDetail = jsonDate
										.optJSONObject(profitItemNum);
								double profit = jsonProfitDetail
										.optDouble("profit");
								String pname = jsonProfitDetail
										.optString("pname");
								String income = jsonProfitDetail
										.optString("income");
								String money = jsonProfitDetail
										.optString("money");
								String time = jsonProfitDetail
										.optString("time");

								// 解析时间
								long ms = Long.parseLong(time);
								String detailDate = TimeUtils.getTimeFromSeconds(ms, TimeUtils.DATE_FORMAT_DATE);
								ProfitDetail detail = new ProfitDetail();
								detail.profit = profit;
								detail.pname = pname;
								detail.income = income;
								detail.date = detailDate;
								detail.money = money;
								sum += profit;
								profitDetailList.add(detail);
							}
							profitDate.profit = sum;
							profitDate.profitDetail = profitDetailList;

							// 判断是否已经存在该日期的收益，存在则添加进去
							boolean isExist = false;
							for (ProfitDate pd : profitList) {
								if (pd.date.equals(profitDate.date)) {
									pd.profit += profitDate.profit;
									pd.profitDetail.addAll(profitDate.profitDetail);
									isExist = true;
									break;
								}
							}

							// 若当前不存在则添加到list集合
							if (!isExist) {
								profitList.add(profitDate);
							}
						}

						// 体验金
						while (expProductKeys != null
								&& expProductKeys.hasNext()) {
							String date = expProductKeys.next();
							ProfitDate profitDate = new ProfitDate();
							profitDate.date = date;
							JSONObject jsonDate = jsonExpProduct.optJSONObject(date);
							List<ProfitDetail> profitDetailList = new ArrayList<>();
							Iterator<String> keys = jsonDate.keys();

							double sum = 0f;

							while (keys != null && keys.hasNext()) {
								String profitItemNum = keys.next();

								JSONObject jsonProfitDetail = jsonDate
										.optJSONObject(profitItemNum);

								double profit = jsonProfitDetail
										.optDouble("profit");

								String pname = jsonProfitDetail
										.optString("pname");

								String income = jsonProfitDetail
										.optString("income");

								String money = jsonProfitDetail
										.optString("money");

								String time = jsonProfitDetail
										.optString("ctime");

								// 解析时间
								long ms = Long.parseLong(time);
								String detailDate = TimeUtils.getTime(ms,
										TimeUtils.DATE_FORMAT_DATE);

								ProfitDetail detail = new ProfitDetail();

								detail.profit = profit;
								detail.pname = pname;
								detail.income = income;
								detail.date = detailDate;
								detail.money = money;

								sum += profit;

								profitDetailList.add(detail);
							}

							profitDate.profit = sum;
							profitDate.profitDetail = profitDetailList;

							// 判断是否已经存在该日期的收益，存在则添加进去
							boolean isExist = false;
							for (ProfitDate pd : profitList) {
								if (pd.date.equals(profitDate.date)) {
									pd.profit += profitDate.profit;
									pd.profitDetail
											.addAll(profitDate.profitDetail);
									isExist = true;
									break;
								}
							}

							// 若当前不存在则添加到list集合
							if (!isExist) {
								profitList.add(profitDate);
							}
						}

						if (handler != null) {
							handler.onSuccess(response.result, profitList);
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
	 * 用户快活宝数据
	 *
	 * @param type
	 * @param page
	 * @param days
	 * @param handler
	 */
	public void getUserKHProfitList(int type, int page, int days, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("page", page + "");
		if (type != -1) {
			params.addBodyParameter("type", type + "");
		}
		if (days == 7 || days == 30) {
			params.addBodyParameter("days", days + "");
		}
		Http.post(API.Account.HQ_PROFIT_LIST, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String response = responseInfo.result;
				try {
					JSONObject jsonObj = new JSONObject(response);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						List<HQProfit> profits = new ArrayList<HQProfit>();
						JSONObject jsonList = null;
						Iterator<String> jsonKlProductKeys = null;
						if (jsonData != null) {
							jsonList = jsonData.optJSONObject("profit_list");
							if (jsonList != null) {
								jsonKlProductKeys = jsonList.keys();
							}
						}

						while (jsonKlProductKeys != null && jsonKlProductKeys.hasNext()) {
							String date = jsonKlProductKeys.next();
							HQProfit profit = new HQProfit();
							profit.profitInfos = new ArrayList<ProfitInfo>();
							ProfitInfo profitInfo = new ProfitInfo();
							profit.date = date;
							JSONObject jsonDate = jsonList.optJSONObject(date);
							JSONObject jsonProfit = jsonDate.optJSONObject("110");
							profit.profit = SystemUtils.getDoubleStr(jsonProfit.optString("profit", "0.00"));
							profitInfo.balance = jsonProfit.optString("balance","0.00");
							profitInfo.income = jsonProfit.optDouble("income",0);
							profitInfo.money = jsonProfit.optString("money","0.00");
							profitInfo.pname = jsonProfit.optString("pname","");
							profit.profitInfos.add(profitInfo);
							profits.add(profit);
						}
						if (handler != null) {
							handler.onSuccess(response, profits);
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
					handler.onFailure(error.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});
	}


	/**
	 * 用户快乐宝数据
	 *
	 * @param type
	 * @param page
	 * @param days
	 * @param handler
	 */
	public void getUserKlProfitList(int type, int page, int days, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("page", page + "");
		if (type != -1) {
			params.addBodyParameter("type", type + "");
		}
		if (days == 7 || days == 30) {
			params.addBodyParameter("days", days + "");
		}
		Http.post(API.Account.KL_PROFIT_LIST, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String response = responseInfo.result;
				try {
					JSONObject jsonObj = new JSONObject(response);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						List<HQProfit> profits = new ArrayList<HQProfit>();
						JSONObject jsonList = null;
						Iterator<String> jsonKlProductKeys = null;
						if (jsonData != null) {
							jsonList = jsonData.optJSONObject("profit_list");
							if (jsonList != null) {
								jsonKlProductKeys = jsonList.keys();
							}
						}

						while (jsonKlProductKeys != null && jsonKlProductKeys.hasNext()) {
							String date = jsonKlProductKeys.next();
							HQProfit profit = new HQProfit();
							profit.date = date;

							JSONObject jsonDate = jsonList.optJSONObject(date);
							JSONObject jsonProfit = jsonDate.optJSONObject("110");
							profit.profit = SystemUtils.getDoubleStr(jsonProfit.optString("profit", "0.00"));
							profits.add(profit);
						}
						if (handler != null) {
							handler.onSuccess(response, profits);
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
					handler.onFailure(error.getExceptionCode() + "", NETWORK_ERROR);
				}
			}
		});
	}


	/**
	 * 获取用户操作记录
	 *
	 * @param type    all:全部 in:收入 out:支出 product:定期 longproduct:活期
	 * @param page    分页
	 * @param handler
	 */
	public void getUserActionLog(String type, int page,
								 final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("type", type);
		params.addBodyParameter("page", page + "");
		Http.post(API.Account.ACTION_LOG, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> response) {
						try {
							JSONObject jsonObj = new JSONObject(response.result);
							int error = jsonObj.optInt("error", -1);
							String msg = jsonObj.optString("msg", SYSTEM_ERROR);
							JSONObject jsonData = jsonObj.optJSONObject("data");
							if (error == 0) {
								List<ActionLog> list = new ArrayList<>();
								if (jsonData != null) {
									JSONArray jsonLogs = jsonData
											.optJSONArray("actionlog");
									if (jsonLogs != null) {
										int len = jsonLogs.length();
										for (int i = 0; i < len; i++) {
											JSONObject json = jsonLogs
													.optJSONObject(i);
											ActionLog log = JSONUtils
													.parseJsonString(
															ActionLog.class,
															json.toString());
											list.add(log);
										}
									}
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

					@Override
					public void onFailure(HttpException e, String error) {
						if (handler != null) {
							handler.onFailure(e.getExceptionCode() + "",
									NETWORK_ERROR);
						}
					}
				});
	}

	public void getCompanyNews(int page,final IResponseHandler handler){
		RequestParams params = new RequestParams();
		params.addBodyParameter("page", page + "");
		Http.post(API.COMPANY_NEWS, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try{
					JSONObject jsonObj = new JSONObject(responseInfo.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONArray jsonData = jsonObj.optJSONArray("data");
					if (error == 0){
						List<ComNews> list = new ArrayList<ComNews>();
						if (jsonData != null){
							int len = jsonData.length();
							for (int i = 0; i < len; i++) {
								JSONObject json = jsonData
										.optJSONObject(i);
								ComNews log = JSONUtils
										.parseJsonString(
												ComNews.class,
												json.toString());
								list.add(log);
							}
						}
						if (handler != null) {
							handler.onSuccess(responseInfo.result, list);
						}
					} else {
						if (handler != null) {
							handler.onFailure(error + "", msg);
						}
					}
				}catch (Exception e){
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

	public void getUserNotice(final IResponseHandler handler){
		Http.get(API.Account.USER_NOTICE_MESSAGE, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					JSONObject jsonObj = new JSONObject(responseInfo.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONArray noti = jsonObj.getJSONArray("data");
					if (error == 0){
						List<Notice> nList = new ArrayList<Notice>();
						if (noti != null){
							int len = noti.length();
							for (int i = 0;i < len;i++){
								JSONObject info = noti.getJSONObject(i);
								Notice n = JSONUtils.parseJsonString(Notice.class,info.toString());
								nList.add(n);
							}
						}
						if (handler != null && responseInfo.result != null){
							handler.onSuccess(responseInfo.result, nList);
						}
					}else {
						if (handler != null){
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

			}
		});
	}

	/**
	 * 用户活期数据
	 */
	public void getUserHQMoney(final IResponseHandler handler) {
		Http.get(API.Account.USER_HQ_MONEY, null,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> response) {
						try {
							JSONObject jsonObj = new JSONObject(response.result);
							int error = jsonObj.optInt("error", -1);
							String msg = jsonObj.optString("msg", SYSTEM_ERROR);
							JSONObject jsonData = jsonObj.optJSONObject("data");
							if (error == 0) {
								UserHQMoney khbMoney = null;
								if (jsonData != null) {
									khbMoney = JSONUtils.parseJsonString(
											UserHQMoney.class,
											jsonData.toString());
								}
								if (handler != null && khbMoney != null && response.result!= null) {
									handler.onSuccess(response.result, khbMoney);
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
	 * 活期转出到余额
	 *
	 * @param money    转出金额
	 * @param password 交易密码
	 * @param handler
	 */
	public void hqToBalance(int to, double money, String password,
							final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("type", to + "");
		params.addBodyParameter("tpwd", password);
		params.addBodyParameter("longmoney", SystemUtils.getDoubleStr(money));
		String api = API.Account.LONG_MONEY_TO_BALANCE;
		Http.post(api, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						String balance = null;
						String longmoney = null;
						String userlogid = null;
						if (jsonData != null) {
							balance = jsonData.optString("balance", "0.00");
							longmoney = jsonData.optString("longmoney", "0.00");
							userlogid = jsonData.optString("userlogid", null);
						}
						Map<String, String> map = new HashMap<>();
						map.put("balance", balance);
						map.put("longmoney", longmoney);
						map.put("userlogid", userlogid);
						if (handler != null) {
							handler.onSuccess(response.result, map);
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
	 * 获取转出到银行卡，先转出到余额，再提现到银行卡
	 *
	 * @param money    金额
	 * @param password 交易密码
	 * @param handler
	 */
	public void hqToBank(int to, final double money,
						 final String password, final IResponseHandler handler) {
		// 第一步，活期转出到余额
		hqToBalance(to, money, password, new ResponseHandler() {
			@Override
			public <T> void onSuccess(String response, T t) {
				super.onSuccess(response, t);
				// 活期转出到余额成功
				Map<String, String> map = (Map<String, String>) t;
				final String userlogid = map.get("userlogid");
				// 第二步，余额提现到银行卡
				Handler mHandler = new Handler();
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						cash(money, password, userlogid, new ResponseHandler() {
							public <U> void onSuccess(String response, U t) {
								// 余额提现到银行卡成功
								if (handler != null) {
									handler.onSuccess(response, t);
								}
							}

							@Override
							public void onFailure(String errorCode, String msg) {
								super.onFailure(errorCode, msg);
								// 余额提现到银行卡失败
								if (handler != null) {
									int h = TimeUtils.getServerHour();
									String time3 = StringUtils.getDaoZhangTime(h);
									if (errorCode.equals("-1")){
										handler.onFailure("-1", "银行处理中，"+time3);
									}else {
										handler.onFailure("-2", "银行处理中，"+time3);
									}

								}
							}
						});
					}
				}, 1000);
			}

			@Override
			public void onFailure(String errorCode, String msg) {
				super.onFailure(errorCode, msg);
				// 活期转出到余额失败
				if (handler != null) {
					handler.onFailure(errorCode, msg);
				}
			}
		});
	}

	/**
	 * 重置登录密码第一步，发送验证码
	 *
	 * @param account
	 * @param handler
	 */
	public void resetLoginPassStep1(String account,
									final ResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("account", account);
		Http.post(API.Account.RESET_LOGIN_PASS_STEP_1, params,
				new RequestCallBack<String>() {
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
								String message = null;
								if (jsonData != null) {
									message = jsonData.optString("message");
								}

								if (handler != null) {
									handler.onSuccess(response.result, message);
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
	 * 重置登录密码第二步，提交验证码和新密码
	 *
	 * @param account
	 * @param code
	 * @param pwd
	 * @param handler
	 */
	public void resetLoginPassStep2(String account, String code, String pwd,
									final ResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("account", account);
		params.addBodyParameter("code", code);
		params.addBodyParameter("pwd", pwd);
		Http.post(API.Account.RESET_LOGIN_PASS_STEP_2, params,
				new RequestCallBack<String>() {
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
								String message = null;
								if (jsonData != null) {
									message = jsonData.optString("message");
								}

								if (handler != null) {
									handler.onSuccess(response.result, message);
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
	 * 重置交易密码第一步，发送短信验证码
	 *
	 * @param idcard
	 * @param realname
	 * @param handler
	 */
	public void resetPayPassStep1(String idcard, String realname,
								  final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("idcard", idcard);
		params.addBodyParameter("realname", realname);
		Http.post(API.Account.RESET_PAY_PASS_STEP_1, params,
				new RequestCallBack<String>() {
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
								String message = null;
								if (jsonData != null) {
									message = jsonData.optString("message");
								}
								if (handler != null) {
									handler.onSuccess(response.result, message);
								}

							} else {
								if (handler != null) {
									handler.onFailure(error + "",
											jsonObj.getString("msg"));
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
	 * 重置交易密码第二步，提交验证码和新交易密码
	 *
	 * @param code
	 * @param newTpwd
	 * @param handler
	 */
	public void resetPayPassStep2(String code, String newTpwd,
								  final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("code", code);
		params.addBodyParameter("newTpwd", newTpwd);
		Http.post(API.Account.RESET_PAY_PASS_STEP_2, params,
				new RequestCallBack<String>() {
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
	 * 获取用户充值和取现次数限制
	 *
	 * @param handler
	 */
	public void getUserCD(final IResponseHandler handler) {
		Http.get(API.Account.GET_USER_CD, null, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", NETWORK_ERROR);
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
						UserCD usercd = null;
						if (jsonData != null) {
							usercd = JSONUtils.parseJsonString(UserCD.class,
									jsonData.toString());
						}

						if (handler != null) {
							handler.onSuccess(response.result, usercd);
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
	 * 获取7日万份收益
	 *
	 * @param handler
	 */
	public void getHQSevenProfit(final IResponseHandler handler) {
		Http.get(API.Account.KH_MONEY_PROFIT, null,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String response = responseInfo.result;
						try {
							JSONObject jsonObj = new JSONObject(response);
							int error = jsonObj.optInt("error", -1);
							String msg = jsonObj.optString("msg", SYSTEM_ERROR);
							JSONObject jsonData = jsonObj.optJSONObject("data");
							if (error == 0) {
								List<SevenProfit> list = new ArrayList<SevenProfit>();
								if (jsonData != null) {
									Iterator<String> keys = jsonData.keys();
									while (keys.hasNext()) {
										String date = keys.next();
										SevenProfit sp = new SevenProfit();
										sp.date = date;
										JSONObject jsonDate = jsonData.optJSONObject(date);
										if (jsonDate != null) {
											sp.income = jsonDate.optString("income", "0.00");
											sp.profit = jsonDate.optString("profit", "0.00");
										}
										list.add(sp);
									}
								}
								if (handler != null) {
									handler.onSuccess(response, list);
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
	 * 我邀请的交易过的人
	 */
	public void getMyInviteBuy(final IResponseHandler handler) {
		Http.get(API.Account.MY_INVITE_BUY, null,
				new RequestCallBack<String>() {
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
								List<YaoQingAward> awards = new ArrayList<YaoQingAward>();
								JSONArray jsonData = jsonObj
										.optJSONArray("data");

								int length = jsonData.length();
								for (int i = 0; i < length; i++) {
									JSONObject json = jsonData.optJSONObject(i);
									YaoQingAward award = JSONUtils
											.parseJsonString(
													YaoQingAward.class,
													json.toString());
									awards.add(award);
								}

								if (handler != null) {
									handler.onSuccess(response.result, awards);
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
	 * 我邀请的人
	 */
	public void getMyInvite(final IResponseHandler handler) {
		Http.get(API.Account.MY_INVITE, null, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException e, String error) {
				if (handler != null) {
					handler.onFailure(e.getExceptionCode() + "", NETWORK_ERROR);
				}
			}

			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					if (error == 0) {
						List<InviteFriend> awards = new ArrayList<InviteFriend>();
						JSONArray jsonData = jsonObj.optJSONArray("data");

						int length = jsonData.length();
						for (int i = 0; i < length; i++) {
							JSONObject json = jsonData.optJSONObject(i);
							InviteFriend award = JSONUtils.parseJsonString(
									InviteFriend.class, json.toString());

							awards.add(award);
						}

						if (handler != null) {
							handler.onSuccess(response.result, awards);
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
	 * 我邀请的交易奖励
	 */
	public void getUserInviteReward(final IResponseHandler handler) {
		Http.get(API.Account.GET_USER_INVITE_REWARD, null,
				new RequestCallBack<String>() {
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
								List<YongJinAward> awards = new ArrayList<>();
								JSONArray jsonData = jsonObj
										.optJSONArray("data");

								int length = jsonData.length();
								for (int i = 0; i < length; i++) {
									JSONObject json = jsonData.optJSONObject(i);
									YongJinAward award = JSONUtils
											.parseJsonString(
													YongJinAward.class,
													json.toString());
									awards.add(award);
								}

								if (handler != null) {
									handler.onSuccess(response.result, awards);
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
	 * 获取用户推送标签
	 */
	protected void getUserPushTag(final IResponseHandler handler) {
		Http.get(API.Account.GET_USER_PUSH_TAG, null,
				new RequestCallBack<String>() {
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
	 * 获取体验金余额
	 */
	public void getExpMoneyBalance(final IResponseHandler handler) {
		Http.get(API.Account.GET_EXPMONEY_BALANCE, null,
				new RequestCallBack<String>() {
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
								String expmoney = null;
								if (jsonData != null) {
									expmoney = jsonData.optString("expmoney",
											"0.00");
								}

								if (handler != null) {
									handler.onSuccess(response.result, expmoney);
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
	 * 获取体验金数据
	 */
	public void getExpMoney(final IResponseHandler handler) {
		Http.get(API.Account.GET_EXPMONEY_INFO, null,
				new RequestCallBack<String>() {
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
								ExpMoney expmoney = null;
								if (jsonData != null) {
									expmoney = JSONUtils.parseJsonString(
											ExpMoney.class, jsonData.toString());
								}

								if (handler != null) {
									handler.onSuccess(response.result, expmoney);
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
	 * 获取体验金日志
	 *
	 * @param handler
	 */
	public void getExpMoneyLog(int page, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("page", page + "");
		Http.post(API.Account.GET_EXPMONEY_LOG, params,
				new RequestCallBack<String>() {
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
								JSONArray jsonData = jsonObj
										.optJSONArray("data");
								List<ExpMoneyLog> logs = new ArrayList<ExpMoneyLog>();
								if (jsonData != null && jsonData.length() > 0) {
									for (int i = 0; i < jsonData.length(); i++) {
										JSONObject json = jsonData
												.optJSONObject(i);
										ExpMoneyLog log = JSONUtils
												.parseJsonString(
														ExpMoneyLog.class,
														json.toString());

										log.date = TimeUtils
												.getTimeFromSeconds(
														log.ctime,
														TimeUtils.DATE_FORMAT_CHINA_MONTH);
										logs.add(log);
									}
								}

								if (handler != null) {
									handler.onSuccess(response.result, logs);
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
	 * 使用新体验金
	 * */
	public void useExpMoney(String eid,final IResponseHandler handler){
		RequestParams params = new RequestParams();
		params.addBodyParameter("eid", eid);
		Http.post(API.Account.USE_NEWEXPMONEY,params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				JSONObject jsonObj = null;
				try {
					jsonObj = new JSONObject(responseInfo.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					if (error == 0){
						if (handler != null) {
							handler.onSuccess(responseInfo.result, msg);
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
	 * 新体验金
	 */
	public void newExpMoney(final IResponseHandler handler){
		Http.post(API.Account.GET_NEWEXPMONEY_LOG, null, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					JSONObject jsonObj = new JSONObject(responseInfo.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONArray jsonData = jsonObj.optJSONArray("data");
					if(error==0){
						List<NewExp> list = new ArrayList<NewExp>();
						if (jsonData!= null){
							int len = jsonData.length();
							for (int i = 0; i < len; i++) {
								JSONObject json = jsonData
										.optJSONObject(i);
								NewExp log = JSONUtils
										.parseJsonString(
												NewExp.class,
												json.toString());
								list.add(log);
							}
						}
						if (handler != null && responseInfo.result != null) {
							handler.onSuccess(responseInfo.result, list);
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
	 * 使用体验金
	 */
	public void useExpMoneyBuy(String money, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		int m = (int) Float.parseFloat(money);
		params.addBodyParameter("money", m + "");
		Http.post(API.Account.USE_EXPMONEY_BUY, params,
				new RequestCallBack<String>() {
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
								ExpMoneyBuy expBuy = null;
								if (jsonData != null) {
									expBuy = JSONUtils.parseJsonString(
											ExpMoneyBuy.class,
											jsonData.toString());
								}
								if (handler != null) {
									handler.onSuccess(response.result, expBuy);
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
	 * 获取体验金收益
	 *
	 * @param page
	 * @param handler
	 */
	public void getExpMoneyProfit(int type, int page, final IResponseHandler handler) {
		RequestParams params = new RequestParams();
		// 缓存1小时
		params.addHeader("Cache-Control", "max-age=3600");
		// post参数
		params.addBodyParameter("type", type + "");
		params.addBodyParameter("page", page + "");
		Http.post(API.Account.GET_EXPMONEY_PROFIT, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> response) {
				try {
					JSONObject jsonObj = new JSONObject(response.result);
					int error = jsonObj.optInt("error", -1);
					String msg = jsonObj.optString("msg", SYSTEM_ERROR);
					JSONObject jsonData = jsonObj.optJSONObject("data");
					if (error == 0) {
						List<ProfitDate> profitDateList = new ArrayList<>();
						if (jsonData != null) {
							JSONObject jsonList = jsonData.optJSONObject("profit_list");
							Iterator<String> keys = null;
							if (jsonList != null) {
								keys = jsonList.keys();
							}
							while (keys != null && keys.hasNext()) {
								String key = keys.next();
								ProfitDate profitDate = new ProfitDate();
								profitDate.date = key;
								List<ProfitDetail> details = new ArrayList<ProfitDetail>();
								JSONObject jsonDate = jsonList
										.optJSONObject(key);
								Iterator<String> keys2 = jsonDate
										.keys();
								while (keys2 != null && keys2.hasNext()) {
									String key2 = keys2.next();
									JSONObject jsonDetail = jsonDate
											.optJSONObject(key2);
									ProfitDetail detail = new ProfitDetail();
									detail.profit = Float.parseFloat(jsonDetail
											.optString("profit"));
									detail.money = jsonDetail
											.optString("money");
									detail.date = jsonDetail
											.optString("odate");
									detail.pname = jsonDetail
											.optString("pname");
									detail.income = jsonDetail
											.optString("income");
									details.add(detail);
									profitDate.profit += detail.profit;
								}
								profitDate.profitDetail = details;
								profitDateList.add(profitDate);
							}
						}

						if (handler != null) {
							handler.onSuccess(response.result,
									profitDateList);
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
	 * @param bankid  银行ID
	 * @param phone   银行预留手机号
	 * @param handler
	 */
	public void getMsgCode(String bankid, String phone,
						   final ResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("bankid", bankid);
		params.addBodyParameter("phone", phone);

		Http.post(API.Account.GET_MSG_CODE, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> response) {
						try {
							JSONObject jsonObj = new JSONObject(response.result);
							int error = jsonObj.optInt("error", -1);
							JSONObject jsonData = jsonObj.optJSONObject("data");
							String msg = jsonObj.optString("msg", SYSTEM_ERROR);
							if (error == 0) {
								String message = null;
								if (jsonData != null) {
									message = jsonData.optString("message",
											null);
								}
								if (handler != null) {
									handler.onSuccess(response.result, message);
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
							handler.onFailure(e.getExceptionCode() + "",
									NETWORK_ERROR);
						}
					}
				});
	}

}
