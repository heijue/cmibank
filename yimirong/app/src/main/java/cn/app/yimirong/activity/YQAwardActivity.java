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
import cn.app.yimirong.model.bean.YaoQingAward;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;

public class YQAwardActivity extends BaseActivity {

	private TextView tvYaoQingSum;

	private ListView listView;

	private List<YaoQingAward> list;

	private BaseAdapter adapter;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_yaoqing_award);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("邀请奖励");

		tvYaoQingSum = (TextView) findViewById(R.id.activity_yaoqing_award_sum);
		listView = (ListView) findViewById(R.id.activity_yaoqing_award_list);
	}

	@Override
	public void initData() {
		list = new ArrayList<YaoQingAward>();
		addTitle();
		adapter = new YaoQingAwardAdapter(list);
		listView.setAdapter(adapter);

		Intent intent = getIntent();
		double invite = intent.getDoubleExtra("invite", 0.00f);
		tvYaoQingSum.setText(SystemUtils.getDoubleStr(invite));
		loadMyInViteBuy();
	}

	protected void addTitle() {
		YaoQingAward title = new YaoQingAward();
		title.account = "注册号码";
		title.itime = "首次交易时间";
		title.rewardmoney = "奖励金额";
		list.add(0, title);
	}

	private void loadMyInViteBuy() {
		if (amodel != null) {
			showLoading("玩命向钱冲");
			amodel.getMyInviteBuy(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null && t instanceof List) {
						List<YaoQingAward> awards = (List<YaoQingAward>) t;
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

	private class YaoQingAwardAdapter extends BaseAdapter {

		private List<YaoQingAward> list;

		public YaoQingAwardAdapter(List<YaoQingAward> list) {
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
						R.layout.activity_yaoqing_award_item, null);
				TextView tvNumber = (TextView) convertView
						.findViewById(R.id.activity_yaoqing_award_item_number);
				TextView tvTime = (TextView) convertView
						.findViewById(R.id.activity_yaoqing_award_item_time);
				TextView tvMoney = (TextView) convertView
						.findViewById(R.id.activity_yaoqing_award_item_money);

				holder = new ViewHolder();

				holder.tvNumber = tvNumber;
				holder.tvTime = tvTime;
				holder.tvMoney = tvMoney;

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			YaoQingAward award = (YaoQingAward) getItem(position);
			if (award != null) {
				String time = null;
				String money = null;
				if (position == 0) {
					time = award.itime;
					money = award.rewardmoney;
				} else {
					time = TimeUtils.getTimeFromSeconds(award.itime,
							TimeUtils.DEFAULT_DATE_FORMAT);
					money = award.rewardmoney + "元";
				}
				holder.tvNumber.setText(award.account);
				holder.tvTime.setText(time);
				holder.tvMoney.setText(money);
			}
			return convertView;
		}
	}

	private class ViewHolder {
		TextView tvNumber;
		TextView tvTime;
		TextView tvMoney;
	}

}
