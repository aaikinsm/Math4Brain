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
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.tapjoy.TapjoyConnect;
import com.tapjoy.TapjoyDisplayAdNotifier;
import com.tapjoy.TapjoyFullScreenAdNotifier;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class MinuteRunActivity extends Activity implements TapjoyDisplayAdNotifier, TapjoyFullScreenAdNotifier{
	
	Handler mHandler;
	Runnable mUpdateTimer, gotInput, breakTimer;
	MediaPlayer mp3Tick;
	double startTime = 0, nextTime=0, time=0;
	int count = 0, combo = 0, minPointsPro = 5000, displaySecs, hintSleep, FILESIZE=25;
	boolean update_display_ad=false, blackberry = false, amazon = false, pro = false, colorful=false, connection =true;
	TapjoyFullScreenAdNotifier fullAdNotif = this;
	String review = "", FILEEXTRA = "m4bfileExt";
	LinearLayout adLinearLayout;
	View adView;
	String[] gFile;
	ArrayList<String> speechMatches;
	private SpeechRecognizer sr;
	boolean speechActive = false, admobActive = false;
	ImageButton micButton;
	AdView adView1;
	InterstitialAd mInterstitialAd;
	ProgressBar progress;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

		setContentView(R.layout.mathquestion);

        TapjoyConnect.requestTapjoyConnect(getApplicationContext(),"d199877d-7cb0-4e00-934f-d04eb573aa47","1SgBmHKgJUk8cw9IOY3s");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final TextView showEq = (TextView) findViewById(R.id.textViewEquation);
        final TextView clock = (TextView) findViewById(R.id.textViewTimer);
        final TextView showIn = (TextView) findViewById(R.id.textViewInput);
        final TextView result = (TextView) findViewById(R.id.textViewResult);
        final TextView info = (TextView) findViewById(R.id.textViewInfo);
        final TextView corrections = (TextView) findViewById(R.id.textViewReview);
        final ImageView emoticon0 = (ImageView) findViewById(R.id.ImageView00);
        final ImageView emoticon1 = (ImageView) findViewById(R.id.ImageView01);
        final ImageView emoticon2 = (ImageView) findViewById(R.id.ImageView02);
        final ImageView emoticon3 = (ImageView) findViewById(R.id.ImageView03);
        final ImageView emoticon4 = (ImageView) findViewById(R.id.ImageView04);
        final ImageView emoticon5 = (ImageView) findViewById(R.id.ImageView05);
        final ImageView emoticon6 = (ImageView) findViewById(R.id.ImageView06);
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
        final Button next = (Button) findViewById(R.id.buttonNext);
        final Button back = (Button) findViewById(R.id.buttonBack);
        final Button ad = (Button) findViewById(R.id.buttonAd);
        final FrameLayout comboPad = (FrameLayout) findViewById(R.id.frameLayoutNumPad);
        final LinearLayout scroll = (LinearLayout) findViewById(R.id.linearLayoutScroll);
        final ImageView backgroundImg = (ImageView) findViewById(R.id.imageViewEqnBackground);
        final MediaPlayer mp3Correct = MediaPlayer.create(this, R.raw.correct);
        final MediaPlayer mp3Timeup = MediaPlayer.create(this, R.raw.wrong);
        final MediaPlayer mp3Payout = MediaPlayer.create(this, R.raw.payout);
        mp3Tick = MediaPlayer.create(this, R.raw.ticktok);
        final GameSettings gSettings = new GameSettings();       
        final Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); 
        mHandler = new Handler();
        final String FILENAME = "m4bfile1", FILEPRO = "m4bfilePro1";
        final int[] aScores = new int [3];
        Tips tp = new Tips();
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "digital.ttf");
        Typeface myTypeface2 = Typeface.createFromAsset(getAssets(), "fawn.ttf");
        clock.setTypeface(myTypeface);
        ad.setTypeface(myTypeface2);
        showEq.setTypeface(myTypeface2);
        info.setTypeface(myTypeface2);
        result.setTypeface(myTypeface2);
        showIn.setTypeface(myTypeface2);
        corrections.setTypeface(myTypeface2);
        micButton = (ImageButton) findViewById(R.id.buttonMic);
        sr = SpeechRecognizer.createSpeechRecognizer(this);       
        sr.setRecognitionListener(new listener()); 
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() == null) connection = false;
        if(android.os.Build.BRAND.toLowerCase().contains("blackberry"))blackberry=true;
        else if(android.os.Build.MODEL.toLowerCase().contains("kindle"))amazon=true;
		progress = (ProgressBar) findViewById(R.id.progressBarBlue);
		progress.setVisibility(View.VISIBLE);
		clock.setVisibility(View.GONE);


        //Locate the Banner Ad in activity_main.xml
  		adView1 = (AdView) this.findViewById(R.id.adView);
  		// Request for Ads
  		AdRequest adRequest = new AdRequest.Builder()
  		// Add a test device to show Test Ads
  		 .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
  				.build();
  		// Load ads into Banner Ads
  		adView1.loadAd(adRequest);
		// Load Interstitial ads
		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId("ca-app-pub-8528343456081396/2957766464");
		requestNewInterstitial();
		mInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				//:TODO
				//requestNewInterstitial();
			}
		});
  		
        
        //set ad frequency
        int fb = (int) (Math.random()*(4)) ;
        if (fb==2 && !blackberry && connection && !pro){
	        //Display ad rewarded.
	      	TapjoyConnect.getTapjoyConnectInstance().enableDisplayAdAutoRefresh(true);
	      	TapjoyConnect.getTapjoyConnectInstance().getDisplayAdWithCurrencyID(this, "d199877d-7cb0-4e00-934f-d04eb573aa47", this);
	      	adLinearLayout = (LinearLayout)findViewById(R.id.AdLinearLayout1);
        }
