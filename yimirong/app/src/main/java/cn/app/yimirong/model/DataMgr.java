package cn.app.yimirong.model;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.http.cookie.Cookie;

import java.io.Serializable;
import java.util.List;

import cn.app.yimirong.ACache;
import cn.app.yimirong.App;
import cn.app.yimirong.common.Constant;
import cn.app.yimirong.log.Logger;
import cn.app.yimirong.model.bean.ActivityTime;
import cn.app.yimirong.model.bean.LoginData;
import cn.app.yimirong.model.bean.OffLineInfo;
import cn.app.yimirong.model.bean.Questions;
import cn.app.yimirong.model.bean.SpecialDays;
import cn.app.yimirong.model.bean.User;
import cn.app.yimirong.model.bean.UserInfo;
import cn.app.yimirong.model.bean.UserInfo.Identity;
import cn.app.yimirong.model.bean.UserProfit;
import cn.app.yimirong.model.bean.VersionData;
import cn.app.yimirong.model.bean.WithDrawTips;
import cn.app.yimirong.model.db.dao.CookieDao;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.SystemUtils;

@SuppressWarnings("deprecation")
public class DataMgr {

	protected static final String TAG = "DataMgr";

	protected Logger logger;

	private Context appContext;

	private ACache mCache;

	public static SpecialDays sd;

	private static DataMgr manager;

	private ActivityTime activityTime;

	private long time = 0;

	private String version;

	private boolean isShow = false;

	private boolean allow = false;

	private boolean isAllow = false;

	private boolean isEyes = false;

	private long ntime = 0;

	private long comtime = 0;

	private long Ttime = 0;

	private long vtime = 0;

	private long rtime = 0;

	private long mtime = 0;

	private VersionData versionData;

	private WithDrawTips withDrawTips;

	public synchronized static DataMgr getInstance(Context appContext) {
		if (manager == null) {
			manager = new DataMgr(appContext);
		}
		return manager;
	}

	private DataMgr(Context appContext) {
		this.appContext = appContext;
		mCache = ACache.get(appContext);
		logger = Logger.getInstance();
	}

