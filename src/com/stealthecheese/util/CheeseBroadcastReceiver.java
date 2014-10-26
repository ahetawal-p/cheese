package com.stealthecheese.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParsePushBroadcastReceiver;
import com.stealthecheese.R;

public class CheeseBroadcastReceiver extends ParsePushBroadcastReceiver {
	@Override
	protected void onPushOpen(Context context, Intent intent) {
		Intent newIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		newIntent.putExtras(intent.getExtras());
        context.startActivity(newIntent);
	  }
	
	
	@Override
	protected Bitmap getLargeIcon(Context context, Intent intent){
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.cheese_stealing_4);
		return largeIcon;
        
    }
	
	
}


