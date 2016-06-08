package com.blackstar.math4brain;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tapjoy.TapjoyAwardPointsNotifier;
import com.tapjoy.TapjoyConnect;
import com.tapjoy.TapjoyConnectFlag;
import com.tapjoy.TapjoyDisplayAdNotifier;
import com.tapjoy.TapjoyEarnedPointsNotifier;
import com.tapjoy.TapjoyFullScreenAdNotifier;
import com.tapjoy.TapjoyLog;
import com.tapjoy.TapjoyNotifier;
import com.tapjoy.TapjoySpendPointsNotifier;
import com.tapjoy.TapjoyVideoNotifier;
import com.tapjoy.TapjoyVideoStatus;
import com.tapjoy.TapjoyViewNotifier;
import com.tapjoy.TapjoyViewType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.Scanner;


public class TapJoyLauncher extends Activity implements View.OnClickListener, TapjoyNotifier, TapjoyFullScreenAdNotifier, TapjoySpendPointsNotifier, TapjoyDisplayAdNotifier, TapjoyAwardPointsNotifier, TapjoyEarnedPointsNotifier, TapjoyVideoNotifier 
{
	TextView pointsTextView;
	TextView totalPoints;

	int point_total;
	int GAMEPOINTS=0;
	int minPointsPro = 5000;
	int FILESIZE = 25;
	String currency_name;
	
	String displayText = "", displayPoints = "", FILENAME ="m4bfile1";
	boolean update_text = false;
	boolean earnedPoints = false;
	boolean viewOffers = false;
	
	// Display Ads.
	boolean update_display_ad = false;
	View adView;
	LinearLayout adLinearLayout;
	String [] fileData = new String[FILESIZE];
	
