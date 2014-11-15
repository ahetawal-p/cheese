package com.stealthecheese.activity;

import com.squareup.picasso.Picasso;
import com.stealthecheese.R;
import com.stealthecheese.application.StealTheCheeseApplication;
import com.stealthecheese.util.AnimationHandler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FlyingCheeseActivity extends Activity {
	
	/* UI controls */
	TextView challengeMessageTextView;
	LinearLayout challengeContainer;
	ImageView victimImageView;
	ImageView thiefImageView;
	AnimationHandler animationHandler;
	Boolean theftSuccessful;
	ImageView flyingCheeseImage1;
	ImageView flyingCheeseImage2;	
	ImageView flyingCheeseImage3;
	ImageView flyingCheeseImage4;
	ImageView flyingCheeseImage5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flying_cheese);
		
		String thiefFacebookId = getIntent().getStringExtra(getResources().getString(R.string.thief_facebookId));
		String victimFacebookId = getIntent().getStringExtra(getResources().getString(R.string.victim_facebookId));
		
		initializeUtilities();
		initializeUIControls(thiefFacebookId, victimFacebookId);
		countDownToChallenge();
		startFlyingCheeseChallenge();
	}
	
	private void initializeUtilities() {
		this.animationHandler = new AnimationHandler(this);
	}
	
	private void initializeUIControls(String thiefFacebookId, String victimFacebookId)
	{
		challengeMessageTextView = (TextView)findViewById(R.id.challengeMessageTextView);
		challengeContainer = (LinearLayout)findViewById(R.id.challengeContainer);
		victimImageView = (ImageView)findViewById(R.id.victimImageView);
		thiefImageView = (ImageView)findViewById(R.id.thiefImageView);
		flyingCheeseImage1 = (ImageView)findViewById(R.id.flyingCheeseImageView1);
		flyingCheeseImage2 = (ImageView)findViewById(R.id.flyingCheeseImageView2);
		flyingCheeseImage3 = (ImageView)findViewById(R.id.flyingCheeseImageView3);
		flyingCheeseImage4 = (ImageView)findViewById(R.id.flyingCheeseImageView4);
		flyingCheeseImage5 = (ImageView)findViewById(R.id.flyingCheeseImageView5);
		
		loadPlayerImages(thiefFacebookId, victimFacebookId);
	}
	
	private void loadPlayerImages(String thiefFacebookId, String victimFacebookId)
	{
		String thiefImageUrl = String.format(StealTheCheeseApplication.FRIEND_CHEESE_COUNT_PIC_URL, thiefFacebookId);
        Picasso.with(this).load(thiefImageUrl)
					        .fit()
					        .centerCrop()
					        .into(thiefImageView);

		String victimImageUrl = String.format(StealTheCheeseApplication.FRIEND_CHEESE_COUNT_PIC_URL, victimFacebookId);
        Picasso.with(this).load(victimImageUrl)
					        .fit()
					        .centerCrop()
					        .into(victimImageView);
	}
	
	private void countDownToChallenge()
	{
		challengeMessageTextView.setVisibility(View.VISIBLE);
		final Handler handler = new Handler();
		
		for (int ii=3; ii>0; ii--)
		{
			final int counter = ii;
			handler.postDelayed(new Runnable() {
			    @Override
			    public void run() {
			    	challengeMessageTextView.setText(Integer.toString(counter));
			    }
			}, 2000);
		}
		
		challengeMessageTextView.setVisibility(View.GONE);
	}
	
	private void startFlyingCheeseChallenge()
	{
		challengeContainer.setVisibility(View.VISIBLE);
	}
	
	
}
