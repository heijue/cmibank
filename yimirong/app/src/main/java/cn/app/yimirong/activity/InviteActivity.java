package cn.app.yimirong.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.DataMgr;
import cn.app.yimirong.model.bean.InviteFriend;
import cn.app.yimirong.model.bean.LoginData;
import cn.app.yimirong.model.bean.ShareInvite;
import cn.app.yimirong.model.bean.YaoQingAward;
import cn.app.yimirong.model.http.API;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;

import static com.umeng.socialize.bean.SHARE_MEDIA.WEIXIN;

public class InviteActivity extends BaseActivity implements OnClickListener {

	private TextView tvYiYaoQing;
	private TextView tvYiJiaoYi;
	private TextView zcNumber;
	private TextView zcTime, ivNtext;

	private boolean isBuy = true;

	private ListView listView;

	private List<InviteFriend> list;

	private List<YaoQingAward> aworded;

	private LinearLayout logo;

	private RelativeLayout ivNull;

	private BaseAdapter adapter;

	protected int tab = 1;

	private Bitmap bmp;

	private boolean isFirstIn = true;
	private String code;
	private String shareTile;
	private String shareContent;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_invite);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("邀请好友");
		setTitleRight(true, new OnRightClickListener() {
			@Override
			public void onClick() {
				showDesc();
			}
		});
		setRightText("邀请说明");
		tvYiYaoQing = (TextView) findViewById(R.id.activity_invite_yiyaoqing);
		tvYiYaoQing.setOnClickListener(this);

		zcNumber = (TextView) findViewById(R.id.activity_invite_number);
		zcTime = (TextView) findViewById(R.id.activity_invite_time);
		logo = (LinearLayout) findViewById(R.id.invite_logo);
		ivNull = (RelativeLayout) findViewById(R.id.invite_null);
		ivNtext = (TextView) findViewById(R.id.invite_null_text);

		tvYiJiaoYi = (TextView) findViewById(R.id.activity_invite_yijiaoyi);
		tvYiJiaoYi.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.activity_invite_listview);

		TextView btnInvite = (TextView) findViewById(R.id.activity_invite_btn);
		btnInvite.setOnClickListener(this);
	}

	private void showDesc() {
		Intent intent = new Intent(this, InviteDescActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.alpha_in_50, R.anim.alpha_out_50);
	}

	@Override
	public void initData() {
		list = new ArrayList<>();
		aworded = new ArrayList<>();
		adapter = new InviteFriendAdapter(list, aworded);
		listView.setAdapter(adapter);
		code = App.code;
		if (code == null) {
			LoginData loginData = DataMgr.getInstance(appContext)
					.restoreLoginData();
			code = loginData.code;
			App.code = code;
		}
		setToYiYaoQing();
		ShareInvite shareInvite = (ShareInvite) mCache
				.getAsObject("share_invite");
		if (shareInvite != null) {
			shareTile = shareInvite.share_title;
			shareContent = shareInvite.share_content;
		} else {
			// 邀请文本 默认值
			shareTile = "易米融";
			shareContent = "我正在使用易米融，是个靠谱的理财平台，推荐试一下。";
		}
		if (list != null && list.size() > 0) {
			logo.setVisibility(View.VISIBLE);
			listView.setVisibility(View.VISIBLE);
			ivNull.setVisibility(View.GONE);
		} else {
			logo.setVisibility(View.GONE);
			listView.setVisibility(View.GONE);
			ivNull.setVisibility(View.VISIBLE);
			ivNtext.setText("您还没有邀请好友哦！");
		}
	}

	@Override
	protected void onResume() {
		hideInputMethod();
		super.onResume();
	}


	private void addTitle() {
		if (isBuy) {

			zcNumber.setText("注册号码");
			zcTime.setText("注册时间");

		} else {
			zcNumber.setText("注册号码");
			zcTime.setText("首次交易时间");
		}

	}

	private void loadMyInvite() {
		if (!App.isLogin) {
			return;
		}

		if (amodel != null) {
			showLoading("玩命向钱冲");
			amodel.getMyInvite(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null && t instanceof List) {
						List<YaoQingAward> friends = (List<YaoQingAward>) t;
						if (!friends.isEmpty()) {
							sort(friends);
							list.addAll(friends);
							adapter.notifyDataSetChanged();
						} else {
							if (isFirstIn) {
								isFirstIn = false;
								showDesc();
							}
						}
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					ToastUtils.show(context, msg);
				}
			});
		}
	}

	private void getMyInvivteBuy() {
		if (!App.isLogin) {
			return;
		}
		if (amodel != null) {
			showLoading("玩命向钱冲");
			amodel.getMyInviteBuy(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null && t instanceof List) {
						List<YaoQingAward> awards = (List<YaoQingAward>) t;
						if (!awards.isEmpty()) {
							sort(awards);
							aworded.addAll(awards);
							adapter.notifyDataSetChanged();
						}
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					ToastUtils.show(context, msg);
				}
			});
		}
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.activity_invite_yiyaoqing:
				// 已邀请
				isBuy = true;
				setToYiYaoQing();
				break;

			case R.id.activity_invite_yijiaoyi:
				// 已交易
				isBuy = false;
				setToYiJiaoYi();

				break;

			case R.id.activity_invite_btn:
				// 打开邀请
				openInvite();
				break;
			default:
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if(bmp!=null){
			bmp.isRecycled();
			bmp=null;
		}
	}

	/**
	 * 打开分享面板
	 */
	private void openInvite() {
		if (!StringUtils.isBlank(code)) {

			Resources res = getResources();
			bmp = BitmapFactory.decodeResource(res, R.mipmap.icon);
			String baseUrl = "";
			if (App.DEBUG_LEVEL == App.DEBUG_LEVEL_TEST) {
				baseUrl = API.API_TEST;
			} else {
				baseUrl = API.API_HTTPS;
			}
			UMWeb web = new UMWeb(API.API_HTTP+"invite_page?code=" + code);
			web.setThumb(new UMImage(context,bmp));
			web.setTitle(shareTile);
			web.setDescription(shareContent);

			new ShareAction(this)
				.setDisplayList(SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE, WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
				.withMedia(web)
				.setCallback(umShareListener)
				.open();
		}
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

			Toast.makeText(InviteActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			if (t.getMessage().contains("没有安装应用")) {
				switch (platform) {
					case WEIXIN:
						ToastUtils.show(InviteActivity.this,"您没有安装微信");
						break;
					case QQ:
						ToastUtils.show(InviteActivity.this,"您没有安装QQ");
						break;
					case QZONE:
						ToastUtils.show(InviteActivity.this,"您没有安装QZONE");
						break;
					case WEIXIN_CIRCLE:
						ToastUtils.show(InviteActivity.this,"您没有安装微信");
						break;
				}
			} else {
				Toast.makeText(InviteActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {

		}
	};

	/**
	 * 已邀请
	 */
	@SuppressWarnings("deprecation")
	private void setToYiYaoQing() {
		tab = 1;
		tvYiJiaoYi.setBackgroundDrawable(null);
		tvYiJiaoYi.setTextColor(Color.parseColor("#ff4747"));

		tvYiYaoQing.setBackgroundResource(R.drawable.shape_invite_left);
		tvYiYaoQing.setTextColor(Color.WHITE);

		list.clear();
		aworded.clear();
		addTitle();
		loadMyInvite();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				resd();
			}
		}, 300);
	}

	private void resd() {
		if (list != null && list.size() > 0) {
			logo.setVisibility(View.VISIBLE);
			listView.setVisibility(View.VISIBLE);
			ivNull.setVisibility(View.GONE);
		} else {
			logo.setVisibility(View.GONE);
			listView.setVisibility(View.GONE);
			ivNull.setVisibility(View.VISIBLE);
			ivNtext.setText("您还没有邀请好友哦！");
		}
	}

	/**
	 * 已交易
	 */
	@SuppressWarnings("deprecation")
	private void setToYiJiaoYi() {
		tab = 2;
		tvYiYaoQing.setBackgroundDrawable(null);
		tvYiYaoQing.setTextColor(Color.parseColor("#ff4747"));

		tvYiJiaoYi.setBackgroundResource(R.drawable.shape_invite_right);
		tvYiJiaoYi.setTextColor(Color.WHITE);

		list.clear();
		aworded.clear();
		addTitle();
		getMyInvivteBuy();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				rest();
			}
		}, 300);
	}

	private void rest() {
		if (aworded != null && aworded.size() > 0) {
			logo.setVisibility(View.VISIBLE);
			listView.setVisibility(View.VISIBLE);
			ivNull.setVisibility(View.GONE);
		} else {
			logo.setVisibility(View.GONE);
			listView.setVisibility(View.GONE);
			ivNull.setVisibility(View.VISIBLE);
			ivNtext.setText("您还没有已交易好友哦！");
		}
	}

	/**
	 * 排序
	 *
	 * @param awards
	 */
	protected void sort(List<YaoQingAward> awards) {
		if (isBuy) {
			Collections.sort(awards, new Comparator<InviteFriend>() {
				@Override
				public int compare(InviteFriend o1, InviteFriend o2) {
					long time1 = Long.parseLong(o1.itime);
					long time2 = Long.parseLong(o2.itime);

					if (time1 > time2) {
						return -1;
					}

					if (time1 < time2) {
						return 0;
					}

					return 0;
				}
			});
		} else {
			Collections.sort(awards, new Comparator<YaoQingAward>() {
				@Override
				public int compare(YaoQingAward o1, YaoQingAward o2) {
					long time1 = Long.parseLong(o1.buytime);
					long time2 = Long.parseLong(o2.buytime);

					if (time1 > time2) {
						return -1;
					}

					if (time1 < time2) {
						return 0;
					}

					return 0;
				}
			});
		}

	}

	private class InviteFriendAdapter extends BaseAdapter {

		private List<InviteFriend> list;

		private List<YaoQingAward> award;

		public InviteFriendAdapter(List<InviteFriend> list, List<YaoQingAward> award) {
			this.list = list;
			this.award = award;
		}

		@Override
		public int getCount() {
			return isBuy ? list.size() : award.size();
		}

		@Override
		public Object getItem(int position) {
			return isBuy ? list.get(position) : award.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.activity_invite_item, null);
				TextView tvNumber = (TextView) convertView
						.findViewById(R.id.activity_invite_item_number);
				TextView tvTime = (TextView) convertView
						.findViewById(R.id.activity_invite_item_time);
				LinearLayout tvBack = (LinearLayout) convertView.findViewById(R.id.invite_item_layout);

				holder = new ViewHolder();
				holder.tvNumber = tvNumber;
				holder.tvTime = tvTime;
				holder.tvbackground = tvBack;

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (isBuy) {
				InviteFriend friends = (InviteFriend) getItem(position);
				if (friends != null) {
					String time = TimeUtils.getTimeFromSeconds(friends.itime,
							TimeUtils.DEFAULT_DATE_FORMAT);
					if ((position + 1) % 2 != 0) {
						holder.tvbackground.setBackgroundResource(R.color.white);
					}
					holder.tvNumber.setText(friends.account);
					holder.tvNumber.setTextColor(Color.parseColor("#666666"));
					holder.tvTime.setText(time);
					holder.tvTime.setTextColor(Color.parseColor("#666666"));
				}
			} else {
				YaoQingAward friends = (YaoQingAward) getItem(position);
				if (friends != null) {
					String time = TimeUtils.getTimeFromSeconds(friends.buytime,
							TimeUtils.DEFAULT_DATE_FORMAT);
					if ((position + 1) % 2 != 0) {
						holder.tvbackground.setBackgroundResource(R.color.white);
					}
					holder.tvNumber.setText(friends.account);
					holder.tvNumber.setTextColor(Color.parseColor("#666666"));
					holder.tvTime.setText(time);
					holder.tvTime.setTextColor(Color.parseColor("#666666"));
				}
			}


			return convertView;
		}
	}

	private class ViewHolder {
		TextView tvNumber;
		TextView tvTime;
		LinearLayout tvbackground;
	}
}
