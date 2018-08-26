package com.blackstar.math4brain;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackstar.math4brain.util.IabHelper;
import com.blackstar.math4brain.util.IabResult;
import com.blackstar.math4brain.util.Inventory;
import com.blackstar.math4brain.util.Purchase;
import com.facebook.AccessToken;
import com.flurry.android.FlurryAgent;
import com.tapjoy.TapjoyConnect;
import com.tapjoy.TapjoyNotifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;


public class MainMenu extends AppCompatActivity implements TapjoyNotifier {
    int minPointsPro = 5000, points, FILESIZE = 25, tries = 3;
    MediaPlayer mp3Bg;
    GameSettings gSettings;
    String FILENAME = "m4bfile1", FILEPRO = "m4bfilePro1", FILEMULT = "m4bfileMul", FILETRACK = "m4bfileTrack";
    boolean resumable = false, pro = false, blackberry = false, amazon = false,
            connection = true, billUsed = false, openPurchase = false;
    TextView tv;
    String[] gFile = new String[FILESIZE];
    String sku = "pro_version";
    IabHelper mHelper;
    Activity activity = this;
    LinearLayout menuSpace;
    LinearLayout tipLayout;
    MediaPlayer mp3Click;
    Typeface myTypeface;
    long[][] dataT = new long[365][2];


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        final ImageButton practice = findViewById(R.id.buttonPractice);
        final ImageButton minRun = findViewById(R.id.button60SRun);
        final ImageButton challenge = findViewById(R.id.buttonChallenge);
        final ImageButton faceOff = findViewById(R.id.buttonFaceOff);
        final ImageButton settings = findViewById(R.id.buttonSettings);
        final ImageButton userInfo = findViewById(R.id.buttonUserInfo);
        final ImageView logo = findViewById(R.id.imageViewLogo);
        final TextView version = findViewById(R.id.version);

        menuSpace = findViewById(R.id.linearLayoutMenu);
        tipLayout = findViewById(R.id.linearLayoutTip);
        mp3Click = MediaPlayer.create(this, R.raw.click);
        gSettings = new GameSettings();
        tv = findViewById(R.id.textViewTip);
        ImageView tipImg = findViewById(R.id.imageViewTip);
        myTypeface = Typeface.createFromAsset(getAssets(), "fawn.ttf");

        // Initialize the Facebook SDK,
        //FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(getApplication());

