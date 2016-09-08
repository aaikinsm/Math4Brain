package com.blackstar.math4brain;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent i) {
		// Open NotificationView Class on Notification Click
		Intent intent = new Intent(context, MainMenu.class);
		// Open NotificationView.java Activity
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// Create Notification using NotificationCompat.Builder
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
				// Set Icon
				.setSmallIcon(R.drawable.math4thebrain_icon)
						// Set Title
				.setContentTitle("Kokotoa")
						// Set Text
				.setContentText(context.getString(R.string.notification_msg))
						// Set PendingIntent into Notification
				.setContentIntent(pIntent)
						// Dismiss Notification
				.setAutoCancel(true);

		// Create Notification Manager
		NotificationManager notificationmanager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// Build Notification with Notification Manager
		notificationmanager.notify(0, builder.build());
	}

}
