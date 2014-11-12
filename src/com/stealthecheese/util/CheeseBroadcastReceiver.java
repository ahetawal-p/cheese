package com.stealthecheese.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.SaveCallback;
import com.stealthecheese.R;
import com.stealthecheese.activity.LoginActivity;
import com.stealthecheese.activity.TheftActivity;
import com.stealthecheese.application.StealTheCheeseApplication;
import com.stealthecheese.enums.UpdateType;

public class CheeseBroadcastReceiver extends ParsePushBroadcastReceiver {
	@Override
	protected void onPushOpen(Context context, Intent intent) {
		//Intent newIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        //context.startActivity(newIntent);

		
        try
		{        	
			Intent newIntent = new Intent(context, TheftActivity.class);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			newIntent.putExtra("UpdateType", UpdateType.REFRESH);
			//newIntent.putExtras(intent.getExtras());05104
	        context.startActivity(newIntent);
	        
		}
		catch (Exception ex)
		{
			Log.e(StealTheCheeseApplication.LOG_TAG, ex.toString());
		}
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


