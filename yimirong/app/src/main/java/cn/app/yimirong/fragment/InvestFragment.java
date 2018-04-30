package cn.app.yimirong.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.listener.OnBannerListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.app.yimirong.R;
import cn.app.yimirong.activity.MainActivity;
import cn.app.yimirong.activity.ProductDetailActivity;
import cn.app.yimirong.activity.WebViewActivity;
import cn.app.yimirong.banner.GlideImageLoader;
import cn.app.yimirong.base.BaseFragment;
import cn.app.yimirong.event.custom.BaseEvent;
import cn.app.yimirong.model.base.BaseModel;
import cn.app.yimirong.model.bean.Banner;
import cn.app.yimirong.model.bean.BaseProduct;
import cn.app.yimirong.model.bean.DQProduct;
import cn.app.yimirong.model.bean.ProductMore;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.utils.StringUtils;
import cn.app.yimirong.utils.SystemUtils;
import cn.app.yimirong.utils.ToastUtils;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class InvestFragment extends BaseFragment implements
		OnRefreshListener,OnBannerListener {

	private static Map<String, Integer> tags = new HashMap<>();

	static {
		tags.put("xinshoubiao_hong", R.drawable.yellow);
		tags.put("yugao_lan", R.drawable.lable_blue);
		tags.put("tuijian_huang", R.drawable.yellow);
		tags.put("shouwan", R.drawable.grayyuanjiao);
	}
	static {
		//设置全局的Header构建器
		SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
			@Override
			public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
				layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
				return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
			}
		});
	}
	private List<Banner> banners;
	private List<BaseProduct> list;
	private ProductAdapter adapter;
	private List<BaseProduct> selloutlist;
	private List<BaseProduct> completelist;
	private List<BaseProduct> productslist;
	private List<BaseProduct> AllProductlist;
	private View rootView;
	private SmartRefreshLayout refresher;
	private StickyListHeadersListView mListview;
	private com.youth.banner.Banner bannerView;

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDetach() {

		clearList();
		super.onDetach();
	}

	private void init(View rootView){
		refresher = (SmartRefreshLayout) rootView.findViewById(R.id.fragment_invest_refresher);
		mListview = (StickyListHeadersListView) rootView.findViewById(R.id.fragment_invest_listview);
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		selloutlist = new ArrayList<BaseProduct>();
		completelist = new ArrayList<BaseProduct>();
		productslist = new ArrayList<BaseProduct>();
		AllProductlist = new ArrayList<BaseProduct>();
	}


	@Override
	public View loadView(LayoutInflater inflater, ViewGroup container) {
		if (rootView == null){
			rootView = inflater.inflate(R.layout.fragment_invest, container, false);
			init(rootView);
		}
		ViewGroup par = (ViewGroup)rootView.getParent();
		if (par != null){
			par.removeView(rootView);
		}
		return rootView;
	}

	@Override
	public void initView() {
//		int[] colors = getResources().getIntArray(R.array.google_colors);
//		refresher.setColorSchemeColors(colors);
		refresher.setOnRefreshListener(this);
		float height = MainActivity.screenWidth * 0.42f;
//		if (mBannerView == null) {
//			mBannerView = new BannerView(context);

//			AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//					(int) height);
//			mBannerView.setLayoutParams(params);
//			mBannerView.setBannerListener(this);
		    bannerView = (com.youth.banner.Banner) View.inflate(context, R.layout.banner_layout,null);
		    AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					(int) height);
			bannerView.setDelayTime(5000);
			bannerView.setLayoutParams(params);
			mListview.addHeaderView(bannerView);
			mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					if (i > 0) {
						BaseProduct product = list.get(i - 1);
						toProductDetail(product);
					}
				}
			});
//		}
		mListview.setOnScrollListener(new AbsListView.OnScrollListener() {
			private boolean scrollFlag = false;// 标记是否滑动
			private int lastVisibleItemPosition;// 标记上次滑动位置

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					scrollFlag = true;
				} else {
					scrollFlag = false;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				MainActivity activity = (MainActivity) getActivity();
				if (scrollFlag) {
					if (firstVisibleItem > lastVisibleItemPosition) {
						activity.HideRadioButton(true);
					}
					if (firstVisibleItem < lastVisibleItemPosition) {
						activity.HideRadioButton(false);
					}
					if (firstVisibleItem == lastVisibleItemPosition) {
						return;
					}
					lastVisibleItemPosition = firstVisibleItem;
				}

			}
		});
