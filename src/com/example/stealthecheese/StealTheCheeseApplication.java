package com.example.stealthecheese;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

public class StealTheCheeseApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    // Add your initialization code here
    Parse.initialize(this, "nNADkosy5X7RyeklWIDkBqdYdEkI0RGpklYvm456", "0K62bbYxCq5U6hHo2XcWJ5fxbxjdYOB606tT0xdM");
    ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
      
    // If you would like all objects to be private by default, remove this line.
    defaultACL.setPublicReadAccess(true);
    
    ParseACL.setDefaultACL(defaultACL, true);
  }
}
