package cn.app.yimirong.model.http;

public interface IResponseHandler {

	public void onStart();

	public <T> void onSuccess(String response, T t);

	public void onFailure(String errorCode, String msg);

	public void onStop();

}
