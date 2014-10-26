package com.stealthecheese.util;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

public class Animations {
public Animation fromAtoB(float fromX, float fromY, float toX, float toY, AnimationListener l, int speed){
	
	float deltaX = toX - fromX;
	float deltaY = toY - fromY;
	
	Animation fromAtoB = new TranslateAnimation(
			Animation.RELATIVE_TO_SELF,
			0, 
			Animation.ABSOLUTE,
			deltaX, 
			Animation.RELATIVE_TO_SELF,
			0,
			Animation.ABSOLUTE,
			deltaY);
	
	
       fromAtoB.setDuration(speed);
       fromAtoB.setInterpolator(new LinearInterpolator());

       
       if(l != null)
           fromAtoB.setAnimationListener(l);               
               return fromAtoB;
   }






}