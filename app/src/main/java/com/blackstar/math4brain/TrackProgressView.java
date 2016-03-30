package com.blackstar.math4brain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TrackProgressView extends View{
	
	Paint textP = new Paint(), pointP = new Paint(), point2P = new Paint(), lineP = new Paint();
	long[][] dataPoints = new long[][]{{1500,0},{2344,1},{2344,0},{3000,0},{2636,1},{4000,1},{5097,0}};
	long max = 5098;
	long min = 1500;
	int length = dataPoints.length; 
	
	public TrackProgressView(Context context) {
		super(context);
	}
	
	public TrackProgressView(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	
	public TrackProgressView(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs,defStyle);
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		pointP.setColor(Color.GREEN);
		point2P.setColor(Color.RED);
		point2P.setStrokeWidth(2);
		point2P.setStyle(Paint.Style.STROKE);
		lineP.setColor(Color.BLUE);
		lineP.setStrokeWidth(4);
		lineP.setAlpha(75);
		textP.setAlpha(50);
		
		int height = getMeasuredHeight()-20;
		int width = getMeasuredWidth();
		
		canvas.drawText(min/100+"",5,height, textP);
		canvas.drawText((min+(max-min)/2)/100+"",5,height/2, textP);
		canvas.drawText(max/100+"",5,20, textP);
		canvas.drawLine(0, height/2, canvas.getWidth(), height/2, textP);
		canvas.drawLine(0, height, canvas.getWidth(), height, textP);
		for (int i = 0; i<length; i++){
			//Log.d("testTrackC","k="+dataPoints[i][0]+" level:"+dataPoints[i][1]);
			if(dataPoints[i][1]==1 && dataPoints[i][0]!=0) {
				canvas.drawCircle(width / length * (i + 1) - 10, height + 10
						- (int) ((1.0 * dataPoints[i][0] - min) / (max - min) * height), 6, pointP);
			}
			else{
				canvas.drawCircle(width / length * (i + 1) - 10, height + 10
						- (int) ((1.0 * dataPoints[i][0] - min) / (max - min) * height), 4, point2P);
			}
			if (i!=0) canvas.drawLine(width/length*(i)-10, height+10-(int)((1.0*dataPoints[i-1][0]-min)/(max-min)*height),
					width/length*(i+1)-10, height+10-(int)((1.0*dataPoints[i][0]-min)/(max-min)*height), lineP);
		}	
	}
	
	public void update(long[][] dataT, int len){
		dataPoints = new long[365][2];
		length = len;
		int j=1, k=0;
		min = dataT[0][1]; max = dataT[0][1];
		long time, time2;
		while(j<=length){
			long days1 = 86400000;
			time = dataT[k][0];
			time2 = dataT[k+1][0];
			long elapsed = (time-time2)/days1;
			if(elapsed>length)elapsed=0;
			dataPoints[length-j][0]=dataT[k][1];
			dataPoints[length-j][1]=1;
			if (dataPoints[length-j][0]<min) min = dataPoints[length-j][0];
			if (dataPoints[length-j][0]>max) max = dataPoints[length-j][0];
			for(int i=1; i<elapsed; i++) {
				j++;
				if (j >= length) break;
				if(elapsed<length) dataPoints[length - j][0] = dataT[k][1]-((elapsed-i)*60);
				else dataPoints[length - j][0] = dataT[k][1];
				dataPoints[length - j][1] = 0;
				if (dataPoints[length-j][0]<min) min = dataPoints[length-j][0];
			}
			if(length-j>=0)dataPoints[length - j][1] = 1;
			j++; k++;
		}
		if(max==min) max+=10;
	}
}
