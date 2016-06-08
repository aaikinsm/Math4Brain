package com.blackstar.math4brain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Multiplayer2Activity extends Activity{
	String IPADRS = "blackstar.herobo.com", FILENAME = "m4bfile1", FILEMULT = "m4bfileMul", FILEPRO = "m4bfilePro1",
			name = "", id = "", message="", equations="", output="";
	int myScr = 0, displaySecs, numEqn =30, index=0, p1Scr=0, p2Scr=0, anim=0, waitTimer=0, FILESIZE=25;
	boolean player1= true, connected=false, gameOver = false, win = false, pro = false, stopSearch = false;
	TextView text, info;
	Handler mHandler = new Handler();
	Runnable gameClock, gotInput;
	String[] arry;
	String[][] eqnArry;
	AsyncTask<String, String, String> connection;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer2);
        
        final Button p1 = (Button) findViewById(R.id.buttonP1);
        final Button p2 = (Button) findViewById(R.id.buttonP2);
        final Button refresh = (Button) findViewById(R.id.buttonRefresh);
        final Button enter = (Button) findViewById(R.id.buttonEnterID);
        final EditText input = (EditText) findViewById(R.id.editTextInput);
        info = (TextView) findViewById(R.id.textViewInform); 
        text = (TextView) findViewById(R.id.textViewOut); 
        arry = new String[FILESIZE]; 
        final Context cntx =this;
        
        //check connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() == null){
        	Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
    		finish();
        }
        //Get User Data from file then display it
        try {	
			FileInputStream fi = openFileInput(FILENAME);
			Scanner in = new Scanner(fi);
			int i = 0;
			while(in.hasNext()){
				arry[i]=in.next();
				i++;
			}			
			name = (arry[13]);
			id = arry[12].substring(0,3);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
        //generate equations
        Equation eqn = new Equation(Integer.parseInt(arry[1]),Integer.parseInt(arry[5]), this);
        eqnArry = new String[numEqn][2]; 
        for(int i=0; i<numEqn; i++){
        	eqn.createNew();
            eqnArry[i][0]=(eqn.getEquation());
            eqnArry[i][1]=(eqn.getAnswer());
            equations += eqnArry[i][0].replace(" ","_")+" "+eqn.getAnswer()+" ";
        }
        
        p1.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		p2.setVisibility(View.GONE);
        		info.setText(getResources().getString(R.string.your_id_is)+": "+id);
        		message="p1 msg";
        		connection = new UpdateDatabase().execute();
        	}
        });
        
        p2.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		p1.setVisibility(View.GONE);
        		input.setVisibility(View.VISIBLE);
        		enter.setVisibility(View.VISIBLE);
        		info.setText(R.string.enter_opponent_id);
        		player1 = false;
        	}
        });
        
        enter.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        		 mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);//hide keyboard
        		String in = input.getText()+""; message="p2 msg";
        		if(in.equals(id)){
        			info.setText(R.string.wrong_id);
        			input.setText("");
        		}
        		else{
					info.setText("");
        			id=in;
        			connection = new UpdateDatabase().execute();
        		}
        	}
        });
        
        refresh.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		startActivity(new Intent("android.intent.action.MULTIPLAYER2"));
        		finish();
        	}
        });

      
        final Runnable startMatch = new Runnable(){
        	@Override
			public void run(){
		        setContentView(R.layout.mathquestion);
		        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
		        final ImageButton clear = (ImageButton) findViewById(R.id.buttonClr);
		        final MediaPlayer mp3Correct = MediaPlayer.create(cntx, R.raw.correct);
		        final GameSettings gSettings = new GameSettings();
		        final Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); 
		        final ProgressBar progress1 = (ProgressBar) findViewById(R.id.progressBarBlue);
		        final ProgressBar progress2 = (ProgressBar) findViewById(R.id.progressBarRed);
		        final ImageButton next = (ImageButton) findViewById(R.id.buttonNext);
		        final ImageButton back = (ImageButton) findViewById(R.id.buttonBack);
		        final FrameLayout numPad = (FrameLayout) findViewById(R.id.frameLayoutNumPad);
		        final ImageView winImg = (ImageView) findViewById(R.id.ImageView05);
		        final ImageView loseImg = (ImageView) findViewById(R.id.ImageView01);
		        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "digital.ttf");
		        Typeface myTypeface2 = Typeface.createFromAsset(getAssets(), "fawn.ttf");
		        clock.setTypeface(myTypeface);
		        showEq.setTypeface(myTypeface2);
		        info.setTypeface(myTypeface2);
		        result.setTypeface(myTypeface2);
		        showIn.setTypeface(myTypeface2);
		        
		        if (player1) backgroundImg.setImageResource(R.drawable.bg_player1);
		        else backgroundImg.setImageResource(R.drawable.bg_player2);

				//read pro file
				File file = getApplicationContext().getFileStreamPath(FILEPRO);
				pro = file.exists();

		        
		        //update filemult
		        String date;
		        int tries;
		    	try{
		        	//read
		        	FileInputStream fi = openFileInput(FILEMULT);
					Scanner in = new Scanner(fi);
					date = in.next(); tries=Integer.parseInt(in.next());
					in.close();
					Calendar c = Calendar.getInstance();
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			        String currentDate = df.format(c.getTime());
			        if (tries == 0){ 
			        	Toast.makeText(getApplicationContext(), R.string.no_more_games,Toast.LENGTH_LONG).show();
			        	finish();
			        }
			        if (date.equals(currentDate)&& !pro)tries--;
			        try{
				        OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEMULT,0)); 			
						out.write(currentDate+" "+tries);
						out.close();
			        }catch(IOException e1){e1.printStackTrace();}
			        System.out.print(currentDate);
		        }catch (FileNotFoundException e) {
		        	e.printStackTrace(); 
		        }
		        
		        showEq.setText(eqnArry[index][0]);
		        showIn.setText("");
		        clock.setText("");
		        progress1.setVisibility(View.VISIBLE);
		        progress2.setVisibility(View.VISIBLE);
		        gSettings.sound = Integer.parseInt(arry[3]);
		        gSettings.vibrate = Integer.parseInt(arry[17]);
		        
		        //Handler for all timers
		        final Handler mHandler = new Handler();
		        
				//Decrement input timer an check if input is correct
		        gotInput = new Runnable() {
		        	@Override
					public void run() {
		        		gSettings.inputTimer -= 1;
		        		if(showIn.getText().equals(eqnArry[index][1])){
		        			try{
		        			if(gSettings.sound==1) mp3Correct.start();
		        			}catch(Exception E){E.printStackTrace();}
		        			gSettings.score +=1;
		        			myScr ++;
		        			result.setText("");
		        			index++;
		        	        showEq.setText(eqnArry[index][0]);
		        	        showIn.setText("");
		        	        gSettings.inputTimer = -1;
		        		}else{
			        		if (gSettings.inputTimer == 0){
			        				displaySecs = 20;
			        				result.setTextColor(Color.rgb(200,0,0));
				        			result.setText("X");
				        			if(gSettings.vibrate==1)vb.vibrate(500);
				        			showIn.setText("");
				        			myScr --;
			        		}
		        		}		        		
		        		//result display timer
		        		if (displaySecs > 0)displaySecs-=2;
		        		else result.setText("");
		        		//update score and progress bars
		        		if(player1)p1Scr=myScr;
		        		else p2Scr=myScr;
		        		progress1.setProgress(100/numEqn*2*p1Scr);
		        		progress2.setProgress(100/numEqn*2*p2Scr);
		        		Animation newAnimation = new AlphaAnimation(0,1);
	                    newAnimation.setDuration(200);
		        		if (progress1.getProgress() > 80)progress1.startAnimation(newAnimation);
		        		if (progress2.getProgress() > 80)progress2.startAnimation(newAnimation);		        		
		        		if(index== numEqn-2){
		        			showEq.setText(R.string.too_many_mistakes);
		        		}		        		
		        		if(gameOver){
		        			mHandler.removeCallbacks(this);
		        			if(win){
		        				showEq.setText(R.string.you_win);
		        				winImg.setVisibility(View.VISIBLE);
		        			}
		        			else{ 
		        				showEq.setText(R.string.you_lose);
		        				loseImg.setVisibility(View.VISIBLE);
		        			}
		            		next.setVisibility(View.VISIBLE);
		            		back.setVisibility(View.VISIBLE);
		        			numPad.setVisibility(View.GONE);
		        			showEq.startAnimation(newAnimation);
		        		}else{
		        			mHandler.postDelayed(this,80);
		        		}
		        	}
		        };            
		        
		        b0.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v){
		        		if(gSettings.vibrate==1)vb.vibrate(25);
		        		showIn.setText(showIn.getText()+"0");
		        		gSettings.inputTimer=10;
		        	}
		        });
		        b1.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v){
		        		if(gSettings.vibrate==1)vb.vibrate(25);
		        		showIn.setText(showIn.getText()+"1");
		        		gSettings.inputTimer=10;
		        	}
		        });
		        b2.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v){
		        		if(gSettings.vibrate==1)vb.vibrate(25);
		        		showIn.setText(showIn.getText()+"2");
		        		gSettings.inputTimer=10;
		        	}
		        });
		        b3.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v){
		        		if(gSettings.vibrate==1)vb.vibrate(25);
		        		showIn.setText(showIn.getText()+"3");
		        		gSettings.inputTimer=10;
		        	}
		        });
		        b4.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v){
		        		if(gSettings.vibrate==1)vb.vibrate(25);
		        		showIn.setText(showIn.getText()+"4");
		        		gSettings.inputTimer=10;
		        	}
		        });
		        b5.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v){
		        		if(gSettings.vibrate==1)vb.vibrate(25);
		        		showIn.setText(showIn.getText()+"5");
		        		gSettings.inputTimer=10;
		        	}
		        });
		        b6.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v){
		        		if(gSettings.vibrate==1)vb.vibrate(25);
		        		showIn.setText(showIn.getText()+"6");
		        		gSettings.inputTimer=10;
		        	}
		        });
		        b7.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v){
		        		if(gSettings.vibrate==1)vb.vibrate(25);
		        		showIn.setText(showIn.getText()+"7");
		        		gSettings.inputTimer=10;
		        	}
		        });
		        b8.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v){
		        		if(gSettings.vibrate==1)vb.vibrate(25);
		        		showIn.setText(showIn.getText()+"8");
		        		gSettings.inputTimer=10;
		        	}
		        });
		        b9.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v){
		        		if(gSettings.vibrate==1)vb.vibrate(25);
		        		showIn.setText(showIn.getText()+"9");
		        		gSettings.inputTimer=10;
		        	}
		        });
		        
		        clear.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v){
		        		if(gSettings.vibrate==1)vb.vibrate(25);
		        		showIn.setText("");
		        		gSettings.inputTimer=-1;
		        	}
		        });
		        
		        pass.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v){
		        		displaySecs = 30;
		        		result.setText(eqnArry[index][0]+eqnArry[index][1]);
		        		index++;
		                showEq.setText(eqnArry[index][0]);
		                showIn.setText("");
		        		gSettings.wrong+=1;
		        		if(gSettings.vibrate==1)vb.vibrate(500);
		        		myScr --;
		        	}
		        });
		        
		        next.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v){
		        		startActivity(new Intent("android.intent.action.MULTIPLAYER2"));
		        		finish();
		        	}
		        });
		        back.setOnClickListener (new View.OnClickListener(){
		        	@Override
					public void onClick (View v){
		        		finish();
		        	}
		        });
		        mHandler.postDelayed(gotInput, 100);
        	}
        };
        
        gameClock = new Runnable(){
        	@Override
			public void run(){
        		waitTimer ++;
        		text.setText(output);
        		if(connected && anim==0){
        			info.setText(R.string.connected);
        			input.setVisibility(View.INVISIBLE);
            		enter.setVisibility(View.INVISIBLE);
            		animateText();
            		mHandler.postDelayed(gameClock, 200);
            	}else if(connected && anim==2){
        			//start match
        			mHandler.post(startMatch);
            	}else if(waitTimer>250){
            		text.setText(R.string.p1_not_found);
                    stopSearch = true;
        		}else mHandler.postDelayed(gameClock, 200);
        	}
        };
        mHandler.post(gameClock);
	}
	
	class UpdateDatabase extends AsyncTask<String, String, String> {
    	
    	// url to update product
        private  String create_session = "http://"+IPADRS+"/sqlphp/m_create_session.php";
        private  String join_session = "http://"+IPADRS+"/sqlphp/m_join_session.php";
        private  String is_connected = "http://"+IPADRS+"/sqlphp/m_is_connected.php";
        private  String update_scores = "http://"+IPADRS+"/sqlphp/m_update_scores.php";
        private  String close_session = "http://"+IPADRS+"/sqlphp/m_close_sesssion.php";
    	// JSON Node names
        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";
        private static final String TAG_UID = "id";
        private static final String TAG_NAME = "name";
        private static final String TAG_EQ = "eq";
        private static final String TAG_MSG = "msg";
        private static final String TAG_P1 = "p1";
        private static final String TAG_P2 = "p2";
        
        @Override
		protected String doInBackground(String... args) {
        	try{
	            JSONParser jsonParser = new JSONParser();
	            
	            /**
	             * Player1 connect
	             * */
	            // Building Parameters
	            if(player1){
		            List<NameValuePair> p1 = new ArrayList<NameValuePair>();
		            p1.add(new BasicNameValuePair(TAG_UID, id));
		            p1.add(new BasicNameValuePair(TAG_NAME, name));
		            p1.add(new BasicNameValuePair(TAG_MSG, message));
		            p1.add(new BasicNameValuePair(TAG_EQ, equations));
		            
		            //create session
		            JSONObject json = jsonParser.makeHttpRequest(create_session,"POST", p1);
		            output =(json.getString(TAG_MESSAGE));
		            int created = json.getInt(TAG_SUCCESS);
		            
		            if(created==1){
		            	List<NameValuePair> p1b = new ArrayList<NameValuePair>();
			            p1b.add(new BasicNameValuePair(TAG_UID, id));
		            	//wait for opponent to connect and receive info
		            	output = getResources().getString(R.string.waiting_for_opponent);
		            	while(!connected && !stopSearch){
		            		//if ( isCancelled()) break;				            
				            JSONObject json3 = jsonParser.makeHttpRequest(is_connected,"POST", p1b);
				            if (json3.getInt(TAG_SUCCESS)== 1){
				            	output = getResources().getString(R.string.connected);
				            	output =(json3.getString(TAG_NAME)+" "+getResources().getString(R.string.vs)+" "+name);	 
				            	connected = true;
				            }else if (json3.getInt(TAG_SUCCESS)== 0){
				            	output += (".");
				            	if (output.length() > 25) output = getResources().getString(R.string.waiting);
				            }
			            }
		            	try{
		            	//continue to retrieve scores for p1 till game is over

							List<NameValuePair> pScores = new ArrayList<NameValuePair>();
			            	while(connected && !gameOver){
			            		//if ( isCancelled()) break;
								pScores.clear();
								int p1scr = p1Scr;
								pScores.add(new BasicNameValuePair(TAG_UID, id));
								pScores.add(new BasicNameValuePair(TAG_P1, p1scr+""));
					            JSONObject json4 = jsonParser.makeHttpRequest(update_scores,"POST", pScores);
					            if (json4.getInt(TAG_SUCCESS)== 1){
					            	p2Scr =(json4.getInt(TAG_P2));
					            	if (p2Scr == numEqn/2){
					            		gameOver = true;
					            		win = false;
					            	}
					            	if (p1scr == numEqn/2){
					            		gameOver = true;
					            		win = true;
					            	}
					            	
					            }else{ 
					            	System.out.println("Score update failed");
					            	Toast.makeText(getApplicationContext(), R.string.disconnected,Toast.LENGTH_SHORT).show();
				            		finish();
					            }
			            	}
		            	}catch(JSONException e){
		            		Log.d("Error","Error:"+e);
		            		Toast.makeText(getApplicationContext(), R.string.disconnected,Toast.LENGTH_SHORT).show();
		            		finish();
		            	}
		            }
		            else  Toast.makeText(getApplicationContext(), R.string.disconnected,Toast.LENGTH_SHORT).show();
	            }
	            
	            /**
	             * Player2 connect
	             * */
	            else{	            
		            List<NameValuePair> p2 = new ArrayList<>();
		            p2.add(new BasicNameValuePair(TAG_UID, id));
		            p2.add(new BasicNameValuePair(TAG_NAME, name));
		            p2.add(new BasicNameValuePair(TAG_MSG, message));
		            
		            //connect to opponent and retrieve info
		            JSONObject json2 = jsonParser.makeHttpRequest(join_session,"POST", p2);
		            if(json2.getInt(TAG_SUCCESS)==0){
		            	output =(json2.getString(TAG_MESSAGE));
		            	if (output.equals("opponent is not ready.")) output = getResources().getString(R.string.p1_not_ready);
		            	else if (output.equals("opponent not found.")) output = getResources().getString(R.string.p1_not_found);
		            	else if (output.equals("session not joined.")) output = getResources().getString(R.string.p1_not_joined);
		            }else if(json2.getInt(TAG_SUCCESS)==1){			            
			            output =(json2.getString(TAG_NAME)+" "+getResources().getString(R.string.vs)+" "+name);	                    
			            //output +=(" |P1Msg: "+json2.getString(TAG_MSG));
			            equations =(""+json2.getString(TAG_EQ));
			            Log.d("Equations", equations);
			            Scanner in = new Scanner(equations);
			            for(int i=0; i<numEqn; i++){
			            	String str = in.next();
			            	try{
			            	eqnArry[i][0]=str.replace("_"," ");
			            	eqnArry[i][1]=in.next();
			            	}catch(NoSuchElementException e){
			            		e.printStackTrace();
			            		i = numEqn;
			            	}
			            }
			            connected = true;
	            	}
		            try{
		            //continue to retrieve scores till game is over
						List<NameValuePair> pScores = new ArrayList<>();
		            	while(connected && !gameOver){
		            		//if ( isCancelled()) break;
		            		int p2scr = p2Scr;
							pScores.clear();
							pScores.add(new BasicNameValuePair(TAG_UID, id));
		            		pScores.add(new BasicNameValuePair(TAG_P2, p2Scr+""));
				            JSONObject json5 = jsonParser.makeHttpRequest(update_scores,"POST", pScores);
				            if (json5.getInt(TAG_SUCCESS)== 1){
				            	p1Scr =(json5.getInt(TAG_P1));
				            	if (p1Scr == numEqn/2){
				            		gameOver = true;
				            		win = false;
				            	}
				            	if (p2scr == numEqn/2){
				            		gameOver = true;
				            		win = true;
				            	}
				            }else{
				            	System.out.println("Score update failed");
				            	Toast.makeText(getApplicationContext(), R.string.disconnected,Toast.LENGTH_SHORT).show();
			            		finish();
				            }
		            	}
		            }catch(JSONException e){
	            		Log.d("Error","Error:"+e);
	            		Toast.makeText(getApplicationContext(), R.string.disconnected,Toast.LENGTH_SHORT).show();
	            		finish();
	            	}
	            }
	            //Remove connection
            	List<NameValuePair> p = new ArrayList<>();
        		p.add(new BasicNameValuePair(TAG_UID, id));
        		JSONObject json = jsonParser.makeHttpRequest(close_session,"POST", p);
        		Log.d("Session Close",(json.getString(TAG_MESSAGE))+"");
        	}
        	catch (NullPointerException e) {
				output = getString(R.string.disconnected);
                e.printStackTrace();
				finish();
            }
        	catch (JSONException e) {
                e.printStackTrace();
            }
           return null;
        }
    }


    public void animateText(){
    	anim=1;
    	Animation newAnimation = new TranslateAnimation(0,0,0,-180);
        newAnimation.setDuration(2000);
        final Animation scaleAnimation = new ScaleAnimation(0,2,0,2);
        scaleAnimation.setDuration(1000);
        text.startAnimation(newAnimation);
        newAnimation.setAnimationListener(new AnimationListener() {
            @Override
			public void onAnimationEnd(Animation animation) {
            	text.setText(R.string.go);
            	anim=2;
            }
            @Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationStart(Animation animation) {}
        });
    }

	@Override
	public void onStop() {
		super.onStop();
        stopSearch = true;
        if (connection != null) connection.cancel(true);
        mHandler.removeCallbacks(gameClock);
        mHandler.removeCallbacks(gotInput);
        finish();
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		if (connection != null) connection.cancel(true);
	}
}