	/**
	 * 保存用户名和密码
	 *
	 * @param user
	 */
	public void saveUserData(User user) {
		if (user != null) {
			// 先存入缓存
			mCache.put("user", user);

			// 再存入配置文件
			SharedPreferences sp = appContext.getSharedPreferences(
					Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
			sp.edit().putString("username", user.username)
					.putString("password", user.password).commit();
		}
	}

	/**
	 * 清除用户名和密码
	 */
	public void clearUserData() {
		mCache.remove("user");
		SharedPreferences sp = appContext.getSharedPreferences(
				Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
		sp.edit().remove("username").remove("password").remove("ntime").remove("mtime").
		remove("allow").remove("isAllow").remove("isShow").remove("time").remove("Ttime").remove("vtime").remove("rtime").remove("comtime").
				remove("isEyes").commit();
	}

	/**
	 * 恢复用户名和密码
	 *
	 * @return
	 */
	public User restoreUserData() {
		// 先从缓存中获取
		User user = (User) mCache.getAsObject("user");
		if (user == null) {
			// 缓存中没有从配置文件中获取
			user = new User();
			SharedPreferences sp = appContext.getSharedPreferences(
					Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
			user.username = sp.getString("username", null);
			user.password = sp.getString("password", null);
		}
		return user;
	}

	/**
	 * 保存登录数据
	 */
	public void saveLoginData(LoginData data) {
		if (data != null) {
			// 先存入缓存
			mCache.put("login_data", data);

			SharedPreferences sp = appContext.getSharedPreferences(
					Constant.LOGIN_DATA, Context.MODE_PRIVATE);
			sp.edit().putString("uid", data.uid)
					.putString("account", data.account)
					.putString("birth", data.birth)
					.putString("cip", data.cip)
					.putString("code", data.code)
					.putString("ctime", data.ctime)
					.putString("ltime", data.ltime)
					.putString("plat", data.plat).putString("sex", data.sex)
					.commit();
		}
	}

	/**
	 * 清除LoginData
	 */
	public void clearLoginData() {
		mCache.remove("login_data");

		SharedPreferences sp = appContext.getSharedPreferences(
				Constant.LOGIN_DATA, Context.MODE_PRIVATE);
		sp.edit().remove("uid").remove("account").remove("birth")
				.remove("code").remove("ctime").remove("ltime").remove("plat")
				.commit();
	}

	/**
	 * 恢复登录数据
	 *
	 * @return
	 */
	public LoginData restoreLoginData() {
		LoginData loginData = App.loginData;
		if (loginData == null) {
			loginData = (LoginData) mCache.getAsObject("login_data");
		}
		if (loginData == null) {
			SharedPreferences sp = appContext.getSharedPreferences(
					Constant.LOGIN_DATA, Context.MODE_PRIVATE);
			loginData = new LoginData();
			loginData.uid = sp.getString("uid", null);
			loginData.account = sp.getString("account", null);
			loginData.birth = sp.getString("birth", null);
			loginData.cip = sp.getString("cip", null);
			loginData.ctime = sp.getString("ctime", null);
			loginData.lip = sp.getString("ltime", null);
			loginData.ltime = sp.getString("ltime", null);
			loginData.plat = sp.getString("plat", null);
			loginData.sex = sp.getString("sex", null);
			loginData.code = sp.getString("code", null);
		}
		return loginData;
	}

	/**
	 * 保存Cookies
	 */
	public boolean saveCookies(List<Cookie> cookies) {
		// 存入数据库
		CookieDao dao = CookieDao.getInstance(appContext);
		// 先删除再插入
		dao.deleteAll();
		boolean isInserted = dao.insert(cookies);
		return isInserted;
	}

	/**
	 * 清空Cookies
	 */
	public void clearCookies() {
		CookieDao dao = CookieDao.getInstance(appContext);
		dao.deleteAll();
	}



	/**
	 * 保存收益
	 */
	public void  saveUserProfitInfo(UserProfit mProfit){
		if (mProfit != null){
			// 先缓存
			mCache.put("mProfit", mProfit);

			// 存到配置文件
			SharedPreferences sp = appContext.getSharedPreferences(
					Constant.USER_PROFIT_INFO, Context.MODE_PRIVATE);

			sp.edit()
					.putString("transaction",String.valueOf(mProfit.transaction))
					.putString("buyNum",String.valueOf(mProfit.buyNum))
					.putString("countProfit",String.valueOf(mProfit.countProfit))
					.putString("expmoney",String.valueOf(mProfit.expmoney))
					.putString("expmoneyCountprofit",String.valueOf(mProfit.expmoneyCountprofit))
					.putString("expmoneyYesterdayprofit",String.valueOf(mProfit.expmoneyYesterdayprofit))
					.putString("invite",String.valueOf(mProfit.invite))
					.putString("klmoney",String.valueOf(mProfit.klmoney))
					.putString("klmoneyCountProfit",String.valueOf(mProfit.klmoneyCountProfit))
					.putString("longmoney",String.valueOf(mProfit.longmoney))
					.putString("yesterdayProfit",String.valueOf(mProfit.yesterdayProfit))
					.putString("longmoneyCountprofit",String.valueOf(mProfit.longmoneyCountprofit))
					.putString("expmoneyCurrentProfit",String.valueOf(mProfit.expmoneyCurrentProfit))
					.commit();
		}

	}

	/**
	 * 清空用户收益
	 * */
	public void clearUserProfitInfo(){
		mCache.remove("mProfit");

		SharedPreferences sp = appContext.getSharedPreferences(Constant.USER_PROFIT_INFO,
				Context.MODE_PRIVATE);
		sp.edit().remove("transaction").remove("buyNum")
				.remove("countProfit").remove("expmoney").remove("expmoneyCountprofit")
				.remove("expmoneyYesterdayprofit").remove("invite")
				.remove("klmoney").remove("klmoneyCountProfit")
				.remove("longmoney").remove("yesterdayProfit")
				.remove("longmoneyCountprofit").remove("expmoneyCurrentProfit").commit();


	}

	/**
	 * 恢复我的资产中数据
	 *
	 * @return
	 */
	public UserProfit restoreUserProfitInfo() {
		// 先从缓存活获取
		UserProfit mProfit = (UserProfit) mCache.getAsObject("mProfit");
		if (mProfit == null) {
			// 缓存中没有，从配置文件中获取
			SharedPreferences sp = appContext.getSharedPreferences(
					Constant.USER_PROFIT_INFO, Context.MODE_PRIVATE);
			mProfit = new UserProfit();

			mProfit.transaction = Double.parseDouble(sp.getString("transaction", null));
			mProfit.buyNum = Integer.parseInt(sp.getString("buyNum", null));
			mProfit.countProfit = Double.parseDouble(sp.getString("countProfit", null));
			mProfit.expmoney = Double.parseDouble(sp.getString("expmoney", null));
			mProfit.expmoneyCountprofit = Double.parseDouble(sp.getString("expmoneyCountprofit", null));
			mProfit.expmoneyCurrentProfit = Double.parseDouble(sp.getString("expmoneyCurrentProfit", null));
			mProfit.invite = Double.parseDouble(sp.getString("invite", null));
			mProfit.klmoney = Double.parseDouble(sp.getString("klmoney", null));
			mProfit.klmoneyCountProfit = Double.parseDouble(sp.getString("klmoneyCountProfit", null));
			mProfit.longmoney = Double.parseDouble(sp.getString("longmoney", null));
			mProfit.longmoneyCountprofit = Double.parseDouble(sp.getString("longmoneyCountprofit", null));
			mProfit.yesterdayProfit = Double.parseDouble(sp.getString("yesterdayProfit", null));
			mProfit.expmoneyYesterdayprofit = Double.parseDouble(sp.getString("expmoneyYesterdayprofit", null));

		}
		return mProfit;

	}



	/**
	 * 保存在我的资产中数据
	 */
	public void savOffLineInfo(OffLineInfo mOffLineInfo) {
		if (mOffLineInfo != null) {

			// 先缓存
			mCache.put("offline_info", mOffLineInfo);

			// 存到配置文件
			SharedPreferences sp = appContext.getSharedPreferences(
					Constant.OFFLINE_INFO, Context.MODE_PRIVATE);

			sp.edit()
					.putString("yesterday_profit", mOffLineInfo.yesterday_profit)
					.putString("kymoney", mOffLineInfo.kymoney)
					.putString("buynum", mOffLineInfo.buynum)
					.putString("longmoney", mOffLineInfo.longmoney)
					.putString("dqmoney", mOffLineInfo.dqmoney)
					.putString("allmoney", mOffLineInfo.allmoney)
					.putString("allprofit", mOffLineInfo.allprofit)
					.putString("tyjin", mOffLineInfo.tyjin)
					.putString("invite", mOffLineInfo.invite)
					.commit();


		}
	}

	/**
	 * 清空我的资产中数据
	 */
	public void clearOffLineInfo() {
		// 删除UserInfo
		mCache.remove("offline_info");

		SharedPreferences sp = appContext.getSharedPreferences(Constant.OFFLINE_INFO,
				Context.MODE_PRIVATE);
		sp.edit().remove("yesterday_profit").remove("kymoney")
				.remove("buynum").remove("longmoney").remove("dqmoney")
				.remove("allmoney").remove("allprofit")
				.remove("tyjin").commit();
	}

	/**
	 * 恢复我的资产中数据
	 *
	 * @return
	 */
	public OffLineInfo restoreOffLineInfo() {
		// 先从缓存活获取
		OffLineInfo mOffLineInfo = (OffLineInfo) mCache.getAsObject("offline_info");
		if (mOffLineInfo == null) {
			// 缓存中没有，从配置文件中获取
			SharedPreferences sp = appContext.getSharedPreferences(
					Constant.OFFLINE_INFO, Context.MODE_PRIVATE);
			mOffLineInfo = new OffLineInfo();
			mOffLineInfo.yesterday_profit = sp.getString("yesterday_profit", null);
			mOffLineInfo.kymoney = sp.getString("kymoney", null);
			mOffLineInfo.buynum = sp.getString("buynum", null);
			mOffLineInfo.longmoney = sp.getString("longmoney", null);
			mOffLineInfo.dqmoney = sp.getString("dqmoney", null);
			mOffLineInfo.allmoney = sp.getString("allmoney", null);
			mOffLineInfo.allprofit = sp.getString("allprofit", null);
			mOffLineInfo.tyjin = sp.getString("tyjin", null);
			mOffLineInfo.invite = sp.getString("invite", null);
		}
		return mOffLineInfo;
	}


	/**
	 * 恢复Cookies
	 *
	 * @return
	 */
	public List<Cookie> restoreCookies() {
		// 如果缓存中没有，从数据库中查询
		CookieDao dao = CookieDao.getInstance(appContext);
		List<Cookie> cookies = dao.queryAll();
		return cookies;
	}

	/**
	 * 保存用户数据
	 */
	public void saveUserInfo(UserInfo userinfo) {
		if (userinfo != null) {
			// 先缓存
			mCache.put("userinfo", userinfo);

			// 存到配置文件
			SharedPreferences sp = appContext.getSharedPreferences(
					Constant.USER_INFO, Context.MODE_PRIVATE);

			sp.edit()
					.putString("balance", userinfo.balance)
					.putString("canBuyLong", SystemUtils.getDoubleStr(userinfo.canBuyLong))
					.putString("top_uid", userinfo.top_uid)
					.putString("top_pwd", userinfo.top_pwd)
					.putInt("qiandao",userinfo.qiandao)
					.commit();

			if (userinfo.identity != null) {
				sp.edit().putString("bankid", userinfo.identity.bankid)
						.putString("bankname", userinfo.identity.bankname)
						.putString("cardnoTop", userinfo.identity.cardnoTop)
						.putString("cardno", userinfo.identity.cardno)
						.putString("idCard", userinfo.identity.idCard)
						.putString("realName", userinfo.identity.realName)
						.putBoolean("tpwd", userinfo.identity.tpwd)
						.putString("nameCard", userinfo.identity.nameCard)
						.putString("isnew", userinfo.identity.isnew)
						.commit();
			} else {
				sp.edit().remove("bankid").remove("bankname")
						.remove("cardnoTop").remove("cardno").remove("idCard")
						.remove("realName").remove("tpwd").remove("nameCard")
						.remove("isnew")
						.commit();
			}
		}
	}

	/**
	 * 清空用户数据
	 */
	public void clearUserInfo() {
		// 删除UserInfo
		mCache.remove("userinfo");

		SharedPreferences sp = appContext.getSharedPreferences(Constant.USER_INFO,
				Context.MODE_PRIVATE);
		sp.edit().remove("balance").remove("canBuyLong").remove("bankid")
				.remove("bankname").remove("cardnoTop").remove("cardno")
				.remove("idCard").remove("realName").remove("tpwd").remove("top_uid").remove("top_pwd")
				.remove("nameCard").commit();
	}

	/**
	 * 恢复用户数据
	 *
	 * @return
	 */
	public UserInfo restoreUserInfo() {
		// 先从缓存活获取
		UserInfo userinfo = (UserInfo) mCache.getAsObject("userinfo");
		if (userinfo == null) {
			// 缓存中没有，从配置文件中获取
			SharedPreferences sp = appContext.getSharedPreferences(
					Constant.USER_INFO, Context.MODE_PRIVATE);
			userinfo = new UserInfo();
			Identity identify = userinfo.new Identity();
			userinfo.balance = sp.getString("balance", "0.00");
			userinfo.canBuyLong = SystemUtils.getDouble(sp.getString("canBuyLong", "0.00"));
			userinfo.top_uid = sp.getString("top_uid",null);
			userinfo.top_pwd = sp.getString("top_pwd",null);
			userinfo.qiandao = sp.getInt("qiandao",0);
			identify.bankid = sp.getString("bankid", null);
			identify.bankname = sp.getString("bankname", null);
			identify.cardnoTop = sp.getString("cardnoTop", null);
			identify.cardno = sp.getString("cardno", null);
			identify.idCard = sp.getString("idCard", null);
			identify.realName = sp.getString("realName", null);
			identify.tpwd = sp.getBoolean("tpwd", false);
			identify.nameCard = sp.getString("nameCard", null);
			userinfo.identity = identify;
		}
		return userinfo;
	}

	/**
	 * 保存用户余额
	 *
	 * @param balance
	 */
	public void saveBalance(String balance) {
		if (balance != null) {
			mCache.put("blance", balance);
		}
	}

	/**
	 * 从缓存清除账户余额
	 */
	public void clearBalance() {
		mCache.remove("balance");
	}

	/**
	 * 从缓存中获取Balance
	 *
	 * @return
	 */
	public String restoreBalance() {
		return mCache.getAsString("balance");
	}

	/**
	 * 保存常见问题
	 */
	public void saveQuestions(List<Questions> list) {
		mCache.put("questions", (Serializable) list);
	}

	/**
	 * 清空常见问题
	 */
	public void clearQuestions() {
		mCache.remove("questions");
	}

	/**
	 * 恢复常见问题
	 *
	 * @return
	 */
	public List<Questions> restoreQuestions() {
		@SuppressWarnings("unchecked")
		List<Questions> list = (List<Questions>) mCache
				.getAsObject("questions");
		return list;
	}

	/**
	 * 保存体验金余额
	 */
	public void saveExpMoneyBalance(String expmoney) {
		mCache.put("expmoney_balance", expmoney);
	}

	/**
	 * 清空体验金余额
	 */
	public void clearExpMoneyBalance() {
		mCache.remove("expmoney_balance");
	}

	/**
	 * 恢复体验金余额
	 *
	 * @return
	 */
	public String restoreExpMoneyBalance() {
		String expmoney = mCache.getAsString("expmoney_balance");
		if (StringUtils.isBlank(expmoney)) {
			expmoney = "0.00";
		}
		return expmoney;
	}

	/**
	 * 保存
	 *
	 * @param key
	 * @param obj
	 */
	public void save(String key, Serializable obj) {
		if (StringUtils.isBlank(key) || obj == null) {
			return;
		}
		mCache.put(key, obj);
	}

	/**
	 * 清除
	 *
	 * @param key
	 */
	public void clear(String key) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		mCache.remove(key);
	}

	/**
	 * 恢复
	 *
	 * @param key
	 * @return
	 */
	public Object restore(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		Object obj = mCache.getAsObject(key);
		return obj;
	}

	public void saveVtime(long vtime){
		this.vtime = vtime;

		SharedPreferences sp = appContext.getSharedPreferences(Constant.SP_CONFIG_NAME,Context.MODE_PRIVATE);
		sp.edit().putLong("vtime",vtime).commit();
	}

	public void saveRtime(long rtime){
		this.rtime = rtime;

		SharedPreferences sp = appContext.getSharedPreferences(Constant.SP_CONFIG_NAME,Context.MODE_PRIVATE);
		sp.edit().putLong("rtime",rtime).commit();
	}

	public long restorRtime(){
		SharedPreferences sp = appContext.getSharedPreferences(Constant.SP_CONFIG_NAME,Context.MODE_PRIVATE);
		rtime = sp.getLong("rtime",0);
		return rtime;
	}

	public long restorVtime(){
		SharedPreferences sp = appContext.getSharedPreferences(Constant.SP_CONFIG_NAME,Context.MODE_PRIVATE);
		vtime = sp.getLong("vtime",0);
		return vtime;
	}

//	public void saveTicTime(long Ttime){
//		this.Ttime = Ttime;
//
//		SharedPreferences sp = appContext.getSharedPreferences(Constant.SP_CONFIG_NAME,Context.MODE_PRIVATE);
//		sp.edit().putLong("Ttime",Ttime).commit();
//	}
//
//	public long restorTtime(){
//		SharedPreferences sp = appContext.getSharedPreferences(Constant.SP_CONFIG_NAME,Context.MODE_PRIVATE);
//		Ttime = sp.getLong("Ttime",0);
//		return Ttime;
//	}

	public void saveComTime(long comtime){
		this.comtime = comtime;
		SharedPreferences sp = appContext.getSharedPreferences(Constant.SP_CONFIG_NAME,Context.MODE_PRIVATE);
		sp.edit().putLong("comtime",comtime).commit();
	}

	public long restorComTime(){
		SharedPreferences sp = appContext.getSharedPreferences(Constant.SP_CONFIG_NAME,Context.MODE_PRIVATE);
		comtime =sp.getLong("comtime",0);
		return comtime;
	}

	public void saveNtime(long ntime){
		this.ntime = ntime;

		SharedPreferences sp = appContext.getSharedPreferences(Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
		sp.edit().putLong("ntime",ntime).commit();
	}

	public long restorNtime(){
		SharedPreferences sp = appContext.getSharedPreferences(Constant.SP_CONFIG_NAME,Context.MODE_PRIVATE);
		ntime =sp.getLong("ntime",0);
		return ntime;
	}

	public void saveMtime(long mtime){
		this.mtime = mtime;

		SharedPreferences sp = appContext.getSharedPreferences(Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
		sp.edit().putLong("mtime",mtime).commit();
	}

	public long restorMtime(){
		SharedPreferences sp = appContext.getSharedPreferences(Constant.SP_CONFIG_NAME,Context.MODE_PRIVATE);
		mtime =sp.getLong("mtime",0);
		return mtime;
	}

	public void saveAllow(boolean allow){
		this.allow = allow;

		// 再存入配置文件
		SharedPreferences sp = appContext.getSharedPreferences(
				Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
		sp.edit().putBoolean("allow", allow).commit();
	}

	public boolean saveAllow(){

			// 缓存中没有从配置文件中获取
			SharedPreferences sp = appContext.getSharedPreferences(
					Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
			allow = sp.getBoolean("allow", false);

		return allow;
	}

	public void saveisAllow(boolean isAllow){
		this.isAllow = isAllow;

		// 再存入配置文件
		SharedPreferences sp = appContext.getSharedPreferences(
				Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
		sp.edit().putBoolean("isAllow", isAllow).commit();
	}

	public boolean saveisAllow(){

			// 缓存中没有从配置文件中获取
			SharedPreferences sp = appContext.getSharedPreferences(
					Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
			isAllow = sp.getBoolean("isAllow", false);
		return isAllow;
	}

	public void saveShow(boolean isShow){
		this.isShow = isShow;

		// 再存入配置文件
		SharedPreferences sp = appContext.getSharedPreferences(
				Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
		sp.edit().putBoolean("isShow", isShow).commit();
	}

	public boolean restorShow(){

		// 缓存中没有从配置文件中获取
		SharedPreferences sp = appContext.getSharedPreferences(
				Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
		isShow = sp.getBoolean("isShow", false);

		return isShow;
	}

	public void saveEys(boolean isEyes){
		this.isEyes = isEyes;
		// 再存入配置文件
		SharedPreferences sp = appContext.getSharedPreferences(
				Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
		sp.edit().putBoolean("isEyes", isEyes).commit();
	}

	public boolean restorEyes(){

		// 缓存中没有从配置文件中获取
		SharedPreferences sp = appContext.getSharedPreferences(
				Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
		isEyes = sp.getBoolean("isEyes", false);

		return isEyes;
	}

	public void saveEvaluateTime(long time) {
		this.time = time;

		// 再存入配置文件
		SharedPreferences sp = appContext.getSharedPreferences(
				Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
		sp.edit().putLong("time", time).commit();
	}

	public long restoreEvaluateTime() {

			// 缓存中没有从配置文件中获取
			SharedPreferences sp = appContext.getSharedPreferences(
					Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
			time = sp.getLong("time", 0);
		return time;
	}

	public void saveisVersion(String version) {
		this.version = version;

		// 再存入配置文件
		SharedPreferences sp = appContext.getSharedPreferences(
				Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
		sp.edit().putString("version", version).commit();
	}

	public String restoreisversion() {

			// 缓存中没有从配置文件中获取
			SharedPreferences sp = appContext.getSharedPreferences(
					Constant.SP_CONFIG_NAME, Context.MODE_PRIVATE);
			version = sp.getString("version", null);
		return version;
	}

	public void clearEvaluateTime() {
		mCache.remove("time");
	}

	public void saveActivityTime(ActivityTime activityTime) {
		this.activityTime = activityTime;
		mCache.put("activity_time", activityTime);
	}

	public ActivityTime restoreActivityTime() {
		if (activityTime == null) {
			activityTime = (ActivityTime) mCache.getAsObject("activity_time");
		}
		return activityTime;
	}

	public void clearActivityTime() {
		activityTime = null;
		mCache.remove("activity_time");
	}

	public void saveVersionData(VersionData data) {
		this.versionData = data;
		mCache.put("version_data", data);
	}

	public void clearVersionData() {
		this.versionData = null;
		if (mCache != null) {
			mCache.remove("version_data");
		}
	}

	public VersionData restoreVersionData() {
		if (this.versionData == null) {
			if (mCache != null) {
				this.versionData = (VersionData) mCache.getAsObject("version_data");
			}
		}

		if (this.versionData == null) {
			versionData = new VersionData();
		}
		return this.versionData;
	}

	public void saveWithDrawTips(WithDrawTips tips) {
		this.withDrawTips = tips;
		if (mCache != null) {
			mCache.put("withdraw_tips", tips);
		}
	}

	public WithDrawTips restoreWithDrawTips() {
		if (this.withDrawTips == null) {
			if (mCache != null) {
				this.withDrawTips = (WithDrawTips) mCache.getAsObject("withdraw_tips");
			}
		}
		if (this.withDrawTips == null) {
			withDrawTips = new WithDrawTips();
		}
		return this.withDrawTips;
	}
}
