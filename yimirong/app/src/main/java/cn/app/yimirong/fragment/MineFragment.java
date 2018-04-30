package cn.app.yimirong.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.YWLoginParam;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.IYWConversationUnreadChangeListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.activity.AboutUsActivity;
import cn.app.yimirong.activity.AccountActivity;
import cn.app.yimirong.activity.CompanyNewsActivity;
import cn.app.yimirong.activity.FeedBackActivity;
import cn.app.yimirong.activity.InviteActivity;
import cn.app.yimirong.activity.LoginActivity;
import cn.app.yimirong.activity.MessageActivity;
import cn.app.yimirong.activity.MyRewardActivity;
import cn.app.yimirong.activity.QAActivity;
import cn.app.yimirong.activity.WebViewActivity;
import cn.app.yimirong.base.BaseFragment;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.LoginChangeEvent;
import cn.app.yimirong.event.custom.ProfitEvent;
import cn.app.yimirong.event.custom.ReadMsgEvent;
import cn.app.yimirong.event.custom.ShareInviteEvent1;
import cn.app.yimirong.event.custom.ShareInviteEvent2;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.bean.ComNews;
import cn.app.yimirong.model.bean.Message;
import cn.app.yimirong.model.bean.ShareInvite;
import cn.app.yimirong.model.bean.UserProfit;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.presenter.impl.MsgPresenter;
import cn.app.yimirong.utils.PhoneUtils;
import cn.app.yimirong.utils.PromptUtils;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.TimeUtils;

public class MineFragment extends BaseFragment {

	public static final int InvestFlag = 212;


	private MsgPresenter presenter;
	private List<Message> messages;
	private List<ComNews> cList;

	private UserProfit mProfit;

	private DataMgr offdata;

	private TextView bindNull,tvInviteInfo,tvAccount,tvTitle;

	private ImageView ivMsgDot;

	private View rootView;

	private YWIMKit mIMKit;

	private IYWConversationUnreadChangeListener mConversationUnreadChangeListener;

	private Handler mHandler = new Handler(Looper.getMainLooper());

	private IYWConversationService mConversationService;
	private TextView insurance_tv;

