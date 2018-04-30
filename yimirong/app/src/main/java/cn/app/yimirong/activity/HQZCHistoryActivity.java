package cn.app.yimirong.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.ActionLog;
import cn.app.yimirong.model.bean.ActionLogDate;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;
import cn.app.yimirong.view.pull.PullToRefreshLayout;

public class HQZCHistoryActivity extends BaseActivity implements
		PullToRefreshLayout.OnRefreshListener {

	private ExpandableListView listView;

	private String type = "cashout";

	private int currentPage = 1;

	private List<ActionLogDate> list;

	private DQDetailAdapter adapter;

	private PullToRefreshLayout refresher;

	private boolean isRefresh = true;


	@Override
	public void loadView() {
		setContentView(R.layout.activity_hqzchistory);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("提现记录");

		refresher = (PullToRefreshLayout) findViewById(R.id.activity_action_list_pullable);
		refresher.setOnRefreshListener(this);

		listView = (ExpandableListView) this.findViewById(R.id.fragment_hqzhuanchu_listview);
		assert listView != null;
		listView.setGroupIndicator(null);
		listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
										int groupPosition, int childPosition, long id) {
				ActionLog actionlog = (ActionLog) adapter.getChild(
						groupPosition, childPosition);
				toActionDetail(actionlog);
				return true;
			}
		});

	}

	/**
	 * 去操作详情
	 */
	private void toActionDetail(ActionLog log) {
		if (log != null) {
			Intent intent = new Intent(context, CashDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("actionlog", log);
			intent.putExtra("chuli",2);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	@Override
	public void initData() {
		showLoading("玩命向钱冲");
		refresher.autoRefresh(false);
	}

	/**
	 * 加用户操作记录
	 */
	private void loadUserActionLog() {
		if (amodel != null) {
			amodel.getUserActionLog(type, currentPage, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
//                    refreshDone(true);
					if (t != null && t instanceof List) {
						if (list == null) {
							list = new ArrayList<>();
						}
						List<ActionLog> logs = (List<ActionLog>) t;

						// 如果是刷新，先清空集合
						if (isRefresh) {
							list.clear();
						} else {
							if (logs.size() == 0) {
								ToastUtils.show(context, "没有更多数据了");
							}
						}

						// 添加到集合
						addToList(logs);

						// 按时间顺序排序
						sortByTime();

						if (adapter == null) {
							adapter = new DQDetailAdapter();
							listView.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}

						refreshDone(PullToRefreshLayout.SUCCEED);
						currentPage++;
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					refreshDone(PullToRefreshLayout.FAIL);
					ToastUtils.show(context, msg);
				}
			});
		}
	}

	private void refreshDone(int succeed) {
		if (isRefresh) {
			refresher.refreshFinish(succeed);
		} else {
			refresher.loadmoreFinish(succeed);
		}
	}


	/**
	 * 添加到集合中，时间相同的添加到同一集合
	 *
	 * @param logs
	 */
	protected void addToList(List<ActionLog> logs) {
		// 遍历列表添加到集合
		for (int i = 0; i < logs.size(); i++) {
			ActionLog log = logs.get(i);
			String date = log.getTimeStr();

			boolean isExists = false;
			for (int j = 0; j < list.size(); j++) {
				if (date.equals(list.get(j).date)) {
					list.get(j).actionLogs.add(log);
					isExists = true;
					break;
				}
			}

			if (!isExists) {
				ActionLogDate logDate = new ActionLogDate();
				List<ActionLog> actionLogs = new ArrayList<ActionLog>();
				actionLogs.add(log);
				logDate.date = date;
				logDate.actionLogs = actionLogs;
				list.add(logDate);
			}
		}
	}

	/**
	 * 按时间顺序先后排序
	 */
	protected void sortByTime() {
		Collections.sort(list, new Comparator<ActionLogDate>() {
			@Override
			public int compare(ActionLogDate lhs, ActionLogDate rhs) {
				try {
					Date ldate = TimeUtils.DATE_FORMAT_DAY_MINUTE.parse(lhs.date);
					Date rdate = TimeUtils.DATE_FORMAT_DAY_MINUTE.parse(rhs.date);

					long lms = ldate.getTime();
					long rms = rdate.getTime();

					if (lms < rms) {
						return 1;
					} else if (lms > rms) {
						return -1;
					} else {
						return 0;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
	}

	@Override
	public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
		currentPage = 1;
		isRefresh = true;
		loadUserActionLog();
	}

	@Override
	public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
		isRefresh = false;
		loadUserActionLog();
	}

	/**
	 * 数据适配器
	 *
	 * @author android
	 */
	private class DQDetailAdapter extends BaseExpandableListAdapter {

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			if (list != null && list.size() > 0) {
				return list.get(groupPosition).actionLogs.get(childPosition);
			} else {
				return null;
			}
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
								 boolean isLastChild, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.activity_action_list_item, null);
				TextView tvAction = (TextView) convertView
						.findViewById(R.id.activity_asset_list_item_action);
				TextView tvYue = (TextView) convertView
						.findViewById(R.id.activity_asset_list_item_status);
				TextView tvMoney = (TextView) convertView
						.findViewById(R.id.activity_asset_list_item_money);
				TextView tvTime = (TextView) convertView
						.findViewById(R.id.activity_asset_list_item_time);

				holder = new ViewHolder();

				holder.tvAction = tvAction;
				holder.tvYue = tvYue;
				holder.tvMoney = tvMoney;
				holder.tvTime = tvTime;

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ActionLog log = (ActionLog) getChild(groupPosition, childPosition);
			if (log != null) {
				String symbol;
				String color;
				int type = log.getActionType();
				if (type == 0) {
					symbol = "+";
					color = "#1aac19";
				} else if (type == 1) {
					symbol = "-";
					color = "#ff4747";
				} else {
					symbol = "";
					color = "#9d9d9d";
				}
				holder.tvYue.setText(type == 2 ? "交易失败" : "交易成功");
				holder.tvAction.setText(log.getActionStr());
				holder.tvMoney.setTextColor(Color.parseColor(color));
				holder.tvMoney.setText(symbol
						+ SystemUtils.getDoubleStr(log.money));
//                holder.tvYue.setText("余额:"
//                        + SystemUtils.getDoubleStr(log.balance));
				String today = TimeUtils.getServerDate(null);
				String buyTime = TimeUtils.getTimeFromSeconds(log.ctime);
				if (today.equals(buyTime)) {
					holder.tvTime.setText("今天" + TimeUtils.getTimeFromSeconds3(log.ctime));
				} else {
					holder.tvTime.setText(buyTime);
				}

			}
			return convertView;
		}

		/**
		 * 某个标题栏下的Item数量
		 */
		@Override
		public int getChildrenCount(int groupPosition) {
			return list.get(groupPosition).actionLogs.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			if (list != null && list.size() > 0) {
				return list.get(groupPosition).date;
			}
			return null;
		}

		/**
		 * 标题栏数量
		 */
		@Override
		public int getGroupCount() {
			return list.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
								 View convertView, ViewGroup parent) {
			GroupViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.activity_dingqi_detail_title, null);
				TextView tvTitle = (TextView) convertView
						.findViewById(R.id.activity_dingqi_detail_title);
				holder = new GroupViewHolder();
				holder.tvTitle = tvTitle;
				convertView.setTag(holder);
			} else {
				holder = (GroupViewHolder) convertView.getTag();
			}
			String title = (String) getGroup(groupPosition);
			String now = TimeUtils.getServerDate(TimeUtils.DATE_FORMAT_CHINA_MONTH);
			if (now.equals(title)) {
				holder.tvTitle.setText("本月");
			} else {
				holder.tvTitle.setText(title);
			}
			holder.tvTitle.setWidth(parent.getWidth());
			if (!isExpanded) {
				listView.expandGroup(groupPosition);
			}
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

	private class GroupViewHolder {
		TextView tvTitle;
	}

	private class ViewHolder {
		TextView tvAction;
		TextView tvYue;
		TextView tvMoney;
		TextView tvTime;
	}

}
