package com.stealthecheese.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.parse.ParsePushBroadcastReceiver;
import com.stealthecheese.R;
import com.stealthecheese.activity.TheftActivity;
import com.stealthecheese.application.StealTheCheeseApplication;
import com.stealthecheese.enums.UpdateType;

public class CheeseBroadcastReceiver extends ParsePushBroadcastReceiver {
	@Override
	protected void onPushOpen(Context context, Intent intent) {
		
		/* if app is running, start TheftActivity. if not, start LoginActivity */
		if (isAppRunning())
		{
	        try
			{        	
				Intent newIntent = new Intent(context, TheftActivity.class);
				newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				newIntent.putExtra("UpdateType", UpdateType.REFRESH);
		        context.startActivity(newIntent);
			}
			catch (Exception ex)
			{
				Log.e(StealTheCheeseApplication.LOG_TAG, "onPushOpen failed with msg: " + ex.toString());
			}
		}
		else
		{
			/* LoginActivity is set to be launch activity via AndroidManifest */
			Intent newIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
			newIntent.putExtras(intent.getExtras());
	        context.startActivity(newIntent);
		}
	  }
	
	
	private Boolean isAppRunning()
	{
		return StealTheCheeseApplication.isActivityVisible();
	}
	
	private Context getResources() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected Bitmap getLargeIcon(Context context, Intent intent){
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.cheese_stealing_4);
		return largeIcon;
        
    }
	
	
}


