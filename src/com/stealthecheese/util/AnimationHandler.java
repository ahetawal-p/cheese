package com.stealthecheese.util;

import android.content.Context;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.stealthecheese.R;

public class AnimationHandler {
	
	private Context activityContext;
	public AnimationHandler(Context activityContext)
	{
		this.activityContext = activityContext;
	}
	
	public void startAnimateRefresh(View view)
	{
		Animation rotation = AnimationUtils.loadAnimation(activityContext, R.anim.rotate);
		view.startAnimation(rotation);
	}
	
	public void stopAnimateRefresh(View view)
	{
		view.clearAnimation();
	}
	
	public void fadeInOutView(View view, AnimationListener listener)
	{
		view.setVisibility(View.VISIBLE);
        Animation animFade  = AnimationUtils.loadAnimation(activityContext, R.anim.fadeinout);
        animFade.setAnimationListener(listener);
        view.startAnimation(animFade);
        //view.setVisibility(View.INVISIBLE);
        //view.setVisibility(View.GONE);
	}
	
	public void animateCheeseTheft(View viewItemClicked, final ImageView movedCheeseImg, final TextView cheeseCounter, 	final ImageView userProfileImageView,
									final int updatedCurrentCount, 
									final int updateFriendCheeseCount) 
	{
		AnimationListener animL = new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				movedCheeseImg.setVisibility(View.GONE);
				wobbleImageView(userProfileImageView);
			}
		};

		/* destination position */
		int[] destPos = new int[2];
		userProfileImageView.getLocationOnScreen(destPos);

		destPos[0]+=userProfileImageView.getWidth()/2;
		destPos[1]+=userProfileImageView.getHeight()/2;

		/* original position of cheese, want it to start at victim's image position */
		int [] origPos = new int[2];
		viewItemClicked.getLocationOnScreen(origPos);

		Animations anim = new Animations();
		Animation a = anim.fromAtoB(origPos[0],origPos[1], destPos[0], destPos[1], animL, 300);
		movedCheeseImg.setVisibility(View.VISIBLE);
		movedCheeseImg.startAnimation(a);
	}
	
    private void wobbleImageView(View imageView)
    {
        Animation animationWobble  = AnimationUtils.loadAnimation(activityContext, R.anim.wobble);
        imageView.startAnimation(animationWobble);
        Vibrator v = (Vibrator) activityContext.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(250);
    }
    
    public void fadeIn(View view)
    {
    	YoYo.with(Techniques.FadeIn).duration(3000).playOn(view);
    }
    
    public void bounceCheeseCounters(View ... views) {
    	for(int i = 0; i< views.length; i++){
    		YoYo.with(Techniques.Bounce).duration(1000).playOn(views[i]);
    	}
		
    }
	
}
