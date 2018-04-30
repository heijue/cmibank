package cn.app.yimirong.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
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
import cn.app.yimirong.view.pull.PullToRefreshLayout.OnRefreshListener;

public class DQListActivity extends BaseActivity implements OnRefreshListener {

	private static final String TYPE_PRODUCT = "product";

	private static final String TYPE = TYPE_PRODUCT;

	private PullToRefreshLayout refresher;

	private ExpandableListView expandListView;

	private List<ActionLogDate> list;

	private DQDetailAdapter adapter;

	private int currentPage = 1;

	private boolean isRefreshing = true;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_dingqi_detail);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("产品投资明细");
		refresher = (PullToRefreshLayout) findViewById(R.id.activity_dingqi_detail_refresher);
		assert refresher != null;
		refresher.setOnRefreshListener(this);
		expandListView = (ExpandableListView) findViewById(R.id.activity_dingqi_detail_list);
		assert expandListView != null;
		expandListView.setGroupIndicator(null);
		expandListView.setOnChildClickListener(new OnChildClickListener() {

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

	@Override
	public void initData() {
		refresher.autoRefresh(false);
	}

	/**
	 * 加载用户操作日志
	 */
	private void loadUserActionLog() {
		if (amodel != null) {
			showLoading("玩命向钱冲");
			amodel.getUserActionLog(TYPE, currentPage, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					if (t != null && t instanceof List) {
						if (list == null) {
							list = new ArrayList<>();
						}
						List<ActionLog> logs = (List<ActionLog>) t;

						// 如果是刷新，先清空集合
						if (isRefreshing) {
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
							expandListView.setAdapter(adapter);
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
				}
			});
		}
	}

	private void refreshDone(int succeed) {
		if (isRefreshing) {
			refresher.refreshFinish(succeed);
		} else {
			refresher.loadmoreFinish(succeed);
		}
	}

	/**
	 * 下拉刷新
	 */
	@Override
	public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
		currentPage = 1;
		isRefreshing = true;
		loadUserActionLog();
	}

	/**
	 * 上拉加载
	 */
	@Override
	public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
		isRefreshing = false;
		loadUserActionLog();
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
	 * 去操作详情
	 */
	private void toActionDetail(ActionLog log) {
		if (log != null) {
			Class<?> clas = null;
			switch (log.action) {
				case 1:
				case 11:// 购买
					clas = DQBuyDetailActivity.class;
					break;

				case 4:
				case 14:// 回款
					clas = DQPayBackDetailActivity.class;
					break;

				default:
					break;
			}

			if (clas != null) {
				Intent intent = new Intent(context, clas);
				Bundle bundle = new Bundle();
				bundle.putSerializable("actionlog", log);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
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
			ChildViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.activity_dq_detail_item, null);
				holder = new ChildViewHolder();
				holder.tvAction = (TextView) convertView.findViewById(R.id.activity_action_list_item_action);
				holder.tvMoney = (TextView) convertView.findViewById(R.id.activity_action_list_item_money);
				holder.tvTime = (TextView) convertView.findViewById(R.id.activity_action_list_item_time);
                holder.tvName2 = (ImageView) convertView.findViewById(R.id.mx_icon);
				convertView.setTag(holder);

			} else {
				holder = (ChildViewHolder) convertView.getTag();
			}
			ActionLog log = (ActionLog) getChild(groupPosition, childPosition);
			if (log != null) {
				String action;
				int id1;
				String symbol;
				int color;
				int id2;
				if (log.action == 1) {
					action = "购买成功";
					symbol = "+";
					color = Color.parseColor("#ff4747");
					holder.tvName2.setBackgroundResource(R.drawable.gm);
				} else {
					action = "还款成功";
					symbol = "-";
					color = Color.parseColor("#25ac01");
					holder.tvName2.setBackgroundResource(R.drawable.hai);
				}
				holder.tvAction.setText(action);
				holder.tvMoney.setTextColor(color);
				String str = symbol + SystemUtils.getDoubleStr(log.money);
				holder.tvMoney.setText(str);
				holder.tvTime.setText(TimeUtils.getTimeFromSeconds(log.ctime,TimeUtils.DATE_FORMAT_MONTH_DAY));
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
			holder.tvTitle.setText(title);
			if (!isExpanded) {
				expandListView.expandGroup(groupPosition);
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

	private class ChildViewHolder {
		TextView tvAction;
		TextView tvMoney;
		TextView tvTime;
        ImageView tvName2;
	}
}