	public static final String TAG = "EASY APP";
	
	
	// Need handler for callbacks to the UI thread
	final Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tapjoy);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    if (extras.getString("view_offers").equals("true"))viewOffers=true;
		}
		
		//Get game points
		try {
			FileInputStream fi = openFileInput(FILENAME);
			Scanner in = new Scanner(fi);
			int i=0;
			while(in.hasNext()){
				fileData[i]=in.next();
				i++;
			}
			GAMEPOINTS = Integer.parseInt(fileData[9]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// Enables logging to the console.
		//TapjoyLog.enableLogging(true);
		
		// OPTIONAL: For custom startup flags.
		Hashtable<String, String> flags = new Hashtable<>();
		flags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");
		
		// Connect with the Tapjoy server.  Call this when the application first starts.
		// PRAMETERS; TAPJOY APP ID, SECRET KEY.
		TapjoyConnect.requestTapjoyConnect(getApplicationContext(),"d199877d-7cb0-4e00-934f-d04eb573aa47","1SgBmHKgJUk8cw9IOY3s");
		
		// Set our earned points notifier to this class.
		TapjoyConnect.getTapjoyConnectInstance().setEarnedPointsNotifier(this);
		
		// Set to get notifications on Tapjoy views.
		TapjoyConnect.getTapjoyConnectInstance().setTapjoyViewNotifier(new TapjoyViewNotifier()
		{
			@Override
			public void viewWillOpen(int viewType)
			{
				TapjoyLog.i(TAG, "viewWillOpen: " + getViewName(viewType));
			}
			
			@Override
			public void viewWillClose(int viewType)
			{
				TapjoyLog.i(TAG, "viewWillClose: " + getViewName(viewType));
			}
			
			@Override
			public void viewDidOpen(int viewType)
			{
				TapjoyLog.i(TAG, "viewDidOpen: " + getViewName(viewType));
			}
			
			@Override
			public void viewDidClose(int viewType)
			{
				TapjoyLog.i(TAG, "viewDidClose: " + getViewName(viewType));
			}
		});
		
		//relativeLayout = (RelativeLayout)findViewById(R.id.RelativeLayout01);
		adLinearLayout = (LinearLayout)findViewById(R.id.AdLinearLayout);
		// This bt_white launches the offers page when clicked.
		Button offers = (Button) findViewById(R.id.OffersButton);
		offers.setOnClickListener(this);
		
		//Close Screen
		Button closeScreen = (Button) findViewById(R.id.buttonClose);
		closeScreen.setOnClickListener(this);
		
//		// This bt_white retrieves the virtual currency info from the server.
//		Button getPoints = (Button) findViewById(R.id.GetPointsButton);
//		getPoints.setOnClickListener(this);
//		
//		// This spends virtual currency for this device.
//		Button spendPoints = (Button) findViewById(R.id.SpendPointsButton);
//		spendPoints.setOnClickListener(this);
		
//		// This spends virtual currency for this device.
//		Button awardPoints = (Button) findViewById(R.id.AwardPointsButton);
//		awardPoints.setOnClickListener(this);
		
//		// This bt_white displays the full screen ad when clicked.
//		Button getFeaturedApp = (Button) findViewById(R.id.GetFeaturedApp);
//		getFeaturedApp.setOnClickListener(this);
//		
//		// This bt_white displays the Daily Reward ad when clicked.
//		Button getDailyReward = (Button) findViewById(R.id.GetDailyReward);
//		getDailyReward.setOnClickListener(this);
//		
		// This bt_white displays the Display ad when clicked.
		//Button displayAd = (Button) findViewById(R.id.DisplayAD);
		//displayAd.setOnClickListener(this);
		
//		// Event tracking.
//		Button iapEvent = (Button) findViewById(R.id.IAPEventButton);
//		iapEvent.setOnClickListener(this);
		
		pointsTextView = (TextView)findViewById(R.id.textViewTJ);
		totalPoints = (TextView)findViewById(R.id.textViewTotalPt);
		Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fawn.ttf");
        pointsTextView.setTypeface(myTypeface);
        totalPoints.setTypeface(myTypeface);
		
		//Display ad
		TapjoyConnect.getTapjoyConnectInstance().enableDisplayAdAutoRefresh(true);
		TapjoyConnect.getTapjoyConnectInstance().getDisplayAd(this, this);
		//show points
		TapjoyConnect.getTapjoyConnectInstance().getTapPoints(this);
		//open offers enable this to open when launched
		if(viewOffers)TapjoyConnect.getTapjoyConnectInstance().showOffers();
	}
	
	@Override
	public void onClick(View v)
	{
		if (v instanceof Button) 
		{
			int id = v.getId();
			
			switch (id)
			{
					
				case R.id.OffersButton:
					// This will show Offers web view from where you can download the latest offers.
					TapjoyConnect.getTapjoyConnectInstance().showOffers();
					break;
					
				case R.id.buttonClose:
					// This will show Offers web view from where you can download the latest offers.
					finish();
					break;
					
//				case R.id.AwardPointsButton:
//					// Award virtual currency.
//					TapjoyConnect.getTapjoyConnectInstance().awardTapPoints(10, this);
//					break;					
					
//				case R.id.GetPointsButton:
//					// Retrieve the virtual currency amount from the server.
//					TapjoyConnect.getTapjoyConnectInstance().getTapPoints(this);
//					break;
//					
//				case R.id.SpendPointsButton:
//					// Spend virtual currency.
//					TapjoyConnect.getTapjoyConnectInstance().spendTapPoints(25, this);
//					break;				
//					
//				case R.id.GetFeaturedApp:
//					// Show the full screen ad.
//					TapjoyConnect.getTapjoyConnectInstance().getFullScreenAd(this);
//					break;
//					
//					
//				case R.id.DisplayAD:
					// Show the display/banner ad.
//					TapjoyConnect.getTapjoyConnectInstance().enableDisplayAdAutoRefresh(true);
//					TapjoyConnect.getTapjoyConnectInstance().getDisplayAd(this);
//					break;
					
//				case R.id.IAPEventButton:
//					TapjoyConnect.getTapjoyConnectInstance().sendIAPEvent("swag", 0.99f, 1, "usd");
//					break;
			}
		}
	}

	@Override
	protected void onDestroy()
	{
		// Send shutdown event tracking event.
		TapjoyConnect.getTapjoyConnectInstance().sendShutDownEvent();
		try {
			int avg, numplayed;
			avg = Integer.parseInt(fileData[9])/Integer.parseInt(fileData[10]);
			if(avg==0) avg=1;
			numplayed = (point_total/avg)+Integer.parseInt(fileData[10]);
			fileData[10]=numplayed+"";
			String data="";
			fileData[9]=(GAMEPOINTS+point_total)+"";
			for(String dat:fileData){
				if(dat!=null) data += dat+" ";
			}
			OutputStreamWriter out = new OutputStreamWriter(openFileOutput(FILENAME,0)); 
			out.write(data);
			out.close(); 
			TapjoyConnect.getTapjoyConnectInstance().spendTapPoints(point_total, this);
		} catch (IOException z) {
    		z.printStackTrace(); 
    	}
		
		super.onDestroy();
	}

	@Override
	protected void onResume()
	{
		TapjoyConnect.getTapjoyConnectInstance().getTapPoints(this);
		super.onResume();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		TapjoyConnect.getTapjoyConnectInstance().enableDisplayAdAutoRefresh(false);
	}
	
	// Create runnable for posting
	final Runnable mUpdateResults = new Runnable() 
	{
		@Override
		public void run() 
		{
			updateResultsInUi();
		}
	};
	
	private void updateResultsInUi() 
	{
		if (update_display_ad)
		{
			// Remove all subviews of our ad layout.
			adLinearLayout.removeAllViews();
			
			// Add the ad to our layout.
			adLinearLayout.addView(adView);
			
			update_display_ad = false;
		}
		
		// Back in the UI thread -- update our UI elements based on the data in mResults
		if (pointsTextView != null)
		{	
			// Update the display text.
			if (update_text)
			{
				pointsTextView.setText(displayText);
				update_text = false;
				totalPoints.setText(displayPoints);
			}
		}
	}

	
	//================================================================================
	// CALLBACK Methods
	//================================================================================
	// Notifier when TapjoyConnect.getTapPoints succeeds.
	@Override
	public void getUpdatePoints(String currencyName, int pointTotal)
	{
		Log.i(TAG, "currencyName: " + currencyName);
		Log.i(TAG, "pointTotal: " + pointTotal);
		
		currency_name = currencyName;
		point_total = pointTotal;
		
		update_text = true;
		
		if (earnedPoints)
		{
			displayText = displayText + "\n"+this.getString(R.string.collected)+" "+ currencyName + ": " + pointTotal;
			earnedPoints = false;
		}
		else
		{
			displayText = this.getString(R.string.collected)+" "+currencyName + ": " + pointTotal;
		}
		displayPoints = this.getString(R.string.total_points)+": "+(GAMEPOINTS+pointTotal);
		if ((GAMEPOINTS+pointTotal)<minPointsPro) displayPoints += "\n"+this.getString(R.string.you_are)+" "
		+(minPointsPro-GAMEPOINTS-pointTotal)+" "+this.getString(R.string.pts_away_from_unlock);
		
		// We must use a handler since we cannot update UI elements from a different thread.
		mHandler.post(mUpdateResults);
	}
	
	// Notifier triggered when TapjoyConnect.getTapPoints fails.
	@Override
	public void getUpdatePointsFailed(String error)
	{
		Log.i(TAG, "getTapPoints error: " + error);
		
		update_text = true;
		displayText = this.getString(R.string.unable_to_get_pts);
		
		// We must use a handler since we cannot update UI elements from a different thread.
		mHandler.post(mUpdateResults);
	}
	
	// Notifier for when spending virtual currency succeeds.
	@Override
	public void getSpendPointsResponse(String currencyName, int pointTotal)
	{
		Log.i(TAG, "currencyName: " + currencyName);
		Log.i(TAG, "pointTotal: " + pointTotal);
		
		update_text = true;
		displayText = currencyName + ": " + pointTotal;
		
		// We must use a handler since we cannot update UI elements from a different thread.
		mHandler.post(mUpdateResults);
	}

	
	// Notifier for when spending virtual currency fails.
	@Override
	public void getSpendPointsResponseFailed(String error)
	{
		Log.i(TAG, "spendTapPoints error: " + error);
		
		update_text = true;
		displayText = "Spend Tap Points: " + error;
		
		// We must use a handler since we cannot update UI elements from a different thread.
		mHandler.post(mUpdateResults);
	}

	// Notifier for a successful display ad.
	@Override
	public void getDisplayAdResponse(View view)
	{
		// Using screen width, but substitute for the any width.
		int desired_width = adLinearLayout.getMeasuredWidth();
		
		// Scale the display ad to fit incase the width is smaller than the display ad width.
		adView = scaleDisplayAd(view, desired_width);
		
		update_display_ad = true;
		
		// We must use a handler since we cannot update UI elements from a different thread.
		mHandler.post(mUpdateResults);
	}

	// Notifier for a failed display ad request.
	@Override
	public void getDisplayAdResponseFailed(String error)
	{
		Log.i(TAG, "getDisplayAd error: " + error);
		
		update_text = true;
		displayText = "Display Ads: " + error;
		
		// We must use a handler since we cannot update UI elements from a different thread.
		mHandler.post(mUpdateResults);
	}

	// Notifier for a successful TapjoyConnect.awardPoints call.
	@Override
	public void getAwardPointsResponse(String currencyName, int pointTotal)
	{
		update_text = true;
		displayText = currencyName + ": " + pointTotal;
		
		// We must use a handler since we cannot update UI elements from a different thread.
		mHandler.post(mUpdateResults);
	}

	// Notifier for a failed TapjoyConnect.awardPoints call.
	@Override
	public void getAwardPointsResponseFailed(String error)
	{
		update_text = true;
		displayText = "Award Points: " + error;
		
		// We must use a handler since we cannot update UI elements from a different thread.
		mHandler.post(mUpdateResults);
	}

	// Notifier whenever virtual currency is awarded outside the app.  Must call TapjoyConnect.setEarnedPointsNotifier first.
	@Override
	public void earnedTapPoints(int amount)
	{
		earnedPoints = true;
		update_text = true;
		displayText = this.getString(R.string.you_just_earned)+" " + amount +" "+this.getString(R.string.points)+"!";
		
		// We must use a handler since we cannot update UI elements from a different thread.
		mHandler.post(mUpdateResults);
	}

	// Notifier when a video ad starts.
	@Override
	public void videoStart()
	{
		Log.i(TAG, "VIDEO START");
	}

	// Notifier when a video error occurs.
	@Override
	public void videoError(int statusCode)
	{
		Log.i(TAG, "VIDEO ERROR: " + statusCode);
		
		switch (statusCode)
		{
			case TapjoyVideoStatus.STATUS_MEDIA_STORAGE_UNAVAILABLE:
				displayText = "VIDEO ERROR: No SD card or external media storage mounted on device";
				break;
			case TapjoyVideoStatus.STATUS_NETWORK_ERROR_ON_INIT_VIDEOS:
				displayText = "VIDEO ERROR: Network error on init videos";
				break;
			case TapjoyVideoStatus.STATUS_UNABLE_TO_PLAY_VIDEO:
				displayText = "VIDEO ERROR: Error playing video";
				break;
		}
		
		update_text = true;
		mHandler.post(mUpdateResults);
	}

	// Notifier when a video finishes playing.
	@Override
	public void videoComplete()
	{
		Log.i(TAG, "VIDEO COMPLETE");
	}   

	// Notifier when a TapjoyConnect.getFullScreenAd is successful.
	@Override
	public void getFullScreenAdResponse() 
	{
		Log.i(TAG, "Displaying Full Screen Ad..");
		TapjoyConnect.getTapjoyConnectInstance().showFullScreenAd();
	}

	// Notifier when a TapjoyConnect.getFullScreenAd request fails.
	@Override
	public void getFullScreenAdResponseFailed(int error) 
	{
		Log.i(TAG, "No Full Screen Ad to display: " + error);
		
		update_text = true;
		displayText = "No Full Screen Ad to display.";
		
		// We must use a handler since we cannot update UI elements from a different thread.
		mHandler.post(mUpdateResults);
	}
	
	/**
	 * Scales a display ad view to fit within a specified width. Returns a resized (smaller) view if the display ad
	 * is larger than the width. This method does not modify the view if the banner is smaller than the width (does not resize larger).
	 * @param adView                                                Display Ad view to resize.
	 * @param targetWidth                                   Width of the parent view for the display ad.
	 * @return                                                              Resized display ad view.
	 */
	public static View scaleDisplayAd(View adView, int targetWidth)
	{
		int adWidth = adView.getLayoutParams().width;
		int adHeight = adView.getLayoutParams().height;

		// Scale if the ad view is too big for the parent view.
		if (adWidth > targetWidth)
		{
			int scale;
			Double val = (double)(targetWidth) / (double)(adWidth);
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
	
	/**
	 * Helper method to get the name of each view type.
	 * @param type							Tapjoy view type from the view notification callbacks.
	 * @return								Name of the view.
	 */
	public String getViewName(int type)
	{
		String name;
		switch (type)
		{
			case TapjoyViewType.FULLSCREEN_AD:
				name = "fullscreen ad";
				break;
			case TapjoyViewType.OFFER_WALL_AD:
				name = "offer wall ad";
				break;
			case TapjoyViewType.VIDEO_AD:
				name = "video ad";
				break;
			default:
				name = "undefined type: " + type;
				break;
		}
		
		return name;
	}
}

