package cn.app.yimirong.model.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 定期产品更多
 *
 * @author android
 */
public class ProductMore implements Serializable {

	public ArrayList<BaseProduct> complete;

	public ArrayList<BaseProduct> product;

	public ArrayList<BaseProduct> sellout;

	public String defaultIncome;
}
