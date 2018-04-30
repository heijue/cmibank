/*
 * Copyright 2015-2016 TakWolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.yimirong.view.lock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.woncaidog.view.lock.R;

public class LockView extends ViewGroup {

    /**
     * 节点相关定义
     */
    private List<Pair<NodeView, NodeView>> lineList = new ArrayList<Pair<NodeView, NodeView>>(); // 已经连线的节点链表
    private NodeView currentNode; // 最近一个点亮的节点，null表示还没有点亮任何节点
    private float x; // 当前手指坐标x
    private float y; // 当前手指坐标y

    /**
     * 自定义属性列表
     */
    private Drawable nodeNormal;
    private Drawable nodePressed;
    private Drawable nodeWrong;
    private int lineColor;
    private float lineWidth;
    private float padding; // 内边距
    private float spacing; // 节点间隔距离

    private List<NodeView> selectedNodes;

    private Handler mHandler;

    public String password;

    private boolean isCreate = false;

    private boolean isWrong = false;

    /**
     * 画线用的画笔
     */
    private Paint paint;

    /**
     * 密码构建器
     */
    private StringBuilder passwordBuilder = new StringBuilder();

    /**
     * 结果回调监听器接口
     */
    private CallBack callBack;

    public interface CallBack {

        public void onStart();

        public void onFinish(String password);

    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public void setPassword(boolean isCreate, String password) {
        this.isCreate = isCreate;
        this.password = password;
    }

    /**
     * 构造函数
     */
    public LockView(Context context) {
        this(context, null);
    }

    public LockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHandler = new Handler();
        initFromAttributes(attrs, defStyleAttr);
        selectedNodes = new ArrayList<NodeView>();
    }

    /**
     * 初始化
     */
    private void initFromAttributes(AttributeSet attrs, int defStyleAttr) {
        // 获取定义的属性
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.LockView, defStyleAttr, 0);

        nodeNormal = a.getDrawable(R.styleable.LockView_lock_nodeNormal);
        nodePressed = a.getDrawable(R.styleable.LockView_lock_nodePressed);
        nodeWrong = a.getDrawable(R.styleable.LockView_lock_nodeWrong);
        lineColor = a.getColor(R.styleable.LockView_lock_lineColor,
                Color.argb(0, 0, 0, 0));
        lineWidth = a.getDimension(R.styleable.LockView_lock_lineWidth, 0);
        padding = a.getDimension(R.styleable.LockView_lock_padding, 0);
        spacing = a.getDimension(R.styleable.LockView_lock_spacing, 0);

        a.recycle();

        // 初始化画笔
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(lineColor);
        // 抗锯齿
        paint.setAntiAlias(true);

        // 构建node
        for (int n = 0; n < 9; n++) {
            NodeView node = new NodeView(getContext(), n + 1);
            addView(node);
        }

        // 清除FLAG，否则 onDraw() 不会调用，原因是 ViewGroup 默认透明背景不需要调用 onDraw()
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = measureWidth(widthMeasureSpec);
        int measureHeight = measureHeight(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    /**
     * 测量宽度
     *
     * @param pWidthMeasureSpec
     * @return
     */
    private int measureWidth(int pWidthMeasureSpec) {
        int result = 0;
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = widthSize;
                break;
        }
        return result;
    }

    /**
     * 测量高度
     *
     * @param pHeightMeasureSpec
     * @return
     */
    private int measureHeight(int pHeightMeasureSpec) {
        int result = 0;
        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = heightSize;
                break;
        }
        return result;
    }

    /**
     * 在这里进行node的布局
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        if (changed) {
            int nodeWidth = (int) ((right - left - padding * 2 - spacing * 2) / 3);
            for (int n = 0; n < 9; n++) {
                NodeView node = (NodeView) getChildAt(n);
                // 获取3*3宫格内坐标
                int row = n / 3;
                int col = n % 3;
                // 计算实际的坐标，要包括内边距和分割边距
                /*int l = (int) (padding + col * (nodeWidth + spacing));
                int t = (int) (padding + row * (nodeWidth + spacing));*/
                int l = (int) (padding + col * (nodeWidth + spacing));
                int t = (int) (padding * 2 / 3 + row * (nodeWidth + spacing));
                int r = l + nodeWidth;
                int b = t + nodeWidth;
                node.layout(l, t, r, b);
            }
        }
    }

    /**
     * 在这里处理手势
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        paint.setColor(lineColor);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // 这里要实时记录手指的坐标
                x = event.getX();
                y = event.getY();
                NodeView nodeAt = getNodeAt(x, y);
                if (currentNode == null) {
                    // 之前没有点
                    if (nodeAt != null) {
                        if (callBack != null) {
                            callBack.onStart();
                        }
                        // 第一个点
                        currentNode = nodeAt;
                        currentNode.setNodePressed();
                        // 保存进List
                        selectedNodes.add(nodeAt);
                        passwordBuilder.append(currentNode.getNum());
                        // 通知重绘
                        invalidate();
                    }
                } else {
                    // 之前有点-所以怎么样都要重绘
                    if (nodeAt != null && !nodeAt.isPressed()) {
                        // 当前碰触了新点
                        nodeAt.setNodePressed();
                        selectedNodes.add(nodeAt);
                        Pair<NodeView, NodeView> pair = new Pair<NodeView, NodeView>(
                                currentNode, nodeAt);
                        lineList.add(pair);
                        // 赋值当前的node
                        currentNode = nodeAt;
                        passwordBuilder.append(currentNode.getNum());
                    }
                    // 通知重绘
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (passwordBuilder.length() > 0) {
                    // 有触摸点
                    if (passwordBuilder.length() < 4) {
                        // 小于4个点
                        showWrong();
                    } else {
                        if (!isCreate) {
                            String pass = passwordBuilder.toString();
                            if (!pass.equals(this.password)) {
                                showWrong();
                            }
                        }
                    }

                    if (callBack != null) {
                        callBack.onFinish(passwordBuilder.toString());
                    }

                    // 500ms后复位状态
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reset();
                        }
                    }, 500);
                }
                break;
        }
        return true;
    }

    protected void showWrong() {
        isWrong = true;
        for (NodeView nodeView : selectedNodes) {
            nodeView.setNodeWrong();
        }
        paint.setColor(Color.RED);
        invalidate();
    }

    /**
     * 重置
     */
    protected void reset() {
        // 清空状态
        isWrong = false;
        selectedNodes.clear();
        lineList.clear();
        currentNode = null;
        passwordBuilder.setLength(0);
        // 清除高亮
        for (int n = 0; n < getChildCount(); n++) {
            NodeView node = (NodeView) getChildAt(n);
            node.setNodeNormal();
        }
        // 通知重绘
        invalidate();
    }

    /**
     * 系统绘制回调-主要绘制连线
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // 先绘制已有的连线
        for (Pair<NodeView, NodeView> pair : lineList) {
            canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
                    pair.second.getCenterX(), pair.second.getCenterY(), paint);
        }
        // 如果已经有点亮的点，则在点亮点和手指位置之间绘制连线
        if (currentNode != null && !isWrong) {
            canvas.drawLine(currentNode.getCenterX(), currentNode.getCenterY(),
                    x, y, paint);
        }
    }

    /**
     * 获取给定坐标点的Node，返回null表示当前手指在两个Node之间
     */
    private NodeView getNodeAt(float x, float y) {
        for (int n = 0; n < getChildCount(); n++) {
            NodeView node = (NodeView) getChildAt(n);
            if (!(x >= node.getLeft() && x < node.getRight())) {
                continue;
            }
            if (!(y >= node.getTop() && y < node.getBottom())) {
                continue;
            }
            return node;
        }
        return null;
    }

    /**
     * 结点描述类
     */
    private class NodeView extends View {

        private static final int STATE_NORMAL = 0;
        private static final int STATE_PRESSED = 1;
        private static final int STATE_WRONG = 2;

        private int num;

        private int nodeState = STATE_NORMAL;

        @SuppressWarnings("deprecation")
        public NodeView(Context context, int num) {
            super(context);
            this.num = num;
            nodeState = STATE_NORMAL;
            setBackgroundDrawable(nodeNormal);
        }

        public boolean isPressed() {
            if (nodeState == STATE_PRESSED) {
                return true;
            }
            return false;
        }

        @SuppressWarnings("deprecation")
        public void setNodeNormal() {
            if (nodeState != STATE_NORMAL) {
                nodeState = STATE_NORMAL;
                setBackgroundDrawable(nodeNormal);
            }
        }

        @SuppressWarnings("deprecation")
        public void setNodePressed() {
            if (nodeState != STATE_PRESSED) {
                nodeState = STATE_PRESSED;
                setBackgroundDrawable(nodePressed);
            }
        }

        @SuppressWarnings("deprecation")
        public void setNodeWrong() {
            if (nodeState != STATE_WRONG) {
                nodeState = STATE_WRONG;
                setBackgroundDrawable(nodeWrong);
            }
        }

        public int getCenterX() {
            return (getLeft() + getRight()) / 2;
        }

        public int getCenterY() {
            return (getTop() + getBottom()) / 2;
        }

        public int getNum() {
            return num;
        }

    }

}
