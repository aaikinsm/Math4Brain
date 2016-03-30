package com.blackstar.math4brain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MultiplayerActivity extends Activity{
	
	Equation eq;
	GameSettings gSettings;
	TextView scoreP1, scoreP2, showP1Eq, showP2Eq, p2b1, p2b2, p2b3, p2b4;
	TextView p1b1, p1b2,p1b3,p1b4;
	ProgressBar prog1, prog2;
	MediaPlayer mp3Correct, mp3Ding, mp3Over;
	ImageView block1, block2;
	Vibrator vb;
	int maxQuestions=10, score1=0, score2=0, count, FILESIZE=25;
	boolean gameOver = false;
	String FILENAME="m4bfile1";
	Handler mHandler = new Handler();
	Runnable countDown;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.multiplayer);
		}catch(RuntimeException e){finish();}
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        showP1Eq = (TextView) findViewById(R.id.textViewP1Equation);
        showP2Eq = (TextView) findViewById(R.id.textViewP2Equation);
        scoreP1 = (TextView) findViewById(R.id.textViewP1Score);
        scoreP2 = (TextView) findViewById(R.id.textViewP2Score);
        prog1 = (ProgressBar) findViewById(R.id.progressBar1);
        prog2 = (ProgressBar) findViewById(R.id.progressBar2);
        p1b1 = (TextView) findViewById(R.id.p1b1);
        p1b2 = (TextView) findViewById(R.id.p1b2);
        p1b3 = (TextView) findViewById(R.id.p1b3);
        p1b4 = (TextView) findViewById(R.id.p1b4);
        p2b1 = (TextView) findViewById(R.id.p2b1);
        p2b2 = (TextView) findViewById(R.id.p2b2);
        p2b3 = (TextView) findViewById(R.id.p2b3);
        p2b4 = (TextView) findViewById(R.id.p2b4);
        mp3Correct = MediaPlayer.create(this, R.raw.correct);
        mp3Ding = MediaPlayer.create(this, R.raw.ding);
        mp3Over = MediaPlayer.create(this, R.raw.gameover);
        block1 = (ImageView) findViewById(R.id.imageViewBlock1);
        block2 = (ImageView) findViewById(R.id.imageViewBlock2);
        gSettings = new GameSettings();
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Typeface myTypeface2 = Typeface.createFromAsset(getAssets(), "fawn.ttf");
        showP1Eq.setTypeface(myTypeface2);
        showP2Eq.setTypeface(myTypeface2);
        scoreP1.setTypeface(myTypeface2);
        scoreP2.setTypeface(myTypeface2);
        p1b1.setTypeface(myTypeface2);
        p1b2.setTypeface(myTypeface2);
        p1b3.setTypeface(myTypeface2);
        p1b4.setTypeface(myTypeface2);
        p2b1.setTypeface(myTypeface2);
        p2b2.setTypeface(myTypeface2);
        p2b3.setTypeface(myTypeface2);
        p2b4.setTypeface(myTypeface2);
        
        class player1 extends AsyncTask<String, String, String> {
			@Override
			protected String doInBackground(String... arg0) {
				p1b1.setOnTouchListener (new View.OnTouchListener(){
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						checkAnswer(1, (String)p1b1.getText());
						return false;
					}
		        });
		        p1b2.setOnTouchListener (new View.OnTouchListener(){
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						checkAnswer(1, (String)p1b2.getText());
						return false;
					}
		        });
		        p1b3.setOnTouchListener (new View.OnTouchListener(){
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						checkAnswer(1, (String)p1b3.getText());
						return false;
					}
		        });
		        p1b4.setOnTouchListener (new View.OnTouchListener(){
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						checkAnswer(1, (String)p1b4.getText());
						return false;
					}
		        });
		        return null;
			}
        }
        new player1().execute();
        
        class player2 extends AsyncTask<String, String, String> {
			@Override
			protected String doInBackground(String... arg0) {
				p2b1.setOnTouchListener (new View.OnTouchListener(){
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						checkAnswer(2, (String)p2b1.getText());
						return false;
					}
		        });
		        p2b2.setOnTouchListener (new View.OnTouchListener(){
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						checkAnswer(2, (String)p2b2.getText());
						return false;
					}
		        });
		        p2b3.setOnTouchListener (new View.OnTouchListener(){
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						checkAnswer(2, (String)p2b3.getText());
						return false;
					}
		        });
		        p2b4.setOnTouchListener (new View.OnTouchListener(){
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						checkAnswer(2, (String)p2b4.getText());
						return false;
					}
		        });
				return null;
			}
        }
        new player2().execute();
        
        //get user settings then create equation 
        try {
        	String[] gFile = new String[FILESIZE];
        	FileInputStream fi = openFileInput(FILENAME);
			Scanner in = new Scanner(fi);
			int i = 0;
			while(in.hasNext()){
				gFile[i] = in.next();
				i++;
			}
			gSettings.equationType = Integer.parseInt(gFile[1]);
			gSettings.sound = Integer.parseInt(gFile[3]);
			gSettings.difficulty = Integer.parseInt(gFile[5]);
			gSettings.vibrate= Integer.parseInt(gFile[17]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        eq = new Equation(gSettings.equationType, gSettings.difficulty, this);
        next();
        
    }
    public void checkAnswer(int p, String answer){
    	if (answer.equals(this.getString(R.string.menu))){
    		finish();
    	}
    	else if (answer.equals(this.getString(R.string.play_again))){
    		startActivity(new Intent("android.intent.action.MULTIPLAYER"));
    		finish();
    	}
    	else if (!answer.equals("")){
    	int ans = Integer.parseInt(answer);
    	if (p==1){
    		if (block1.getVisibility()!=View.VISIBLE){
	    		if (ans==eq.ans){ 
	    			score1 +=1;
	    			//Quick fix
	    			try{
	    			if(gSettings.sound==1) mp3Correct.start();
	    			}catch(Exception E){E.printStackTrace();}
	    			checkWin();
	    			next();	
	    		}
	    		else{ 
	    			score1 -=1;
	    			if(gSettings.vibrate==1)vb.vibrate(100);
	    			block1.setVisibility(View.VISIBLE);
	    		}
	    		scoreP1.setText(this.getString(R.string.score)+": "+ score1);
    		}
    	}else{
    		if (block2.getVisibility()!=View.VISIBLE){
	    		if (ans==eq.ans){ 
	    			score2 +=1;
	    			//Quick fix
	    			try{
	    			if(gSettings.sound==1) mp3Correct.start();
	    			}catch(Exception E){E.printStackTrace();}
	    			checkWin();
	    			next();	
	    		}
	    		else{ 
	    			score2 -=1;
	    			if(gSettings.vibrate==1)vb.vibrate(100);
	    			block2.setVisibility(View.VISIBLE);
	    		}
	    		scoreP2.setText(this.getString(R.string.score)+": "+ score2);
    		}
    	}
    	prog1.setProgress(100-((score1*100)/maxQuestions));
    	prog2.setProgress((score2*100)/maxQuestions);
    	if (block1.getVisibility()==View.VISIBLE && block2.getVisibility()==View.VISIBLE) next();
    	
    	}
    }
    
    public void checkWin(){
    	//gameOver = false;
    	if(score1==maxQuestions){
    		showP1Eq.setText(R.string.you_win);
    		showP2Eq.setText(R.string.you_lose);
    		gameOver=true;
    	}
    	if(score2==maxQuestions){
    		showP2Eq.setText(R.string.you_win);
    		showP1Eq.setText(R.string.you_lose);
    		gameOver=true;
    	}
    	if(gameOver){
    		p2b1.setVisibility(View.INVISIBLE);
    		p2b2.setVisibility(View.INVISIBLE);
    		p1b1.setVisibility(View.INVISIBLE);
    		p1b2.setVisibility(View.INVISIBLE);
    		p2b3.setText(R.string.play_again);
    		p2b4.setText(R.string.menu);
    		p1b3.setText(R.string.play_again);
    		p1b4.setText(R.string.menu);
    		try{
    			if(gSettings.sound==1) mp3Over.start();
  			}catch(Exception E){E.printStackTrace();}
    	}
    }
    
    public void next(){
    	if (!gameOver){
	    	eq.createNew();
	    	block1.setVisibility(View.INVISIBLE); block2.setVisibility(View.INVISIBLE); 
	    	count =3;
	    	p1b1.setText(""); p2b1.setText("");
	        p1b2.setText(""); p2b2.setText("");
	        p1b3.setText(""); p2b3.setText("");
	        p1b4.setText(""); p2b4.setText("");

	        //Decrement input timer an check if input is correct
	        countDown = new Runnable() {
	          	@Override
				public void run() {
	          		showP1Eq.setText(count+"");
	                showP2Eq.setText(count+"");
	                count--;
	          		if (count <0){
	          			if(gSettings.sound==1){ 
	          				//Quick fix try-catch
	          				try{
	          				mp3Ding.start();
	          				}catch(Exception E){E.printStackTrace();}
	          			}
				        showP1Eq.setText(eq.getEquation());
				        showP2Eq.setText(eq.getEquation());
				        
				        int min = eq.ans-10, max = eq.ans+10, ans1, ans2, ans3, ans4, num;
				        ans1 = ( min + (int) ( Math.random()*(max - min) ) );
				        ans2 = ( min + (int) ( Math.random()*(max - min) ) );
				        ans3 = ( min + (int) ( Math.random()*(max - min) ) );
				        ans4 = ( min + (int) ( Math.random()*(max - min) ) );
				        p1b1.setText(ans1+""); p2b1.setText(ans1+"");
				        p1b2.setText(ans2+""); p2b2.setText(ans2+"");
				        p1b3.setText(ans3+""); p2b3.setText(ans3+"");
				        p1b4.setText(ans4+""); p2b4.setText(ans4+"");
				        
				        num = 1 + (int)(Math.random()*3.5);
				        
				        switch (num) {
					        case 1:  p2b1.setText(eq.getAnswer()); p1b1.setText(eq.getAnswer()); break;
					        case 2:  p2b2.setText(eq.getAnswer()); p1b2.setText(eq.getAnswer()); break;
					        case 3:  p2b3.setText(eq.getAnswer()); p1b3.setText(eq.getAnswer()); break;
					        case 4:  p2b4.setText(eq.getAnswer()); p1b4.setText(eq.getAnswer()); break;
				        }
				        mHandler.removeCallbacks(this);
	          		}else mHandler.postDelayed(this,500);
	        	}
	        };
	        mHandler.removeCallbacks(countDown);            
			mHandler.postDelayed(countDown, 100);
	    }
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(countDown);
	}
}