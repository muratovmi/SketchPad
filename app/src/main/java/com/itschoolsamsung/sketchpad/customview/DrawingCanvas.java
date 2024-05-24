package com.itschoolsamsung.sketchpad.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.itschoolsamsung.sketchpad.activity.DrawingBoard;
import com.itschoolsamsung.sketchpad.modal.Circle;
import com.itschoolsamsung.sketchpad.modal.InputText;
import com.itschoolsamsung.sketchpad.modal.Ellipse;
import com.itschoolsamsung.sketchpad.modal.PathStore;
import com.itschoolsamsung.sketchpad.modal.Rectangle;
import com.itschoolsamsung.sketchpad.modal.Shape;
import com.itschoolsamsung.sketchpad.util.Constants;

import java.util.ArrayList;

// Класс, формирующий представление о холсте. Все фигуры рисуются на нём.
public class DrawingCanvas extends View {
    private int mCanvasRightBounds;
    private int mCanvasBottomBounds;
    private Paint mBrushPaint;
    private Path mBrushPath;
    private Paint mEraserPaint;
    private final Context mContext;
    private static int mCurrentOperation;
    // Перемещение фигур на холсте.
    private static int mStorePrevOperationOnLongPress;
    private static int mIndexOfViewInListToMove;
    private static Shape mShapeToMove;
    //path operations
    private Paint mPathPaint;
    // Переменные для инструмента "Круг".
    private int mCircleRadius;
    private Paint mCirclePaint;
    // Переменные для инструмента "Прямоугольник".
    private int mRectangleWidth;
    private int mRectangleHeight;
    // Переменные для инструмента "Текст".
    private String mTextBoxText;
    private int mTextBoxSize;
    private static ArrayList mShapeList;
    // Объект для обнаружения различных жестов.
    private GestureDetector mDetectGesture;
    private Vibrator mVibrator;
    // Фон холста.
    private Bitmap mCanvasBackground;

