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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import cn.app.yimirong.model.bean.ProfitDate;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;
import cn.app.yimirong.view.pull.PullToRefreshLayout;
import cn.app.yimirong.view.pull.PullToRefreshLayout.OnRefreshListener;

public class HQLogsActivity extends BaseActivity implements
		OnRefreshListener {

	private static final String TYPE_HQB_BUY = "longproduct";

	private static final String TYPE_HQB_OUT = "longtobalance";

	private static final String TYPE_HQB_ALL = "longall";

	private static final String[] TITLES = {"购买", "转出", "收益"};

	private PullToRefreshLayout refresher;

	protected ExpandableListView listView;

	protected DQDetailAdapter adapter;

	private List<ActionLogDate> list;
	private List<ProfitDate> list2;
	private RelativeLayout topMenu;

	private List<MenuItem> menus;

	private int currentPage = 1;
	private int currentSelected = 0;

	private boolean isRefresh = true;

	private String actionType = TYPE_HQB_BUY;
	private RelativeLayout noDataBg;
	private String longmoney;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_hqb_logs);
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

		refresher = (PullToRefreshLayout) findViewById(R.id.activity_khb_logs_refresher);
		assert refresher != null;
		refresher.setOnRefreshListener(this);

		listView = (ExpandableListView) findViewById(R.id.activity_khb_list_lv);
		assert listView != null;
		listView.setGroupIndicator(null);
		listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
										int groupPosition, int childPosition, long id) {
				if (currentSelected == 0) {
					Object obj = adapter.getChild(
							groupPosition, childPosition);
					if (obj != null && obj instanceof ActionLog) {
						toHQBuyDetail((ActionLog) obj);
					}
				}
				if (currentSelected == 1) {
					Object obj = adapter.getChild(
							groupPosition, childPosition);
					if (obj != null && obj instanceof ActionLog) {
						toHQZChuDetail((ActionLog) obj);
					}
				}

				return true;
			}
		});
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			currentSelected = intent.getIntExtra("currentSelected", 0);
		}
		list = new ArrayList<>();
		list2 = new ArrayList<>();
		noDataBg = (RelativeLayout) findViewById(R.id.no_data);
		longmoney = intent.getStringExtra("longmoney");
		createMenu();
		menuSelected(currentSelected);
	}

	/**
	 * 获取用户操作记录
	 */
	private void loadActionLog() {
		if (amodel != null) {
			amodel.getUserActionLog(actionType, currentPage, new ResponseHandler() {
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
							list2.clear();
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
							setNoDataBg();
						} else {
							adapter.notifyDataSetChanged();
							setNoDataBg();
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
					setNoDataBg();
				}
			});
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

	protected void addToList1(List<ProfitDate> logs) {
		// 遍历列表添加到集合
		for (int i = 0; i < logs.size(); i++) {
			ProfitDate log = logs.get(i);
			String date = log.date;
			double profit = log.profit;
			String sort = formatTime(date);

			boolean isExists = false;
			for (int j = 0; j < list2.size(); j++) {
				if (sort.equals(formatTime(list2.get(j).date))) {
					list2.get(j).profitDates.add(log);
					isExists = true;
					break;
				}
			}

			if (!isExists) {
				ProfitDate logDate = new ProfitDate();
				List<ProfitDate> actionLogs = new ArrayList<ProfitDate>();
				actionLogs.add(log);
				logDate.date = date;
				logDate.profit = profit;
				logDate.profitDates = actionLogs;
				list2.add(logDate);
			}
		}
	}

	private void refreshDone(int succeed) {
		if (isRefresh) {
			refresher.refreshFinish(succeed);
		} else {
			refresher.loadmoreFinish(succeed);
		}
	}

	public String formatTime(String time) {
		long format;
		String result;
		format = TimeUtils.getTimeInSecondsFromString(time, TimeUtils.DATE_FORMAT_DATE);
		result = TimeUtils.getTimeFromSeconds(format, TimeUtils.DATE_FORMAT_CHINA_MONTH);
		return result;
	}

	/**
	 * 去活期购买详情
	 */
	private void toHQBuyDetail(ActionLog log) {
		if (log != null) {
			Intent intent = new Intent(context, HQBuyDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("log", log);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	/**
	 * 去活期转出详情
	 *
	 * @param log
	 */
	private void toHQZChuDetail(ActionLog log) {
		if (log != null) {
			Intent intent = null;
		/*	if (log.desc.equals("1")) {
				intent = new Intent(context, HQToYueDetailActivity.class);
			} else {
				intent = new Intent(context, CashDetailActivity.class);
				intent.putExtra("chuli",1);
			}*/
			intent = new Intent(context, HQToYueDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("actionlog", log);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	/**
	 * 加载快活宝累计收益
	 */
	private void loadHQProfit() {
		if (amodel != null) {
			int profitType = ProfitListActivity.PROTIT_TYPE_HQ;
			amodel.getUserProfitList(profitType, -1, currentPage, 0, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					refreshDone(true);
                    if (t != null && t instanceof List) {
                        List<ProfitDate> data = (List<ProfitDate>) t;
                        if (isRefresh) {
                            list.clear();
                            list2.clear();
                        } else {
                            if (data.isEmpty()) {
                                ToastUtils.show(context, "没有更多数据了");
                            }
                        }
                        sortList2();
                        addToList1(data);
                        if (adapter == null) {
                            adapter = new DQDetailAdapter();
                            listView.setAdapter(adapter);
							setNoDataBg();
                        } else {
                            adapter.notifyDataSetChanged();
							setNoDataBg();
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

	/**
	 * 按时间排序
	 */
	private void sortList2() {
		Collections.sort(list2, new Comparator<ProfitDate>() {
			@Override
			public int compare(ProfitDate lhs, ProfitDate rhs) {
				try {
					Date ldate = TimeUtils.DATE_FORMAT_DATE.parse(lhs.date);
					Date rdate = TimeUtils.DATE_FORMAT_DATE.parse(rhs.date);

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
			public void onItemClick(AdapterView<?> adapterView, View view, int i, final long l) {
				menus.get(currentSelected).enabled = true;
				menus.get(i).enabled = false;
				currentSelected = i;
				adapter.notifyDataSetChanged();
				menuSelected(i);
				setNoDataBg();
			}
		});

	}
	private void setNoDataBg(){
		if (listView.getCount() <= 0) {
			noDataBg.setVisibility(View.VISIBLE);
		} else {
			noDataBg.setVisibility(View.GONE);
		}
	}
	/**
	 * 显示顶部菜单
	 */
	private void showMenu() {
		topMenu.setVisibility(View.VISIBLE);
	}

	/**
	 * 关闭顶部菜单
	 */
	private void closeMenu() {
		topMenu.setVisibility(View.INVISIBLE);
	}

	/**
	 * 选中
	 *
	 * @param i
	 */
	private void menuSelected(int i) {
		setTitleText(TITLES[i]);
		showLoading("玩命向钱冲");
		adapter = null;
		currentPage = 1;
		switch (i) {

			case 0:
				actionType = TYPE_HQB_BUY;
				break;

			case 1:
				actionType = TYPE_HQB_OUT;
				break;

			case 2:
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

	private class ViewHolder {
		public TextView tvName;

		public TextView tvTime;

		public TextView tvMoney;

		public TextView ivArrow;

		public ImageView rightImg;

		public ImageView imgGoumai;
	}


	@Override
	public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
		isRefresh = true;
		currentPage = 1;
		if (currentSelected == 0) {
			loadActionLog();
		} else if (currentSelected == 1) {
			loadActionLog();
		} else if (currentSelected == 2) {
			loadHQProfit();
		}
	}


	@Override
	public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
		isRefresh = false;
		if (currentSelected == 0) {
			loadActionLog();
		} else if (currentSelected == 1) {
			loadActionLog();
		} else if (currentSelected == 2) {
			loadHQProfit();
		}
	}

	private void refreshDone(boolean isSucceed) {
		if (isRefresh) {
			refresher.refreshFinish(isSucceed ? PullToRefreshLayout.SUCCEED : PullToRefreshLayout.FAIL);
		} else {
			refresher.loadmoreFinish(isSucceed ? PullToRefreshLayout.SUCCEED : PullToRefreshLayout.FAIL);
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
            if (list != null && list.size() > 0 || list2 != null && list2.size() > 0) {
				if (currentSelected == 1 || currentSelected == 0) {
					return list.get(groupPosition).actionLogs.get(childPosition);
				} else {
					return list2.get(groupPosition).profitDates.get(childPosition);
				}
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
						R.layout.activity_hqb_logs_item, null);
				TextView tvName = (TextView) convertView
						.findViewById(R.id.activity_khb_logs_item_name);
				TextView tvTime = (TextView) convertView
						.findViewById(R.id.activity_khb_logs_item_time);
				TextView tvMoney = (TextView) convertView
						.findViewById(R.id.activity_khb_logs_item_money);
				TextView ivArrow = (TextView) convertView
						.findViewById(R.id.activity_khb_logs_item_arrow);
				ImageView rightImg = (ImageView) convertView
						.findViewById(R.id.tv_right_img);
				ImageView imgGoumai = (ImageView) convertView
						.findViewById(R.id.img_goumai);
				holder = new ViewHolder();
				holder.imgGoumai = imgGoumai;
				holder.tvName = tvName;
				holder.tvTime = tvTime;
				holder.tvMoney = tvMoney;
				holder.ivArrow = ivArrow;
				holder.rightImg = rightImg;
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (currentSelected == 1 || currentSelected == 0) {
				// 购买
				ActionLog log = (ActionLog) getChild(groupPosition, childPosition);
				if (log != null) {
					String symbol = "";
					String color = null;
					String purchase ="";
					int type = log.getActionType();
					if (type == 1) {
						symbol = "+";
						color = "#ff4747";
						purchase = "购买 ";
					} else {
						symbol = "-";
						color = "#0d81f2";
						purchase = "";
					}
					if (currentSelected == 1) {
						holder.tvName.setText("活期转出到余额");
					} else {
						holder.tvName.setText(log.getActionStr());
					}

					holder.tvMoney.setTextColor(Color.parseColor(color));
					holder.tvMoney.setText(symbol
							+ SystemUtils.getDoubleStr(log.money));
					String timeFromSeconds = TimeUtils.getTimeFromSeconds(
							log.ctime, TimeUtils.DATE_FORMAT_DAY_MINUTE);
					holder.tvTime.setText(purchase+timeFromSeconds);
					if (currentSelected == 2 ||currentSelected ==1) {
						holder.ivArrow.setVisibility(View.VISIBLE);
						holder.ivArrow.setText("资产："+longmoney);
						holder.imgGoumai.setVisibility(View.GONE);
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						lp.setMargins(0,3,0,0);
						holder.tvTime.setLayoutParams(lp);
					} else {
						holder.ivArrow.setVisibility(View.VISIBLE);
						holder.ivArrow.setText("资产："+longmoney);
					}

				}
			} else {
				// 收益
				ProfitDate profit = (ProfitDate) getChild(groupPosition, childPosition);

				holder.tvName.setText("收益");
				holder.tvMoney.setTextColor(Color.parseColor("#ff4747"));
				holder.tvMoney.setText("+" + profit.profit);
				holder.tvTime.setText(profit.date);
				holder.ivArrow.setVisibility(View.GONE);
				holder.rightImg.setVisibility(View.GONE);
				holder.imgGoumai.setVisibility(View.GONE);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				lp.setMargins(0,3,0,0);
				holder.tvTime.setLayoutParams(lp);
			}
			return convertView;
		}

		/**
		 * 某个标题栏下的Item数量
		 */
		@Override
		public int getChildrenCount(int groupPosition) {

			if (currentSelected == 1 || currentSelected == 0) {
				return list.get(groupPosition).actionLogs.size();
			} else {
				return list2.get(groupPosition).profitDates.size();
			}
		}

		@Override
		public Object getGroup(int groupPosition) {
			if (list != null && list.size() > 0 || list2 != null && list2.size() > 0) {
				if (currentSelected == 1 || currentSelected == 0) {
					return list.get(groupPosition).date;
				} else {
					return list2.get(groupPosition).date;
				}
			}
			return null;
		}

		/**
		 * 标题栏数量
		 */
		@Override
		public int getGroupCount() {
			if (currentSelected == 1 || currentSelected == 0) {
				return list.size();
			} else {
				return list2.size();
			}
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
								 View convertView, ViewGroup parent) {
			String tempDate = "";
			GroupViewHolder holder;
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
			if (currentSelected == 2) {
				title = formatTime(title);
			}
			String now = TimeUtils.getServerDate(TimeUtils.DATE_FORMAT_CHINA_MONTH);
			if (now.equals(title)) {
				holder.tvTitle.setText("本月");
			} else {
				holder.tvTitle.setText(title);
			}
			if (!isExpanded) {
				listView.expandGroup(groupPosition);
			}
			if (groupPosition != 0 && !tempDate.equals(title)) {
				tempDate = title;
				holder.jiexian.setVisibility(View.VISIBLE );
			} else {
				holder.jiexian.setVisibility(View.GONE);
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
