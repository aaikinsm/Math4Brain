package com.blackstar.math4brain;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;


public class CreateSettingsActivity extends Activity{
	boolean firstPage = true, imageChosen = false, sound = false, vibrate = false, music = false, microphone = false;
	String selectedBg;
	int REQUEST_MEDIA = 1, difficulty=0, FILESIZE=25;
	ImageView d1, d2, d3, d4 ,d5;
	Bitmap myImage;
	Runnable mCheckImageChosen;
	Handler mHandler;
	String FILENAME = "m4bfile1", FILEPRO = "m4bfilePro1", username;
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        final String [] arry = new String[FILESIZE]; 
        final CheckBox addSub = (CheckBox) findViewById(R.id.checkBoxAS);
        final CheckBox mulDiv = (CheckBox) findViewById(R.id.checkBoxMD);
        final CheckBox exponent = (CheckBox) findViewById(R.id.checkBoxExponent);
        final CheckBox percent = (CheckBox) findViewById(R.id.checkBoxPercent);
        final CheckBox all = (CheckBox) findViewById(R.id.checkBoxAll);
        final Button done = (Button) findViewById(R.id.buttonDone);
        final Button userSelect = (Button) findViewById(R.id.buttonSelectUser);
        final Button sPage = (Button) findViewById(R.id.buttonSwitchPage);
        final Button customImage = (Button) findViewById(R.id.buttonCustomImage);
        final ImageButton bg1 = (ImageButton) findViewById(R.id.imageButtonBg1);
        final ImageButton bg2 = (ImageButton) findViewById(R.id.imageButtonBg2);
        final ImageButton bg3 = (ImageButton) findViewById(R.id.imageButtonBg3);
        final ImageButton bg4 = (ImageButton) findViewById(R.id.imageButtonBg4);
        final ImageButton bg5 = (ImageButton) findViewById(R.id.imageButtonBg5);
        final ImageView sndIcon = (ImageView) findViewById(R.id.sndIcon);
        final ImageView vibIcon = (ImageView) findViewById(R.id.vibIcon);
        final ImageView mscIcon = (ImageView) findViewById(R.id.mscIcon);
        final ImageView micIcon = (ImageView) findViewById(R.id.micIcon);
        final LinearLayout page1 = (LinearLayout) findViewById(R.id.settingsPage1);
        final LinearLayout page2 = (LinearLayout) findViewById(R.id.settingsPage2);
        this.d1 = ((ImageView)findViewById(R.id.d1));
        this.d2 = ((ImageView)findViewById(R.id.d2));
        this.d3 = ((ImageView)findViewById(R.id.d3));
        this.d4 = ((ImageView)findViewById(R.id.d4));
        this.d5 = ((ImageView)findViewById(R.id.d5));
        mHandler = new Handler();
        
