package cn.app.yimirong.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * �ҵı�ͼ
 * 
 * @author wangk
 */
public class MyPieChartView extends View {
	/** ��ע������ֳ���Ϊ6 */
	private final int MAX_LABEL_SIZE = 6;

	/**
	 * ��ͼ������
	 */
	private BasePieAdapter myPieAdapter;

	/**
	 * ��Բ����
	 */
	private Paint paint;
	/**
	 * ��ͼ�����ֻ���
	 */
	private TextPaint circlePaint;
	/**
	 * ��ע����
	 */
	private TextPaint labelPaint;
	/**
	 * Ԥ������
	 */
	private TextPaint notePaint;
	/** װ�λ��� */
	private Paint decoratePaint;

	/**
	 * ��ͼ��������ɫ��Ĭ��Ϊ��ɫ
	 */
	private int circleTextColor;

	/**
	 * ��ע������ɫ
	 */
	private int lableTextColor;

	/**
	 * ͼ��ϲ�ģʽ
	 */
	private PorterDuffXfermode pdfMode;
	/**
	 * ��ǰ�ؼ��е����
	 */
	private Point centerCoord;
	/** ��ע����α� */
	private Point cursorLabelPoint;
	/** ��ע����߳� */
	private int labelSquareEdge;

	private RectF oval;
	private RectF transOval;
	private RectF bigOval;
	private RectF decorateOval;

	/** ��ͼ�ڰ뾶,δָ��������Ӧ�뾶 */
	private int innerRadius;
	/** ��ͼ�뾶 */
	private int radius;
	/** ���Բװ�ΰ뾶 */
	private int decorateRadius;
	/** ��ǰ��������� */
	private int clickSector;
	/** ���ע���ֳ��� */
	private int max_label_length;

	/** ��ע���ִ�С */
	private float noteTextSize;
	/** ����ͼ����Ϣ���ִ�С */
	private float circleLabelTextSize;

	private Point click_down_point;
	private Point click_move_point;

	private CopyOnWriteArrayList<MyPie> pies;
	private List<Integer> randColors;
	/**
	 * �Ƿ���ʾ��ע
	 */
	private boolean isShowLabel;
	/** �Ƿ��������ɫ */
	private boolean isRandColor;
	private int totalValue;
	// �ṩ���ⲿ��ǰѡ���ͼ�Ľӿ�
	private OnPieSelectListener onPieSelectListener;
	/** ��û����ݵ�ʱ����ʾ���� */
	private String defaultText;
	/** ���� */
	private PieAnimlThread animThread;
	/** ��ʼ�϶� */
	private boolean beginDrag;

