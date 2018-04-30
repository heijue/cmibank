package cn.app.yimirong.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.app.yimirong.R;
import cn.app.yimirong.adapter.MenuAdapter;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.HQProfit;
import cn.app.yimirong.model.bean.MenuItem;
import cn.app.yimirong.model.bean.UserHQMoney;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.TimeUtils;
import cn.app.yimirong.utils.ToastUtils;
import cn.app.yimirong.view.pull.PullToRefreshLayout;
import cn.app.yimirong.view.pull.PullToRefreshLayout.OnRefreshListener;
import cn.app.yimirong.view.pull.PullableExpandableListView;

public class HQProfitActivity extends BaseActivity implements
		OnRefreshListener {

	public static final String[] TITLES = {"累计投资收益", "万份投资收益",
			"近一周投资收益", "近一月投资收益"};

	private TextView tvTitle;

	private TextView tvHead;

	private PullToRefreshLayout refresher;
	private PullableExpandableListView listView;

	private int currentSelected = 0;

	private RelativeLayout topMenu;

	private List<MenuItem> menus;

	private KHBProfitAdapter adapter;
	private List<HQProfit> list;

	private int type = -1;
	private int page = 1;
	private int days = 0;

	private boolean isRefresh = true;

	private UserHQMoney mHQMoney;
	private TextView titleName;


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
		setContentView(R.layout.activity_hq_profit);
	}

	@Override
	public void initView() {
		listView = (PullableExpandableListView)findViewById(R.id.activity_hq_profit_listview);
		titleName = (TextView) findViewById(R.id.title_bar_title_text);
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
		View header = View.inflate(context,
				R.layout.layout_profit_list_header, null);
		tvTitle = (TextView) header
				.findViewById(R.id.layout_profit_list_title);
		tvHead = (TextView) header
				.findViewById(R.id.layout_profit_list_sum);
		refresher = (PullToRefreshLayout) findViewById(R.id.activity_hq_profit_refresher);
		refresher.setOnRefreshListener(this);

		listView.addHeaderView(header);
		listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
				if (currentSelected==1) {
					return true;
				}
				return false;
			}
		});

	}
	@Override
	public void initData() {
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		if (data != null) {
			currentSelected = data.getInt("currentSelected", 0);
			mHQMoney = (UserHQMoney) data.getSerializable("khbmoney");
		}
		if (adapter == null) {
			list = new ArrayList<>();
			adapter = new KHBProfitAdapter(list);
			listView.setAdapter(adapter);
			loadUserKHProfits();
			adapter.notifyDataSetChanged();
		}
		createMenu();
		menuSelected(currentSelected);
	}


	/**
	 * 加载快活宝收益数据
	 */
	private void loadUserKHProfits() {
		if (amodel != null) {
			amodel.getUserKHProfitList(type, page, days, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					refreshDone(true);
					if (t != null) {
						List<HQProfit> l = (List<HQProfit>) t;
						if (isRefresh) {
							list.clear();
						} else {
							if (l.isEmpty()) {
								ToastUtils.show(context, "没有更多数据了");
							}
						}
						list.addAll(l);
						sortList(list);
						adapter.notifyDataSetChanged();
						page++;
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					refreshDone(false);
					list.clear();

					adapter.notifyDataSetChanged();
					ToastUtils.show(context, msg);
				}
			});
		}
	}

	/**
	 * 七日年化
	 */
	private void loadHQSevenProfit() {
		if (amodel != null) {
			amodel.getHQSevenProfit(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					closeLoading();
					refreshDone(true);
					if (t != null) {
						List<HQProfit> l = (List<HQProfit>) t;
						if (isRefresh) {
							list.clear();
						} else {
							if (l.isEmpty()) {
								ToastUtils.show(context, "没有更多数据了");
							}
						}
						list.addAll(l);
						sortList(list);
						adapter.notifyDataSetChanged();
						page++;
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					closeLoading();
					refreshDone(false);
					list.clear();
					adapter.notifyDataSetChanged();
					ToastUtils.show(context, msg);
				}
			});
		}
	}

	private void refreshDone(boolean isSucceed) {
		int result = isSucceed ? PullToRefreshLayout.SUCCEED : PullToRefreshLayout.FAIL;
		if (isRefresh) {
			refresher.refreshFinish(result);
		} else {
			refresher.loadmoreFinish(result);
		}
	}

	/**
	 * 排序
	 */
	private void sortList(List<HQProfit> list) {
		Collections.sort(list, new Comparator<HQProfit>() {
			@Override
			public int compare(HQProfit lhs, HQProfit rhs) {
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
		View view =getLayoutInflater().inflate(R.layout.title_bar,null);
        //measure的参数为0即可
		view.measure(0,0);
        //获取组件的高度
		int height=view.getMeasuredHeight();
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.setMargins(0,statusBarHeight+height,0,0);
		topMenu.setLayoutParams(layoutParams);

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
	 * @param item
	 */
	private void menuSelected(int item) {
		setTitleText(TITLES[item]);
		showLoading("玩命向钱冲");
		updateHead(item);
		switch (item) {
			case 0:
				type = -1;
				days = 0;
				break;
			case 1:
				type = -1;
				days = 0;
				break;
			case 2:
				type = -1;
				days = 7;
				break;
			case 3:
				type = -1;
				days = 30;
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

	private void updateHead(int item) {
		tvTitle.setText(TITLES[item] + "(元)");
		if (mHQMoney != null) {
			String str = "0.00";
			switch (currentSelected) {
				case 0:
					str = mHQMoney.count_profit;
					break;

				case 1:
					str = mHQMoney.wan;
					break;

				case 2:
					str = mHQMoney.day_7;
					break;

				case 3:
					str = mHQMoney.day_30;
					break;

				default:
					break;
			}
			tvHead.setText(String.format("%.2f",Double.valueOf(str)));
		}
	}

	/**
	 * 刷新
	 */
	@Override
	public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
		isRefresh = true;
		page = 1;
		if (currentSelected == 1) {
			loadHQSevenProfit();
		} else {
			loadUserKHProfits();
		}
	}

	/**
	 * 加载更多
	 */
	@Override
	public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
		isRefresh = false;
		if (currentSelected == 1 || currentSelected == 0) {
			loadUserKHProfits();
		} else {
			refreshDone(true);
		}
	}

	private class KHBProfitAdapter extends BaseExpandableListAdapter {
		private final List<HQProfit> data;

		public KHBProfitAdapter(List<HQProfit> data){
			this.data = data;
		}
		@Override
		public int getGroupCount() {

			return data.size();
		}

		@Override
		public int getChildrenCount(int chidrenCount) {
			return data.get(chidrenCount).profitInfos.size();
		}

		@Override
		public Object getGroup(int i) {
			return data.get(i);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return data.get(groupPosition).profitInfos.get(0);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int chidrenPosition) {
			return chidrenPosition;
		}



		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		@Override
		public View getGroupView(int position, boolean isExpanda, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_hq_profit_item, null);
				holder = new ViewHolder();
				holder.tvDate = (TextView) convertView.findViewById(R.id.layout_hq_profit_item_date);
				holder.tvProfit = (TextView) convertView.findViewById(R.id.layout_hq_profit_item_profit);
				holder.im_arrows = (ImageView) convertView.findViewById(R.id.iv_arrows);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			HQProfit khb = data.get(position);
			holder.tvDate.setText("+" + SystemUtils.getDoubleStr(khb.profit));
			holder.tvProfit.setText(khb.date);
			if (isExpanda) {
				holder.im_arrows.setImageResource(R.drawable.xiangxiasanjiaoxing);
			} else {
				holder.im_arrows.setImageResource(R.drawable.sanjiaoxing);
			}
			if (currentSelected == 1) {
				holder.im_arrows.setVisibility(View.GONE);
				holder.tvProfit.setPadding(0,0,0,0);
			} else {
				holder.im_arrows.setVisibility(View.VISIBLE);
				holder.tvProfit.setPadding(40,0,0,0);
			}
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int chidrenPosition, boolean b, View convertView, ViewGroup parent) {
			ChidrenViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(context,R.layout.profit_info,null);
				holder = new ChidrenViewHolder();
				holder.pname = (TextView) convertView.findViewById(R.id.tv_name);
				holder.income = (TextView) convertView.findViewById(R.id.tv_shouyi);
				holder.money = (TextView) convertView.findViewById(R.id.tv_goumaijine);
				holder.balance = (TextView) convertView.findViewById(R.id.tv_yuqinianhua);
			    convertView.setTag(holder);
			} else {
				holder = (ChidrenViewHolder) convertView.getTag();
			}
			HQProfit khb = data.get(groupPosition);
			if (khb!=null) {
				DecimalFormat df=new DecimalFormat("######0.00");
				holder.pname.setText(khb.profitInfos.get(chidrenPosition).pname);
				holder.money.setText(khb.profitInfos.get(chidrenPosition).money);
				Double aDouble = Double.valueOf(khb.profitInfos.get(chidrenPosition).income);
				String format = df.format(aDouble);
				holder.balance.setText(format+"%");
				holder.income.setText("+"+SystemUtils.getDoubleStr(khb.profit));
			}

			return convertView;
		}

		@Override
		public boolean isChildSelectable(int i, int i1) {
			return true;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isEmpty() {
			ToastUtils.show(context,"俺啥时候执行啊");
			return super.isEmpty();
		}
	}

	private static class ViewHolder {
		private TextView tvDate;
		private TextView tvProfit;
		private ImageView im_arrows;
	}
	private static class ChidrenViewHolder{
		private TextView pname;
		private TextView income;
		private TextView money;
		private TextView balance;
	}
}