        //retrieve saved data
        FileInputStream fi, fip;
		try {
			fi = openFileInput (FILENAME);
			Scanner in = new Scanner(fi);
			int j = 0;
			while(in.hasNext()){
				arry[j]=in.next();
				j++;
			}
			in.close();
			if(Integer.parseInt(arry[3])==1){
				sound=true;
				sndIcon.setImageResource(R.drawable.snd_on);
			}
			if(arry[21]== null || arry[21].equals("null")){
				arry[15]="0";
				arry[17]="0";
				arry[21]="0";
			}else {
				if(Integer.parseInt(arry[15])==1){
					music=true;
					mscIcon.setImageResource(R.drawable.msc_on);
				}
				if(Integer.parseInt(arry[17])==1){
					vibrate=true;
					vibIcon.setImageResource(R.drawable.vib_on);
				}
				if(Integer.parseInt(arry[21])==1){
					microphone=true;
					micIcon.setImageResource(R.drawable.mic_on);
				}
			}
			username = arry[13];
			difficulty = Integer.parseInt(arry[5]);
			showDifficulty();
			
			String eq = arry[1];
			for(int i = 0; i < eq.length(); i++){
				if (eq.charAt(i)-48 == 1) addSub.setChecked(true);
				else if (eq.charAt(i)-48 ==2) mulDiv.setChecked(true); 
				else if (eq.charAt(i)-48 ==3) exponent.setChecked(true);
				else if (eq.charAt(i)-48 ==4) percent.setChecked(true); 
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//retrieve pro data and update UI if it exists
		try{
			fip = openFileInput (FILEPRO);
			Scanner inp = new Scanner(fip);
			inp.next(); inp.next(); inp.next();
			String bgPath = inp.next();
			if (bgPath.equals("bg1")) bg5.setImageResource(R.drawable.bg1);
			else if (bgPath.equals("bg2")) bg5.setImageResource(R.drawable.bg2);
			else if (bgPath.equals("bg3")) bg5.setImageResource(R.drawable.bg3);
			else if (bgPath.equals("default")) bg5.setImageResource(R.drawable.lines_bg_lr);
			else bg5.setImageURI(Uri.parse(bgPath));
			selectedBg = bgPath;
		} catch (FileNotFoundException e) {
			sPage.setVisibility(View.GONE);
			userSelect.setVisibility(View.GONE);
			e.printStackTrace();
		}

		
		sndIcon.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(!sound) {
        			sndIcon.setImageResource(R.drawable.snd_on);
        			sound = true;
        		}
        		else{ 
        			sndIcon.setImageResource(R.drawable.snd_off);
        			sound = false;
        		}
        	}
		});
		
		mscIcon.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(!music) {
        			mscIcon.setImageResource(R.drawable.msc_on);
        			music = true;
        		}
        		else{ 
        			mscIcon.setImageResource(R.drawable.msc_off);
        			music = false;
        		}
        	}
		});
		
		vibIcon.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(!vibrate) {
        			vibIcon.setImageResource(R.drawable.vib_on);
        			vibrate = true;
        		}
        		else{ 
        			vibIcon.setImageResource(R.drawable.vib_off);
        			vibrate = false;
        		}
        	}
		});
		
		micIcon.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(!microphone) {
        			micIcon.setImageResource(R.drawable.mic_on);
        			microphone = true;
        		}
        		else{ 
        			micIcon.setImageResource(R.drawable.mic_off);
        			microphone = false;
        		}
        	}
		});
		
		d1.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v)
          {
        	  difficulty = 1;
        	  if(Integer.parseInt(arry[7])>9) Toast.makeText(getApplicationContext(), R.string.too_easy,Toast.LENGTH_SHORT).show();
              showDifficulty();
          }
        });
        d2.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v)
          {
        	  difficulty = 2;
        	  if(Integer.parseInt(arry[7])>14) Toast.makeText(getApplicationContext(), R.string.too_easy,Toast.LENGTH_SHORT).show();
              showDifficulty();
          }
        });
        d3.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v)
          {
        	  difficulty = 3;
        	  if(Integer.parseInt(arry[7])>19) Toast.makeText(getApplicationContext(), R.string.too_easy,Toast.LENGTH_SHORT).show();
        	  if(Integer.parseInt(arry[7])<5) Toast.makeText(getApplicationContext(), R.string.too_difficult,Toast.LENGTH_SHORT).show();
              showDifficulty();
          }
        });
        d4.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v)
          {
        	  difficulty = 4;
        	  if(Integer.parseInt(arry[7])<10) Toast.makeText(getApplicationContext(), R.string.too_difficult,Toast.LENGTH_SHORT).show();
              showDifficulty();
          }
        });
        d5.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View v)
          {
            difficulty = 5;
            if(Integer.parseInt(arry[7])<15) Toast.makeText(getApplicationContext(), R.string.too_difficult,Toast.LENGTH_SHORT).show();
            showDifficulty();
          }
        });
        
        all.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(all.isChecked()){
					addSub.setChecked(true);
					mulDiv.setChecked(true);
					exponent.setChecked(true);
					percent.setChecked(true);
				}else{
					addSub.setChecked(false);
					mulDiv.setChecked(false);
					exponent.setChecked(false);
					percent.setChecked(false);
				}
				
			}
		});
		
		done.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){ 
        		String eq = "", snd="0", msc="0", vib="0", mic="0";
        		
        		//equation
        		if (addSub.isChecked()) eq+= "1";       		
        		if (mulDiv.isChecked()) eq += "2";       		
        		if (exponent.isChecked()) eq += "3";
        		if (percent.isChecked()) eq += "4";
        		if (eq.equals("")){
        			eq = "1";
        			Toast.makeText(getApplicationContext(), R.string.eqn_set_to_default,Toast.LENGTH_SHORT).show();
        		}
        		
        		//sound
        		if (sound) snd="1";
        		
        		//music
        		if (music) msc="1";
        		
        		//vibrate
        		if (vibrate) vib="1";
        		
        		//microphone
        		if (microphone) mic="1";
        		
        		arry[1]=eq; arry[3]=snd+""; arry[5]=difficulty+""; arry[15]=msc; arry[17]=vib; arry[21]=mic;
        		String content = "";
        		for (int i= 0; i<FILESIZE; i++){
        			content += arry[i]+" ";
        		}
        		
        		//save file
        		try {
        			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0)); 
        			out.write(content);
        			out.close();
            	} catch (IOException e) {
            		done.setText("FAIL:"+e.toString());
            		e.printStackTrace();                  
            	}
        		//save background
        		try {
        			FileInputStream f2 = openFileInput(FILEPRO);
        			Scanner in = new Scanner(f2);
        			String a = in.next()+" "+in.next()+" "+in.next()+" "+selectedBg+" \n"; in.nextLine();
        			while (in.hasNextLine()){
        				a += in.nextLine() + " \n";
        			}
        			System.out.println(a);
        			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEPRO,0)); 
        			out.write(a);
        			out.close();        			
            	} catch (IOException e) {
            		e.printStackTrace();                  
            	}
        		finish();
        	}
        });
        
		bg1.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		selectedBg = "bg1";
        		bg5.setImageResource(R.drawable.bg1);
        	}
		});
		bg2.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		selectedBg = "bg2";
        		bg5.setImageResource(R.drawable.bg2);
        	}
		});
		bg3.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		selectedBg = "bg3";
        		bg5.setImageResource(R.drawable.bg3);
        	}
		});
		bg4.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		selectedBg = "default";
        		bg5.setImageResource(R.drawable.lines_bg_lr);
        	}
		});
		
		customImage.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
				Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, REQUEST_MEDIA);
        	}
		});
		
		sPage.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		if(firstPage){
        			firstPage = false;
        			sPage.setText(R.string.game_settings);
        			page1.setVisibility(View.GONE);
        			page2.setVisibility(View.VISIBLE);
					bg1.setImageResource(R.drawable.bg1);
					bg2.setImageResource(R.drawable.bg2);
					bg3.setImageResource(R.drawable.bg3);
					bg4.setImageResource(R.drawable.lines_bg_lr);
        		}else{
        			firstPage = true;
        			sPage.setText(R.string.personalize);
        			page2.setVisibility(View.GONE);
        			page1.setVisibility(View.VISIBLE);
        		}
        	}
		});
		
		userSelect.setOnClickListener (new View.OnClickListener(){
        	@Override
			public void onClick (View v){
        		setUser();
        	}
		});
		
		//update ui to display selected image
		mCheckImageChosen = new Runnable() {   
	    	@Override
			public void run() {
	    		if(imageChosen){ 
	    			bg5.setImageBitmap(myImage);
	    			imageChosen = false;
	    		}
	    		mHandler.postDelayed(this,1000);
	    	}       	
	    };
	    mHandler.postDelayed(mCheckImageChosen, 2000);
	}
	
	@Override
	//get path to user's custom image
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == REQUEST_MEDIA && resultCode == RESULT_OK) {
	        Uri contentUri = data.getData();
	        String[] proj = { MediaColumns.DATA };
	        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
	        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	        cursor.moveToFirst();
	        String imagePath =  cursor.getString(column_index);
	        selectedBg= imagePath;
	        myImage = BitmapFactory.decodeFile(imagePath);
	        imageChosen = true;
	    }
	}
	
	public void setUser(){
		final String [] ubg = new String[4];
		final String [] uData = new String[4];
		final String usr1Name, usr2Name, usr3Name, curBg, data;
		final int userNum;
		try{
			//get current users data
			FileInputStream fi2 = openFileInput(FILENAME);
			Scanner in2 = new Scanner(fi2);
			data = in2.nextLine();
			fi2.close();
			//Get profiles data
			FileInputStream fi = openFileInput(FILEPRO);
			Scanner in = new Scanner(fi);
			in.next();
			userNum  = Integer.parseInt(in.next()); in.next(); curBg = in.next(); 
			usr1Name = in.next(); in.next(); ubg[1]= in.nextLine(); uData[1] = in.nextLine();
			usr2Name = in.next(); in.next(); ubg[2]= in.nextLine(); uData[2] = in.nextLine();
			usr3Name = in.next(); in.next(); ubg[3]= in.nextLine(); uData[3] = in.nextLine();
			//display user profile names in dialog
			String usr[] = {usr1Name.replace("_"," "),usr2Name.replace("_"," "),usr3Name.replace("_"," ")};
			usr[userNum-1] = username.replace("_"," ")+" ("+this.getString(R.string.current)+")";
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle(R.string.select_user_profile) ;
	        builder.setItems(usr, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int pos) {
					String prevUser= username+" background: "+curBg+" \n"+data+" \n";
					String header = "";
					if (pos==0){
						header = "currentUser: 1 curBackground: "+ubg[1]+" \n";
						try {
							System.out.println(uData[1]);
		        			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0)); 
		        			out.write(uData[1]);
		        			out.close();        			
		            	} catch (IOException e) {e.printStackTrace(); }
					}
					if (pos==1){
						header = "currentUser: 2 curBackground: "+ubg[2]+" \n";
						try {
							System.out.println(uData[2]);
		        			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0)); 
		        			out.write(uData[2]);
		        			out.close(); 
		            	} catch (IOException e) {e.printStackTrace(); }
					}
					if (pos==2){
						header = "currentUser: 3 curBackground: "+ubg[3]+" \n";
						try {
							System.out.println(uData[3]);
		        			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0)); 
		        			out.write(uData[3]);
		        			out.close();
		            	} catch (IOException e) {e.printStackTrace(); }
					}

					if (userNum == 1){
						String newData = header;
						newData += prevUser;
						newData += usr2Name + " background: "+ubg[2]+" \n"+uData[2]+" \n";
						newData += usr3Name + " background: "+ubg[3]+" \n"+uData[3]+" \n";	
						try {
							System.out.println(newData);
		        			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEPRO,0)); 
		        			out.write(newData);
		        			out.close();        			
		            	} catch (IOException e) {e.printStackTrace(); }
					}
					if (userNum == 2){
						String newData = header;
						newData += usr1Name + " background: "+ubg[1]+" \n"+uData[1]+" \n";
						newData += prevUser;
						newData += usr3Name + " background: "+ubg[3]+" \n"+uData[3]+" \n";
						try {
							System.out.println(newData);
		        			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEPRO,0)); 
		        			out.write(newData);
		        			out.close();        			
		            	} catch (IOException e) {e.printStackTrace(); }
					}
					if (userNum == 3){
						String newData = header;
						newData += usr1Name + " background: "+ubg[1]+" \n"+uData[1]+" \n";							
						newData += usr3Name + " background: "+ubg[2]+" \n"+uData[2]+" \n";
						newData += prevUser;
						try {
							System.out.println(newData);
		        			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEPRO,0)); 
		        			out.write(newData);
		        			out.close();        			
		            	} catch (IOException e) {e.printStackTrace(); }
					}
					startActivity(new Intent(getApplicationContext(), CreateSettingsActivity.class));
        			finish();
				}
	        });
	        builder.show();
		}catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	public void showDifficulty()
	  {
	    if (this.difficulty == 1)
	    {
	      this.d1.setImageResource(R.drawable.mi1);
	      this.d2.setImageResource(R.drawable.mi0);
	      this.d3.setImageResource(R.drawable.mi0);
	      this.d4.setImageResource(R.drawable.mi0);
	      this.d5.setImageResource(R.drawable.mi0);
	    }
	    if (this.difficulty == 2)
	    {
	      this.d1.setImageResource(R.drawable.mi1);
	      this.d2.setImageResource(R.drawable.mi2);
	      this.d3.setImageResource(R.drawable.mi0);
	      this.d4.setImageResource(R.drawable.mi0);
	      this.d5.setImageResource(R.drawable.mi0);
	    }
	    if (this.difficulty == 3)
	    {
	      this.d1.setImageResource(R.drawable.mi1);
	      this.d2.setImageResource(R.drawable.mi2);
	      this.d3.setImageResource(R.drawable.mi3);
	      this.d4.setImageResource(R.drawable.mi0);
	      this.d5.setImageResource(R.drawable.mi0);
	    }
	    if (this.difficulty == 4)
	    {
	      this.d1.setImageResource(R.drawable.mi1);
	      this.d2.setImageResource(R.drawable.mi2);
	      this.d3.setImageResource(R.drawable.mi3);
	      this.d4.setImageResource(R.drawable.mi4);
	      this.d5.setImageResource(R.drawable.mi0);
	    }
	    if (this.difficulty == 5)
	    {
	      this.d1.setImageResource(R.drawable.mi1);
	      this.d2.setImageResource(R.drawable.mi2);
	      this.d3.setImageResource(R.drawable.mi3);
	      this.d4.setImageResource(R.drawable.mi4);
	      this.d5.setImageResource(R.drawable.mi5);
	    }
	  }
}
