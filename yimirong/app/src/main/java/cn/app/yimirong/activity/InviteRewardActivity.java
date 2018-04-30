package cn.app.yimirong.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.app.yimirong.App;
import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.InviteFriend;
import cn.app.yimirong.model.bean.YaoQingAward;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;

public class InviteRewardActivity extends BaseActivity {

	private ListView lv;

	private TextView allMoney, reType, timeType;

	private double money;

	private List<YaoQingAward> award;

	private BaseAdapter adapter;


	@Override
	public void loadView() {
		setContentView(R.layout.activity_invite_reward);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("邀请好友奖励");

		timeType = (TextView) findViewById(R.id.what_reward);
		allMoney = (TextView) findViewById(R.id.layout_profit_list_sum);
		reType = (TextView) findViewById(R.id.layout_profit_list_title);
		lv = (ListView) findViewById(R.id.reward_invite_listview);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		money = intent.getDoubleExtra("money", 0.00);
		timeType.setText("首次交易时间");
		allMoney.setText(money + "");

		getMyInvivteBuy();

		award = new ArrayList<>();
		adapter = new InviteFriendAdapter();
		lv.setAdapter(adapter);


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
							award.addAll(awards);
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


	protected void sort(List<YaoQingAward> awards) {
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
	}

	private class InviteFriendAdapter extends BaseAdapter {


		@Override
		public int getCount() {
			return award.size();
		}

		@Override
		public Object getItem(int position) {
			return award.get(position);
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
						R.layout.reward_invite_item, null);
				TextView tvNumber = (TextView) convertView
						.findViewById(R.id.is_account);
				TextView tvTime = (TextView) convertView
						.findViewById(R.id.is_time);
				TextView tvReward = (TextView) convertView
						.findViewById(R.id.is_reward);

				holder = new ViewHolder();
				holder.tvNumber = tvNumber;
				holder.tvTime = tvTime;
				holder.tvReward = tvReward;

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			YaoQingAward friends = (YaoQingAward) getItem(position);
			if (friends != null) {
				String time = TimeUtils.getTimeFromSeconds(friends.buytime,
						TimeUtils.DEFAULT_DATE_FORMAT);
				holder.tvNumber.setText(friends.account);
				holder.tvTime.setText(time);
				holder.tvReward.setText(friends.rewardmoney + "元");
			}

			return convertView;
		}
	}

	private class ViewHolder {
		TextView tvNumber;
		TextView tvTime;
		TextView tvReward;
	}


}
