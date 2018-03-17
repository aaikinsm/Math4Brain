package com.blackstar.math4brain;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.tapjoy.TapjoyConnect;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class ChallengeActivity extends Activity{
	Handler mHandler;
	Runnable mUpdateTimer, gotInput;
	MediaPlayer mp3Tick;
	GameSettings gSettings;
	int LVL_DOWNGRADE = 1, displaySecs, FILESIZE =25;
	double startTime = 0, nextTime=0, time=0;
	ArrayList<String> speechMatches;
	private SpeechRecognizer sr;
	boolean speechActive = false, connection = true;
	ImageButton micButton;
	ProgressBar progress;
	FrameLayout numPad;
	InterstitialAd mInterstitialAd;
	String[] gFile;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mathquestion);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() == null) connection = false;
        
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
        final ImageButton pass2 = (ImageButton) findViewById(R.id.buttonPass2);
        final ImageButton clear = (ImageButton) findViewById(R.id.buttonClr);
        final ImageButton next = (ImageButton) findViewById(R.id.buttonNext);
        final ImageButton back = (ImageButton) findViewById(R.id.buttonBack);
        final MediaPlayer mp3Correct = MediaPlayer.create(this, R.raw.correct);
        final MediaPlayer mp3Wrong = MediaPlayer.create(this, R.raw.wrong);
        final MediaPlayer mp3Over = MediaPlayer.create(this, R.raw.gameover);
        final ImageView backgroundImg = (ImageView) findViewById(R.id.imageViewEqnBackground);
       numPad = (FrameLayout) findViewById(R.id.frameLayoutNumPad);
        mp3Tick = MediaPlayer.create(this, R.raw.ticktok);
        gSettings = new GameSettings();       
        final Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); 
        final String FILENAME = "m4bfile1", FILEPRO = "m4bfilePro1";
        mHandler = new Handler();
        final Equation eq;
        gFile = new String[FILESIZE];
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "digital.ttf");
        Typeface myTypeface2 = Typeface.createFromAsset(getAssets(), "fawn.ttf");
        showEq.setTypeface(myTypeface2);
        showIn.setTypeface(myTypeface2);
        result.setTypeface(myTypeface2);
        info.setTypeface(myTypeface2);
        clock.setTypeface(myTypeface);
        micButton = (ImageButton) findViewById(R.id.buttonMic);
        sr = SpeechRecognizer.createSpeechRecognizer(this);       
        sr.setRecognitionListener(new listener());
		progress = (ProgressBar) findViewById(R.id.progressBarRed);
		progress.setVisibility(View.VISIBLE);
		clock.setVisibility(View.VISIBLE);
		numPad.setVisibility(View.INVISIBLE);

		//get user level and create settings
        try {
        	int num1;
        	//if file not found/not created yet, jump to next catch block
			FileInputStream fi = openFileInput(FILENAME);
			Scanner in = new Scanner(fi);
			int i = 0;
			while(in.hasNext()){
				gFile[i] = in.next();
				i++;
			}
			int numSnd = Integer.parseInt(gFile[3]);
			num1 = Integer.parseInt(gFile[7]);
			gSettings.points = Integer.parseInt(gFile[9]);
			gSettings.vibrate= Integer.parseInt(gFile[17]);
			gSettings.level = num1; 
			
			gSettings.sound = numSnd;
			gSettings.difficulty = (int) (num1 * 0.20 + 1);
			gSettings.numOfEquations = (num1 + 9);
			gSettings.wrong = (int) -Math.abs(6 - (num1*0.4)); 
			gSettings.clock = ((int)(num1 * 0.25 + 1)*10)+30;
			if(num1==1) gSettings.clock = 60;
			if(gSettings.wrong==0) gSettings.wrong =-1;
			if(num1<=3)
				gSettings.equationType = 1;
			else if (num1<=7)
				gSettings.equationType = 12;
			else if (num1<=9)
				gSettings.equationType = 124;
			else if (num1<=11)
				gSettings.equationType = 1245;
			else
				gSettings.equationType = 12345;
			if(gFile[21]!=null && !gFile[21].equals("null")){
				gSettings.microphone= Integer.parseInt(gFile[21]);
			}
			if(gSettings.microphone==1){        	
	        	micButton.setVisibility(View.VISIBLE);
	        	pass2.setVisibility(View.VISIBLE);
	        }
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		}


        //set user selected background if PRO
		try{
			FileInputStream fip = openFileInput (FILEPRO);
			Scanner inp = new Scanner(fip);
			inp.next(); inp.next(); inp.next();
			String bgPath = inp.next();
			if (bgPath.equals("bg1")) backgroundImg.setImageResource(R.drawable.bg1);
			else if (bgPath.equals("bg2")) backgroundImg.setImageResource(R.drawable.bg2);
			else if (bgPath.equals("bg3")) backgroundImg.setImageResource(R.drawable.bg3);
			else if (bgPath.equals("default")) backgroundImg.setImageResource(R.drawable.lines_background);
			else backgroundImg.setImageURI(Uri.parse(bgPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

        eq = new Equation(gSettings.equationType, gSettings.difficulty, this);
        final double setTime = gSettings.clock;       
		int requiredPts = gSettings.level*(gSettings.level-1)*5; //required points calculation
        
		//check if user meets min requirement before proceeding
		if (requiredPts > gSettings.points){
			final Dialog dialog = new Dialog(this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.dialogbox);
			TextView txt = (TextView) dialog.findViewById(R.id.textViewMsg);
			TextView title = (TextView) dialog.findViewById(R.id.textViewTitle);
			title.setVisibility(View.VISIBLE);
			title.setText(R.string.not_enough_points);
			txt.setText(this.getString(R.string.you_need_a_min_of)+" "+requiredPts+" "+this.getString(R.string.points_to_proceed)+"\n"
            		+this.getString(R.string.you_currently_have)+" "+gSettings.points+" "+this.getString(R.string.points)+"\n"+this.getString(R.string.you_can_earn_more));
			dialog.setCancelable(false);
			Button dialogButton = (Button) dialog.findViewById(R.id.button1);
			dialogButton.setVisibility(View.VISIBLE);
			dialogButton.setText(R.string.ok);
			dialogButton.setOnClickListener (new View.OnClickListener(){
	        	@Override
				public void onClick (View v) {
	        		finish();
					dialog.dismiss();
				}
			});
			dialog.show();

        }else{        
	        //Initial level info Dialog box
        	final Dialog dialog = new Dialog(this);
        	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
			dialog.setContentView(R.layout.dialogbox);
			String e;
	        if (Math.abs(gSettings.wrong+1)== 0){
	        	e = this.getString(R.string.eqn_with_no_inc_alwd);
	        }else{
	        	e = this.getString(R.string.eqn_with_max_of)+" "+Math.abs(gSettings.wrong+1)+" "+this.getString(R.string.incorrects_allowed);
	        }
			TextView title = (TextView) dialog.findViewById(R.id.textViewTitle);
			title.setVisibility(View.VISIBLE);
			title.setText(this.getString(R.string.level)+" "+gSettings.level);
			TextView txt = (TextView) dialog.findViewById(R.id.textViewMsg);
			txt.setText(this.getString(R.string.you_have_to_complete)+" "+gSettings.numOfEquations+" "+e+" \n"+this.getString(R.string.you_have)
        			+" "+gSettings.clock+" "+this.getString(R.string.secs_to_complete_lvl));
			dialog.setCancelable(false);
			Button dialogButton = (Button) dialog.findViewById(R.id.button1);
			dialogButton.setVisibility(View.VISIBLE);
			dialogButton.setText(R.string.start);
			dialogButton.setOnClickListener (new View.OnClickListener(){
	        	@Override
				public void onClick (View v) {
					gSettings.start = true;
	        		startTime = System.currentTimeMillis();
	        		eq.createNew();
	        		showEq.setText(eq.getEquation());
	        		dialog.dismiss();
					numPad.setVisibility(View.VISIBLE);
					animate();
				}
			});
			dialog.show();
        }
        
        showEq.setText(R.string.press_start_to_begin);
        showIn.setText("");


		//check if user should take a break
		breakTime();

        //Decrement game timer and check if time is up
        mUpdateTimer = new Runnable() {   
        	@Override
			public void run() {
        		String content = getResources().getString(R.string.level)+": "+gSettings.level+" \n"+getResources().getString(R.string.completed_equations)+": "
        				+gSettings.score+"/"+gSettings.numOfEquations+" \n"+getResources().getString(R.string.number_of_inc_alwd)+": "+Math.abs(gSettings.wrong+1);
        		info.setText(content);
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
        						Toast.makeText(getApplicationContext(), "Sorry this language is not supported yet. Supported Langeages are; English, French, Spanish, and German",Toast.LENGTH_SHORT).show();
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
        		clock.setText(gSettings.getClock());
				progress.setProgress((int) (Double.parseDouble(gSettings.getClock()) * 100 / setTime));
        		//if time is up
        		if(gSettings.clock==0){
        			try{
        				if(gSettings.sound==1) mp3Wrong.start();
        			}catch(Exception E){E.printStackTrace();}
        			if(gSettings.vibrate==1)vb.vibrate(1000);
            		showEq.setText(R.string.time_is_up);
            		result.setText(getResources().getString(R.string.score)+": "+gSettings.score);
            		gSettings.timeUp = true;
            		numPad.setVisibility(View.GONE);
            		next.setVisibility(View.VISIBLE);
            		back.setVisibility(View.VISIBLE);
            		showIn.setVisibility(View.GONE);
					if(gSettings.level%5!=0) {
						try {//save level to file
							String c = "";
							gFile[7] = ((gSettings.level) - LVL_DOWNGRADE) + "";
							if ((gSettings.level) - LVL_DOWNGRADE < 1) gFile[7] = "1";
							for (int i = 0; i < FILESIZE; i++) {
								c += gFile[i] + " ";
							}
							OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME, 0));
							out.write(c);
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
            	}
        		//if goal achieved
        		else if(gSettings.numOfEquations==gSettings.score){ 
            		try{
          				if(gSettings.sound==1) mp3Over.start();
          			}catch(Exception E){E.printStackTrace();}
            		showEq.setText(getResources().getString(R.string.level)+" "+gSettings.level+" "+getResources().getString(R.string.complete));
            		result.setText(getResources().getString(R.string.score)+": "+gSettings.score);
            		gSettings.timeUp = true;
            		numPad.setVisibility(View.GONE);
            		next.setVisibility(View.VISIBLE);
            		back.setVisibility(View.VISIBLE);
            		showIn.setVisibility(View.GONE);          		
            		try {
            			String c ="";
            			gFile[7]=((gSettings.level)+1)+"";
            			for(int i = 0; i <FILESIZE; i++){
            				c += gFile[i]+" ";
            			}
            			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0));
            			out.write(c);
            			out.close();       			
                	} catch (IOException e) {
                		e.printStackTrace();
                	}
            	} 
        		//if too many errors
        		else if(gSettings.wrong==0){ 
            		showEq.setText(R.string.too_many_mistakes);
            		result.setText(getResources().getString(R.string.score)+": "+gSettings.score);
            		gSettings.timeUp = true;
            		numPad.setVisibility(View.GONE);
            		showIn.setVisibility(View.GONE);
            		next.setVisibility(View.VISIBLE);
            		back.setVisibility(View.VISIBLE);
            		try {
            			String c ="";
            			gFile[7]=((gSettings.level)-LVL_DOWNGRADE)+"";
            			if ((gSettings.level)-LVL_DOWNGRADE <1) gFile[7]="1";
            			for(int i = 0; i <FILESIZE; i++){
            				c += gFile[i]+" ";
            			}
            			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0));
            			out.write(c);
            			out.close();       			
                	} catch (IOException e) {
                		e.printStackTrace();
                	}
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
	        			gSettings.score +=1;
	        			result.setText("");
	        			eq.createNew();
	        	        showEq.setText(eq.getEquation());
	        	        showIn.setText("");
	        	        gSettings.inputTimer = -1;
	        		}else{
	        			//wrong
		        		if (gSettings.inputTimer == 0 || gSettings.inputTimer == 1){
		        				displaySecs = 40;
		        				result.setTextColor(Color.rgb(200,0,0));
			        			result.setText("X");
			        			if(gSettings.vibrate==1)vb.vibrate(500);
			        			showIn.setText("");
			        			gSettings.wrong+=1;
		        		}
	        		}
	        		
	        		//speech input
	        		if (speechActive){
	        			boolean correct = false;
	        			for(int i = 0; i<speechMatches.size();i++){
	        				if(eq.getAnswer().equals(speechMatches.get(i))){
	        					correct = true;
	        					break;
	        				}
	        			}
	        			if(correct){
	        				//correct
		        			try{
		        			if(gSettings.sound==1) mp3Correct.start();
		        			}catch(Exception E){E.printStackTrace();}
		        			gSettings.score +=1;
		        			result.setText("");
		        			eq.createNew();
		        	        showEq.setText(eq.getEquation());
		        	        showIn.setText("");
		        	        gSettings.inputTimer = -1;
		        		}else{
		        			//wrong
			        		displaySecs = 40;
			        		result.setTextColor(Color.rgb(200,0,0));
				        	result.setText("X");
				        	if(gSettings.vibrate==1)vb.vibrate(500);
				        	showIn.setText("");
				        	gSettings.wrong+=1;
		        		}
	            		speechActive = false; 
	            		
	        		}
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
        	}
        });
        pass.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if (!gSettings.timeUp){
	        		result.setText("");
	        		eq.createNew();
	                showEq.setText(eq.getEquation());
	                showIn.setText("");
	        		gSettings.wrong+=1;
	        		if(gSettings.vibrate==1)vb.vibrate(500);
        		}
        	}
        });
        
        pass2.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if (!gSettings.timeUp){
	        		result.setText("");
	        		eq.createNew();
	                showEq.setText(eq.getEquation());
	                showIn.setText("");
	        		gSettings.wrong+=1;
	        		if(gSettings.vibrate==1)vb.vibrate(500);
        		}
        	}
        });
        
        next.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		startActivity(new Intent(getApplicationContext(), ChallengeActivity.class));
        		finish();
        	}
        });
        back.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		finish();
        	}
        });
        
        micButton.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        	            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        	    sr.startListening(intent);
        	}
        });
    }
    
    
    
    class listener implements RecognitionListener{
    	String TAG = "Rec_Listener";
    	@Override
		public void onResults(Bundle results){
        	speechMatches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        	speechActive = true;
        }
    	@Override
		public void onError(int error) { 	
        	Log.d(TAG,  "error " +  error);
        	micButton.setImageResource(R.drawable.mic);
        	if(error==SpeechRecognizer.ERROR_NETWORK || error==SpeechRecognizer.ERROR_SERVER){
        		Toast.makeText(getApplicationContext(), R.string.unable_to_connect,Toast.LENGTH_SHORT).show();
        		micButton.setVisibility(View.GONE);
        	}
        }
    	@Override
		public void onReadyForSpeech(Bundle params){ 	
    		Log.d(TAG, "onReadyForSpeech"); 
    		micButton.setImageResource(R.drawable.mic_ready);
    	}
        @Override
		public void onBeginningOfSpeech(){	
        	Log.d(TAG, "onBeginningOfSpeech"); 
        	micButton.setImageResource(R.drawable.mic_wait);
        }
        @Override
		public void onEndOfSpeech() {  	
        	Log.d(TAG, "onEndofSpeech"); 
        	micButton.setImageResource(R.drawable.mic);
        }
        @Override
		public void onRmsChanged(float rmsdB){ 	
        	Log.d(TAG, "onRmsChanged"); }
        @Override
		public void onBufferReceived(byte[] buffer) { 	
        	Log.d(TAG, "onBufferReceived"); }
        @Override
		public void onPartialResults(Bundle partialResults){
        	Log.d(TAG, "onPartialResults");  }
        @Override
		public void onEvent(int eventType, Bundle params){
            Log.d(TAG, "onEvent " + eventType);}
    }


	long bTime = 0;
	TextView title;
	Runnable breakTimer;

	public void breakTime() {
		String FILEEXTRA = "m4bfileExt";
		final long TIME = 2400000;
		int maxPts = 0;
		final int MAXPTS = 300;
		//check if user has exceeded number of max points within time
		try {
			//read
			FileInputStream fi = openFileInput(FILEEXTRA);
			Scanner in = new Scanner(fi);
			bTime = Long.parseLong(in.next());
			maxPts = Integer.parseInt(in.next());
			in.close();
			if (bTime < System.currentTimeMillis()) {
				bTime = System.currentTimeMillis() + TIME;
				maxPts = Integer.parseInt(gFile[9]) + MAXPTS;
			}
			breakTimer = new Runnable() {
				@Override
				public void run() {
					int min = (int) (bTime - System.currentTimeMillis()) / 60000;
					int sec = (int) ((bTime - System.currentTimeMillis()) - (min * 60000)) / 1000;
					title.setText(min + ":" + sec);
					if (min == 0 && sec == 0) finish();

					//show Fullscreen add
					if (System.currentTimeMillis()%2==0 && mInterstitialAd != null && mInterstitialAd.isLoaded()) {
						mInterstitialAd.show();
					}

					mHandler.postDelayed(this, 100);
				}
			};
			if (maxPts < Integer.parseInt(gFile[9])) {
				final Dialog dialog = new Dialog(this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialogbox);
				dialog.setCancelable(false);
				TextView body = (TextView) dialog.findViewById(R.id.textViewMsg);
				Button dialogButton = (Button) dialog.findViewById(R.id.button1);
				dialogButton.setVisibility(View.VISIBLE);
				dialogButton.setText(R.string.close);
				title = (TextView) dialog.findViewById(R.id.textViewTitle);
				title.setVisibility(View.VISIBLE);
				int min = (int) (bTime - System.currentTimeMillis()) / 60000;
				int sec = (int) ((bTime - System.currentTimeMillis()) - (min * 60000)) / 1000;
				title.setText(min + ":" + sec);
				body.setText(R.string.breakMsg);
				mHandler.postDelayed(breakTimer, 100);
				dialogButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mHandler.removeCallbacks(breakTimer);
						finish();
						dialog.dismiss();
					}
				});

				dialog.show();

				//load admob interstitial full screen ad
                if(System.currentTimeMillis() %2==0 && connection){
                    mInterstitialAd = new InterstitialAd(this);
					mInterstitialAd.setAdUnitId("ca-app-pub-8528343456081396/2957766464");
					requestNewInterstitial();
					mInterstitialAd.setAdListener(new AdListener() {
						@Override
						public void onAdClosed() {
							//requestNewInterstitial();
						}
					});
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			try {
				OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEEXTRA, 0));
				out.write((System.currentTimeMillis() + TIME) + " " + Integer.parseInt(gFile[9]) + MAXPTS);
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		//write
		try {
			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEEXTRA, 0));
			out.write(bTime + " " + maxPts);
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	private void requestNewInterstitial() {
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice("YOUR_DEVICE_HASH")
				.build();

		mInterstitialAd.loadAd(adRequest);
	}

	public void animate(){
		Animation newAnimation = new TranslateAnimation(0,0,1100,0);
		newAnimation.setDuration(700);
		newAnimation.setInterpolator(new OvershootInterpolator());
		numPad.startAnimation(newAnimation);
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimer);
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
        mHandler.removeCallbacks(mUpdateTimer);
		mHandler.removeCallbacks(gotInput);
        finish();
    }
	
}