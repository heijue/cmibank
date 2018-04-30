package cn.app.yimirong.model.http;

import cn.app.yimirong.log.Logger;

public abstract class ResponseHandler implements IResponseHandler {

	private static final String TAG = "ResponseHandler";

	protected Logger logger;

	public ResponseHandler() {
		logger = Logger.getInstance();
	}

	@Override
	public void onStart() {
	}

	@Override
	public <T> void onSuccess(String response, T t) {
//		logger.i(TAG, "onSuccess:" + response);
	}

	@Override
	public void onFailure(String errorCode, String msg) {
//		logger.i(TAG, "onFailure: errorCode: " + errorCode + " " + msg);
	}

	@Override
	public void onStop() {
	}

}