//        if (fb==3 && !blackberry && connection){
//	        //Display ad non-rewarded
//	      	TapjoyConnect.getTapjoyConnectInstance().enableDisplayAdAutoRefresh(true);
//	      	TapjoyConnect.getTapjoyConnectInstance().getDisplayAdWithCurrencyID(this,"684e6285-de7c-47bb-9341-3afbbfeb6eea", this);
//	      	adLinearLayout = (LinearLayout)findViewById(R.id.AdLinearLayout1);
//        }
        if (fb==3 && !blackberry && connection && !pro) admobActive = true;
     		
        //get user settings then create equation 
        try {
        	gFile = new String[FILESIZE];
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
			try{
			gSettings.vibrate= Integer.parseInt(gFile[17]);
			} catch (NumberFormatException e){
				//submitError(e.toString(),Arrays.toString(gFile));
			}
			if(gFile[21]!=null && !gFile[21].equals("null")){
				gSettings.microphone= Integer.parseInt(gFile[21]);
			}
			if(gSettings.microphone==1){        	
	        	micButton.setVisibility(View.VISIBLE);
	        	pass2.setVisibility(View.VISIBLE);
	        }
			aScores[0] = Integer.parseInt(gFile[9]);
			aScores[1] = Integer.parseInt(gFile[10]);
			aScores[2] = Integer.parseInt(gFile[11]);
        } catch (FileNotFoundException e) {
			e.printStackTrace();
        }
		try{	
			FileInputStream fip = openFileInput (FILEPRO);
			Scanner inp = new Scanner(fip);
			inp.next(); inp.next(); inp.next();
			String bgPath = inp.next();
			switch (bgPath) {
				case "bg1":
					backgroundImg.setImageResource(R.drawable.bg1);
					break;
				case "bg2":
					backgroundImg.setImageResource(R.drawable.bg2);
					break;
				case "bg3":
					backgroundImg.setImageResource(R.drawable.bg3);
					break;
				case "default":
					backgroundImg.setImageResource(R.drawable.lines_background);
					break;
				default:
					backgroundImg.setImageURI(Uri.parse(bgPath));
					break;
			}
			pro = true;
		} catch (FileNotFoundException e) {
			backgroundImg.setImageResource(R.drawable.lines_background);
			e.printStackTrace();
		}
		
        final Equation eq = new Equation(gSettings.equationType, gSettings.difficulty, this);
        final double setTime = gSettings.clock;
        //Initial brain fact dialog
        final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialogbox);
		TextView body = (TextView) dialog.findViewById(R.id.textViewMsg);
		TextView title = (TextView) dialog.findViewById(R.id.textViewTitle);
		title.setVisibility(View.VISIBLE);
		title.setText(R.string.brain_fact);
		body.setText(tp.getTip(pro, getResources()));
		dialog.setCancelable(false);
		Button dialogButton = (Button) dialog.findViewById(R.id.button1);
		dialogButton.setVisibility(View.VISIBLE);
		dialogButton.setText(R.string.start);
		dialogButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gSettings.start = true;
				eq.createNew();
				hintSleep = 0;
				showEq.setText(eq.getEquation());
				startTime = System.currentTimeMillis();
				dialog.dismiss();
			}
		});
		dialog.show();

		//check if user should take a break
      	breakTime();
      	
        showEq.setText(R.string.press_start_to_begin);
        showIn.setText("");
      		
        //Decrement game timer and check if time is up
        mUpdateTimer = new Runnable() {   
        	@Override
			public void run() {
        		if (gSettings.start){ 
        			nextTime = System.currentTimeMillis();
        			try{
	        			if(startTime!=nextTime){
	        				String sTim = (nextTime-startTime)+"";
	        				if (sTim.length()<5) time=10;
	        				else time = setTime - Double.parseDouble(sTim.substring(0,sTim.length()-5)+"."+sTim.substring(sTim.length()-5,sTim.length()-4));
	        				gSettings.clock = time;
	        			}
	        			//final seconds
	        			if(Double.parseDouble(gSettings.getClock())<10){
	        				try{
	        					if(gSettings.sound==1 && !mp3Tick.isPlaying()){
	        						mp3Tick.start();
	        						mp3Tick.setLooping(true);
	        					}
	        				}catch(Exception E){ E.printStackTrace();}
	        				clock.setTextColor(Color.rgb(200,0,0));
	        			} 
        			}catch(NumberFormatException e){
						Toast.makeText(getApplicationContext(), "Sorry this language is not supported yet. Supported Languages are; English, French, Spanish, and German",
								Toast.LENGTH_SHORT).show();
						finish();
					}
        		}
        		clock.setText(gSettings.getClock());
				progress.setProgress((int) (Double.parseDouble(gSettings.getClock()) * 100 / setTime));
        		if(gSettings.clock==0){
        			//Display ad or banner if not pro version
        			if(!(aScores[0]>minPointsPro || pro)){
        				if(admobActive){
        					adView1.setVisibility(View.VISIBLE);
        				}
        				else{
		        			if (update_display_ad){       				
		            			adLinearLayout.removeAllViews();          			
		            			adLinearLayout.addView(adView);           			
		            			update_display_ad = false;
		            		}  else { 
		            			ad.setVisibility(View.VISIBLE);
		            			ad.setText(getString(R.string.you_are)+" "+(minPointsPro-aScores[0]-gSettings.getPoints())+" "
		            					+getString(R.string.pts_away_from_unlock));
		            		}
        				}
        			}
        			//Quick fix
        			try{
        			if(gSettings.sound==1) mp3Timeup.start();
        			}catch(Exception E){ E.printStackTrace();}
        			if(gSettings.vibrate==1)vb.vibrate(1000);
            		showEq.setText(R.string.time_is_up);
            		gSettings.timeUp = true;
            		comboPad.setVisibility(View.GONE);
            		showIn.setText("");
            		aScores[0] += gSettings.getPoints();
            		aScores[1] += 1;
            		int average=aScores[0]/aScores[1];
            		info.setText("\n"+getString(R.string.your_avg_score_is)+" "+average
            				+gSettings.getPointCalculation(getApplicationContext()));
            		if (gSettings.getPoints()>aScores[2]){
            			info.setText(getString(R.string.new_highscore)+ gSettings.getPointCalculation(getApplicationContext()));
            			aScores[2]=gSettings.getPoints();
            		}
            		if(!review.equals(""))corrections.setText("*"+getString(R.string.review)+"*\n"+review+"\n");
            		scroll.setBackgroundResource(R.drawable.shadow_bg);
            		next.setText(getString(R.string.try_again));
            		next.setVisibility(View.VISIBLE);
            		back.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
            		
            		//set emoticons
            		if (gSettings.getPoints()>average+10){
            			emoticon6.setVisibility(View.VISIBLE);
            		}else if (gSettings.getPoints()>average+5){
            			emoticon5.setVisibility(View.VISIBLE);
            		}else if (gSettings.getPoints()>=average+2){
            			emoticon4.setVisibility(View.VISIBLE);
            		}else if (gSettings.getPoints()>=average-2){
            			emoticon3.setVisibility(View.VISIBLE);
            		}else if (gSettings.getPoints()>average-5){
            			emoticon2.setVisibility(View.VISIBLE);
            		}else if (gSettings.getPoints()>average-10){
            			emoticon1.setVisibility(View.VISIBLE);
            		}else{
            			emoticon0.setVisibility(View.VISIBLE);
            		}
            		
            		try {
            			String content="";
            			FileInputStream fi = openFileInput(FILENAME);
            			Scanner in = new Scanner(fi);
            			for (int i= 0; i<8; i++){
            				content += in.next()+" ";
            			}
            			content +=(getString(R.string.score)+": "+aScores[0]+" "+aScores[1]+" "+aScores[2]);
            			in.next(); in.next(); in.next(); in.next();
            			while(in.hasNext()){
            				content+= (" "+in.next());
            			}
            			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0)); 
            			out.write(content);
            			out.close();    
            		} catch (IOException e) {
            			e.printStackTrace();
            		}
            	} else{ 
            		mHandler.postDelayed(this,100);
            		//result display timer
            		if (displaySecs > 0){
            			displaySecs-=1;
            			if (colorful) result.setTextColor(Color.rgb(displaySecs%4*100,200-displaySecs%3*100,200));
            		}
            		else {
            			result.setText("");
            			colorful=false;
            		}
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
        			showIn.setVisibility(View.GONE);
        			//Quick fix
        			try{
        			mp3Tick.stop();
        			}catch(Exception E){ E.printStackTrace();}
        			result.setTextColor(Color.rgb(0,0,0));
        			//count down
        			if(count<gSettings.getPoints()&& gSettings.getPoints()>10){
        				count= count+(gSettings.getPoints()/10);
        				result.setText(count+" ");
        				if(gSettings.sound==1){
        					try{ 
        						if(!mp3Payout.isPlaying()){
	        						mp3Payout.start(); 
	        						mp3Payout.setLooping(true);
        						}
        					}catch(Exception E){ E.printStackTrace();}
        				}
        			} else{ 
        				result.setText(getResources().getString(R.string.you_earned) + " "
								+ gSettings.getPoints() + " " + getResources().getString(R.string.points));
	        			if(gSettings.sound==1){
	    					try{ 
	    						if(mp3Payout.isPlaying()) mp3Payout.stop(); 
	    					}catch(Exception E){ E.printStackTrace();}
	    				}
        			}
        		}else{
	        		gSettings.inputTimer -= 1;
	        		if(eq.getAnswer().length()<2) gSettings.inputTimer -= 1; //shorter wait time for multi-digit answers
	        		if(showIn.getText().equals(eq.getAnswer())){
	        			//Correct
	        			try{
	        			if(gSettings.sound==1) mp3Correct.start();
	        			}catch(Exception E){ E.printStackTrace();}
	        			gSettings.score +=1;	        			
	        			result.setText("");
	        			eq.createNew();
	        			hintSleep = 0;
	        	        showEq.setText(eq.getEquation());
	        	        showIn.setText("");
	        	        gSettings.inputTimer = -1;
	        	        combo++;
	        	        if(combo%10==0){
	        	        	displaySecs = 30;
	        	        	result.setTextColor(Color.rgb(25,100,200));	        	        	
	        	        	result.setText(getResources().getString(R.string.combo)+" +"+(combo/10));
	        	        	gSettings.score += combo/10;
	        	        	colorful = true;
	        	        }
	        		}else{
	        			//Wrong
		        		if (gSettings.inputTimer == 0 || gSettings.inputTimer == 1){
		        			displaySecs = 10;
		        			result.setTextColor(Color.rgb(200,0,0));
			        		result.setText("X");
			        		if(gSettings.vibrate==1)vb.vibrate(500);
			        		showIn.setText("");
			        		gSettings.wrong+=1;
			        		combo=0;
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
	        				//Correct
		        			try{
		        			if(gSettings.sound==1) mp3Correct.start();
		        			}catch(Exception E){ E.printStackTrace();}
		        			gSettings.score +=1;	        			
		        			result.setText("");
		        			eq.createNew();
		        			hintSleep = 0;
		        	        showEq.setText(eq.getEquation());
		        	        showIn.setText("");
		        	        gSettings.inputTimer = -1;
		        	        combo++;
		        	        if(combo%10==0){
		        	        	displaySecs = 30;
		        	        	result.setTextColor(Color.rgb(25,100,200));	        	        	
		        	        	result.setText(getResources().getString(R.string.combo)+" +"+(combo/10));
		        	        	gSettings.score += combo/10;
		        	        	colorful = true;
		        	        }
		        		}else{
		        			//Wrong
			        		displaySecs = 10;
			        		result.setTextColor(Color.rgb(200,0,0));
				        	result.setText("X");
				        	if(gSettings.vibrate==1)vb.vibrate(500);
				        	showIn.setText("");
				        	gSettings.wrong+=1;
				        	combo=0;
		        		}
	            		speechActive = false; 	            		
	        		}
        		}
        		//hint display timer
        		hintSleep++;
        		if(hintSleep==60 && !gSettings.timeUp){
            		displaySecs = 40;
        			result.setTextColor(Color.rgb(0,0,200));
            		result.setText(eq.getHint());
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
        		result.setTextColor(Color.rgb(200, 0, 0));
        		displaySecs = 30;
        		result.setText(eq.getEquation() + eq.getAnswer());
        		String hint = eq.getHint();
        		if(hint.equals("")) review += "\n"+eq.getEquation() + eq.getAnswer()+"\n-\n";
        		else review += eq.getEquation() +"\n"+hint+"\n"+eq.getEquation() + eq.getAnswer() + "\n-\n";
				eq.createNew();
        		hintSleep = 0;
                showEq.setText(eq.getEquation());
                showIn.setText("");
        		gSettings.wrong+=1;
        		if(gSettings.vibrate==1)vb.vibrate(500);
        		combo = 0;
        	}
        });
        
        pass2.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		result.setTextColor(Color.rgb(200,0,0));
        		displaySecs = 30;
        		result.setText(eq.getEquation() + eq.getAnswer());
        		String hint = eq.getHint();
        		if(hint.equals("")) review += "\n"+eq.getEquation() + eq.getAnswer()+"\n-\n";
        		else review += eq.getEquation() +"\n"+hint+"\n"+eq.getEquation() + eq.getAnswer()+"\n-\n";
        		eq.createNew();
        		hintSleep = 0;
                showEq.setText(eq.getEquation());
                showIn.setText("");
        		gSettings.wrong+=1;
        		if(gSettings.vibrate==1)vb.vibrate(500);
        		combo = 0;
        	}
        });		
        
        next.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		startActivity(new Intent("android.intent.action.MINUTERUN"));
        		finish();
        	}
        });
        back.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		finish();
        	}
        });
        
        ad.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		Intent i = new Intent(getApplicationContext(), TapJoyLauncher.class);
        		i.putExtra("view_offers","false");
        		startActivity(i);
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
    public void breakTime(){ 
		final long TIME = 2400000;
    	int maxPts = 0;
		final int MAXPTS = 300;
    	//check if user has exceeded number of max points within time
    	try{
        	//read
        	FileInputStream fi = openFileInput(FILEEXTRA);
			Scanner in = new Scanner(fi);
			bTime = Long.parseLong(in.next()); maxPts=Integer.parseInt(in.next());
			in.close();
			if(bTime < System.currentTimeMillis()){
				bTime = System.currentTimeMillis()+TIME;
				maxPts = Integer.parseInt(gFile[9])+MAXPTS;
			}
			breakTimer = new Runnable() {
	        	@Override
				public void run() { 
	        		int min = (int)(bTime-System.currentTimeMillis())/60000;
					int sec = (int)((bTime-System.currentTimeMillis())-(min*60000))/1000;
					title.setText(min+":"+sec);
					if(min==0 && sec==0) finish();
					mHandler.postDelayed(this, 100);
	        	}
	    	};
			if(maxPts<Integer.parseInt(gFile[9])){
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
				int min = (int)(bTime-System.currentTimeMillis())/60000;
				int sec = (int)((bTime-System.currentTimeMillis())-(min*60000))/1000;
				title.setText(min+":"+sec);
	    		body.setText(R.string.breakMsg);            
	    		mHandler.postDelayed(breakTimer, 100);
				dialogButton.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v) {	
		        		mHandler.removeCallbacks(breakTimer);
		        		finish();
		        		dialog.dismiss();
					}
				});
				if(connection){
					Button dialogButton2 = (Button) dialog.findViewById(R.id.button2);
					dialogButton2.setVisibility(View.VISIBLE);
					dialogButton2.setText(R.string.get_free_points);
					dialogButton2.setOnClickListener (new View.OnClickListener(){
			        	@Override
						public void onClick (View v) {
			        		mHandler.removeCallbacks(breakTimer);
			        		Intent i = new Intent(getApplicationContext(), TapJoyLauncher.class);
			        		i.putExtra("view_offers","true");
			        		startActivity(i);
			        		finish();
			        		dialog.dismiss();
						}
					});
				}
				dialog.show();
				//show Fullscreen add
				if(System.currentTimeMillis() %3==0 && connection && !pro){
					TapjoyConnect.getTapjoyConnectInstance().getFullScreenAd(fullAdNotif);
					FlurryAgent.logEvent("Video_Ad");
				}
				//admob interstitial full screen ad
				/*else if(System.currentTimeMillis() %4==0 && connection && !pro){
					mInterstitialAd = new InterstitialAd(this);
					mInterstitialAd.setAdUnitId("ca-app-pub-8528343456081396/2957766464");
					requestNewInterstitial();
					mInterstitialAd.setAdListener(new AdListener() {
						@Override
						public void onAdClosed() {
							requestNewInterstitial();
						}
					});
					if (mInterstitialAd.isLoaded()) {
						mInterstitialAd.show();
					}
				}*/
			}
        }catch (FileNotFoundException e) {
        	e.printStackTrace(); 
        	try {
        		OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEEXTRA,0)); 			
				out.write((System.currentTimeMillis()+TIME)+" "+Integer.parseInt(gFile[9])+MAXPTS);
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}			
        }
    	//write
    	try {
    		OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEEXTRA,0)); 			
			out.write(bTime+" "+maxPts);
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        	
    }
    
    @Override
	public void getDisplayAdResponse(View view)
	{
		// Using screen width, but substitute for the any width.
		int desired_width = adLinearLayout.getMeasuredWidth();		
		// Scale the display ad to fit incase the width is smaller than the display ad width.
		adView = scaleDisplayAd(view, desired_width);		
		update_display_ad = true;
	}

	// Notifier for a failed display ad request.
	@Override
	public void getDisplayAdResponseFailed(String error){
		System.out.println("failed ad display request");
	}
	public static View scaleDisplayAd(View adView, int targetWidth)
	{
		int adWidth = adView.getLayoutParams().width;
		int adHeight = adView.getLayoutParams().height;

		// Scale if the ad view is too big for the parent view.
		if (adWidth > targetWidth)
		{
			int scale;
			Double val = (double)targetWidth / (double)adWidth;
			val = val * 100d;
			scale = val.intValue();

			((android.webkit.WebView) (adView)).getSettings().setSupportZoom(true);
			adView.setPadding(0, 0, 0, 0);
			adView.setVerticalScrollBarEnabled(false);
			adView.setHorizontalScrollBarEnabled(false);
			((android.webkit.WebView) (adView)).setInitialScale(scale);

			// Resize banner to desired width and keep aspect ratio.
			LayoutParams layout = new LayoutParams(targetWidth, (targetWidth*adHeight)/adWidth);
			adView.setLayoutParams(layout);
		}

		return adView;
	}
	

	@Override
	public void getFullScreenAdResponse() {
		Log.i("TAPJOY", "Displaying Full Screen Ad..");
		TapjoyConnect.getTapjoyConnectInstance().showFullScreenAd();		
	}

	@Override
	public void getFullScreenAdResponseFailed(int arg0) {
		Log.i("TAPJOY", "Failed to display Full Screen Ad..");
	}

	private void requestNewInterstitial() {
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice("YOUR_DEVICE_HASH")
				.build();

		mInterstitialAd.loadAd(adRequest);
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimer);
        //Quick fix
        try{
        mp3Tick.stop();
        }catch(Exception E){E.printStackTrace();}

		int rn = (int) (Math.random()*(20)) ;
		if(!pro && rn==1 && Integer.parseInt(gFile[9])>1500 && mInterstitialAd.isLoaded()){
			mInterstitialAd.show();
		}
		else if(!pro && rn==3 && Integer.parseInt(gFile[9])>2000){
			TapjoyConnect.getTapjoyConnectInstance().getFullScreenAd(fullAdNotif);
			FlurryAgent.logEvent("Video_Ad");
		}
		else if(rn==4 && Integer.parseInt(gFile[9])>10000){
			mInterstitialAd.show();
		}
    }
    
    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mUpdateTimer);
        mHandler.removeCallbacks(gotInput);
        mHandler.removeCallbacks(breakTimer);
        finish();
    }
}
