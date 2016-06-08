package com.blackstar.math4brain;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class InitialEvaluationActivity extends Activity{
	Handler mHandler;
	Runnable mUpdateTimer, gotInput;
	MediaPlayer mp3Tick;
	GameSettings gSettings;
	int displaySecs, FILESIZE =25, e1=0,e2=0,e3=0,e4=0,hintSleep, diff=2, numEqn=0;
	double startTime = 0, nextTime=0, time=0, setTime=90;
	Equation eq;
	boolean e1a=true, e2a=true,e3a=true,e4a=true;
    ProgressBar progress;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mathquestion);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final TextView showEq = (TextView) findViewById(R.id.textViewEquation);
        final TextView clock = (TextView) findViewById(R.id.textViewTimer);
        final TextView showIn = (TextView) findViewById(R.id.textViewInput);
        final TextView result = (TextView) findViewById(R.id.textViewResult);
        final TextView info = (TextView) findViewById(R.id.textViewInfo);
        final ImageButton b0 = (ImageButton) findViewById(R.id.button0);
        final ImageButton b1 = (ImageButton) findViewById(R.id.button1);
        final ImageButton b2 = (ImageButton) findViewById(R.id.button2);
        final ImageButton b3 = (ImageButton) findViewById(R.id.button3);
        final ImageButton b4 = (ImageButton) findViewById(R.id.button4);
        final ImageButton b5 = (ImageButton) findViewById(R.id.button5);
        final ImageButton b6 = (ImageButton) findViewById(R.id.button6);
        final ImageButton b7 = (ImageButton) findViewById(R.id.button7);
        final ImageButton b8 = (ImageButton) findViewById(R.id.button8);
        final ImageButton b9 = (ImageButton) findViewById(R.id.button9);
        final ImageButton pass = (ImageButton) findViewById(R.id.buttonPass);
        final ImageButton clear = (ImageButton) findViewById(R.id.buttonClr);
        final MediaPlayer mp3Correct = MediaPlayer.create(this, R.raw.correct);
        final MediaPlayer mp3Over = MediaPlayer.create(this, R.raw.gameover);
        final FrameLayout numPad = (FrameLayout) findViewById(R.id.frameLayoutNumPad);
		progress = (ProgressBar) findViewById(R.id.progressBarBlue);
        mp3Tick = MediaPlayer.create(this, R.raw.ticktok);
        gSettings = new GameSettings();       
        final Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final String FILENAME = "m4bfile1";
        mHandler = new Handler();
        final String[] gFile = new String[FILESIZE];
        Typeface myTypeface2 = Typeface.createFromAsset(getAssets(), "fawn.ttf");
        showEq.setTypeface(myTypeface2);
        showIn.setTypeface(myTypeface2);
        result.setTypeface(myTypeface2);
        info.setTypeface(myTypeface2);
        progress.setProgress(0);
		progress.setVisibility(View.VISIBLE);
		clock.setVisibility(View.GONE);


		gSettings.level=1;
		gSettings.sound=1;
		gSettings.equationType = 1;
		gSettings.difficulty = 1;

        eq = new Equation(gSettings.equationType, gSettings.difficulty, this);
        

	        //Initial level info Dialog box
        	final Dialog dialog = new Dialog(this);
        	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
			dialog.setContentView(R.layout.dialogbox);
			TextView title = (TextView) dialog.findViewById(R.id.textViewTitle);
			title.setVisibility(View.VISIBLE);
			title.setText(R.string.evaluation);
			TextView txt = (TextView) dialog.findViewById(R.id.textViewMsg);
			txt.setText(R.string.evaluation_msg);
			dialog.setCancelable(false);
			Button dialogButton = (Button) dialog.findViewById(R.id.button1);
			dialogButton.setVisibility(View.VISIBLE);
			dialogButton.setText(R.string.start);
			dialogButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					gSettings.start = true;
					startTime = System.currentTimeMillis();
					eq.createNew();
					showEq.setText(eq.getEquation());
					hintSleep = 0;
					dialog.dismiss();
				}
			});
			dialog.show();
        
        showEq.setText(R.string.press_start_to_begin);
        showIn.setText("");

		//Tutorial Dialog box
		final Dialog dialogT = new Dialog(this);
		dialogT.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogT.setContentView(R.layout.dialogbox);
		TextView titleT = (TextView) dialogT.findViewById(R.id.textViewTitle);
		titleT.setVisibility(View.GONE);
		TextView txtT = (TextView) dialogT.findViewById(R.id.textViewMsg);
		txtT.setVisibility(View.GONE);
		LinearLayout tutorial = (LinearLayout) dialogT.findViewById(R.id.tutorialLayout);
		tutorial.setVisibility(View.VISIBLE);
		Button dialogButtonT = (Button) dialogT.findViewById(R.id.button1);
		dialogButtonT.setVisibility(View.VISIBLE);
		dialogButtonT.setText(R.string.ok);
		dialogButtonT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogT.dismiss();
			}
		});
 
        
        //Decrement game timer and check if time is up
        mUpdateTimer = new Runnable() {   
        	@Override
			public void run() {
				if(gSettings.clock==80){
					showIn.setText("");
					gSettings.inputTimer=-1;
					dialogT.show();
				}
        		if (gSettings.start){
        			//set timer
        			nextTime = System.currentTimeMillis();
        			if(startTime!=nextTime){
        				String sTim = (nextTime-startTime)+"";
        				if (sTim.length()<5) time=10;
        				else{
        					try{
        						time = setTime - Double.parseDouble(sTim.substring(0,sTim.length()-5)+"."+sTim.substring(sTim.length()-5,sTim.length()-4));
        					}catch(NumberFormatException e){
        						Toast.makeText(getApplicationContext(), "Sorry this language is not supported yet. Supported Languages are; English, French, Spanish, and German",Toast.LENGTH_SHORT).show();
        						finish();
        					}
        				}
        				gSettings.clock = time;
        			}

        			try{
	        			if(gSettings.sound==1 && !mp3Tick.isPlaying() && Double.parseDouble(gSettings.getClock())<10){
	        	        	mp3Tick.start();
	        	        	mp3Tick.setLooping(true);
	        			}
        			}
        			catch(Exception e){e.printStackTrace();}
        		}
        		//if time is up
        		if(gSettings.clock==0){
        			try{
        				if(gSettings.sound==1) mp3Over.start();
        			}catch(Exception E){E.printStackTrace();}
        			if(gSettings.vibrate==1)vb.vibrate(1000);
            		showEq.setText(R.string.time_is_up);
            		gSettings.timeUp = true;
            		numPad.setVisibility(View.GONE);
            		showIn.setVisibility(View.GONE);

            		//read
            		try {
						FileInputStream fi = openFileInput(FILENAME);
						Scanner in = new Scanner(fi);
						int i = 0;
						while(in.hasNext()){
							gFile[i] = in.next();
							i++;
						}
						in.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

					//modify
					gFile[1]="1";
					if(e2a&&diff>=3)gFile[1]+="2";
					if(e3a&&diff>=5)gFile[1]+="3";
					if(e4a&&diff>=5)gFile[1]+="4";
					gFile[5]=""+(gSettings.difficulty);

					//write
					try {
						String data="";
						for(int i=0; i<FILESIZE; i++) data+= gFile[i]+" ";
						OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0));
						out.write(data);
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					//next
					Intent i = new Intent(getApplicationContext(),UserActivity.class);
					i.putExtra("setName",true);
					startActivity(i);
					finish();
            	}

        		else{
            		mHandler.postDelayed(this,50);
            		//result display timer
            		if (displaySecs > 0)displaySecs-=1;
            		else result.setText("");
            	}
        	}
        };
        mHandler.removeCallbacks(mUpdateTimer);            
		mHandler.postDelayed(mUpdateTimer, 100);
        
		//Decrement input timer an check if input is correct
        gotInput = new Runnable() {
        	@Override
			public void run() {
        		if(gSettings.timeUp){
        			result.setTextColor(Color.rgb(0,0,0));
        			try{
        			mp3Tick.stop();
        			}catch(Exception E){E.printStackTrace();}
        		}else{
	        		gSettings.inputTimer -= 1;
	        		if(eq.getAnswer().length()<2) gSettings.inputTimer -= 1;
	        		if(showIn.getText().equals(eq.getAnswer())){
	        			//correct
	        			try{
	        			if(gSettings.sound==1) mp3Correct.start();
	        			}catch(Exception E){E.printStackTrace();}

						numEqn++;
						getNextEquation(true);

	        			gSettings.score +=1;
	        			result.setText("");
	        	        showEq.setText(eq.getEquation());
	        	        showIn.setText("");
	        	        gSettings.inputTimer = -1;
						hintSleep=0;
	        		}else{
	        			//wrong
		        		if (gSettings.inputTimer == 0 || gSettings.inputTimer == 1){
		        				displaySecs = 40;
		        				result.setTextColor(Color.rgb(200,0,0));
			        			result.setText("X");
			        			if(gSettings.vibrate==1)vb.vibrate(500);
			        			showIn.setText("");
			        			gSettings.score-=1;
		        		}
	        		}
	        	}

				//hint display timer
				hintSleep++;
				if(hintSleep==80 && !gSettings.timeUp){
					displaySecs = 40;
					result.setTextColor(Color.rgb(0,0,200));
					result.setText(eq.getHint());
				}

        		mHandler.postDelayed(this,100);
        	}
        };
        mHandler.removeCallbacks(gotInput);            
		mHandler.postDelayed(gotInput, 100);
		
		
        //setup buttons
		b0.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"0");
        		gSettings.inputTimer= 10;
        	}
        });
        b1.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"1");
        		gSettings.inputTimer= 10;
        	}
        });
        b2.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"2");
        		gSettings.inputTimer= 10;
        	}
        });
        b3.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"3");
        		gSettings.inputTimer= 10;
        	}
        });
        b4.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"4");
        		gSettings.inputTimer= 10;
        	}
        });
        b5.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"5");
        		gSettings.inputTimer= 10;
        	}
        });
        b6.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"6");
        		gSettings.inputTimer= 10;
        	}
        });
        b7.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"7");
        		gSettings.inputTimer= 10;
        	}
        });
        b8.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"8");
        		gSettings.inputTimer= 10;
        	}
        });
        b9.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"9");
        		gSettings.inputTimer= 10;
        	}
        });
        
        clear.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText("");
        		gSettings.inputTimer=-1;
				//info.setText(gSettings.difficulty + ":" + e1a + "," + e2a + "," + e3a + "," + e4a + ":" + numEqn);
        	}
        });
        pass.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if (!gSettings.timeUp){
	        		result.setText("");
					numEqn++;
	        		getNextEquation(false);
	                showEq.setText(eq.getEquation());
	                showIn.setText("");
	        		gSettings.score-=1;
	        		if(gSettings.vibrate==1)vb.vibrate(500);
					hintSleep = 0;
        		}
        	}
        });

    }

	public void getNextEquation(boolean correct){
		if(correct){
			if(gSettings.equationType==2)e2++;
			else if(gSettings.equationType==3)e3++;
			else if(gSettings.equationType==4)e4++;
		}else{
			if(gSettings.equationType==2)e2--;
			else if(gSettings.equationType==3)e3--;
			else if(gSettings.equationType==4)e4--;
		}

		if(numEqn %5==0){
			if (gSettings.score < 2 && diff >= 4) {
				diff -= 2;
			}
			else diff++;
			gSettings.score = 0;
			if (e2 < -1) e2a = false;
			if (e3 < -1) e3a = false;
			if (e4 < -1) e4a = false;
		}

		boolean proceed = false;
		while(!proceed) {
			if (gSettings.equationType == 4) {
				gSettings.equationType = 1;
				proceed = e1a;
			} else if (gSettings.equationType == 1) {
				gSettings.equationType++;
				proceed = e2a;
				if(diff<3) proceed=false;
			} else if (gSettings.equationType == 2) {
				gSettings.equationType++;
				proceed = e3a;
				if(diff<5) proceed=false;
			} else if (gSettings.equationType == 3) {
				gSettings.equationType++;
				proceed = e4a;
				if(diff<5) proceed=false;
			}
		}
		gSettings.difficulty=diff/2;
		if(gSettings.difficulty>5) gSettings.difficulty=5;
		eq.diff=gSettings.difficulty;
		eq.eqType = gSettings.equationType;
		eq.eqType2 = gSettings.equationType;
		eq.createNew();
        progress.setProgress(100- (int)(Double.parseDouble(gSettings.getClock()) * 100 / setTime));
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimer);
		mHandler.removeCallbacks(gotInput);
        //Quick fix try-catch
        try{
        mp3Tick.stop();
        }catch(Exception E){E.printStackTrace();}
    }
	
	@Override
    public void onPause() {
		super.onPause();
        try{
        if(mp3Tick.isPlaying()) mp3Tick.stop();
        }catch(Exception E){E.printStackTrace();}
    }
	
}