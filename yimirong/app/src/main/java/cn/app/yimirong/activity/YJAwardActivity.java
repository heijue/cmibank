package cn.app.yimirong.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.YongJinAward;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.ToastUtils;

public class YJAwardActivity extends BaseActivity {

	private TextView tvYongJinSum;

	private ListView listView;

	private List<YongJinAward> list;

	private BaseAdapter adapter;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_yongjin_award);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("好友佣金奖励");

		tvYongJinSum = (TextView) findViewById(R.id.activity_yongjin_award_sum);
		listView = (ListView) findViewById(R.id.activity_yongjin_award_list);
	}

	@Override
	public void initData() {
		list = new ArrayList<>();
		adapter = new YongJinAwardAdapter(list);
		listView.setAdapter(adapter);

		Intent intent = getIntent();
		double transaction = intent.getDoubleExtra("money", 0.00f);
		tvYongJinSum.setText(SystemUtils.getDoubleStr(transaction));

		getUserInviteReward();
	}


	private void getUserInviteReward() {
		showLoading("玩命向钱冲");
		if (amodel != null) {
			amodel.getUserInviteReward(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null && t instanceof List) {
						List<YongJinAward> awards = (List<YongJinAward>) t;
						if (awards.size() > 0) {
							list.addAll(awards);
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

	private class YongJinAwardAdapter extends BaseAdapter {

		private List<YongJinAward> list;

		public YongJinAwardAdapter(List<YongJinAward> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
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
						R.layout.activity_yongjin_award_item, null);

				TextView tvTime = (TextView) convertView
						.findViewById(R.id.activity_yongjin_award_item_time);

				TextView tvMoney = (TextView) convertView
						.findViewById(R.id.activity_yongjin_award_item_money);

				holder = new ViewHolder();

				holder.tvTime = tvTime;

				holder.tvMoney = tvMoney;

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			YongJinAward award = (YongJinAward) getItem(position);
			if (award != null) {
//				String time = null;
//				String money = null;
//				if (position == 0) {
//					time = award.account;
//					money = award.rewardmoney;
//				} else {
//					time = TimeUtils.getTimeFromSeconds(award.account,
//							TimeUtils.DEFAULT_DATE_FORMAT);
//					money = award.rewardmoney + "元";

				holder.tvTime.setText(StringUtils.getSecretAccount(award.account));
				holder.tvMoney.setText(award.rewardmoney + "元");
			}
			return convertView;
		}
	}

	private class ViewHolder {
		TextView tvTime;
		TextView tvMoney;
	}

}
