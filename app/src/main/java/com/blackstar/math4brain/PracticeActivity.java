package com.blackstar.math4brain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class PracticeActivity extends Activity{
	int displaySecs, hintSleep, FILESIZE = 25;
	ArrayList<String> speechMatches;
	private SpeechRecognizer sr;
	boolean speechActive = false;
	ImageButton micButton;
	Runnable gotInput;
    Handler mHandler = new Handler();
	FrameLayout numPad;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mathquestion);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		numPad = (FrameLayout) findViewById(R.id.frameLayoutNumPad);
        final TextView showEq = (TextView) findViewById(R.id.textViewEquation);
        final TextView showIn = (TextView) findViewById(R.id.textViewInput);
        final TextView result = (TextView) findViewById(R.id.textViewResult);
        final TextView clock = (TextView) findViewById(R.id.textViewTimer);
        final ImageView backgroundImg = (ImageView) findViewById(R.id.imageViewEqnBackground);
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
        final MediaPlayer mp3Correct = MediaPlayer.create(this, R.raw.correct);
        final GameSettings gSettings = new GameSettings();
        final Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); 
        final String FILENAME = "m4bfile1", FILEPRO = "m4bfilePro1";
        Typeface myTypeface2 = Typeface.createFromAsset(getAssets(), "fawn.ttf");
        showEq.setTypeface(myTypeface2);
        result.setTypeface(myTypeface2);
        showIn.setTypeface(myTypeface2);
        micButton = (ImageButton) findViewById(R.id.buttonMic);
        sr = SpeechRecognizer.createSpeechRecognizer(this);       
        sr.setRecognitionListener(new listener());   
        
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
			if(gFile[21]!=null && !gFile[21].equals("null")){
				gSettings.microphone= Integer.parseInt(gFile[21]);
			}
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
			backgroundImg.setImageResource(R.drawable.lines_background);
			e.printStackTrace();
		}
        final Equation eq = new Equation(gSettings.equationType, gSettings.difficulty, this);
        
        eq.createNew();
        hintSleep=0;
        showEq.setText(eq.getEquation());
        showIn.setText("");
        clock.setText("");
        if(gSettings.microphone==1){        	
        	micButton.setVisibility(View.VISIBLE);
        	pass2.setVisibility(View.VISIBLE);
        }
        

		//Decrement input timer an check if input is correct
        gotInput = new Runnable() {
        	@Override
			public void run() {
        		gSettings.inputTimer -= 1;
        		if(eq.getAnswer().length()<2) gSettings.inputTimer -= 1;
        		if(showIn.getText().equals(eq.getAnswer())){
        			try{
        			if(gSettings.sound==1) mp3Correct.start();
        			}catch(Exception E){ E.printStackTrace();}
        			gSettings.score +=1;
        			result.setText("");
        			eq.createNew();
        			hintSleep=0;
        	        showEq.setText(eq.getEquation());
        	        showIn.setText("");
        	        gSettings.inputTimer = -1;
        		}else{
	        		if (gSettings.inputTimer == 0 || gSettings.inputTimer == 1){
	        				displaySecs = 20;
	        				result.setTextColor(Color.rgb(200,0,0));
		        			result.setText("X");
		        			if(gSettings.vibrate==1)vb.vibrate(500);
		        			showIn.setText("");
	        		}
        		}
        		//result display timer
        		if (displaySecs > 0)displaySecs-=2;
        		else result.setText("");
        		//hint display timer
        		hintSleep++;
        		if(hintSleep==30){
            		displaySecs = 40;
        			result.setTextColor(Color.rgb(0,0,200));
            		result.setText(eq.getHint());
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
        				try{
                		if(gSettings.sound==1) mp3Correct.start();
                		}catch(Exception E){ E.printStackTrace();}
                		gSettings.score +=1;
                		result.setText("");
                		eq.createNew();
                		hintSleep=0;
                	    showEq.setText(eq.getEquation());
                	    showIn.setText("");
                	    gSettings.inputTimer = -1;
        			}
        			else{
        				displaySecs = 20;
        				result.setTextColor(Color.rgb(200,0,0));
	        			result.setText("X");
	        			if(gSettings.vibrate==1)vb.vibrate(500);
	        			showIn.setText("");
        			}
        			speechActive = false;        			
        		}
        		
        		mHandler.postDelayed(this,200);        		
        	}
        };
        mHandler.removeCallbacks(gotInput);            
		mHandler.postDelayed(gotInput, 100);
        
		b0.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"0");
        		gSettings.inputTimer= 10 - gSettings.difficulty;
        	}
        });
        b1.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"1");
        		gSettings.inputTimer= 10 - gSettings.difficulty;
        	}
        });
        b2.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"2");
        		gSettings.inputTimer= 10 - gSettings.difficulty;
        	}
        });
        b3.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"3");
        		gSettings.inputTimer= 10 - gSettings.difficulty;
        	}
        });
        b4.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"4");
        		gSettings.inputTimer= 10 - gSettings.difficulty;
        	}
        });
        b5.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"5");
        		gSettings.inputTimer= 10 - gSettings.difficulty;
        	}
        });
        b6.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"6");
        		gSettings.inputTimer= 10 - gSettings.difficulty;
        	}
        });
        b7.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"7");
        		gSettings.inputTimer= 10 - gSettings.difficulty;
        	}
        });
        b8.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"8");
        		gSettings.inputTimer= 10 - gSettings.difficulty;
        	}
        });
        b9.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(gSettings.vibrate==1)vb.vibrate(15);
        		showIn.setText(showIn.getText()+"9");
        		gSettings.inputTimer= 10 - gSettings.difficulty;
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
        		result.setTextColor(Color.rgb(200,0,0));
        		displaySecs = 30;
        		result.setText(eq.getEquation()+eq.getAnswer());
        		eq.createNew();
        		hintSleep=0;
                showEq.setText(eq.getEquation());
                showIn.setText("");
        		gSettings.wrong+=1;
        		if(gSettings.vibrate==1)vb.vibrate(500);
        	}
        });
        
        pass2.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		result.setTextColor(Color.rgb(200,0,0));
        		displaySecs = 30;
        		result.setText(eq.getEquation()+eq.getAnswer());
        		eq.createNew();
        		hintSleep=0;
                showEq.setText(eq.getEquation());
                showIn.setText("");
        		gSettings.wrong+=1;
        		if(gSettings.vibrate==1)vb.vibrate(500);
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

	@Override
	public void onResume(){
		super.onResume();
		Animation newAnimation = new TranslateAnimation(0,0,1100,0);
		newAnimation.setDuration(700);
		newAnimation.setInterpolator(new OvershootInterpolator());
		numPad.startAnimation(newAnimation);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
        mHandler.removeCallbacks(gotInput);
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
}
