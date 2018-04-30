package cn.app.yimirong.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import cn.app.yimirong.R;
import cn.app.yimirong.adapter.MenuAdapter;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.ActionLog;
import cn.app.yimirong.model.bean.ActionLogDate;
import cn.app.yimirong.model.bean.MenuItem;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;
import cn.app.yimirong.view.pull.PullToRefreshLayout;
import cn.app.yimirong.view.pull.PullToRefreshLayout.OnRefreshListener;

/**
 * 收支明细
 *
 * @author android
 */
public class ActionListActivity extends BaseActivity implements
		OnRefreshListener {

	private static final String[] TITLES = {"交易明细", "支出明细", "收入明细"};

	private static final String TYPE_ALL = "all";
	private static final String TYPE_IN = "in";
	private static final String TYPE_OUT = "out";

	private String type = TYPE_ALL;
	private int currentPage = 1;

	private PullToRefreshLayout refresher;

	private RelativeLayout topMenu;

	private List<MenuItem> menus;
	private int currentSelected = 0;

	private ExpandableListView listView;

	private List<ActionLogDate> list;

	private DQDetailAdapter adapter;

	private List<ActionLog> actionLogs;

	private boolean isRefresh = true;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (topMenu.getVisibility() == View.VISIBLE) {
				closeMenu();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void loadView() {
		setContentView(R.layout.activity_action_list);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText(TITLES[0]);
		setTitleArrow(true, new OnTitleArrowChangeListener() {
			@Override
			public void onArrowUp() {
				showMenu();
			}

			@Override
			public void onArrowDown() {
				closeMenu();
			}
		});

		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeMenu();
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						exit();
					}
				}, 100);
			}
		});

		refresher = (PullToRefreshLayout) findViewById(R.id.activity_action_list_pullable);
		refresher.setOnRefreshListener(this);

		listView = (ExpandableListView) findViewById(R.id.activity_action_list_listview);
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

	@Override
	public void initData() {
		createMenu();
		menuSelected(currentSelected);
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

	private void refreshDone(boolean isSuccess) {
		int result = isSuccess ? PullToRefreshLayout.SUCCEED : PullToRefreshLayout.FAIL;
		if (isRefresh) {
			refresher.refreshFinish(result);
		} else {
			refresher.loadmoreFinish(result);
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

	public String formatTime(String time) {
		long format;
		String result;
		format = TimeUtils.getTimeInSecondsFromString(time, TimeUtils.DATE_FORMAT_DATE);
		result = TimeUtils.getTimeFromSeconds(format, TimeUtils.DATE_FORMAT_CHINA_MONTH);
		return result;
	}

	private void refreshDone(int succeed) {
		if (isRefresh) {
			refresher.refreshFinish(succeed);
		} else {
			refresher.loadmoreFinish(succeed);
		}
	}

	/**
	 * 创建弹出的顶部菜单
	 */
	private void createMenu() {
		topMenu = (RelativeLayout) findViewById(R.id.layout_menu_wrapper);
		ListView menuGrid = (ListView) topMenu.findViewById(R.id.layout_menu_grid);
		menus = new ArrayList<>();
		for (int i = 0; i < TITLES.length; i++) {
			MenuItem menu = new MenuItem(TITLES[i], i == currentSelected ? false : true);
			menus.add(i, menu);
		}
		final MenuAdapter adapter = new MenuAdapter(menus);
		menuGrid.setAdapter(adapter);
		menuGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				menus.get(currentSelected).enabled = true;
				menus.get(i).enabled = false;
				currentSelected = i;
				adapter.notifyDataSetChanged();
				menuSelected(i);
			}
		});
	}

	/**
	 * 显示顶部菜单
	 */
	private void showMenu() {
		topMenu.setVisibility(View.VISIBLE);
		if (isArrowDown) {
			isArrowDown = false;
		}
	}

	/**
	 * 关闭顶部菜单
	 */
	private void closeMenu() {
		topMenu.setVisibility(View.INVISIBLE);
		if (!isArrowDown) {
			isArrowDown = true;
		}
	}


	/**
	 * 选中
	 *
	 * @param i
	 */
	private void menuSelected(int i) {
		setTitleText(TITLES[i]);
		showLoading("玩命向钱冲");
		switch (i) {
			case 0:
				type = TYPE_ALL;
				break;

			case 1:
				type = TYPE_OUT;
				break;

			case 2:
				type = TYPE_IN;
				break;
			default:
				break;
		}
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				closeMenu();
			}
		}, 100);
		refresher.autoRefresh(false);
	}

	/**
	 * 去操作详情
	 */
	private void toActionDetail(ActionLog log) {
		if (log != null) {
			Class<?> clas = null;
			switch (log.action) {
				case 0:
				case 10:// 充值失败
					clas = PayDetailActivity.class;
					break;

				case 2:// 提现
				case 20://提现失败
					clas = CashDetailActivity.class;
					break;

				case 1:
				case 11:
				case 31:// 购买
					clas = BuyDetailActivity.class;
					break;

				case 5: // 活动赠送
				case 6:
					clas = HDDetailActivity.class;
					break;

				case 4:
				case 14:// 回款详情
					clas = PaybackDetailActivity.class;
					break;

				case 13:// 活期转余额
					/*if (log.desc.equals("1")) {
						clas = HQToYueDetailActivity.class;
					} else {
						clas = CashDetailActivity.class;
					}*/
					clas = HQToYueDetailActivity.class;
					break;

				case 21://提现退回
					break;

				default:
					break;
			}

			if (clas != null) {
				Intent intent = new Intent(context, clas);
				Bundle bundle = new Bundle();
				bundle.putSerializable("actionlog", log);
				if (log.action==13) {
					intent.putExtra("chuli", 1);
				}else {
					intent.putExtra("chuli", 2);
				}
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
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

	private class ViewHolder {
		TextView tvAction;
		TextView tvYue;
		TextView tvMoney;
		TextView tvTime;
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
					color = "#ff4747";
				} else if (type == 1) {
					symbol = "-";
					color = "#0d81f2";
				} else {
					symbol = "";
					color = "#9d9d9d";
				}
				holder.tvTime.setText("余额:"
						+ SystemUtils.getDoubleStr(log.balance));
				holder.tvAction.setText(log.getActionStr());
				holder.tvMoney.setTextColor(Color.parseColor(color));
				holder.tvMoney.setText(symbol
						+ SystemUtils.getDoubleStr(log.money));
				String today = TimeUtils.getServerDate(null);
				String buyTime = TimeUtils.getTimeFromSeconds2(log.ctime);
				if (today.equals(buyTime)) {
					holder.tvYue.setText("今天" + TimeUtils.getTimeFromSeconds3(log.ctime));
				} else {
					holder.tvYue.setText(buyTime);
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
			String tempTime = "";
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.activity_dingqi_detail_title, null);
				TextView tvTitle = (TextView) convertView
						.findViewById(R.id.activity_dingqi_detail_title);
				holder = new GroupViewHolder();
				holder.jiexian = (TextView) convertView.findViewById(R.id.tv_jiexian);
				holder.tvTitle = tvTitle;
				convertView.setTag(holder);
			} else {
				holder = (GroupViewHolder) convertView.getTag();
			}
			String title = (String) getGroup(groupPosition);
			holder.tvTitle.setText(title);
			if (groupPosition != 0 && !tempTime.equals(title)) {
				holder.jiexian.setVisibility(View.VISIBLE);
				tempTime = title;
			} else {
				holder.jiexian.setVisibility(View.GONE);
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
		TextView jiexian;
	}

}
