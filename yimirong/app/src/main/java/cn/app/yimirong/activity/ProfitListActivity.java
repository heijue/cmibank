package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.app.yimirong.R;
import cn.app.yimirong.adapter.MenuAdapter;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.MenuItem;
import cn.app.yimirong.model.bean.ProfitDate;
import cn.app.yimirong.model.bean.ProfitDetail;
import cn.app.yimirong.model.bean.UserProfit;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;
import cn.app.yimirong.view.pull.PullToRefreshLayout;
import cn.app.yimirong.view.pull.PullToRefreshLayout.OnRefreshListener;

public class ProfitListActivity extends BaseActivity implements
		OnRefreshListener {

	public static final int PROTIT_TYPE_ALL = 0;
	public static final int PROTIT_TYPE_DQ = 1;
	public static final int PROTIT_TYPE_HQ = 2;

	public static final String[] TITLES = {"累计投资收益", "定期累计收益",
			"活期累计收益", "体验金累计收益", "当前定期收益", "当前体验金收益"};

	private int currentSelected = 0;

	private TextView tvTitle;

	private TextView tvHead;

	private PullToRefreshLayout refresher;

	private ExpandableListView expandListView;

	private RelativeLayout topMenu;

	private List<MenuItem> menus;

	private ProfitListAdapter adapter;

	private int currentPage = 1;

	private List<ProfitDate> profitList;

	private int days = 0;

	private int profitType = PROTIT_TYPE_ALL;

	private boolean isRefresh = true;
	private int type = -1;

	private UserProfit mUserProfit;

	private double countprofit;

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
		setContentView(R.layout.activity_profit_list);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("累计投资收益");
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

		View header = View.inflate(context,
				R.layout.layout_profit_list_header, null);
		tvTitle = (TextView) header.findViewById(R.id.layout_profit_list_title);
		tvHead = (TextView) header.findViewById(R.id.layout_profit_list_sum);

		refresher = (PullToRefreshLayout) findViewById(R.id.activity_profit_refresher);
		assert refresher != null;
		refresher.setOnRefreshListener(this);

		expandListView = (ExpandableListView) findViewById(R.id.activity_profit_listview);
		assert expandListView != null;
		expandListView.addHeaderView(header);
		expandListView.setGroupIndicator(null);
		expandListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
										int groupPosition, long id) {
				if (expandListView.isGroupExpanded(groupPosition)) {
					// 折叠分组
					expandListView.collapseGroup(groupPosition);
				} else if (!expandListView.isGroupExpanded(groupPosition)) {
					// 展开分组
					expandListView.expandGroup(groupPosition);
				} else ;
				return true;
			}
		});
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		currentSelected = intent.getIntExtra("selected", 0);
		countprofit = intent.getDoubleExtra("countprofit", 0d);
		Bundle data = intent.getExtras();
		if (data != null) {
			mUserProfit = (UserProfit) data.getSerializable("userprofit");
		}
		if (adapter == null) {
			profitList = new ArrayList<>();
			adapter = new ProfitListAdapter(profitList);
			expandListView.setAdapter(adapter);
		}
		createMenu();
		menuSelected(currentSelected);
	}

	/**
	 * 加载用户收益列表
	 */
	private void LoadUserProfit() {
		if (amodel != null) {
			amodel.getUserProfitList(profitType, type, currentPage, days, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, final T t) {
					super.onSuccess(response, t);
					closeLoading();
					refreshDone(true);
					handleSuccess(t);
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					refreshDone(false);
					ToastUtils.show(context, msg);
				}
			});
		}
	}

	/**
	 * 获取体验金收益
	 */
	private void loadExpMoneyProfit() {
		if (amodel != null) {
			amodel.getExpMoneyProfit(type, currentPage, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					refreshDone(true);
					handleSuccess(t);
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					refreshDone(false);
					ToastUtils.show(context, msg);
				}
			});
		}
	}

	/**
	 * 处理成功事件
	 *
	 * @param t
	 */
	private <T> void handleSuccess(final T t) {
		if (t != null && t instanceof List) {
			List<ProfitDate> list = (List<ProfitDate>) t;
			if (isRefresh) {
				profitList.clear();
			} else {
				if (list.isEmpty()) {
					ToastUtils.show(context, "没有更多数据了");
				}
			}
			if (profitList.isEmpty()) {
				profitList.addAll(list);
			} else {
				boolean isExist;
				for (int i = 0; i < list.size(); i++) {
					ProfitDate pd1 = list.get(i);
					isExist = false;
					Iterator<ProfitDate> iterator = profitList.iterator();
					while (iterator.hasNext()) {
						ProfitDate pd2 = iterator.next();
						if (pd1.date.equals(pd2.date)) {
							pd2.profitDetail.addAll(pd1.profitDetail);
							pd2.profit += pd1.profit;
							isExist = true;
							break;
						}
					}
					if (isExist == false) {
						profitList.add(pd1);
					}
				}
			}
			sortList();
			adapter.notifyDataSetChanged();
			currentPage++;
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
	 * 排序
	 */
	private void sortList() {
		Collections.sort(profitList, new Comparator<ProfitDate>() {
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
	 * 选中项目
	 *
	 * @param
	 */
	private void menuSelected(int i) {
		setTitleText(TITLES[i]);
		showLoading("玩命向钱冲");
		updateHead(i);
		switch (i) {
			case 0:
				type = -1;
				days = 0;
				profitType = PROTIT_TYPE_ALL;
				break;
			case 1:
				type = 1;
				days = 0;
				profitType = PROTIT_TYPE_DQ;
				break;
			case 2:
				type = -1;
				days = 0;
				profitType = PROTIT_TYPE_HQ;
				break;
			case 3:
				type = 1;
				break;
			case 4:
				type = 0;
				days = 0;
				profitType = PROTIT_TYPE_DQ;
				break;
			case 5:
				type = 0;
				days = 0;
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

	private void updateHead(int i) {
		tvTitle.setText(TITLES[i] + "(元)");
		if (mUserProfit != null) {
			double number = 0d;
			switch (i) {
				case 0: {
					// 总收益
					number = mUserProfit.countProfit
							+ mUserProfit.longmoneyCountprofit
							+ mUserProfit.expmoneyCountprofit;
					break;
				}

				case 1: {
					// 定期累计收益
					number = mUserProfit.countProfit;
					break;
				}

				case 2: {
					// 活期累计收益
					number = mUserProfit.longmoneyCountprofit;
					break;
				}

				case 3: {
					// 体验金累计收益
					number = mUserProfit.expmoneyCountprofit;
					break;
				}

				case 4: {
					// 当前定期收益
					number = countprofit;
					break;
				}

				case 5: {
					// 当前体验金收益
					number = mUserProfit.expmoneyCurrentProfit;
					break;
				}
				default:
					break;
			}
			tvHead.setText(SystemUtils.getDoubleStr(number));
		}
	}

	/**
	 * 排序
	 */
	private void sortList1(List<ProfitDetail> list) {
		Collections.sort(list, new Comparator<ProfitDetail>() {
			@Override
			public int compare(ProfitDetail lhs, ProfitDetail rhs) {
				try {
//                    Date ldate = TimeUtils.DATE_FORMAT_DATE.parse(lhs.date);
//                    Date rdate = TimeUtils.DATE_FORMAT_DATE.parse(rhs.date);

					double lms = lhs.profit;
					double rms = rhs.profit;

					if (lms < rms) {
						return 1;
					} else if (lms > rms) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
	}

	/**
	 * 刷新
	 */
	@Override
	public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
		isRefresh = true;
		currentPage = 1;
		if (currentSelected == 3
				|| currentSelected == 5) {
			loadExpMoneyProfit();
		} else {
			LoadUserProfit();
		}
	}

	/**
	 * 加载更多
	 */
	@Override
	public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
		isRefresh = false;
		if (currentSelected == 3
				|| currentSelected == 5) {
			loadExpMoneyProfit();
		} else {
			LoadUserProfit();
		}
	}

	/**
	 * 可展开列表的适配器
	 *
	 * @author android
	 */
	private class ProfitListAdapter extends BaseExpandableListAdapter {

		private List<ProfitDate> profitList;

		public ProfitListAdapter(List<ProfitDate> profitList) {
			this.profitList = profitList;
			if (this.profitList == null) {
				this.profitList = new ArrayList<>();
			}
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			List<ProfitDetail> list = profitList.get(groupPosition).profitDetail;
			sortList1(list);
			return profitList.get(groupPosition).profitDetail
					.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		/**
		 * 子View
		 */
		@Override
		public View getChildView(int groupPosition, int childPosition,
								 boolean isLastChild, View convertView, ViewGroup parent) {
			ChildViewHolder holder;
			if (convertView == null) {

				convertView = View.inflate(context,
						R.layout.activity_profit_item_child, null);
				TextView tvName = (TextView) convertView
						.findViewById(R.id.activity_profit_item_child_name);
				TextView tvMoney = (TextView) convertView
						.findViewById(R.id.activity_profit_item_child_money);
				TextView tvIncome = (TextView) convertView
						.findViewById(R.id.activity_profit_item_child_income);
				TextView tvProfit = (TextView) convertView
						.findViewById(R.id.activity_profit_item_child_profit);
				holder = new ChildViewHolder();
				holder.tvName = tvName;
				holder.tvMoney = tvMoney;
				holder.tvIncome = tvIncome;
				holder.tvProfit = tvProfit;

				convertView.setTag(holder);
			} else {
				holder = (ChildViewHolder) convertView.getTag();
			}

			ProfitDetail profitDetail = (ProfitDetail) getChild(groupPosition, childPosition);
			if (profitDetail != null) {
				holder.tvName.setText(profitDetail.pname);
				double money = SystemUtils.getDouble(profitDetail.money);
				String str1 = SystemUtils.getDoubleStr(money);
				holder.tvMoney.setText(str1);
				String str2 = profitDetail.income + "%";
				holder.tvIncome.setText(str2);
				String str3 = "+"+SystemUtils.getDoubleStr(profitDetail.profit);
				holder.tvProfit.setText(str3);
			}
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return profitList.get(groupPosition).profitDetail.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return profitList.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return profitList.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		/**
		 * 父View
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
								 View convertView, ViewGroup parent) {
			GroupViewHolder holder;
			if (convertView == null) {

				convertView = View.inflate(context,
						R.layout.activity_profit_item_group, null);
				TextView tvDate = (TextView) convertView
						.findViewById(R.id.activity_profit_item_group_date);
				TextView tvProfit = (TextView) convertView
						.findViewById(R.id.activity_profit_item_group_profit);
				ImageView ivArrow = (ImageView) convertView
						.findViewById(R.id.activity_profit_item_group_indicator);

				holder = new GroupViewHolder();
				holder.tvDate = tvDate;
				holder.tvProfit = tvProfit;
				holder.ivArrow = ivArrow;
				convertView.setTag(holder);
			} else {
				holder = (GroupViewHolder) convertView.getTag();
			}
			ProfitDate profitDate = profitList.get(groupPosition);
			holder.tvDate.setText(profitDate.date);
			String str = "+" + SystemUtils.getDoubleStr(profitDate.profit);
			holder.tvProfit.setText(str);
			if (isExpanded) {
				holder.ivArrow.setImageResource(R.drawable.xiangxiasanjiaoxing);
			} else {
				holder.ivArrow.setImageResource(R.drawable.sanjiaoxing);
			}
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
	}

	private class GroupViewHolder {
		TextView tvDate;
		TextView tvProfit;
		ImageView ivArrow;
	}

	private class ChildViewHolder {
		TextView tvName;
		TextView tvMoney;
		TextView tvIncome;
		TextView tvProfit;
	}

}
