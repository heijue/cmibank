package cn.app.yimirong.model.bean;

/**
 * 定期产品
 *
 * @author android
 */
public class DQProduct extends BaseProduct {

	/**
	 * ID
	 */
	private static final long serialVersionUID = -1866283080729010981L;

	public String activity_url;
	public String text_text;
	public String text_url;
	public String cietime;
	public String cistime;
	public String corcid;
	public String repaymode;
	public String ucid;
	public String uietime;
	public String uistime;
	public String updatetime;
	public String yugaotime;

	public DQProduct() {
		ptype = TYPE_DQ;
	}

}
