package cn.app.yimirong.view;


/**
 * ��ͼ�������
 * @author wangk
 */
public interface BasePieAdapter {
	
	/**
	 * ��ͼ�������
	 * @return
	 */
	public int getCount();
	
	/**
	 * ��ȡ��Ӧ�������
	 * @return
	 */
	public PieModel getItem(int position);
	
	/**
	 * ��ȡ���
	 * @param position
	 * @return
	 */
	public int getId(int position);
	
}
