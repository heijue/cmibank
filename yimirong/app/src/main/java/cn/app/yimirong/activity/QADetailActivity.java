package cn.app.yimirong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.app.yimirong.R;
import cn.app.yimirong.base.BaseActivity;
import cn.app.yimirong.model.bean.Questions;
import cn.app.yimirong.model.bean.Questions.Answer;

public class QADetailActivity extends BaseActivity {

	private ExpandableListView listView;

	private QADetailListAdapter adapter;

	private List<Answer> list;

	@Override
	public void loadView() {
		setContentView(R.layout.activity_qa_detail);
	}

	@Override
	public void initView() {
		setTitleBack(true);
		setTitleText("常见问题");
		listView = (ExpandableListView) findViewById(R.id.activity_qa_detail_list);
		listView.setGroupIndicator(null);
		listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
				return true;
			}
		});
	}

	@Override
	public void initData() {
		list = new ArrayList<Answer>();
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		if (data != null) {
			Questions q = (Questions) data.getSerializable("questions");
			if (q != null) {
				setTitleText(q.title);
				list.addAll(q.data);
				sort();
				adapter = new QADetailListAdapter(list);
				listView.setAdapter(adapter);
			}
		}

		int groupCount = listView.getCount();
		for (int i=0; i<groupCount; i++) {
			listView.expandGroup(i);
		}
	}

	private void sort() {
		Collections.sort(list, new Comparator<Answer>() {
			@Override
			public int compare(Answer o1, Answer o2) {
				int nid1 = Integer.parseInt(o1.nid);
				int nid2 = Integer.parseInt(o2.nid);
				if (nid1 < nid2) {
					return -1;
				} else if (nid1 > nid2) {
					return 1;
				} else {
					return 0;
				}
			}
		});
	}

	private class QADetailListAdapter extends BaseExpandableListAdapter {

		private List<Answer> list;

		public QADetailListAdapter(List<Answer> list) {
			this.list = list;
		}

		@Override
		public int getGroupCount() {
			return list.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return 1;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return list.get(groupPosition).title;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return list.get(groupPosition).content;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
								 View groupView, ViewGroup parent) {
			GroupViewHolder holder = null;
			if (groupView == null) {
				groupView = View.inflate(context,
						R.layout.activity_qa_detail_item_group, null);
				TextView tvTitle = (TextView) groupView
						.findViewById(R.id.activity_qa_detail_item_group_title);
				ImageView ivArrow = (ImageView) groupView
						.findViewById(R.id.activity_qa_detail_item_group_icon);
				holder = new GroupViewHolder();
				holder.tvTitle = tvTitle;
				holder.ivArrow = ivArrow;
				groupView.setTag(holder);
			} else {
				holder = (GroupViewHolder) groupView.getTag();
			}

			String title = (String) getGroup(groupPosition);
			if (title != null) {
				holder.tvTitle.setText(title);
			}

			if (!isExpanded) {
			/*	holder.ivArrow.setPivotX(holder.ivArrow.getWidth()/2);
				holder.ivArrow.setPivotY(holder.ivArrow.getHeight()/2);//支点在图片中心*/
				holder.ivArrow.setRotation(0);
			} else {
/*				holder.ivArrow.setPivotX(holder.ivArrow.getWidth()/2);
				holder.ivArrow.setPivotY(holder.ivArrow.getHeight()/2);//支点在图片中心*/
				holder.ivArrow.setRotation(90);
			}

			return groupView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
								 boolean isLastChild, View childView, ViewGroup parent) {
			ChildViewHolder holder = null;
			if (childView == null) {
				childView = View.inflate(context,
						R.layout.activity_qa_detail_item_child, null);
				TextView tvContent = (TextView) childView
						.findViewById(R.id.activity_qa_detail_item_child_content);
				holder = new ChildViewHolder();
				holder.tvContent = tvContent;
				childView.setTag(holder);
			} else {
				holder = (ChildViewHolder) childView.getTag();
			}

			String content = (String) getChild(groupPosition, childPosition);
			if (content != null) {
				holder.tvContent.setText(content);
			}

			return childView;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
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
		public TextView tvTitle;
		public ImageView ivArrow;
	}

	private class ChildViewHolder {
		public TextView tvContent;
	}

}
