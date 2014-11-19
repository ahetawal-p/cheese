package com.stealthecheese.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParsePushBroadcastReceiver;
import com.stealthecheese.R;
import com.stealthecheese.activity.TheftActivity;
import com.stealthecheese.application.StealTheCheeseApplication;
import com.stealthecheese.enums.UpdateType;

public class CheeseBroadcastReceiver extends ParsePushBroadcastReceiver {
	
	
	/**
	 * This method is used for updating the user view in real time,
	 * if the player is already on the theft activity playing the game
	 */
	@Override
	protected void onPushReceive(Context context, Intent intent){
		super.onPushReceive(context, intent);
		
		if(isAppRunning() && !isAppPaused()){
			JSONObject pushData = null;
			try {
				pushData = new JSONObject(intent.getStringExtra("com.parse.Data"));
			}catch(JSONException e){
				Log.e("com.parse.ParsePushReceiver", "Unexpected JSONException when receiving push data: ", e);
			}
			String thiefId = (String)pushData.optString("thiefId", "");
			Integer thieCheeseCount = (Integer)pushData.optInt("thiefCheeseCount", 0);
			Boolean animateMe = (Boolean)pushData.optBoolean("animateMe", false);
			Integer currentUserCheeseCount = (Integer)pushData.optInt("victimCheeseCount", 0);
			
			Intent newIntent = new Intent(context, TheftActivity.class);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			newIntent.putExtra("UpdateType", UpdateType.REALTIME);
			newIntent.putExtra("ThiefId", thiefId);
			newIntent.putExtra("ThiefCheeseCount", thieCheeseCount);
			newIntent.putExtra("CurrentUserCheeseCount", currentUserCheeseCount);
			newIntent.putExtra("AnimateMe", animateMe);
			context.startActivity(newIntent);
		}
	}
	
	/**
	 * This method is used for launching a particular activity
	 * when the user opens the notification
	 */
	@Override
	protected void onPushOpen(Context context, Intent intent) {
		
		/* if app is running, start TheftActivity. if not, start LoginActivity */
		if (isAppRunning() || isAppPaused()) {
	        try {        	
				Intent newIntent = new Intent(context, TheftActivity.class);
				newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				newIntent.putExtra("UpdateType", UpdateType.REFRESH);
		        context.startActivity(newIntent);
			} catch (Exception ex) {
				Log.e(StealTheCheeseApplication.LOG_TAG, "onPushOpen failed with msg: " + ex.toString());
			}
		} else {
			/* LoginActivity is set to be launch activity via AndroidManifest */
			Intent newIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
			newIntent.putExtras(intent.getExtras());
	        context.startActivity(newIntent);
		}
	  }
	
	
	private Boolean isAppRunning() {
		return StealTheCheeseApplication.isActivityRunning();
	}
	
	private Boolean isAppPaused() {
		return StealTheCheeseApplication.isActivityPaused();
	}
	
	
	@Override
	protected Bitmap getLargeIcon(Context context, Intent intent){
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.cheese_stealing_4);
		return largeIcon;
        
    }
	
	
}