//        initData();
	}

	@Override
	public void initData() {
		if (adapter == null) {
			list = new ArrayList<>();
			adapter = new ProductAdapter(list);
			mListview.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
		loadBanners();
		loadHQProducts(null);
	}

	@Override
	public void onVisible() {
		super.onVisible();
		initData();
	}

	@Override
	public void onInVisible() {
		super.onInVisible();
	}

	/**
	 * 去产品详情
	 *
	 * @param product
	 */
	private void toProductDetail(BaseProduct product) {
		if (product != null) {
			boolean canBuy = true;
			boolean isSellOut = product.state != BaseProduct.STATE_SELLING;
			if (isSellOut) {
				canBuy = false;
			}
			Intent intent = new Intent(activity, ProductDetailActivity.class);
			Bundle data = new Bundle();
			data.putSerializable("product", product);
			data.putInt("productType", product.ptype);
			data.putInt("productState", product.state);
			data.putString("title", product.pname);
			data.putString("pid", product.pid);
			data.putInt("canbuyuser", Integer.parseInt(product.canbuyuser));
			data.putBoolean("canbuy", canBuy);
			data.putBoolean("issellout", isSellOut);
			data.putBoolean("isyugao", product.isYuGao());
			intent.putExtras(data);
			startActivity(intent);
		}
	}

	/**
	 * 加载Banner数据
	 */
	private void loadBanners() {
		if (pmodel != null) {
			pmodel.getBannerList(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (t != null && t instanceof List) {
						List<Banner> list = (List<Banner>) t;
						if (banners == null) {
							banners = new ArrayList<Banner>();
						}
						banners.clear();
						banners.addAll(list);
						mCache.put("banner", (Serializable) banners);
						refreshBanner(banners);
					}
				}
			});
		}
	}

	/**
	 * 刷新Banner
	 *
	 * @param list
	 */
	private void refreshBanner(List<Banner> list) {
		List<String> images = new ArrayList<>();
		for (Banner banner : list) {
			images.add(banner.img);
		}
//		mBannerView.refreshBanner(images);
		if (images.size() > 0) {
			bannerView.setImages(images)
					.setImageLoader(new GlideImageLoader())
					.setOnBannerListener(this)
					.start();
		}

	}
/*
	@Override
	public void onClick(int i) {
		if (banners != null && banners.size() > i) {
			Intent intent = new Intent(context, WebViewActivity.class);
			String url = banners.get(i).uri;
			intent.putExtra("url", url);
			intent.putExtra("title", banners.get(i).title);
			startActivity(intent);
		}
	}*/

	/**
	 * 加载快活宝列表
	 */

	private void loadHQProducts(final RefreshLayout refreshLayout) {
		if (pmodel != null) {
			pmodel.getKHProducts(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (refreshLayout!=null) {
						refreshLayout.finishRefresh(true);
					}
					clearList();
					if (t != null && t instanceof ProductMore) {
						ProductMore pm = (ProductMore) t;
							if (pm.product != null) {
								productslist.addAll(pm.product);
							}

							if (pm.complete != null) {
								completelist.addAll(pm.complete);
							}
							if (pm.sellout != null) {
								selloutlist.addAll(pm.sellout);
							}

					}
					loadProducts();
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					if (refreshLayout!=null) {
						refreshLayout.finishRefresh(false);
					}
					ToastUtils.show(context, BaseModel.NETWORK_ERROR, Toast.LENGTH_SHORT);
				}
			});
		}
	}


	/**
	 * 获取产品列表
	 */
	private void loadProducts() {
		if (pmodel != null) {
			pmodel.getDQProducts(new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
/*					if (refresher!=null) {
						refresher.setRefreshing(false);
					}*/
					if (t != null && t instanceof ProductMore) {
						ProductMore pm = (ProductMore) t;

						if (pm.product != null) {
							productslist.addAll(pm.product);
						}

						if (pm.complete != null) {
							completelist.addAll(pm.complete);
						}
						if (pm.sellout != null) {
							selloutlist.addAll(pm.sellout);
						}

						ReListViewSetData();

					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					ReListViewSetData();
					ToastUtils.show(getContext(), msg);
/*					if (refresher != null) {
						refresher.setRefreshing(false);
					}*/
				}
			});
		}
	}


	public void ReListViewSetData() {
		if (productslist.size()>0) {
			AllProductlist.addAll(productslist);
		}
		if (completelist.size()>0) {
			AllProductlist.addAll(completelist);
		}
		if (selloutlist.size()>0) {
			AllProductlist.addAll(selloutlist);
		}
		if (list.size() > 0) {
			list.clear();
		}
		if (AllProductlist.size()>0) {
			list.addAll(AllProductlist);
		}

		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		AllProductlist.clear();
		clearList();

	}


	public void clearList() {

		productslist.clear();
		completelist.clear();
		selloutlist.clear();

	}