	private void init(View rootView){

		bindNull = (TextView) rootView.findViewById(R.id.bind_null);
		tvInviteInfo = (TextView) rootView.findViewById(R.id.fragment_mine_invite_info);
		tvAccount = (TextView) rootView.findViewById(R.id.fragment_mine_account);
		tvTitle = (TextView) rootView.findViewById(R.id.main_title_bar_title_text);
		ivMsgDot = (ImageView) rootView.findViewById(R.id.fragment_mine_message_dot);
		insurance_tv = (TextView) rootView.findViewById(R.id.insurance_tv);
		Drawable drawable1 = getResources().getDrawable(R.drawable.insurance);
		drawable1.setBounds(0, 0, 47, 47);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
		insurance_tv.setCompoundDrawables(drawable1, null, null, null);//只放左边
		RelativeLayout titleBar = (RelativeLayout) rootView.findViewById(R.id.mine_title_bar);
		rootView.findViewById(R.id.insurance).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, WebViewActivity.class);
				intent.putExtra("title", "安全保障");
				intent.putExtra("url", API.QUALIFICATION);
				startActivity(intent);
			}
		});
		/**
		 * 获取状态栏高度——方法1
		 * */
		int statusBarHeight1 = -1;
		//获取status_bar_height资源的ID
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			//根据资源ID获取响应的尺寸值
			statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
		}
		titleBar.setPadding(0,statusBarHeight1,0,0);

	}

	@Override
	public View loadView(LayoutInflater inflater, ViewGroup container) {
		if (rootView == null){
			rootView = inflater.inflate(R.layout.fragment_mine, container, false);
			init(rootView);
		}
		ViewGroup par = (ViewGroup)rootView.getParent();
		if (par != null){
			par.removeView(rootView);
		}
		return rootView;
	}

	@Override
	public void initView() {
		if (tvTitle != null) {
			tvTitle.setText("我");
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		checkLogin();
		initData();
	}

	@Override
	public void onVisible() {
		super.onVisible();
		checkLogin();
		initData();
	}

	@Override
	public void initData() {
		if (!activity.bottomNav.isShown()){
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					activity.bottomNav.setVisibility(View.VISIBLE);
				}
			},500);
		}

		presenter = new MsgPresenter();
		presenter.bind(this);
		presenter.loadMessages();
		messages = presenter.getMessages();

		if (cList == null) {
			cList = new ArrayList<>();
		}

		loadShareInvite();


		getCmNews();

		final long mtime = DataMgr.getInstance(context).restorMtime();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				if (messages != null) {
					if (mtime == 0) {
						if (messages.size() > 0) {
							showMsgDot();
						}else {
							hideMsgDot();
						}
					} else {
						int num = 0;
						for (int i = 0;i < messages.size();i++){
							if (Long.parseLong(messages.get(i).ctime) > mtime){
								num += 1;
							}
							if (num > 0){
								showMsgDot();
							}else {
								hideMsgDot();
							}

						}
					}
				}


			}
		}, 300);

	}

	private void loginYW(){
		activity.showLoading("玩命加载中");
		String userid = App.userinfo.top_uid;
		String password = App.userinfo.top_pwd;
		if(!userid.isEmpty() && !password.isEmpty()) {
//			mIMKit = YWAPI.getIMKitInstance(userid, "");
			mConversationService = mIMKit.getConversationService();
			IYWLoginService loginService = mIMKit.getLoginService();
			YWLoginParam loginParam = YWLoginParam.createLoginParam(userid, password);
			loginService.login(loginParam, new IWxCallback() {

				@Override
				public void onSuccess(Object... arg0) {
					activity.closeLoading();
					toOnlineKF();
				}

				@Override
				public void onProgress(int arg0) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onError(int errCode, String description) {
					//如果登录失败，errCode为错误码,description是错误的具体描述信息
					activity.closeLoading();
				}
			});
			initConversationServiceAndListener();
		}
	}

	private void initConversationServiceAndListener() {
		mConversationUnreadChangeListener = new IYWConversationUnreadChangeListener() {
			@Override
			public void onUnreadChange() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mConversationService = mIMKit.getConversationService();
						if(mConversationService != null){
							int unReadCount = mConversationService.getAllUnreadCount();
						}
					}
				});
			}
		};
		mConversationService.addTotalUnreadChangeListener(mConversationUnreadChangeListener);
	}



	private void getCmNews(){
		if (amodel != null){
			amodel.getCompanyNews(1, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof List){
						List<ComNews> list = (List<ComNews>)t;
						if (cList != null){
							cList.clear();
						}
						cList.addAll(list);
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
				}
			});
		}
	}

	private void checkLogin() {
		if (App.isLogin) {
			if (App.userinfo != null
					&& App.userinfo.identity != null
					&& App.userinfo.identity.realName != null) {
				String str = "<font size='32px' color='#303030'>您好，"
						+ App.userinfo.identity.realName + "</font>";
				if (App.account != null) {
					String phone = StringUtils.getSecretAccount(App.account);
					str += "<br><font size='26px' color='#cccccc'>" + phone + "</font>";
				}

				tvAccount.setText(Html.fromHtml(str));

				bindNull.setVisibility(View.GONE);
			} else {
				String str1 = "<font size='32px' color='#000000'>您好</font>";
				if (App.account != null) {
					String phone = StringUtils.getSecretAccount(App.account);
					str1 += "<br><font size='26px' color='#cccccc'>" + phone + "</font>";
				}
				bindNull.setVisibility(View.VISIBLE);
				tvAccount.setText(Html.fromHtml(str1));
			}

		} else {
			bindNull.setVisibility(View.GONE);
			tvAccount.setText("大家一起来赚钱吧~");
		}
	}

	/**
	 * 获取邀请数据
	 */
	private void loadShareInvite() {
		ShareInvite shareInvite = (ShareInvite) mCache
				.getAsObject("share_invite");
		if (shareInvite != null) {
			tvInviteInfo.setText(shareInvite.share_invite);
		}
		tvInviteInfo.setVisibility(View.VISIBLE);
		EventBus.getDefault().post(new ShareInviteEvent1());
	}


	/**
	 * 显示公告小圆点
	 */
	public void showMsgDot() {
		ivMsgDot.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏公告小圆点
	 */
	public void hideMsgDot() {
		ivMsgDot.setVisibility(View.INVISIBLE);
	}


	@OnClick({R.id.fragment_mine_account_wrapper,
			R.id.fragment_mine_invite,
			R.id.fragment_mine_message,
			R.id.fragment_mine_question,
			R.id.fragment_mine_feedback,
			R.id.fragment_mine_aboutus,
			R.id.fragment_mine_service_time})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.fragment_mine_account_wrapper:
				toAccount();
				break;
			case R.id.fragment_mine_invite:
				toInvite();
				break;
			case R.id.fragment_mine_message:
				toMessage();
				break;
			case R.id.fragment_mine_question:
				toQuesion();
				break;
			case R.id.fragment_mine_feedback:
				toFeedBack();
				break;
			case R.id.fragment_mine_aboutus:
				toAboutUs();
				break;
			case R.id.fragment_mine_service_time:
				callService();
				break;
			default:
				break;
		}
	}

	public void toOnlineKF(){
			//userid是客服帐号，第一个参数是客服帐号，第二个是组ID，如果没有，传0
//			EServiceContact contact = new EServiceContact("", 0);
			//如果需要发给指定的客服帐号，不需要Server进行分流(默认Server会分流)，请调用EServiceContact对象
			//的setNeedByPass方法，参数为false。
			//contact.setNeedByPass(false);
//			Intent intent = mIMKit.getChattingActivityIntent(contact);
//			startActivity(intent);
	}


	private void toCompany(){
		DataMgr.getInstance(context).saveComTime(TimeUtils.getServerTime());
		Intent intent = new Intent(context, CompanyNewsActivity.class);
		intent.putExtra("cList",(Serializable)cList);
		startActivity(intent);
	}


	/**
	 * 去我的账户
	 */
	private void toAccount() {
		if (App.isLogin) {
			// 已登录
			Intent intent = new Intent(context, AccountActivity.class);
			startActivity(intent);
		} else {
			// 未登录
			Intent intent = new Intent(context, LoginActivity.class);
			startActivity(intent);
		}
	}

	/**
	 * 去邀请好友
	 */
	private void toInvite() {
		if (App.isLogin) {
			Intent intent = new Intent(context, InviteActivity.class);
			startActivity(intent);
		} else {
			PromptUtils.showDialog4(activity, context, "", "您还没有登录哦！", "取消", "登录",
					new PromptUtils.OnDialogClickListener2() {
						@Override
						public void onLeftClick(Dialog dialog) {
							dialog.dismiss();
						}

						@Override
						public void onRightClick(Dialog dialog) {
							Intent intent = new Intent(context, LoginActivity.class);
							startActivity(intent);
						}
					}, false);

		}
	}

	/**
	 * 去查看公告
	 */
	private void toMessage() {
		DataMgr.getInstance(context).saveMtime(TimeUtils.getServerTime());
		Intent intent = new Intent(context, MessageActivity.class);
		if (!messages.isEmpty()) {
			intent.putExtra("message", (Serializable) messages);
			intent.putExtra("notice", true);
		}
		startActivity(intent);
	}

	/**
	 * 去公司资质
	 */
	private void toQualification() {
		Intent intent = new Intent(context, WebViewActivity.class);
		intent.putExtra("title", "公司资质");
		intent.putExtra("url", API.QUALIFICATION);
		startActivity(intent);
	}

	/**
	 * 去意见反馈
	 */
	private void toFeedBack() {
		Intent intent = new Intent(context, FeedBackActivity.class);
		startActivity(intent);
	}

	/**
	 * 去常见问题
	 */
	private void toQuesion() {
		Intent intent = new Intent(context, QAActivity.class);
		startActivity(intent);
	}

	/**
	 * 去关于我们
	 */
	private void toAboutUs() {
		Intent intent = new Intent(context, AboutUsActivity.class);
		startActivity(intent);
	}



	/**
	 * 去我的奖励
	 */
	private void toMyinviteReward() {
		Intent intent = new Intent(context, MyRewardActivity.class);
		Bundle data = new Bundle();
		data.putSerializable("userprofit", mProfit);
		intent.putExtras(data);
		context.startActivity(intent);
	}

	/**
	 * 拨打客服电话
	 */
	private void callService() {
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

	public void onEventMainThread(ShareInviteEvent2 event) {
		if (isViewInited) {
			ShareInvite shareInvite = (ShareInvite) mCache
					.getAsObject("share_invite");
			if (shareInvite != null) {
				tvInviteInfo.setText(shareInvite.share_invite);
			}
		}
	}

	public void onEventMainThread(ProfitEvent event) {
		mProfit=offdata.getInstance(context).restoreUserProfitInfo();
		checkLogin();
	}

	public void onEventMainThread(LoginChangeEvent event) {
		checkLogin();
	}

	public void onEventMainThread(ReadMsgEvent event) {
		updateView(null);
	}
}
