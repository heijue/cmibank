package cn.app.yimirong.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;

import java.math.BigDecimal;

/**
 * ���ζ���
 * @author wangk
 */
public class MyPie {
	/**
	 * ��ʼ���
	 */
	public float startAngle;
	/**
	 * �������
	 */
	public float sweepAngle;
	
	public RectF oval;
	/**
	 * Բ�ص����
	 */
	private Point centerCoord;
	/** Բ�뾶 */
	private int radius;
	/** ��Բ�뾶 */
	private int innerRadius;
	/** ������ݶ��� */
	private PieModel p;
	/** ��ռ�ٷֱ� */
	private String rate;
	/** �Ƿ񱻵��� */
	private boolean isTouch;
	/** ��ͼ������Ϣ��� */
	private Point textP;
	/** ��ע��ɫʾ��߳� */
	private int labelSqaureEdge;
	/** ��עλ�� */
	private int labelGrivity;
	/** ��ע��� */
	private Point labelPoint;
	/** ��עλ�� */
	private int position;
	/** ��ע����λ�� */
	private Rect rect;
	/** �������α�Ƕ� */
	public float cursorEndAngle;
	/** �������� */
	/* region�ʺ�path��ɵĲ�����ͼ�Σ����ʺϱ�ͼ���ж� */
	//private Region region;
	
	public MyPie(float startAngle, float sweepAngle,Point centerCoord,PieModel p,int labelGrivity,int position) {
		this.startAngle = startAngle;
		this.sweepAngle = sweepAngle;
		this.centerCoord = centerCoord;
		this.p = p;
		this.labelGrivity = labelGrivity;
		this.position = position;
		textP = new Point();
		cursorEndAngle =  startAngle;
	}

	/**
	 * �滭����
	 * @param canvas 
	 * @param oval
	 * @param paint ���ε�ɫ����
	 * @param textPaint ��������ʾ��Ϣ����
	 * @param radius Բ�뾶
	 * @param innerRadius ��Բ�뾶
	 * @param isDrawLabel �Ƿ��Ҳ��ע
	 */
	public void drawSelf(Canvas canvas,RectF oval,Paint paint,Paint textPaint,int radius,int innerRadius) {
		if(centerCoord == null) {
			throw new IllegalArgumentException("center coord is null");
		}
		this.radius = radius;
		this.innerRadius = innerRadius;
		this.oval = oval;
		double numRate = sweepAngle/3.6;
		BigDecimal bigRate = new BigDecimal(numRate);
		bigRate = bigRate.setScale(1, BigDecimal.ROUND_HALF_UP);
		rate = "%"+bigRate;
		myDraw(canvas, startAngle, sweepAngle, paint, oval);
		drawInfo(canvas, textPaint);
	}
	
	/**
	 * easyDraw
	 * @param canvas
	 * @param startAngle
	 * @param sweepAngle
	 * @param paint
	 * @param textPaint
	 * @param oval
	 */
	public void myDraw(Canvas canvas,float startAngle,float sweepAngle,Paint paint,RectF oval) {
		if(TextUtils.isEmpty(rate)) {
			double numRate = this.sweepAngle/3.6;
			BigDecimal bigRate = new BigDecimal(numRate);
			bigRate = bigRate.setScale(1, BigDecimal.ROUND_HALF_UP);
//			rate = bigRate+ "%";
			rate = "";
		}
		if(sweepAngle > 0) {
			paint.setColor(p.sectorColor);
			canvas.drawArc(oval, startAngle, sweepAngle, true, paint);
			textP = computeSectorMiddleCoord(textP, startAngle+sweepAngle, sweepAngle);
		}
	}
	
	public void drawInfo(Canvas canvas,Paint textPaint) {
		canvas.drawText(p.sectorName,textP.x, textP.y, textPaint);
		canvas.drawText(rate,textP.x, textP.y+textPaint.getTextSize(), textPaint);
	}
	
	public void setRadius(int radius,int innerRadius) {
		this.radius = radius;
		this.innerRadius = innerRadius;
	}
	
	
	/**
	 * ��ʼ��label����
	 * @param labelGrivity
	 * @param labelSqaureEdge
	 * @param labelPoint
	 */
	private void initLabel(int labelSqaureEdge,Point labelPoint) {
		// label��־Խ�磬�򲻻�
		if(!LABEL_GRIVITY.isBelongLabel_grivity(labelGrivity)) {
			labelGrivity = LABEL_GRIVITY.DRAW_NONE_LABEL.navtieInt;
		}
		this.rect = new Rect();
		this.labelPoint = new Point();
		
		this.labelPoint.x = (int) (labelPoint.x+labelSqaureEdge*1.5f); 
		this.labelPoint.y = (int) (labelPoint.y+labelSqaureEdge*0.8f+position*labelSqaureEdge*1.5);
		
		this.labelSqaureEdge = labelSqaureEdge;
		rect.left = (int) (this.labelPoint.x-labelSqaureEdge*1.5f);
		rect.top = (int) (this.labelPoint.y-labelSqaureEdge*0.8f);
		rect.right = (int) (this.labelPoint.x-labelSqaureEdge*0.5f);
		rect.bottom = this.labelPoint.y;
	}
	