        if ((int) (Math.random() * 4) == 0) mp3Bg = MediaPlayer.create(this, R.raw.main_bg_music2);
        else mp3Bg = MediaPlayer.create(this, R.raw.main_bg_music);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) connection = false;

        if (android.os.Build.BRAND.toLowerCase().contains("blackberry")) blackberry = true;
        else if (android.os.Build.MODEL.toLowerCase().contains("kindle")) amazon = true;

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey("purchase_dialog")) {
            if (extras.getString("purchase_dialog").equals("true")) openPurchase = true;
        }

        //start tapjoy
        TapjoyConnect.requestTapjoyConnect(getApplicationContext(), "d199877d-7cb0-4e00-934f-d04eb573aa47", "1SgBmHKgJUk8cw9IOY3s");

        //if file not found/not created yet, jump to next catch block
        //if file is old format convert to new format
        try {
            //read
            FileInputStream fi = openFileInput(FILENAME);
            Scanner in = new Scanner(fi);
            int i = -2;
            String scan = "";
            while (in.hasNext() && !scan.equals("null")) {
                i++;
                scan = in.next();
            }
            in.close();
            if (i < 15) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            String myUID = UUID.randomUUID().toString().substring(0, 10);
            try {
                Toast.makeText(getApplicationContext(), R.string.welcome_to_m4b, Toast.LENGTH_LONG).show();
                String c1 = "Type: 12  Sound: 1  Difficulty: 2 ";
                String c2 = "Level: 1  Scores: 0 0 0 ";
                String c3 = " User:_no_name  music: 1  vibrate: 1 rate_popup: 0 mic: 0 email";
                OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME, 0));
                out.write(c1 + c2 + myUID + c3);
                out.close();
                mp3Bg.start();
                mp3Bg.setLooping(true);
                startActivity(new Intent(getApplicationContext(), InitialEvaluationActivity.class));
            } catch (IOException z) {
                z.printStackTrace();
            }
        }

        //read main file
        try {
            FileInputStream fi = openFileInput(FILENAME);
            Scanner in = new Scanner(fi);
            int i = 0;
            while (in.hasNext()) {
                gFile[i] = in.next();
                i++;
            }
            gSettings.sound = Integer.parseInt(gFile[3]);
            gSettings.music = Integer.parseInt(gFile[15]);
            points = Integer.parseInt(gFile[9]);
            if (points >= minPointsPro) pro = true;
            if (gSettings.music == 1) {
                try {
                    mp3Bg.start();
                    mp3Bg.setLooping(true);
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException n) {
            Toast.makeText(getApplicationContext(), R.string.file_is_corrupt, Toast.LENGTH_LONG).show();
        }

        //read pro file
        File file = getApplicationContext().getFileStreamPath(FILEPRO);
        if (file.exists()) {
            try {
                logo.setImageResource(R.drawable.math4thebrain_pro_logo);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            pro = true;
        } else {
            if (pro) {
                createPro();
            }
        }


        //read progress tracking data
        FileInputStream ft;
        try {
            ft = openFileInput(FILETRACK);
            Scanner in = new Scanner(ft);
            int i = 0;
            while (in.hasNext()) {
                dataT[i][0] = in.nextLong();
                dataT[i][1] = in.nextLong();
                i++;
            }
        } catch (FileNotFoundException e) {
            try {
                OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILETRACK, 0));
                out.write("0 0 \n");
                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        //show brain fact
        Tips tp = new Tips();
        tv.setText(tp.getTip(pro, getResources()));
        tipImg.setImageResource(tp.getImgResource());

        //user data to report to flurry analytics
        final Map<String, String> userParams = new HashMap<>();
        userParams.put("Name", gFile[13]);
        userParams.put("Type", gFile[1]);
        userParams.put("Difficulty", gFile[5]);
        userParams.put("Level", gFile[7]);

        //In app billing setup
        if (!blackberry && connection && !pro) {
            String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0AJ6MHObBIHIoexJCqMlkm2ZMfZ/";
            base64EncodedPublicKey += "nV5Z1nR1IVRlkFT6iKLk6VsS/mh90HlDzh9QRELNd1Fw1gix3Y0jelNNAU3h6UQE1964HGDCu1PBtZadmlt";
            base64EncodedPublicKey += "RX4ofD+5OFgBElmTDFAuhCHxeUFsY0IM+OsPSYYp5tNu0UvA+4NakRVR33JVwOWzTrrUcZaRRsd1mYgz47ihvotn/";
            base64EncodedPublicKey += "d5Lhm8HnERnZLKYo2jKfwZYg9ped11lafvfsJu2dZC2gJuRvY+MzQZ9bo28Fm+cFT6MMU+FhgMnctzoXQE6fgit/";
            base64EncodedPublicKey += "gXyJMUEypwR6whDufn/LqZTPrdYWqPl2WVMwUUkPHjYMqyUYELaTCQIDAQAB";
            // compute your public key and store it in base64EncodedPublicKey
            mHelper = new IabHelper(this, base64EncodedPublicKey);

            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                @Override
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        // Oh no, there was a problem.
                        Log.d("INAPP BILLING", "Problem setting up In-app Billing: " + result);
                        connection = false;
                    } else {
                        // Hooray, IAB is fully set up!
                        // if user has requested a purchase then open in-app billing else check to see if already purchased
                        if (openPurchase) {
                            try {
                                mHelper.launchPurchaseFlow(activity, sku, 10001, mPurchaseFinishedListener, gFile[13]);
                                billUsed = true;
                            } catch (NullPointerException e) {
                                finish();
                            }
                        } else mHelper.queryInventoryAsync(mGotInventoryListener);
                    }
                }
            });
        }


        //Get Version and display
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version.setText("v" + pInfo.versionName);
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }


        practice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateTransition(PracticeActivity.class);
                //check
                /*try {
                    FileInputStream i = openFileInput(FILENAME);
        			Scanner n = new Scanner(i);
        			String ct="";
        			while (n.hasNextLine()){
        				ct += n.nextLine();
        			}
        			tv.setText(ct);
        		} catch (FileNotFoundException e1) {
        			e1.printStackTrace();
        		}*/
            }
        });

        minRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryAgent.logEvent("Minute_Run", userParams);
                animateTransition(MinuteRunActivity.class);
            }
        });

        challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryAgent.logEvent("Challenge");
                if (resumable) animateTransition(LevelSelectActivity.class);
                else animateTransition(ChallengeActivity.class);
            }
        });

        faceOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiplayerDialog();
                FlurryAgent.logEvent("Multiplayer");
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryAgent.logEvent("Settings");
                animateTransition(CreateSettingsActivity.class);
            }
        });

        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryAgent.logEvent("User_info");
                animateTransition(UserActivity.class);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        if (AccessToken.getCurrentAccessToken() != null)
            menu.getItem(0).setTitle(R.string.sign_out);
        else
            menu.getItem(0).setTitle(R.string.sign_in);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.rank:
                Intent i = new Intent(getApplicationContext(), UserActivity.class);
                i.putExtra("view_rank", true);
                startActivity(i);
                return true;
            case R.id.feedback:
                leaveFeedback();
                return true;
            case R.id.rate:
                rateApp();
                return true;
            case R.id.getPoints:
                getPoints();
                return true;
            case R.id.getPro:
                if (!blackberry && !amazon)
                    mHelper.launchPurchaseFlow(activity, sku, 10001, mPurchaseFinishedListener, gFile[13]);
                return true;
            case R.id.sync:
                Intent s = new Intent(getApplicationContext(), LoginActivity.class);
                s.putExtra("userData", gFile);
                startActivity(s);
                return true;
            case R.id.share:
                Intent j = new Intent(getApplicationContext(), UserActivity.class);
                j.putExtra("share", true);
                startActivity(j);
                return true;
            case R.id.friends:
                Intent ki = new Intent(getApplicationContext(), FriendActivity.class);
                ki.putExtra("id",gFile[12]);
                startActivity(ki);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        //set sound and check if qualified for pro
        System.gc();
        reload();
        SyncDatabase sync = new SyncDatabase(gFile, this);
        sync.execute();
        int tPoints, ratePopup = 0;
        tPoints = Integer.parseInt(gFile[9]);
        gSettings.music = Integer.parseInt(gFile[15]);
        gSettings.sound = Integer.parseInt(gFile[3]);
        resumable = Integer.parseInt(gFile[7]) > 1;
        try {
            if (gSettings.music == 1) {
                mp3Bg.start();
                mp3Bg.setLooping(true);
            } else {
                if (mp3Bg.isPlaying()) mp3Bg.pause();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        //Check and restart if pro version has been unlocked
        if (!pro && tPoints >= minPointsPro) {
            startActivity(new Intent(getApplicationContext(), MainMenu.class));
            finish();
        }
        //check if rating is active
        try {
            ratePopup = Integer.parseInt(gFile[19]);
        } catch (Exception e) {
            gFile[18] = "rate_popup:";
            gFile[19] = "0";
            write();
        }

        //set feedback frequency
        int fb = (int) (Math.random() * (70));

        if ((fb == 4 || fb == 5) && ratePopup == 0 && Integer.parseInt(gFile[7]) > 2 && connection) {
            //Google Play rating dialog
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialogbox);
            TextView title = dialog.findViewById(R.id.textViewTitle);
            title.setVisibility(View.VISIBLE);
            title.setText(this.getString(R.string.rate));
            TextView body =  dialog.findViewById(R.id.textViewMsg);
            body.setText(R.string.rate_msg);
            Button dialogButton = dialog.findViewById(R.id.button1);
            dialogButton.setVisibility(View.VISIBLE);
            dialogButton.setText(R.string.rate);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rateApp();
                    dialog.dismiss();
                }
            });
            Button dialogButton2 = dialog.findViewById(R.id.button2);
            dialogButton2.setVisibility(View.VISIBLE);
            dialogButton2.setText(R.string.perhaps_later);
            dialogButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            Button dialogButton3 = dialog.findViewById(R.id.button3);
            dialogButton3.setVisibility(View.VISIBLE);
            dialogButton3.setText(R.string.no_thanks);
            dialogButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gFile[19] = "1";
                    write();
                    dialog.dismiss();
                }
            });
            dialog.show();

        }

        if (fb == 6 && resumable && connection) {
            //Leave email feedback dialog
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialogbox);
            dialog.setCancelable(false);
            TextView title = dialog.findViewById(R.id.textViewTitle);
            title.setVisibility(View.VISIBLE);
            title.setText(this.getString(R.string.leave_feedback));
            TextView body = dialog.findViewById(R.id.textViewMsg);
            body.setText(R.string.feedback);
            Button dialogButton = dialog.findViewById(R.id.button1);
            dialogButton.setVisibility(View.VISIBLE);
            dialogButton.setText(R.string.leave_feedback);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    leaveFeedback();
                    dialog.dismiss();
                }
            });
            Button dialogButton2 = dialog.findViewById(R.id.button2);
            dialogButton2.setVisibility(View.VISIBLE);
            dialogButton2.setText(R.string.perhaps_later);
            dialogButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

        if ((fb == 7 || fb == 11 ) && !blackberry && points > 100 && connection) {
            if (getResources().getConfiguration().locale.toString().contains("en")) {
                //open new app dialog
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialogbox);
                TextView body = dialog.findViewById(R.id.textViewMsg);
                body.setText("The developer of this app has selected you to test a new app. \n" +
                        "Are you interested in learning how to code?"); //TODO:Update Text
                Button dialogButton = dialog.findViewById(R.id.button1);
                dialogButton.setVisibility(View.VISIBLE);
                dialogButton.setText(R.string.yes);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/apps/testing/com.amensah.easycoder")));
                        dialog.dismiss();
                    }
                });
                Button dialogButton2 = dialog.findViewById(R.id.button2);
                dialogButton2.setVisibility(View.VISIBLE);
                dialogButton2.setText(R.string.no);
                dialogButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }


        if ((fb == 8) && !blackberry && points > 0 && connection && !pro && !billUsed) {
            //open dialog for purchase request
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialogbox);
            TextView title = dialog.findViewById(R.id.textViewTitle);
            title.setVisibility(View.VISIBLE);
            title.setText(this.getString(R.string.get_pro_version));
            TextView body = dialog.findViewById(R.id.textViewMsg);
            body.setText("");
            if (getResources().getConfiguration().locale.toString().contains("en"))
                body.setBackgroundResource(R.drawable.pro_ad);
            else
                body.setText(R.string.pro_features);
            Button dialogButton = dialog.findViewById(R.id.button1);
            dialogButton.setVisibility(View.VISIBLE);
            dialogButton.setText(R.string.yes);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHelper.launchPurchaseFlow(activity, sku, 10001, mPurchaseFinishedListener, gFile[13]);
                    dialog.dismiss();
                }
            });
            Button dialogButton2 = dialog.findViewById(R.id.button2);
            dialogButton2.setVisibility(View.VISIBLE);
            dialogButton2.setText(R.string.no);
            dialogButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

        TapjoyConnect.getTapjoyConnectInstance().getTapPoints(this);

        animateTransition(null);
        invalidateOptionsMenu();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (mp3Bg.isPlaying()) mp3Bg.pause();
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mp3Bg.stop();
        } catch (Exception E) {
            E.printStackTrace();
        }
        //stop tapjoy
        TapjoyConnect.getTapjoyConnectInstance().sendShutDownEvent();
        //set game reminder for a week
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 002000, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 604800000, pendingIntent);
        //set reminder for 3 days
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 001000, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 259200000, pendingIntent2);
        //stop flurry
        FlurryAgent.onEndSession(this);
        //unbind inApp billing service
        if (connection) {
            if (mHelper != null) mHelper.dispose();
            mHelper = null;
        }
    }

    public void multiplayerDialog() {
        //Multiplayer: 1 or 2;
        clickSound();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);
        TextView body = dialog.findViewById(R.id.textViewMsg);
        ImageButton dialogButton = dialog.findViewById(R.id.imageButton1);
        ImageButton dialogButton2 = dialog.findViewById(R.id.imageButton2);
        dialogButton.setVisibility(View.VISIBLE);
        dialogButton2.setVisibility(View.VISIBLE);
        body.setText(R.string.one_or_two_devices);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSound();
                FlurryAgent.logEvent("multiplayer1");
                startActivity(new Intent(getApplicationContext(), MultiplayerActivity.class));
                dialog.dismiss();
            }
        });
        dialogButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSound();
                FlurryAgent.logEvent("multiplayer2");
                if (pro)
                    startActivity(new Intent(getApplicationContext(), Multiplayer2Activity.class));
                else m2Dialog();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public void m2Dialog() {
        String date = "1234", msg = getResources().getString(R.string.initial_try);
        //check if user has exceeded his trial limit
        try {
            //read
            FileInputStream fi = openFileInput(FILEMULT);
            Scanner in = new Scanner(fi);
            date = in.next();
            tries = Integer.parseInt(in.next());
            in.close();
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = df.format(c.getTime());
            if (!date.equals(currentDate)) {
                tries = 3;
                try {
                    OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEMULT, 0));
                    out.write(currentDate + " " + tries);
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (tries > 1)
                msg = getResources().getString(R.string.you_have) + " " + tries + " " + getResources().getString(R.string.games_avail);
            else if (tries == 1) msg = getResources().getString(R.string.last_game);
            else msg = getResources().getString(R.string.no_more_games);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEMULT, 0));
                out.write(date + " " + tries);
                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);
        dialog.setCancelable(false);
        TextView body = dialog.findViewById(R.id.textViewMsg);
        Button dialogButton = dialog.findViewById(R.id.button1);
        dialogButton.setVisibility(View.VISIBLE);
        dialogButton.setText(R.string.ok);
        body.setText(msg);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tries > 0) {
                    startActivity(new Intent(getApplicationContext(), Multiplayer2Activity.class));
                }
                dialog.dismiss();
            }
        });
        dialog.show();
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
        updateProgressTracker();
    }

    public void write() {
        try {
            String data = "";
            for (int i = 0; i < FILESIZE; i++) data += gFile[i] + " ";
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME, 0));
            out.write(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateProgressTracker() {
        //update progress data if enough time has passed
        long mins2 = 120000, days1 = 86400000;
        if (dataT[0][0] < System.currentTimeMillis() - mins2) {
            String data = "";
            int pts, n, average, level;
            long myGameScore = 0;
            pts = Integer.parseInt(gFile[9]);
            n = Integer.parseInt(gFile[10]);
            level = Integer.parseInt(gFile[7]);
            if (pts != 0 && n != 0) {
                average = pts / n;
                myGameScore = (level * 10000) + (average * 100) + (pts);
            }
            if (dataT[0][0] < System.currentTimeMillis() - days1)
                data += System.currentTimeMillis() + " " + myGameScore + " \n";
            else
                dataT[0][1] = myGameScore;
            try {
                for (int i = 0; i < 365 - 1; i++) data += dataT[i][0] + " " + dataT[i][1] + " \n";
                OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILETRACK, 0));
                out.write(data);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void getUpdatePoints(String currency, int pointTotal) {
        if (pointTotal > 0) {
            Intent i = new Intent(getApplicationContext(), TapJoyLauncher.class);
            i.putExtra("view_offers", "false");
            startActivity(i);
        }
    }

    @Override
    public void getUpdatePointsFailed(String error) {
        // Do nothing.
    }

    public void createPro() {
        Toast.makeText(getApplicationContext(), R.string.pro_version_unlocked, Toast.LENGTH_LONG).show();
        String myUID2 = UUID.randomUUID().toString().substring(0, 10);
        String myUID3 = UUID.randomUUID().toString().substring(0, 10);
        try {
            String cur = "currentUser: 1 curBackground: bg1 \n";
            String a1a = "User:_no_name background: bg1 \n";
            String a1b = "Type: 12  Sound: 1  Difficulty: 2 Level: 1  Scores: 0 0 0 UUID User:_no_name  music: 1  vibrate: 1 \n";
            String b1a = "User:_no_name background: bg1 \n";
            String b1b = "Type: 12  Sound: 1  Difficulty: 2 Level: 1  Scores: 0 0 0 " + myUID2 + " User:_no_name  music: 1  vibrate: 1 \n";
            String c1a = "User:_no_name background: bg1 \n";
            String c1b = "Type: 12  Sound: 1  Difficulty: 2 Level: 1  Scores: 0 0 0 " + myUID3 + " User:_no_name  music: 1  vibrate: 1 \n";
            String data = cur + a1a + a1b + b1a + b1b + c1a + c1b;
            OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILEPRO, 0));
            out.write(data);
            out.close();
        } catch (IOException z) {
            z.printStackTrace();
        }
    }

    public void clickSound() {
        if (this.gSettings.sound == 1) {
            try {
                this.mp3Click.start();
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
    }

    public void animateTransition(final Class intent) {
        if (intent != null) {
            clickSound();
            //close menu animation
            Animation aAnimation = new AlphaAnimation(1, 0);
            aAnimation.setDuration(700);
            tipLayout.startAnimation(aAnimation);
            Animation newAnimation = new TranslateAnimation(0, 0, 0, 1100);
            newAnimation.setDuration(700);
            newAnimation.setInterpolator(new AccelerateInterpolator());
            menuSpace.startAnimation(newAnimation);
            newAnimation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    tipLayout.setVisibility(View.INVISIBLE);
                    menuSpace.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(getApplicationContext(), intent));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationStart(Animation animation) {
                }
            });
        } else {
            //open menu animation
            Animation newAnimation = new TranslateAnimation(0, 0, 1100, 0);
            newAnimation.setDuration(700);
            newAnimation.setInterpolator(new DecelerateInterpolator());
            tipLayout.setVisibility(View.VISIBLE);
            menuSpace.setVisibility(View.VISIBLE);
            menuSpace.startAnimation(newAnimation);
            Animation aAnimation = new AlphaAnimation(0, 1);
            aAnimation.setDuration(1000);
            tipLayout.startAnimation(aAnimation);
        }
    }

    public void leaveFeedback() {
        if(gFile[22] != null && gFile[22].contains("@")){
            Intent intent3 = new Intent(getApplicationContext(), SendMessage.class);
            intent3.putExtra("name", gFile[13]);
            intent3.putExtra("email",gFile[22]);
            startActivity(intent3);
        }else {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"blackstar.feedback@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject);
            i.putExtra(Intent.EXTRA_TEXT, "");
            try {
                startActivity(Intent.createChooser(i, getString(R.string.send_email_using)));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(MainMenu.this, R.string.no_email_client, Toast.LENGTH_SHORT).show();
            }
        }


    }

    public void rateApp() {
        try {
            if (amazon)
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.ca/Blackstar-Math-For-The-Brain/dp/B00DR7TK6I")));
            else if (blackberry)
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://appworld.blackberry.com/webstore/content/20484402/")));
            else
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.blackstar.math4brain")));
        } catch (Exception E) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.blackstar.math4brain")));
        }
        gFile[19] = "1";
        write();
    }

    public void getPoints() {
        if (!gFile[10].equals("0") && !blackberry) {
            Intent i = new Intent(getApplicationContext(), TapJoyLauncher.class);
            i.putExtra("view_offers", "true");
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), R.string.not_enough_points, Toast.LENGTH_SHORT).show();
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Log.d("INAPP BILLING", "Error purchasing: " + result);
            } else if (purchase.getSku().equals(sku)) {
                //update the UI
                createPro();
                startActivity(new Intent(getApplicationContext(), MainMenu.class));
                finish();
            }
        }
    };

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                Log.d("INAPP BILLING", "Error getting inventory: " + result);
            } else if (inventory.hasPurchase(sku) && !pro) {
                createPro();
                startActivity(new Intent(getApplicationContext(), MainMenu.class));
                finish();
            }
        }
    };

    // passes result to PurchaseFinishedListener
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("INAPP BILLING", "onActivityResult(" + requestCode + "," + resultCode + ","
                + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d("INAPP BILLING", "onActivityResult handled by IABUtil.");
        }
    }

}


