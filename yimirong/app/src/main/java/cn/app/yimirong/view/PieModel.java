package cn.app.yimirong.view;

/**
 * ��ͼmodel
 * @author wangk
 */
public class PieModel {
	
	public PieModel(String sectorName, float sectorValue, int sectorColor) {
		this.sectorName = sectorName;
		this.sectorValue = sectorValue;
		this.sectorColor = sectorColor;
	}
	

	public PieModel() {
	}



	/**
	 * ��������
	 */
	public String sectorName;
	
	/**
	 * ������ֵ
	 */
	public float sectorValue;
	
	/**
	 * ������ɫ
	 */
	public int sectorColor;
}
