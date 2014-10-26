package com.stealthecheese.application;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.stealthecheese.R;


public class StealTheCheeseApplication extends Application {
	
	public static final String LOG_TAG = "stealcheese";
	public static final String PIN_TAG = "appData";
	public static final String PROFILE_PIC_URL = "https://graph.facebook.com/%s/picture";
	public static final String FRIEND_CHEESE_COUNT_PIC_URL = "https://graph.facebook.com/%S/picture?type=normal";
	public static final String FRIEND_HISTORY_PIC_URL = "https://graph.facebook.com/%S/picture?type=small";

	
  @Override
  public void onCreate() {
    super.onCreate();
    // Add your initialization code here
    // Parse.initialize(this, "nNADkosy5X7RyeklWIDkBqdYdEkI0RGpklYvm456", "0K62bbYxCq5U6hHo2XcWJ5fxbxjdYOB606tT0xdM");
    
    // enable local store
    Parse.enableLocalDatastore(this);
    
    // (Amits ) Add your initialization code here
    Parse.initialize(this, "He3esVXQ8BlaHA5ArPFQlM7YLvzDfgjpnL9INv0C", "zV6XlXFZ1AhqPUtiDveOUhjN5gZM11zG68qazbEr");
    
    //PushService.setDefaultPushCallback(this, LoginActivity.class);
    ParseInstallation.getCurrentInstallation().saveInBackground();
    
    
   // ParseUser.enableAutomaticUser();
    //ParseACL defaultACL = new ParseACL();
      
    // If you would like all objects to be private by default, remove this line.
    //defaultACL.setPublicReadAccess(true);
    
    //ParseACL.setDefaultACL(defaultACL, true);
    
    ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));
  }
}
