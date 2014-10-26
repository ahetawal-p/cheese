package com.stealthecheese.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.stealthecheese.R;
import com.stealthecheese.application.StealTheCheeseApplication;

public class LoginActivity extends Activity {
	
	private TextView loadingText;
    private Button loginFBButton;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		loginFBButton = (Button) findViewById(R.id.loginButton);
		loginFBButton.setVisibility(View.GONE);
		
		
		final LinearLayout loadingMsgSection = (LinearLayout) findViewById(R.id.loadingMsgSection);
		loadingMsgSection.setVisibility(View.GONE);
		
		loadingText = (TextView) findViewById(R.id.loadingText);
		
				
		Animation animTranslate  = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.translate);
        animTranslate.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) { }

            @Override
            public void onAnimationRepeat(Animation arg0) { }

            @Override
            public void onAnimationEnd(Animation arg0) {
            	ParseUser currentUser = ParseUser.getCurrentUser();
            	
        		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
        			// user exists
        			loadingMsgSection.setVisibility(View.VISIBLE);
                    Animation animFade  = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade);
                    loadingMsgSection.startAnimation(animFade);
                    loadingText.setText("Preparing Steal Zone...");
                    performExistingUserSteps();
                    
        		} else {
        			// user does not exists
        			loginFBButton.setVisibility(View.VISIBLE);
        			Animation animFade  = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade);
        			loginFBButton.startAnimation(animFade);
        			loginFBButton.setOnClickListener(new View.OnClickListener() {
        				@Override
        				public void onClick(View v) {
        					loginToFBAndCreateUser();
        				}
        			});
        		}
            }
        });
        LinearLayout titleContainer = (LinearLayout) findViewById(R.id.titleContainer);
        titleContainer.startAnimation(animTranslate);
        
	}
	
	private void loginToFBAndCreateUser() {
		List<String> permissions = Arrays.asList("public_profile", "user_friends");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				if(err != null){
					Log.e(StealTheCheeseApplication.LOG_TAG, "Error in creating new user", err);
				}
				if (user == null) {
					Log.i(StealTheCheeseApplication.LOG_TAG, "Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.i(StealTheCheeseApplication.LOG_TAG, "User signed up and logged in through Facebook!");
					try {
						user.pin(StealTheCheeseApplication.PIN_TAG);
					} catch (ParseException e) {
						Log.e(StealTheCheeseApplication.LOG_TAG, "Error pinning user info", e);
					}
					getFBUserInfo();
				} else {
					Log.i(StealTheCheeseApplication.LOG_TAG, "User logged in through Facebook!");
					performExistingUserSteps();
					
				}
			}
		});

	}
	
	private void getFBUserInfo() {
		loadingText.setText("Getting user profile info...");
		final ParseUser loggedInUser = ParseUser.getCurrentUser();
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				loggedInUser.put("facebookId", user.getId());
				loggedInUser.put("firstName", user.getFirstName());
				loggedInUser.put("lastName", user.getLastName());
				// Use ProfilePictureView if needed for UI
				loggedInUser.put("profilePicUrl", String.format(StealTheCheeseApplication.PROFILE_PIC_URL, user.getId()));
				//loggedInUser.put("cheeseCount", getResources().getInteger(R.integer.initialCheeseCount));
				
				// Update default cheese count tab
				ParseObject cheeseUpdates = new ParseObject("cheese");
				cheeseUpdates.put("facebookId", user.getId());
				cheeseUpdates.put("cheeseCount", getResources().getInteger(R.integer.initialCheeseCount));
				try {
					cheeseUpdates.save();
				}catch(ParseException ex){
					Log.e(StealTheCheeseApplication.LOG_TAG, "Errror adding default cheese", ex);
				}
				
				getAndSaveFBUserFriendsInfo(loggedInUser);
			}
		});
		request.executeAsync();

	}
	
	
	private void performExistingUserSteps(){
		//1. Update users cheese count
		ParseQuery<ParseObject> query = ParseQuery.getQuery("cheese");
		query.whereEqualTo("facebookId", ParseUser.getCurrentUser().get("facebookId"));
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> users, ParseException ex) {
				if(ex == null){
					try {
						// update cheese count for the current user
						users.get(0).pin(StealTheCheeseApplication.LOG_TAG);
					} catch (ParseException e) {
						Log.e(StealTheCheeseApplication.LOG_TAG, "Error pinning user", ex);
					}
					//2. Get and Update user friends list
					getAndSaveFBUserFriendsInfo(ParseUser.getCurrentUser());
				}else {
					Log.e(StealTheCheeseApplication.LOG_TAG, "Error finding user", ex);
				}
				
			}
		});
	}
	
	
	private void getAndSaveFBUserFriendsInfo(final ParseUser loggedInUser) {
		loadingText.setText("Getting user friends list...");
		// Returns only the list of friends which use the app also
		Request request = Request.newMyFriendsRequest(ParseFacebookUtils.getSession(), new Request.GraphUserListCallback() {
			@Override
			public void onCompleted(List<GraphUser> friends, Response response) {
				final List<String> friendsList = new ArrayList<String>();
				if(friends.size() > 0){
					Log.i(StealTheCheeseApplication.LOG_TAG, "Friend list size: " + friends.size());
					for(GraphUser friend : friends){
						friendsList.add(friend.getId());
					}

				}
				loggedInUser.addAllUnique("friends", friendsList);
				
				
				loggedInUser.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException arg0) {
						ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
						userQuery.whereContainedIn("facebookId", friendsList);
						userQuery.findInBackground(new FindCallback<ParseUser>() {
							public void done(List<ParseUser> allFriendsInfo, ParseException e) {
						    if (allFriendsInfo == null) {
						      Log.e(StealTheCheeseApplication.LOG_TAG, "The getFirst request failed.", e);
						    } else {
						    	System.out.println(allFriendsInfo);
						    	Log.d(StealTheCheeseApplication.LOG_TAG, "Retrieved the object.");
						    	try {
						    		ParseObject.pinAll(StealTheCheeseApplication.PIN_TAG, allFriendsInfo);
									updateCheeseCountInLocalStore(friendsList);
						    		
						    		//verifyLocalDataStore(allFriendsInfo.size());
								} catch (ParseException e1) {
									Log.e(StealTheCheeseApplication.LOG_TAG, "Error pinning friends", e1);
								}
						    }
						  }
						});
						
						ParseInstallation.getCurrentInstallation().put("facebookId", ParseUser.getCurrentUser().get("facebookId"));
						ParseInstallation.getCurrentInstallation().saveInBackground();
					}
				});
			}

		});
		request.executeAsync();

	}
	
	protected void updateCheeseCountInLocalStore(List<String> friendsList){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("cheese");
		query.whereContainedIn("facebookId", friendsList);
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> allCheeseCountInfo, ParseException e) {
				if (allCheeseCountInfo == null) {
					Log.e(StealTheCheeseApplication.LOG_TAG, "The getFirst request failed.", e);
				} else {
					System.out.println(allCheeseCountInfo);
					Log.d(StealTheCheeseApplication.LOG_TAG, "Retrieved the object.");
					try {
						ParseObject.pinAll(StealTheCheeseApplication.PIN_TAG, allCheeseCountInfo);
						//verifyLocalDataStore(allFriendsInfo.size());
					} catch (ParseException e1) {
						Log.e(StealTheCheeseApplication.LOG_TAG, "Error pinning friends", e1);
					}
				}
				
				startTheftActivity();
			}
		});
	
	}
	
	
	private void verifyLocalDataStore(int origSize) {
		ParseQuery<ParseUser> savedUsertest = ParseUser.getQuery();
		savedUsertest.fromLocalDatastore();
		List<ParseUser> mytest;
		try {
			mytest = savedUsertest.find();
			System.out.println(mytest.get(0).get("facebookId"));
			System.out.println(mytest.get(1).get("facebookId"));
			Log.i(StealTheCheeseApplication.LOG_TAG, "Size of the fecthed result is " + origSize);
			Log.i(StealTheCheeseApplication.LOG_TAG, "Size of the local data store is " + (mytest.size() - 1));
			System.out.println(mytest);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	
	/**
	 * TODO : FIX ME !!!
	 */
	private void startTheftActivity() {
		Intent intent = new Intent(LoginActivity.this, TheftActivity.class);
		// removing this activity from backstack
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		
		startActivity(intent);
		finish();
	}
	
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
	
}