	/**
	 * ����ע
	 * @param canvas
	 * @param textPaint
	 */
	public void drawLabel(Canvas canvas,Paint rectPaint,TextPaint textPaint,int labelSqaureEdge,Point labelPoint) {
		initLabel(labelSqaureEdge, labelPoint);
		rectPaint.setColor(p.sectorColor);
		canvas.drawRect(rect, rectPaint);
		canvas.drawText(getSelectorName(), this.labelPoint.x, this.labelPoint.y, textPaint);
		// Ԥ��
		if(labelGrivity == LABEL_GRIVITY.DRAW_TOP_RIGHT_LABEL.navtieInt) {
		}
	}
	
	/**
	 * �жϴ�����������Ƿ��ڵ�ǰ������
	 * @param point ��Ҫ�жϵ����
	 * @return
	 */
	public boolean isInArea(Point point) {
		if(point == null) {
			isTouch = false;
			return false;
		}
		float bevelledEdge = (float) Math.sqrt(Math.pow(point.x-centerCoord.x, 2)+Math.pow(point.y-centerCoord.y, 2));
		if(bevelledEdge <= radius && bevelledEdge > innerRadius) {
			double angle = getPointAngle(point);
			float overFlowAngle = 0f;
			if(startAngle+sweepAngle > 360f) {
				overFlowAngle = startAngle+sweepAngle - 360f;
			}
			if(angle > startAngle && angle < (startAngle+sweepAngle)) {
				isTouch = true;
				return true;
			} else if(angle > 0f && angle < overFlowAngle) {
				isTouch = true;
				return true;
			}
		}
		isTouch = false;
		return false;
	}
	
	
	
	/**
	 * �������������Բ�ĵĶ���
	 * @param point
	 * @return
	 */
	private double getPointAngle(Point point) {
		float a = point.x - centerCoord.x;
		float b = point.y - centerCoord.y;
		if(b <= 0) {
			return 180f+(180f+Math.atan2(b, a)*(180f/Math.PI));
		}
		return Math.atan2(b, a)*(180f/Math.PI);
	}
	
	/**
	 * ���������е�����
	 * @param centerPoint ���
	 * @param totalSweepAngle ��ǰ�Ѿ������Ѿ�ɨ�����ܶ���
	 * @param currentSweepAngle  ��ǰ����������ռ�Ķ���
	 * @return
	 */
	private Point computeSectorMiddleCoord(Point centerPoint,float totalSweepAngle,float currentSweepAngle) {
		if(centerPoint == null) {
			centerPoint = new Point();
		}
		Point point = new Point();
		float angle = totalSweepAngle-currentSweepAngle/2;
		angle = (float) Math.floor(angle);
		int delRadius = radius - (radius-innerRadius)/2;
		int x = 0;
		int y = 0;
		x =  (int)(Math.cos(Math.toRadians(angle))*delRadius);
		y =  (int)(Math.sin(Math.toRadians(angle))*delRadius);
		point.x = centerCoord.x+x;
		point.y = centerCoord.y+y;
		return point;
	}
	
	public int getSelectorColor() {
		return p.sectorColor;
	}
	
	public String getSelectorName() {
		return p.sectorName;
	}
	
	public String getSelectorRate() {
		return rate;
	}
	
	public boolean isTouch() {
		return isTouch;
	}
	
	/**
	 * ����ѡ��״̬
	 */
	public void setSelect(boolean flag) {
		isTouch = flag;
	}
	
	public static enum LABEL_GRIVITY {
		/**
		 * ������ע
		 */
		DRAW_NONE_LABEL(0),
		/**
		 * ������໭��ע
		 */
		DRAW_TOP_LEFT_LABEL(1),
		/**
		 * �����Ҳ໭��ע
		 */
		DRAW_TOP_RIGHT_LABEL(2),
		/**
		 * �ڵ���໭��ע
		 */
		DRAW_BOTTOM_LEFT_LABEL(3),
		/**
		 * �ڵ��Ҳ໭��ע
		 */
		DRAW_BOTTOM_RIGHT_LABEL(4);
		
		public int navtieInt;
		private LABEL_GRIVITY(int navtieInt) {
			this.navtieInt = navtieInt;
		}
		
		public static boolean isBelongLabel_grivity(int num) {
			if(num == 0 || num == 1 || num == 2 || num ==3 || 
					num == 4) {
				return true;
			}
			return false;
		}
	}
	
}
