package com.stealthecheese.util;

import com.parse.ParsePushBroadcastReceiver;

import android.content.Context;
import android.content.Intent;

public class CheeseBroadcastReceiver extends ParsePushBroadcastReceiver {
	@Override
	protected void onPushOpen(Context context, Intent intent) {
		Intent newIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		newIntent.putExtras(intent.getExtras());
        context.startActivity(newIntent);
	  }
}


