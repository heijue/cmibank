package cn.app.yimirong.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.app.yimirong.R;
import cn.app.yimirong.model.bean.MenuItem;

public class MenuAdapter extends BaseAdapter {

	private List<MenuItem> list;

	public MenuAdapter(List<MenuItem> list) {
		this.list = list;
		if (this.list == null) {
			this.list = new ArrayList<>();
		}
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
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(parent.getContext(), R.layout.layout_menu_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MenuItem menu = list.get(position);
		holder.tvMenu.setText(menu.text);
		holder.tvMenu.setEnabled(menu.enabled);
		if (!menu.enabled) {
			holder.ivGou.setVisibility(View.VISIBLE);
		} else {
			holder.ivGou.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	static class ViewHolder {
		@Bind(R.id.layout_menu_item_text)
		TextView tvMenu;
		@Bind(R.id.iv_gou)
		ImageView ivGou;
		ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}
}
