package cn.app.yimirong.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.app.yimirong.R;
import cn.app.yimirong.model.bean.City;
import cn.app.yimirong.model.bean.Province;
import cn.app.yimirong.utils.ScreenUtils;

public class CityView {

	private Context context;

	private View parent;

	private PopupWindow background;

	private PopupWindow dialogProvince;

	private PopupWindow dialogCity;

	private ProvinceAdapter provinceAdapter;

	private CityAdapter cityAdapter;

	private boolean isBackgroundShow = false;

	protected boolean isProvinceShow = false;

	protected boolean isCityShow = false;

	private OnCityChooseListener listener;

	public CityView(Context context, View parent) {
		this.context = context;
		this.parent = parent;
	}

	/**
	 * 创建popupwindow
	 *
	 * @param provinces 省份列表
	 */
	public void create(List<Province> provinces) {
		background = createBackground();
		dialogProvince = createProvince(provinces);
		dialogCity = createCity();
	}

	/**
	 * 显示省份选择列表
	 */
	public void show() {
		showBackground();
		showProvince();
	}

	public boolean isShow() {
		return (isBackgroundShow || isProvinceShow || isCityShow);
	}

	/**
	 * 关闭所有窗口
	 */
	public void close() {
		closeCity();
		closeProvince();
		closeBackground();
	}

	/**
	 * 设置监听器
	 *
	 * @param listener
	 */
	public void setOnCityChooseListener(OnCityChooseListener listener) {
		this.listener = listener;
	}

	/**
	 * 显示背景
	 */
	private void showBackground() {
		if (!isBackgroundShow) {
			background.showAtLocation(parent, Gravity.CENTER | Gravity.CENTER,
					0, 0);
			isBackgroundShow = true;
		}
	}

	/**
	 * 关闭背景
	 */
	private void closeBackground() {
		if (isBackgroundShow) {
			background.dismiss();
			isBackgroundShow = false;
		}
	}

	/**
	 * 显示省份列表
	 */
	private void showProvince() {
		if (!isProvinceShow) {
			dialogProvince.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			isProvinceShow = true;
		}
	}

	/**
	 * 关闭省份列表
	 */
	private void closeProvince() {
		if (isProvinceShow) {
			dialogProvince.dismiss();
			isProvinceShow = false;
		}
	}

	/**
	 * 显示城市列表
	 */
	private void showCity() {
		if (!isCityShow) {
			dialogCity.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			isCityShow = true;
		}
	}

	/**
	 * 关闭省份列表
	 */
	private void closeCity() {
		if (isCityShow) {
			dialogCity.dismiss();
			isCityShow = false;
		}
	}

	/**
	 * 创建背景dialog
	 */
	public PopupWindow createBackground() {
		View view = View.inflate(context, R.layout.full_screen_trans, null);
		PopupWindow popup = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		popup.setOutsideTouchable(false);
		return popup;
	}

	/**
	 * 创建省份列表dialog
	 */
	public PopupWindow createProvince(final List<Province> provinces) {

		View view = View.inflate(context, R.layout.dialog_city, null);

		TextView tvBack = (TextView) view.findViewById(R.id.dialog_city_back);
		tvBack.setVisibility(View.INVISIBLE);

		TextView tvCancel = (TextView) view
				.findViewById(R.id.dialog_city_cancel);

		GridView gvProvices = (GridView) view
				.findViewById(R.id.dialog_city_list);

		provinceAdapter = new ProvinceAdapter(provinces);

		gvProvices.setAdapter(provinceAdapter);

		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeProvince();
				closeBackground();
			}
		});

		int y = (int) ScreenUtils.dpToPx(context, 324);
		PopupWindow popup = new PopupWindow(view, LayoutParams.MATCH_PARENT, y);
		popup.setAnimationStyle(R.style.KeyboardDialogAnimation);
		popup.setOutsideTouchable(false);

		return popup;
	}

	/**
	 * 创建城市列表dialog
	 */
	public PopupWindow createCity() {
		View view = View.inflate(context, R.layout.dialog_city, null);

		TextView tvBack = (TextView) view.findViewById(R.id.dialog_city_back);
		tvBack.setVisibility(View.VISIBLE);

		TextView tvCancel = (TextView) view
				.findViewById(R.id.dialog_city_cancel);

		GridView gvCities = (GridView) view.findViewById(R.id.dialog_city_list);

		cityAdapter = new CityAdapter();
		gvCities.setAdapter(cityAdapter);

		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeCity();
			}
		});

		tvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeCity();
			}
		});

		int y = (int) ScreenUtils.dpToPx(context, 324);
		PopupWindow popup = new PopupWindow(view, LayoutParams.MATCH_PARENT, y);
		popup.setAnimationStyle(R.style.PayDialogAnimation);
		popup.setOutsideTouchable(false);

		return popup;
	}

	private class ProvinceAdapter extends BaseAdapter {

		private List<Province> list;

		public ProvinceAdapter(List<Province> list) {
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
		public View getView(final int position, View convertView,
							ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = View.inflate(context, R.layout.dialog_city_item,
						null);

				TextView tv = (TextView) convertView
						.findViewById(R.id.dialog_city_item_text);

				holder = new ViewHolder();

				holder.tvCity = tv;

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final Province p = list.get(position);
			holder.tvCity.setText(p.name);

			// 添加点击事件
			holder.tvCity.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isProvinceShow && !isCityShow) {
						// 如果当前只显示刻省份列表,弹出对应省份的城市列表
						if (listener != null) {
							listener.onProvinceChoose(p.code, p.name);
							cityAdapter.refresh(p.list);
							showCity();
						}
						return;
					}
				}
			});

			return convertView;
		}
	}

	private class CityAdapter extends BaseAdapter {

		private List<City> list;

		public CityAdapter() {
			// Map转List
			list = new ArrayList<>();
		}

		/**
		 * 刷新
		 *
		 * @param map
		 */
		public void refresh(Map<String, String> map) {
			list = new ArrayList<>();
			Set<Entry<String, String>> entrySet = map.entrySet();
			for (Entry<String, String> entry : entrySet) {
				String key = entry.getKey();
				String value = entry.getValue();
				City city = new City();
				city.code = key;
				city.name = value;
				list.add(city);
			}
			notifyDataSetChanged();
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
		public View getView(final int position, View convertView,
							ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = View.inflate(context, R.layout.dialog_city_item,
						null);

				TextView tv = (TextView) convertView
						.findViewById(R.id.dialog_city_item_text);

				holder = new ViewHolder();

				holder.tvCity = tv;

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final City city = list.get(position);
			holder.tvCity.setText(city.name);

			// 添加点击事件
			holder.tvCity.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isCityShow) {
						// 如果显示了城市列表，关闭所有
						if (listener != null) {
							listener.onCityChoose(city.code, city.name);
						}
						close();
						return;
					}
				}
			});
			return convertView;
		}
	}

	/**
	 * ViewHolder
	 *
	 * @author android
	 */
	private class ViewHolder {
		TextView tvCity;
	}

	public interface OnCityChooseListener {
		public void onCityChoose(String cityCode, String city);

		public void onProvinceChoose(String provinceCode, String province);
	}
}
