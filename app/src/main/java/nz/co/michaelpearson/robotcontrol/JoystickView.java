package nz.co.michaelpearson.robotcontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {
    private int xPosition = 0;
    private int yPosition = 0;
    private double centerX = 0;
    private double centerY = 0;
    private Paint mainCircle;
    private Paint button;
    private Paint horizontalLine;
    private int joystickRadius;
    private int buttonRadius;

    public JoystickView(Context context) {
        super(context);
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initJoystickView();
    }

    public JoystickView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        initJoystickView();
    }

    protected void initJoystickView() {
        TypedValue colour = new TypedValue();
        getContext().getTheme().resolveAttribute(R.color.colorAccent, colour, true);

        mainCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mainCircle.setColor(Color.WHITE);
        mainCircle.setStyle(Paint.Style.FILL_AND_STROKE);

        horizontalLine = new Paint();
        horizontalLine.setStrokeWidth(2);
        horizontalLine.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

        button = new Paint(Paint.ANTI_ALIAS_FLAG);
        button.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        button.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        xPosition = getWidth() / 2;
        yPosition = getWidth() / 2;
        int d = Math.min(xNew, yNew);
        buttonRadius = (int) (d / 2 * 0.25);
        joystickRadius = (int) (d / 2 * 0.75);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int d = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec));
        setMeasuredDimension(d, d);
    }

    private int measure(int measureSpec) {
        int result;
        // Decode the measurement specifications.
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.UNSPECIFIED) {
            // Return a default size of 200 if no bounds are specified.
            result = 200;
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            result = specSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        centerX = (getWidth()) / 2;
        centerY = (getHeight()) / 2;
        // painting the main circle
        canvas.drawCircle((int) centerX, (int) centerY, joystickRadius, mainCircle);
        // paint lines
        canvas.drawLine((float) (centerX - joystickRadius), (float) centerY, (float) (centerX + joystickRadius), (float) centerY, horizontalLine);
        canvas.drawLine((float) centerX, (float) (centerY + joystickRadius), (float) centerX, (float) (centerY - joystickRadius), horizontalLine);
        // painting the move button
        canvas.drawCircle(xPosition, yPosition, buttonRadius, button);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xPosition = (int) event.getX();
        yPosition = (int) event.getY();
        double abs = Math.sqrt((xPosition - centerX) * (xPosition - centerX) + (yPosition - centerY) * (yPosition - centerY));
        if (abs > joystickRadius) {
            xPosition = (int) ((xPosition - centerX) * joystickRadius / abs + centerX);
            yPosition = (int) ((yPosition - centerY) * joystickRadius / abs + centerY);
        }
        invalidate();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            xPosition = (int) centerX;
            yPosition = (int) centerY;
        }
        return true;
    }

    public double getJoystickX() {
        return (centerX - xPosition) / joystickRadius;
    }

    public double getJoystickY() {
        return (centerY - yPosition) / joystickRadius;
    }
}