	public MyPieChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initDrawTools();
	}

	/**
	 * ��ʼ�����ʹ���
	 */
	private void initDrawTools() {

		oval = new RectF();
		transOval = new RectF();
		bigOval = new RectF();
		decorateOval = new RectF();
		paint = new Paint();
		circlePaint = new TextPaint();
		labelPaint = new TextPaint();
		centerCoord = new Point();
		notePaint = new TextPaint();
		bigOval = new RectF();
		click_down_point = new Point();
		decoratePaint = new Paint();
		cursorLabelPoint = new Point();
		pies = new CopyOnWriteArrayList<MyPie>();

		circlePaint.setTextSize(getCircleTextSize());
		labelPaint.setTextSize(getLabelTextSize());
		notePaint.setTextSize(getLabelTextSize());

		paint.setColor(Color.RED);
		circlePaint.setColor(getCircleTextColor());
		labelPaint.setColor(getLableTextColor());
		notePaint.setColor(Color.DKGRAY);
		decoratePaint.setColor(Color.argb(150, 255, 255, 255));

		paint.setAntiAlias(true); // �����
		labelPaint.setAntiAlias(true);
		circlePaint.setAntiAlias(true);
		notePaint.setAntiAlias(true);
		decoratePaint.setAntiAlias(true);

		notePaint.setTextAlign(Align.CENTER);
		circlePaint.setTextAlign(Align.CENTER);

		// ����Ӵ�
		notePaint.setFakeBoldText(true);
		// circlePaint.setFakeBoldText(true);

		// init data
		clickSector = -1;
		isRandColor = false;
		isShowLabel = true;
		pdfMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);// ����ͼ�㣬ȡ�ϲ�ǽ���������ʾ
		defaultText = "it's not have data or value is 0";
		innerRadius = -1;
		animThread = new PieAnimlThread();
		beginDrag = false;
		click_move_point = new Point();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//super.onDraw(canvas);
				// �½�ͼ��
		canvas.saveLayer(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint,
				Canvas.ALL_SAVE_FLAG);
		// û����ݵ�ʱ������
		if (myPieAdapter == null || totalValue <= 0) {
			canvas.drawText(defaultText, centerCoord.x, centerCoord.y,
					notePaint);
			canvas.restore();
			return;
		}

		for (MyPie p : pies) {
			p.setRadius(radius, innerRadius);
			if (p.isTouch()) {
				p.myDraw(canvas, p.startAngle, p.cursorEndAngle - p.startAngle,
						paint, bigOval);
			} else {
				p.myDraw(canvas, p.startAngle, p.cursorEndAngle - p.startAngle,
						paint,oval);
			}
			// ����ע
			if (isShowLabel) {
				p.drawLabel(canvas, paint, labelPaint, labelSquareEdge,
						cursorLabelPoint);
			}
		}
		
		/*
		 * ��Ϊ���ڱ�ͼ�������ֵ�����������ڱ�ͼ�������֮�����
		 */
		for (MyPie p : pies) {
			p.drawInfo(canvas, circlePaint);
		}
		// װ��Բ
		canvas.drawArc(decorateOval, 0f, 360f, true, decoratePaint);

		paint.setXfermode(pdfMode);

		canvas.drawArc(transOval, 0f, 360f, true, circlePaint);

		paint.setXfermode(null);

		canvas.restore();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// ��������Ӧ����
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if (widthSize > 0 && heightSize > 0) {
			// ȡ���padding
			int maxPadding = Math.max(getPaddingLeft(), getPaddingRight()) > Math
					.max(getPaddingTop(), getPaddingBottom()) ? Math.max(
					getPaddingLeft(), getPaddingRight()) : Math.max(
					getPaddingTop(), getPaddingBottom());
			if (maxPadding < widthSize / 20) {
				// Ĭ����20/1��ȵ�padding
				maxPadding = widthSize / 20;
			}
			if (isShowLabel) {
				labelSquareEdge = widthSize / 35;
				maxPadding += labelSquareEdge + max_label_length
						* getLabelTextSize();
				centerCoord.x = widthSize / 2 - maxPadding / 2;
				centerCoord.y = heightSize / 2;
			} else {
				centerCoord.x = widthSize / 2;
				centerCoord.y = heightSize / 2;
			}

			radius = (widthSize > heightSize ? heightSize : widthSize) * 7 / 15
					- maxPadding / 2;
			if (innerRadius == -1) {
				innerRadius = (4 * radius) / 7; // ��Բ�뾶Ĭ��Ϊ��Բ��4/7
			}
		} else {
			innerRadius = 0;
			radius = 0;
			centerCoord.x = 0;
			centerCoord.y = 0;
		}

		/*
		 * �������
		 */
		int tempDistance = radius/8;
		decorateRadius = innerRadius <= 0 ? 0 : innerRadius + tempDistance / 2;

		oval.left = centerCoord.x - radius;
		oval.top = centerCoord.y - radius;
		oval.right = centerCoord.x + radius;
		oval.bottom = centerCoord.y + radius;

		transOval.left = centerCoord.x - innerRadius;
		transOval.top = centerCoord.y - innerRadius;
		transOval.right = centerCoord.x + innerRadius;
		transOval.bottom = centerCoord.y + innerRadius;

		bigOval.left = centerCoord.x - radius - tempDistance;
		bigOval.top = centerCoord.y - radius - tempDistance;
		bigOval.right = centerCoord.x + radius + tempDistance;
		bigOval.bottom = centerCoord.y + radius + tempDistance;

		decorateOval.left = centerCoord.x - decorateRadius;
		decorateOval.top = centerCoord.y - decorateRadius;
		decorateOval.right = centerCoord.x + decorateRadius;
		decorateOval.bottom = centerCoord.y + decorateRadius;
		if (isShowLabel) {
			double a = labelPaint.measureText("��") * max_label_length + labelSquareEdge * 1.5;
			// tempDistance ������û�����壬ֻ��Ϊ���ܹ���ԲԶ��
			cursorLabelPoint.x = (int) (centerCoord.x + radius + tempDistance
					- a);
			cursorLabelPoint.y = centerCoord.y - radius;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	/**
	 * �������Դ
	 * 
	 * @param myPieAdapter
	 */
	public void setAdapter(BasePieAdapter myPieAdapter) {
		this.myPieAdapter = myPieAdapter;
		notifySetDataChanged();
	}

	/**
	 * �ֶ�ָ����ͼѡ��״̬
	 * 
	 * @param position
	 */
	public void setSelectPie(int position) {
		if (position > pies.size()) {
			throw new IllegalArgumentException(
					"position is more than max size,the position is:"
							+ position + "," + "but the size is :"
							+ pies.size());
		}
		clickSector = position;
		for (int i = 0; i < pies.size(); i++) {
			if (i == position) {
				pies.get(i).setSelect(true);
			} else {
				pies.get(i).setSelect(false);
			}
		}
		invalidate();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			click_move_point.x = (int) event.getX();
			click_move_point.y = (int) event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			final int tempX = x-click_move_point.x;
			final int tempY = y-click_move_point.y;
			final int relativeCenterX = x-centerCoord.x;
			final int relativeCenterY = y-centerCoord.y;
			int keyFactor = 1;
			double relativeDistance =  Math.sqrt(Math.pow(relativeCenterX, 2)
					+Math.pow(relativeCenterY, 2));
			double tempDistance = Math.sqrt(Math.pow(tempX, 2)
					+Math.pow(tempY, 2));
			if(relativeDistance <= radius && relativeDistance>= innerRadius) {
				beginDrag = true;
				// �ж�����
				switch (getPointQuadrntnt(click_move_point)) {
				case 1:
					if(tempX <= 0 && tempY >= 0) {
						keyFactor = 1;
					} else if((tempX < 0 && tempY <0) || (tempX > 0 && tempY >0)) {
						keyFactor = 2;
					} else {
						keyFactor = -1;
					}
					break;
				case 2:
					if(tempX <= 0 && tempY <= 0) {
						keyFactor = 1;
					} else if((tempX > 0 && tempY <0) || (tempX < 0 && tempY >0)) {
						keyFactor = 2;
					} else{
						keyFactor = -1;
					}
					break;
				case 3:
					if((tempX >=0 && tempY <= 0)) {
						keyFactor = 1;
					} else if((tempX < 0 && tempY <0) || (tempX > 0 && tempY >0)) {
						keyFactor = 2;
					}else {
						keyFactor = -1;
					}
						
					break;
				case 4:
					if(tempX >= 0 && tempY >= 0) {
						keyFactor = 1;
					} else if((tempX > 0 && tempY <0) || (tempX < 0 && tempY >0)) {
						keyFactor = 2;
					} else {
						keyFactor = -1;
					}
					break;
				default:
					Log.e(VIEW_LOG_TAG, "wrong point");
					break;
				}
				if(keyFactor != 2) {
					for (MyPie mp : pies) {
						mp.startAngle+=(tempDistance/5.0f)*keyFactor;
						if(mp.startAngle >= 360f) {
							mp.startAngle = mp.startAngle-360f;
						} else if(mp.startAngle <= 0f) {
							mp.startAngle = 360f-Math.abs(mp.startAngle);
						}
						mp.cursorEndAngle = mp.startAngle+mp.sweepAngle;
						
					}
				}
				click_move_point.x  = x;
				click_move_point.y = y;
				beginDrag = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			if(!beginDrag) {
				click_down_point.x = (int) event.getX();
				click_down_point.y = (int) event.getY();
				for (int i = 0; i < pies.size(); i++) {
					MyPie p = pies.get(i);
					if (p.isInArea(click_down_point)) {
						clickSector = i;
						if (onPieSelectListener != null) {
							onPieSelectListener.selectChanged(i);
						}
					}
				}
			}
			break;
		default:
			break;
		}
	
		invalidate();
		return true;
	}

	/**
	 * ˢ�����
	 */
	public void notifySetDataChanged() {
		if (myPieAdapter == null) {
			return;
			// throw new IllegalArgumentException("adapter is null!");
		}
		if (pies == null) {
			pies = new CopyOnWriteArrayList<MyPie>();
		}
		pies.clear();
		// initDrawTools();
		totalValue = 0;
		// ���������ֵ
		for (int i = 0; i < myPieAdapter.getCount(); i++) {
			totalValue += myPieAdapter.getItem(i).sectorValue;
		}
		float cursorAngle = 0f;
		PieModel p = null;
		max_label_length = 0;
		for (int i = 0; i < myPieAdapter.getCount(); i++) {
			p = myPieAdapter.getItem(i);
			if (isRandColor) {
				p.sectorColor = getRandomColor();
			}
			if (max_label_length < p.sectorName.length()) {
				max_label_length = p.sectorName.length();
			}
			float sweepAngle = ((p.sectorValue / totalValue) * 360f);
			pies.add(new MyPie(cursorAngle, sweepAngle, centerCoord, p,
					MyPie.LABEL_GRIVITY.DRAW_NONE_LABEL.navtieInt, i));
			cursorAngle = cursorAngle + sweepAngle;
		}

		if (max_label_length > MAX_LABEL_SIZE) {
			max_label_length = MAX_LABEL_SIZE;
		}
		if(!animThread.isAlive()) {
			animThread.start();
		}
	//	invalidate();
	}

	public int getCircleTextColor() {
		return circleTextColor == 0 ? Color.WHITE : circleTextColor;
	}

	public int getLableTextColor() {
		return lableTextColor == 0 ? Color.BLACK : lableTextColor;
	}

	/**
	 * ���ñ�ͼ�ڰ뾶
	 */
	public void setInnerRaduis(int radius) {
		this.innerRadius = radius;
		invalidate();
	}

	/**
	 * �Ƿ���ʾ��ע
	 * 
	 * @param flag
	 */
	public void isShowLabel(boolean flag) {
		this.isShowLabel = flag;
	}

	/**
	 * ��ȡ���Դadapter
	 * 
	 * @return
	 */
	public BasePieAdapter getAdapter() {
		return myPieAdapter;
	}

	public void setRandColor(boolean flag) {
		this.isRandColor = flag;
		notifySetDataChanged();
	}

	/**
	 * ���һ�������ɫ������Ψһ
	 * 
	 * @return
	 */
	public int getRandomColor() {
		if (randColors == null) {
			randColors = new ArrayList<Integer>();
		}
		if (randColors.size() >= 16581375) {
			randColors.clear();
		}
		int r = (int) (Math.random() * 255);
		int g = (int) (Math.random() * 255);
		int b = (int) (Math.random() * 255);

		int color = Color.rgb(r, g, b);
		if (!randColors.contains(color)) {
			randColors.add(color);
		} else {
			color = getRandomColor();
		}
		return color;
	}

	public void setLabelTextSize(float pixelSize) {
		this.noteTextSize = pixelSize;
		notePaint.setTextSize(noteTextSize);
		invalidate();
	}

	public float getLabelTextSize() {
		return noteTextSize == 0f ? getRawSize(getContext(),
				TypedValue.COMPLEX_UNIT_SP, 10) : noteTextSize;
	}

	public void setCircleTextSize(float pixelSize) {
		this.circleLabelTextSize = pixelSize;
		circlePaint.setTextSize(circleLabelTextSize);
		invalidate();
	}

	/**
	 * �޸�û�����ʱ���Ĭ����ʾ����
	 * 
	 * @param text
	 */
	public void setDefaultText(String text) {
		this.defaultText = text;
		invalidate();
	}

	public float getCircleTextSize() {
		return circleLabelTextSize == 0f ? getRawSize(getContext(),
				TypedValue.COMPLEX_UNIT_SP, 12) : circleLabelTextSize;
	}

	public void setShowLabel(boolean flag) {
		this.isShowLabel = flag;
		notifySetDataChanged();
	}

	public void setOnPieSelectListener(OnPieSelectListener onPieSelectListener) {
		this.onPieSelectListener = onPieSelectListener;
	}

	/**
	 * �ṩ���ⲿ���õ�����ѡ��״̬
	 * 
	 * @author wangk
	 */
	public interface OnPieSelectListener {
		public void selectChanged(int position);
	}

	/**
	 * ͨ��ָ����λ������һ������ֵ xml��λ����
	 * 
	 * @param context
	 * @param unit
	 *            ��λ
	 * @param value
	 *            ��ֵ
	 * @return
	 */
	public float getRawSize(Context context, int unit, float value) {
		Resources res = context.getResources();
		return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
	}

	/**
	 * ��ͼ��ת����
	 * @author wangk
	 */
	class PieAnimlThread extends Thread {
		@Override
		public void run() {
			// activity�и����ض�������ֹ�߳���ǰ��ʼ���ƣ�����һ���ȴ�ʱ��
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			for (MyPie p : pies) {
				while (p.cursorEndAngle < (p.startAngle + p.sweepAngle)) {
					long startTime = System.currentTimeMillis();
					p.cursorEndAngle+=1f;
					postInvalidate();
					long endTime = System.currentTimeMillis();
					// �޶�֡��
					if ((endTime - startTime) < 2) {
						try {
							Thread.sleep(2 - (endTime - startTime));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
			}
			
		}
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
	 * ��������꣬����һ�������Բ�ĵ�����
	 * @param point
	 * @return
	 */
	private int getPointQuadrntnt(Point point) {
		double angle = getPointAngle(point);
		double realAngle = Math.abs(angle)%360;
		if(realAngle>=0 && realAngle < 90) {
			return 1;
		} else if(realAngle >= 90 && realAngle < 180) {
			return 2;
		} else if(realAngle >= 180 && realAngle < 270) {
			return 3;
		} else if(realAngle >= 270 && realAngle < 360) {
			return 4;
		}
					
		return 0;
	}
}
