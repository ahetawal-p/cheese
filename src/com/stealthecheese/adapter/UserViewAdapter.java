package com.stealthecheese.adapter;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.stealthecheese.application.StealTheCheeseApplication;
import com.stealthecheese.util.CircleTransform;
import com.stealthecheese.viewmodel.PlayerViewModel;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class UserViewAdapter 
{
	TextView userCheeseTextView;
	ImageView userProfileImageView;
	Activity activity;
	
	public UserViewAdapter(Activity activity, View userCheeseTextView, View userProfileImageView)
	{
		this.activity = activity;
		this.userCheeseTextView = (TextView) userCheeseTextView;
		this.userProfileImageView = (ImageView) userProfileImageView;
	}
	
	public void setCheeseCount(int cheeseCount)
	{
		userCheeseTextView.setText("x " + Integer.toString(cheeseCount));
	}
	
	public void setImageString(String imageString)
	{
        Transformation circleTransform = new CircleTransform();
        try
        {
        	Picasso.with(activity).load(imageString).transform(circleTransform).into(userProfileImageView);
        }
        catch (Exception ex)
        {
        	Log.e(StealTheCheeseApplication.LOG_TAG, "Cannot set user profile image with image string: " + imageString);
        }
	}
	
	public void setUser(PlayerViewModel user)
	{
		setCheeseCount(user.getCheese());
		setImageString(user.getImageString());
	}
	
}