/*	@Override
	public void onRefresh() {
		loadBanners();
		loadHQProducts();
	}*/

	@Override
	public void onResume() {
		initData();
		super.onResume();
	}

	@Override
	public void OnBannerClick(int position) {
		if (banners != null && banners.size() > position&&!banners.get(position).uri.isEmpty()) {
			Intent intent = new Intent(context, WebViewActivity.class);
			String url = banners.get(position).uri;
			intent.putExtra("url", url);
			intent.putExtra("title", banners.get(position).title);
			startActivity(intent);
		}
	}

	@Override
	public void onRefresh(RefreshLayout refreshlayout) {
		loadBanners();
		loadHQProducts(refreshlayout);
	}

	private class ProductAdapter extends BaseAdapter implements StickyListHeadersAdapter {


		public ProductAdapter(List<BaseProduct> list) {
			this.list = list;
			if (this.list == null) {
				this.list = new ArrayList<>();
			}
		}

		private List<BaseProduct> list;


		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getHeaderView(int position, View convertView, ViewGroup parent) {
			HeaderViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(parent.getContext(), R.layout.fragment_product_header, null);
				holder = new HeaderViewHolder();
				holder.tvHeader = (TextView) convertView.findViewById(R.id.fragment_product_header);
				convertView.setTag(holder);
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}
			long headerId = getHeaderId(position);
			if (headerId == BaseProduct.STATE_SELLING) {
				holder.tvHeader.setText("正在发售");
			} else if (headerId == BaseProduct.STATE_SELLOUT) {
				holder.tvHeader.setText("募集完成");
			} else {
				holder.tvHeader.setText("还款完成");
			}
			return convertView;
		}

		@Override
		public long getHeaderId(int position) {
			return list.get(position).state;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int i) {
			return list.get(i);
		}


		@Override
		public View getView(int i, View convertView, ViewGroup viewGroup) {
			ItemViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(viewGroup.getContext(), R.layout.fragment_product_item, null);
				holder = new ItemViewHolder();
				holder.rlIcon = (RelativeLayout) convertView.findViewById(R.id.fragment_product_icon);
				holder.itemTag = (View) convertView.findViewById(R.id.item_tag);
				holder.surplus_money = (TextView) convertView.findViewById(R.id.surplus_money);
				holder.benxi = (TextView) convertView.findViewById(R.id.benxi_safe);
				holder.symbol = (TextView) convertView.findViewById(R.id.fragment_product_symbol);
				holder.tvTag = (TextView) convertView.findViewById(R.id.fragment_product_tag);
				holder.tvName = (TextView) convertView.findViewById(R.id.fragment_product_name);
				holder.tvDays = (TextView) convertView.findViewById(R.id.fragment_product_days);
				holder.tvTian = (TextView) convertView.findViewById(R.id.fragment_product_tian);
				holder.tvProfit = (TextView) convertView.findViewById(R.id.fragment_product_yqnh);
				holder.tvQiGou = (TextView) convertView.findViewById(R.id.fragment_product_qigou);
				holder.progress = (ProgressBar) convertView.findViewById(R.id.fragment_product_progress);
				holder.chTimer = (Chronometer) convertView.findViewById(R.id.fragment_product_timer);
				convertView.setTag(holder);
			} else {
				holder = (ItemViewHolder) convertView.getTag();
			}
			BaseProduct product = (BaseProduct) getItem(i);
			setupView(holder, product);
			if (holder.tvTag.getText().toString().isEmpty()) {
				holder.rlIcon.setVisibility(View.INVISIBLE);
			} else {
				holder.rlIcon.setVisibility(View.VISIBLE);
			}
			return convertView;
		}


		private void setupView(ItemViewHolder holder, final BaseProduct product) {
			boolean isDQ = product instanceof DQProduct;

			//设置标签文字和颜色
			if (product.state == BaseProduct.STATE_SELLING) {
				if (product.isYuGao()) {
					//预告标
					holder.rlIcon.setVisibility(View.VISIBLE);
					holder.rlIcon.setBackgroundResource(R.drawable.lable_blue);
					holder.tvTag.setText("预告");

					holder.tvProfit.setTextColor(getResources().getColor(R.color.control_text_checked));
					holder.symbol.setTextColor(getResources().getColor(R.color.control_text_checked));
					holder.itemTag.setBackgroundResource(R.color.fragment_item_tag_yello);
					holder.tvQiGou.setBackgroundResource(R.drawable.shape_product_qigou);
					holder.benxi.setBackgroundResource(R.drawable.shape_product_qigou);
					holder.progress.setProgress(0);
				} else {
					//不是预告
					if (StringUtils.isBlank(product.standard_icon)) {
						//没有指定标签ICON
						holder.rlIcon.setVisibility(View.INVISIBLE);
					} else {
						//指定了标签ICON
						holder.rlIcon.setVisibility(View.VISIBLE);
						holder.tvTag.setText(product.operation_tag);
						if (tags.containsKey(product.standard_icon)) {
							int resid = tags.get(product.standard_icon);
							holder.rlIcon.setBackgroundResource(resid);
						}
					}
				}
			} else {
				holder.rlIcon.setVisibility(View.VISIBLE);
				holder.rlIcon.setBackgroundResource(R.drawable.grayyuanjiao);
				if (product.state == BaseProduct.STATE_SELLOUT) {
					holder.tvTag.setText("已售完");
				} else {
					if (isDQ) {
						if ("2".equals(product.repayment_status)) {
							holder.tvTag.setText("已还款");
						} else {
							holder.tvTag.setText("还款中");
						}
					} else {
						holder.tvTag.setText("已售完");
					}
				}
			}

			if (isDQ) {
				//定期
				DQProduct dp = (DQProduct) product;
				int days = SystemUtils.getDays(dp.uistime, dp.uietime);
				holder.tvDays.setText(days + "");
				holder.tvTian.setVisibility(View.VISIBLE);
			} else {
				//活期
				holder.tvDays.setText("随存随取");
				holder.tvTian.setVisibility(View.INVISIBLE);
			}

			//设置产品名称
			holder.tvName.setText(product.pname);

			//设置年化利率
			String profit = SystemUtils.getDoubleStr(product.income);
			holder.tvProfit.setText(profit);

			//设置起购金额
			holder.tvQiGou.setText(product.startmoney + "元起投");

			// 设置计时器或进度条
			if (product.state == BaseProduct.STATE_SELLING) {
				//计算进度
				Double money = Double.parseDouble(product.money);
				Double sellmoney = Double.parseDouble(product.sellmoney);
				Double progress = (sellmoney / money) * 360;
				int selling = (int) (money - sellmoney);
				int p = (int) Math.ceil((progress * 100));
				int progtext = (int) Math.ceil((p / 360));
				if (progtext >= 100) {
					progtext = 99;
				} else if ((float) p / 360 < 1 && (float) p / 360 > 0) {
					progtext = 1;
				}
				if (product.isYuGao()) {
					holder.chTimer.setVisibility(View.VISIBLE);
					holder.chTimer.setBackgroundResource(R.drawable.orange_btn);
					holder.chTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
					holder.chTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
						@Override
						public void onChronometerTick(Chronometer chronometer) {
							if (product.getLeftSeconds() <= 0) {
								notifyDataSetChanged();
							} else {
								chronometer.setText(getFormatedTime(product.getLeftSeconds()));
							}
						}
					});
					String str = "<font color='#999999'>剩余可投:</font><font color='#eb515e'>" + selling + "</font><font color='#999999'>元</font>";
					holder.surplus_money.setText(Html.fromHtml(str));
					holder.chTimer.start();
				} else {
					holder.chTimer.stop();
					holder.chTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
					holder.chTimer.setText("投资");
					holder.chTimer.setBackgroundResource(R.drawable.invest_btn_bg);
					holder.progress.setProgress(progtext);
					holder.itemTag.setBackgroundResource(R.color.fragment_item_tag_yello);
//					holder.tvDays.setTextColor(getResources().getColor(R.color.fragment_item_tag_black));
					holder.tvName.setTextColor(Color.parseColor("#303030"));
					holder.tvProfit.setTextColor(getResources().getColor(R.color.control_text_checked));
//					holder.tvTian.setTextColor(getResources().getColor(R.color.fragment_item_tag_black));
					holder.symbol.setTextColor(getResources().getColor(R.color.control_text_checked));
					holder.tvQiGou.setBackgroundResource(R.drawable.shape_product_qigou);
					holder.benxi.setBackgroundResource(R.drawable.shape_product_qigou);
					String str = "<font color='#999999'>剩余可投:</font><font color='#eb515e'>" + selling + "</font><font color='#999999'>元</font>";
					holder.surplus_money.setText(Html.fromHtml(str));
				}
			} else if (product.state == BaseProduct.STATE_SELLOUT) {
				holder.chTimer.stop();
				holder.chTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				holder.chTimer.setText("售完");
				holder.chTimer.setBackgroundResource(R.drawable.chronmeter_sell_out);
				holder.progress.setProgress(0);
				holder.itemTag.setBackgroundResource(R.color.look_details_color);
//				holder.tvDays.setTextColor(getResources().getColor(R.color.look_details_color));
				holder.tvName.setTextColor(getResources().getColor(R.color.look_details_color));
				holder.tvProfit.setTextColor(getResources().getColor(R.color.look_details_color));
//				holder.tvTian.setTextColor(getResources().getColor(R.color.look_details_color));
				holder.symbol.setTextColor(getResources().getColor(R.color.look_details_color));
				holder.tvQiGou.setBackgroundResource(R.drawable.shape_product_sellout);
				holder.benxi.setBackgroundResource(R.drawable.shape_product_sellout);
				String str = "<font color='#999999'>剩余可投:</font><font color='#999999'>0</font><font color='#999999'>元</font>";
				holder.surplus_money.setText(Html.fromHtml(str));
			} else {
				if (isDQ) {
					holder.chTimer.stop();
					holder.chTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
					if ("2".equals(product.repayment_status)) {
						holder.chTimer.setText("已还款");
					} else {
						holder.chTimer.setText("还款中");
						holder.tvProfit.setTextColor(getResources().getColor(R.color.control_text_checked));
						holder.symbol.setTextColor(getResources().getColor(R.color.control_text_checked));
						holder.itemTag.setBackgroundResource(R.color.fragment_item_tag_yello);
						holder.tvQiGou.setBackgroundResource(R.drawable.shape_product_qigou);
						holder.benxi.setBackgroundResource(R.drawable.shape_product_qigou);
					}
					holder.chTimer.setBackgroundResource(R.drawable.invest_btn_bg);
					String str = "<font color='#999999'>剩余可投:</font><font color='#eb515e'>0</font><font color='#999999'>元</font>";
					holder.surplus_money.setText(Html.fromHtml(str));
				} else {
					holder.chTimer.stop();
					holder.chTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
					holder.chTimer.setText("售完");
					holder.itemTag.setBackgroundResource(R.color.look_details_color);
//					holder.tvDays.setTextColor(getResources().getColor(R.color.look_details_color));
					holder.tvName.setTextColor(getResources().getColor(R.color.look_details_color));
					holder.tvProfit.setTextColor(getResources().getColor(R.color.look_details_color));
//					holder.tvTian.setTextColor(getResources().getColor(R.color.look_details_color));
					holder.symbol.setTextColor(getResources().getColor(R.color.look_details_color));
					holder.tvQiGou.setBackgroundResource(R.drawable.shape_product_sellout);
					holder.benxi.setBackgroundResource(R.drawable.shape_product_sellout);
					String str = "<font color='#999999'>剩余可投:</font><font color='#999999'>0</font><font color='#999999'>元</font>";
					holder.surplus_money.setText(Html.fromHtml(str));
				}
				holder.progress.setProgress(0);
			}
		}

		private String getFormatedTime(long seconds) {
			int hh = (int) (seconds / 3600);
			int mm = (int) ((seconds % 3600) / 60);
			int ss = (int) ((seconds % 3600) % 60);
			return (hh < 10 ? ("0" + hh) : hh)
					+ ":" + (mm < 10 ? ("0" + mm) : mm)
					+ ":" + (ss < 10 ? ("0" + ss) : ss) + "\n后开抢";
		}

	}

	private class HeaderViewHolder {
		TextView tvHeader;
	}

	private class ItemViewHolder {
		RelativeLayout rlIcon;

		View itemTag;

		TextView surplus_money;

		TextView benxi;

		TextView tvTag;

		TextView symbol;

		TextView tvName;

		TextView tvProfit;

		TextView tvDays;

		TextView tvTian;

		TextView tvQiGou;

		ProgressBar progress;

		Chronometer chTimer;
	}

	public void onEventMainThread(BaseEvent event) {

	}

}
