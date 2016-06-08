package com.blackstar.math4brain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BackgroundAnimated extends View{

    int width=100, height=100;
    public int [][] data = new int [10][3];
    Paint square = new Paint();
    int count = 0, count2 = 0;

    public BackgroundAnimated(Context context){
        super(context);
    }

    public BackgroundAnimated(Context context, AttributeSet attrs) {
        super( context, attrs );
    }

    public BackgroundAnimated(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int x, int y){
        super.onMeasure(x, y);
        setMeasuredDimension(x, y);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        /*//Display random flashing symbols
        square.setStyle(Paint.Style.FILL);
        int xpos = (int)(Math.random()*width);
        int ypos = (int)(Math.random()*height);
        square.setTextSize((int) (Math.random() * 300));
        if(xpos%4==0) canvas.drawText("+", xpos, ypos, square); //show at rand times
        else if(xpos%5==0) canvas.drawText("%", xpos, ypos, square); //show at rand times
        else if(xpos%6==0) canvas.drawText("X", xpos, ypos, square); //show at rand times
        else if(xpos%7==0) canvas.drawText("=", xpos, ypos, square); //show at rand times
        else if(xpos%9==0) canvas.drawText("/", xpos, ypos, square); //show at rand times*/

        //display touch trail
        square.setColor(Color.rgb((int) (Math.random()*255), 100, (int) (Math.random()*255)));//random color
        square.setStrokeWidth(6);
        square.setStyle(Paint.Style.STROKE);
        for(int i=0; i < 10; i++) {
            int size = 20 * (10 - i);
            square.setTextSize(size);
            square.setAlpha(100 - (i * 5)); //Diminishing alpha
            double rand = 5 * Math.random();
            data[i][1] += (rand - 1); //random vertical movement
            data[i][0] += (rand - 2); //random horizontal movement
            if (data[i][0] == 0) data[i][0] = 10000; //start off screen
            canvas.drawText("" + data[i][2], data[i][0], data[i][1], square); //Diminishing size
        }
        count2++;
        if(count2%500 != 0) invalidate();

    }

    @Override
    public boolean onTouchEvent( MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

            case MotionEvent.ACTION_MOVE:
                count++;
                if(count%5==0) {
                    for (int i = 9; i > 0; i--) {
                        data[i][0] = data[i - 1][0];
                        data[i][1] = data[i - 1][1];
                        data[i][2] = (int) (10 * Math.random());
                    }
                    data[0][0] = (int) event.getX();
                    data[0][1] = (int) event.getY();
                    invalidate();
                }
            case MotionEvent.ACTION_UP:
        }
        return true;
    }
}