    public DrawingCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initialize();
    }

    public void initialize() {
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        mBrushPaint = new Paint();
        mBrushPaint.setAntiAlias(true);
        changeBrushColor("#000000");
        changeFillStyle(Constants.PAINT_STYLE_STROKE);
        mBrushPaint.setStrokeJoin(Paint.Join.ROUND);
        mBrushPaint.setStrokeCap(Paint.Cap.ROUND);
        mBrushPath = new Path();
        mEraserPaint = new Paint();
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setStyle(Paint.Style.STROKE);
        changeBrushStroke(1);
        mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEraserPaint.setStrokeCap(Paint.Cap.ROUND);
        mEraserPaint.setColor(Color.parseColor("#ffffff"));
        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeJoin(Paint.Join.ROUND);
        mPathPaint.setStrokeCap(Paint.Cap.ROUND);
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setStrokeJoin(Paint.Join.ROUND);
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mShapeList = new ArrayList();
        mDetectGesture = new GestureDetector(mContext, new CustomGestureDetector());
        mIndexOfViewInListToMove = -1;
        mShapeToMove = null;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (mCanvasBackground != null) {
            mCanvasBackground = Bitmap.createScaledBitmap(mCanvasBackground, getWidth(), getHeight(), true);
            canvas.drawBitmap(mCanvasBackground, 0f, 0f, null);
        }
        for (Object obj : mShapeList) {
            if (obj instanceof PathStore) {
                PathStore path = (PathStore) obj;
                mPathPaint.setColor(path.getPathColor());
                mPathPaint.setStrokeWidth(path.getPathStroke());
                canvas.drawPath(path.getDrawnPath(), mPathPaint);
            } else if (obj instanceof Circle) {
                Circle cir = (Circle) obj;
                mCirclePaint.setColor(cir.getCircleColor());
                canvas.drawCircle(cir.getCenterX(),
                        cir.getCenterY(),
                        cir.getRadius(),
                        mCirclePaint);
            } else if (obj instanceof Rectangle) {
                Rectangle rect = (Rectangle) obj;
                mCirclePaint.setColor(rect.getColor());
                canvas.drawRect(rect.getLeft(), rect.getTop(), rect.getRight(), rect.getBottom(), mCirclePaint);
            } else if (obj instanceof Ellipse) {
                Ellipse ellipse = (Ellipse) obj;
                mCirclePaint.setColor(ellipse.getColor());
                canvas.drawOval(new RectF(ellipse.getLeft(), ellipse.getTop(), ellipse.getRight(), ellipse.getBottom()), mCirclePaint);
            } else if (obj instanceof InputText) {
                InputText text = (InputText) obj;
                mCirclePaint.setColor(text.getTextColor());
                mCirclePaint.setTextSize(text.getTextSize());
                Rect mTextBoxBounds = new Rect();
                mCirclePaint.getTextBounds(text.getTextInput(), 0, text.getTextInput().length(), mTextBoxBounds);
                text.setTextHeight(mTextBoxBounds.height());
                text.setTextWidth(mTextBoxBounds.width());
                canvas.drawText(text.getTextInput(), text.getxLocation(), text.getyLocation(), mCirclePaint);
            }
        }
        if (getCurrentOperation() == Constants.OPERATION_DRAW_PENCIL) {
            canvas.drawPath(mBrushPath, mBrushPaint);
        } else if (getCurrentOperation() == Constants.OPERATION_ERASE) {
            canvas.drawPath(mBrushPath, mEraserPaint);
        }
    }

    // Переопределённый метод onSizeChanged, который нужен для получения новых размеров холста при его изменении.
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCanvasRightBounds = w;
        mCanvasBottomBounds = h;
    }

    // Переопределённый метод onTouchEvent, который отслеживает касание для рисования фигур.
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchEvent = event.getAction();
        int xPos = (int) event.getX();
        int yPos = (int) event.getY();
        mDetectGesture.onTouchEvent(event);
        switch (touchEvent) {
            case MotionEvent.ACTION_DOWN:
                if (mCurrentOperation == Constants.OPERATION_DRAW_PENCIL || mCurrentOperation == Constants.OPERATION_ERASE) {
                    mBrushPath.moveTo(xPos, yPos);
                    mBrushPath.lineTo(xPos, yPos);
                } else if (mCurrentOperation == Constants.OPERATION_DRAW_CIRCLE) {
                    mShapeList.add(new Circle(xPos, yPos, mCircleRadius, mBrushPaint.getColor()));
                    setCurrentOperation(Constants.OPERATION_NO_OPERATION);
                } else if (mCurrentOperation == Constants.OPERATION_DRAW_RECTANGLE) {
                    mShapeList.add(new Rectangle(yPos - ((int) mRectangleHeight / 2),
                            yPos + ((int) mRectangleHeight / 2),
                            xPos - ((int) mRectangleWidth / 2),
                            xPos + ((int) mRectangleWidth / 2),
                            mBrushPaint.getColor()));
                    setCurrentOperation(Constants.OPERATION_NO_OPERATION);
                } else if (mCurrentOperation == Constants.OPERATION_DRAW_OVAL) {
                    mShapeList.add(new Ellipse(yPos - ((int) mRectangleHeight / 2),
                            yPos + ((int) mRectangleHeight / 2),
                            xPos - ((int) mRectangleWidth / 2),
                            xPos + ((int) mRectangleWidth / 2),
                            mBrushPaint.getColor()));
                    setCurrentOperation(Constants.OPERATION_NO_OPERATION);
                } else if (mCurrentOperation == Constants.OPERATION_INSERT_TEXT) {
                    mShapeList.add(new InputText(mTextBoxText, mTextBoxSize, xPos, yPos, mBrushPaint.getColor(), 0, 0));
                    setCurrentOperation(Constants.OPERATION_NO_OPERATION);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurrentOperation == Constants.OPERATION_DRAW_PENCIL || mCurrentOperation == Constants.OPERATION_ERASE) {
                    mBrushPath.lineTo(xPos, yPos);
                } else if (mCurrentOperation == Constants.OPERATION_MOVE_VIEW) {
                    // Переменные, хранящие границы холста.
                    int mCanvasLeftBounds = 1;
                    int mCanvasTopBounds = 1;
                    if (mCanvasLeftBounds < xPos && xPos < mCanvasRightBounds && mCanvasTopBounds < yPos && yPos < mCanvasBottomBounds) {
                        if (mShapeToMove instanceof Circle) {
                            ((Circle) mShapeToMove).setCenterX(xPos);
                            ((Circle) mShapeToMove).setCenterY(yPos);
                        } else if (mShapeToMove instanceof Rectangle) {
                            int left = ((Rectangle) mShapeToMove).getLeft();
                            int right = ((Rectangle) mShapeToMove).getRight();
                            int top = ((Rectangle) mShapeToMove).getTop();
                            int bottom = ((Rectangle) mShapeToMove).getBottom();
                            int width = Math.abs(right - left);
                            int height = Math.abs(top - bottom);
                            ((Rectangle) mShapeToMove).setLeft(xPos - (width / 2));
                            ((Rectangle) mShapeToMove).setRight(xPos + (width / 2));
                            ((Rectangle) mShapeToMove).setTop(yPos - (height / 2));
                            ((Rectangle) mShapeToMove).setBottom(yPos + (height / 2));
                        } else if (mShapeToMove instanceof Ellipse) {
                            int left = ((Ellipse) mShapeToMove).getLeft();
                            int right = ((Ellipse) mShapeToMove).getRight();
                            int top = ((Ellipse) mShapeToMove).getTop();
                            int bottom = ((Ellipse) mShapeToMove).getBottom();
                            int width = Math.abs(right - left);
                            int height = Math.abs(top - bottom);
                            ((Ellipse) mShapeToMove).setLeft(xPos - (width / 2));
                            ((Ellipse) mShapeToMove).setRight(xPos + (width / 2));
                            ((Ellipse) mShapeToMove).setTop(yPos - (height / 2));
                            ((Ellipse) mShapeToMove).setBottom(yPos + (height / 2));
                        } else if (mShapeToMove instanceof InputText) {
                            ((InputText) mShapeToMove).setxLocation(xPos);
                            ((InputText) mShapeToMove).setyLocation(yPos);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mCurrentOperation == Constants.OPERATION_DRAW_PENCIL) {
                    mShapeList.add(new PathStore(mBrushPath, mBrushPaint.getColor(), (int) mBrushPaint.getStrokeWidth()));
                    mBrushPath = new Path();
                } else if (mCurrentOperation == Constants.OPERATION_ERASE) {
                    mShapeList.add(new PathStore(mBrushPath, mEraserPaint.getColor(), (int) mEraserPaint.getStrokeWidth()));
                    mBrushPath = new Path();
                } else if (mCurrentOperation == Constants.OPERATION_MOVE_VIEW) {
                    mCurrentOperation = mStorePrevOperationOnLongPress;
                    mShapeList.remove(mIndexOfViewInListToMove);
                    mShapeList.add(mShapeToMove);
                    mIndexOfViewInListToMove = -1;
                    mShapeToMove = null;
                }
                break;
        }
        invalidate();
        return true;
    }

    public void drawCircle(int radius) {
        mCircleRadius = radius;
    }

    public void drawRectangle(int width, int height) {
        mRectangleWidth = width;
        mRectangleHeight = height;
    }

    public void createTextBox(String text, int textSize) {
        mTextBoxText = text;
        mTextBoxSize = textSize;
    }

    public void changeFillStyle(int style) {
        switch (style) {
            case Constants.PAINT_STYLE_FILL:
                mBrushPaint.setStyle(Paint.Style.FILL);
                break;
            case Constants.PAINT_STYLE_STROKE:
                mBrushPaint.setStyle(Paint.Style.STROKE);
                break;
        }
    }

    public void changeBrushStroke(int stroke) {
        mBrushPaint.setStrokeWidth(stroke);
        mEraserPaint.setStrokeWidth(stroke * 2);
    }

    public void changeBrushColor(String col) {
        mBrushPaint.setColor(Color.parseColor(col));
    }

    public int getCurrentOperation() {
        return mCurrentOperation;
    }

    public void setCurrentOperation(int operation) {
        if (operation == Constants.OPERATION_DRAW_PENCIL) {
            changeFillStyle(Constants.PAINT_STYLE_STROKE);
        }
        mCurrentOperation = operation;
    }

    public void clearCompleteCanvas() {
        mShapeList.clear();
        invalidate();
    }

    public String getBrushColor() {
        return Integer.toHexString(mBrushPaint.getColor()).toUpperCase().substring(2);
    }

    public void undoPreviousOperation() {
        if (mShapeList != null && !mShapeList.isEmpty()) {
            mShapeList.remove(mShapeList.size() - 1);
        } else {
            mCanvasBackground = null;
        }
        invalidate();
    }

    public class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private Context mDetectorContext;
        private int xTouchPos;
        private int yTouchPos;

        @Override
        public void onLongPress(MotionEvent e) {
            xTouchPos = (int) e.getX();
            yTouchPos = (int) e.getY();
            if (mShapeList != null && !mShapeList.isEmpty()) {
                Object obj = mShapeList.get(mShapeList.size() - 1);
                if (obj instanceof Circle) {
                    Circle cir = (Circle) obj;
                    if (isPointInCircle(xTouchPos, yTouchPos, cir)) {
                        mStorePrevOperationOnLongPress = mCurrentOperation;
                        mCurrentOperation = Constants.OPERATION_MOVE_VIEW;
                        mVibrator.vibrate(200);
                        mIndexOfViewInListToMove = mShapeList.size() - 1;
                        mShapeToMove = (Circle) mShapeList.get(mIndexOfViewInListToMove);
                    }
                } else if (obj instanceof Rectangle) {
                    Rectangle rect = (Rectangle) obj;
                    if (isPointInRectangle(xTouchPos, yTouchPos, rect)) {
                        mStorePrevOperationOnLongPress = mCurrentOperation;
                        mCurrentOperation = Constants.OPERATION_MOVE_VIEW;
                        mVibrator.vibrate(200);
                        mIndexOfViewInListToMove = mShapeList.size() - 1;
                        mShapeToMove = (Rectangle) mShapeList.get(mIndexOfViewInListToMove);
                    }
                } else if (obj instanceof Ellipse) {
                    Ellipse ellipse = (Ellipse) obj;
                    if (isPointInOval(xTouchPos, yTouchPos, ellipse)) {
                        mStorePrevOperationOnLongPress = mCurrentOperation;
                        mCurrentOperation = Constants.OPERATION_MOVE_VIEW;
                        mVibrator.vibrate(200);
                        mIndexOfViewInListToMove = mShapeList.size() - 1;
                        mShapeToMove = (Ellipse) mShapeList.get(mIndexOfViewInListToMove);
                    }
                } else if (obj instanceof InputText) {
                    InputText text = (InputText) obj;
                    if (isPointInTextBox(xTouchPos, yTouchPos, text)) {
                        mStorePrevOperationOnLongPress = mCurrentOperation;
                        mCurrentOperation = Constants.OPERATION_MOVE_VIEW;
                        mVibrator.vibrate(200);
                        mIndexOfViewInListToMove = mShapeList.size() - 1;
                        mShapeToMove = (InputText) mShapeList.get(mIndexOfViewInListToMove);
                    }
                }
            }
        }

        // Переопределённый метод onDoubleTap, который используется для изменения при двойном нажатии.
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            xTouchPos = (int) e.getX();
            yTouchPos = (int) e.getY();
            if (mShapeList != null && !mShapeList.isEmpty()) {
                Object obj = mShapeList.get(mShapeList.size() - 1);
                if (obj instanceof Circle) {
                    Circle cir = (Circle) obj;
                    if (isPointInCircle(xTouchPos, yTouchPos, cir)) {
                        DrawingBoard.openColorDrawer();
                        mCurrentOperation = Constants.OPERATION_FILL_VIEW;
                    }
                } else if (obj instanceof Rectangle) {
                    Rectangle rect = (Rectangle) obj;
                    if (isPointInRectangle(xTouchPos, yTouchPos, rect)) {
                        DrawingBoard.openColorDrawer();
                        mCurrentOperation = Constants.OPERATION_FILL_VIEW;
                    }
                } else if (obj instanceof Ellipse) {
                    Ellipse ellipse = (Ellipse) obj;
                    if (isPointInOval(xTouchPos, yTouchPos, ellipse)) {
                        DrawingBoard.openColorDrawer();
                        mCurrentOperation = Constants.OPERATION_FILL_VIEW;
                    }
                } else if (obj instanceof InputText) {
                    InputText text = (InputText) obj;
                    if (isPointInTextBox(xTouchPos, yTouchPos, text)) {
                        DrawingBoard.openColorDrawer();
                        mCurrentOperation = Constants.OPERATION_FILL_VIEW;
                    }
                }
            }
            return true;
        }
    }

    // Метод isPointInCircle, который проверяет, находится ли точка, к которой прикоснулся пользователь,
    // внутри круга или нет.
    public boolean isPointInCircle(int xTouch, int yTouch, Circle circle) {
        int xCenter = circle.getCenterX();
        int yCenter = circle.getCenterY();
        double distanceOfPointFromCenter;
        distanceOfPointFromCenter = Math.sqrt(Math.pow(xCenter - xTouch, 2) + Math.pow(yCenter - yTouch, 2));
        return !(distanceOfPointFromCenter > (circle.getRadius()));
    }

    // Метод isPointInRectangle, который проверяет, находится ли точка, к которой прикоснулся пользователь,
    // внутри прямоугольника или нет.
    public boolean isPointInRectangle(int xTouch, int yTouch, Rectangle rectangle) {
        int top = rectangle.getTop();
        int bottom = rectangle.getBottom();
        int left = rectangle.getLeft();
        int right = rectangle.getRight();
        if (left < xTouch && xTouch < right) {
            return top < yTouch && yTouch < bottom;
        }
        return false;
    }

    // Метод isPointInOval, который проверяет, находится ли точка, к которой прикоснулся пользователь,
    // внутри овала или нет.
    public boolean isPointInOval(int xTouch, int yTouch, Ellipse ellipse) {
        int top = ellipse.getTop();
        int bottom = ellipse.getBottom();
        int left = ellipse.getLeft();
        int right = ellipse.getRight();
        double relativeX = (((double) left + right) / 2) - xTouch;
        double relativeY = (((double) top + bottom) / 2) - yTouch;
        double majorAxis = (double) Math.abs(right - left) / 2;
        double minorAxis = (double) Math.abs(bottom - top) / 2;
        return (Math.pow(relativeX, 2) / Math.pow(majorAxis, 2)) + (Math.pow(relativeY, 2) / Math.pow(minorAxis, 2)) < 1;
    }

    // Метод isPointInTextBox, который проверяет, находится ли точка, к которой прикоснулся пользователь,
    // внутри поля для текста или нет.
    public boolean isPointInTextBox(int xTouch, int yTouch, InputText textBox) {
        int width = textBox.getTextWidth();
        int height = textBox.getTextHeight();
        int xPosition = textBox.getxLocation();
        int yPosition = textBox.getyLocation();
        int top = Math.abs(yPosition - height);
        int right = Math.abs(xPosition + width);
        if (xPosition < xTouch && xTouch < right) {
            return top < yTouch && yTouch < yPosition;
        }
        return false;
    }

    // Метод applyColorToView, который нужен для привязки выбранного пользователем цвета к объекту,
    // для которого было обнаружено двойное касание.
    public void applyColorToView() {
        if (mCurrentOperation == Constants.OPERATION_FILL_VIEW) {
            Object obj = mShapeList.get(mShapeList.size() - 1);
            if (obj instanceof Circle) {
                Circle circle = (Circle) obj;
                circle.setCircleColor(mBrushPaint.getColor());
            } else if (obj instanceof Rectangle) {
                Rectangle rect = (Rectangle) obj;
                rect.setColor(mBrushPaint.getColor());
            } else if (obj instanceof Ellipse) {
                Ellipse ellipse = (Ellipse) obj;
                ellipse.setColor(mBrushPaint.getColor());
            } else if (obj instanceof InputText) {
                InputText text = (InputText) obj;
                text.setTextColor(mBrushPaint.getColor());
            }
            invalidate();
            mCurrentOperation = Constants.OPERATION_NO_OPERATION;
        }
    }

    public void setCanvasBackground(Bitmap background) {
        mCanvasBackground = background;
        invalidate();
    }
}