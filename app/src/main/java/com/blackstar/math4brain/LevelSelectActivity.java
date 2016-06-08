package com.blackstar.math4brain;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;

import com.blackstar.math4brain.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Albert Jr on 2016-05-24.
 */
public class LevelSelectActivity extends Activity {
    int curPage = 0, currLevel = 20, FILESIZE=25;
    ArrayList<Button> buttons;
    TableLayout levelsTable;
    String FILENAME = "m4bfile1";
    String[] gFile = new String[FILESIZE];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_select);

        buttons = new ArrayList<Button>();
        buttons.add((Button) findViewById(R.id.button_lv1));
        buttons.add((Button) findViewById(R.id.button_lv2));
        buttons.add((Button) findViewById(R.id.button_lv3));
        buttons.add((Button) findViewById(R.id.button_lv4));
        buttons.add((Button) findViewById(R.id.button_lv5));
        buttons.add((Button) findViewById(R.id.button_lv6));
        buttons.add((Button) findViewById(R.id.button_lv7));
        buttons.add((Button) findViewById(R.id.button_lv8));
        buttons.add((Button) findViewById(R.id.button_lv9));
        buttons.add((Button) findViewById(R.id.button_lv10));
        buttons.add((Button) findViewById(R.id.button_lv11));
        buttons.add((Button) findViewById(R.id.button_lv12));
        levelsTable = (TableLayout) findViewById(R.id.levelsTable);
        ImageButton next = (ImageButton) findViewById(R.id.buttonRight);
        ImageButton prev = (ImageButton) findViewById(R.id.buttonLeft);
        Button resumeBtn = (Button) findViewById(R.id.buttonResume);

        reload();
        currLevel = Integer.parseInt(gFile[7]);
        loadPage();

        if(currLevel<12){
            next.setVisibility(View.GONE);
            prev.setVisibility(View.GONE);
        }

        resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChallengeActivity.class));
                finish();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curPage<(currLevel/12)) {
                    curPage++;
                    Animation newAnimation = new TranslateAnimation(0, -500, 0, 0);
                    newAnimation.setInterpolator(new AccelerateInterpolator());
                    newAnimation.setDuration(300);
                    levelsTable.startAnimation(newAnimation);
                    newAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            loadPage();
                            Animation newAnimation2 = new TranslateAnimation(500, 0, 0, 0);
                            newAnimation2.setDuration(300);
                            newAnimation2.setInterpolator(new DecelerateInterpolator());
                            levelsTable.startAnimation(newAnimation2);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curPage>0) {
                    curPage--;
                    Animation newAnimation = new TranslateAnimation(0, 700, 0, 0);
                    newAnimation.setDuration(300);
                    levelsTable.startAnimation(newAnimation);
                    newAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            loadPage();
                            Animation newAnimation2 = new TranslateAnimation(-700, 0, 0, 0);
                            newAnimation2.setDuration(300);
                            levelsTable.startAnimation(newAnimation2);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            }
        });

        for(final Button lvlButton : buttons){
            lvlButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gFile[7] = lvlButton.getText() + "";
                    write();
                    startActivity(new Intent(getApplicationContext(), ChallengeActivity.class));
                    finish();
                }
            });
        }

    }

    public void loadPage(){
        int value;
        for(int i = 0; i<buttons.size(); i++){
            value = (curPage*12)+i+1;
            if(value<=currLevel){
                buttons.get(i).setText(""+value);
                buttons.get(i).setEnabled(true);
            }
            else{
                buttons.get(i).setText("-");
                buttons.get(i).setEnabled(false);
            }
        }
    }

    public void write(){
        try {
            String data="";
            for(int i=0; i<FILESIZE; i++) data+= gFile[i]+" ";
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0));
            out.write(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        try {
            FileInputStream fi = openFileInput(FILENAME);
            Scanner in = new Scanner(fi);
            int i = 0;
            while (in.hasNext()) {
                gFile[i] = in.next();
                i++;
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
