package cn.app.yimirong.presenter.impl;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.app.yimirong.common.Constant;
import cn.app.yimirong.event.EventBus;
import cn.app.yimirong.event.custom.ReadMsgEvent;
import cn.app.yimirong.model.CModel;
import cn.app.yimirong.model.bean.Message;
import cn.app.yimirong.model.db.dao.MsgDao;
import cn.app.yimirong.model.http.ResponseHandler;
import cn.app.yimirong.presenter.AbstractPresenter;
import cn.app.yimirong.presenter.IViewController;
import cn.app.yimirong.utils.ToastUtils;

public class MsgPresenter extends AbstractPresenter {

	private List<Message> messages;
	private CModel cmodel;
	private MsgDao dao;

	public MsgPresenter() {
		messages = new ArrayList<>();
	}

	@Override
	public void bind(@NonNull IViewController view) {
		super.bind(view);
		initModel();
		initDao();
	}

	@Override
	public void unBind() {
		super.unBind();
		messages = null;
		dao = null;
		cmodel = null;
	}

	private void initModel() {
		cmodel = CModel.getInstance(appContext);
	}

	private void initDao() {
		dao = MsgDao.getInstance(appContext);
//		loadFromDB();
	}

	/**
	 * 从数据库加载
	 */
	private void loadFromDB() {
		List<Message> list = dao.queryAll();
		messages.clear();
		messages.addAll(list);
		if (view != null) {
			view.updateView(null);
		}
	}

	/**
	 * 从网络加载
	 */
	public void loadMessages() {
		if (cmodel != null) {
			int currentPage = 1;
			cmodel.getMessages(currentPage, new ResponseHandler() {
				@Override
				public <T> void onSuccess(String response, T t) {
					super.onSuccess(response, t);
					if (isBinded) {
//						view.closeLoading();
						if (t != null && t instanceof List) {
							List<Message> list = (ArrayList<Message>) t;
							if (messages.isEmpty()) {
								messages.addAll(list);
							} else {
								boolean isExist;
								for (int i = 0; i < list.size(); i++) {
									Message msg1 = list.get(i);
									isExist = false;
									Iterator<Message> iterator = messages.iterator();
									while (iterator.hasNext()) {
										Message msg2 = iterator.next();
										if (msg1.nid.equals(msg2.nid)) {
											isExist = true;
											break;
										}
									}
									if (!isExist) {
										messages.add(msg1);
									}
								}
							}
							dao.insert(messages);
							if (view != null) {
								view.updateView(null);
							}
						}
					}
				}

				@Override
				public void onFailure(String errorCode, String msg) {
					super.onFailure(errorCode, msg);
					if (!isBinded) {
//						view.closeLoading();
						ToastUtils.show(activity, msg);
					}
				}
			});
		}
	}

	public List<Message> getMessages() {
		return messages;
	}

	public boolean checkNew() {
		List<Message> list = dao.queryAll();
		messages.clear();
		messages.addAll(list);
		boolean hasNew = false;
		for (Message msg : messages) {
			if (!msg.isread) {
				hasNew = true;
				break;
			}
		}
		return hasNew;
	}

	public void setReaded(int position) {
		Message msg = messages.get(position);
		msg.isread = true;
		dao.update(msg);
		EventBus.getDefault().post(new ReadMsgEvent());
	}
}
