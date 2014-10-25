package com.stealthecheese.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.stealthecheese.R;
import com.stealthecheese.application.StealTheCheeseApplication;
import com.stealthecheese.model.User;

public class LoginActivity extends Activity {
	
	private TextView loadingText;
    private Button loginFBButton;
    private List<User> playersFriends = new ArrayList<User>();
    private List<String> tempFriendsList = new ArrayList<String>();
    
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
                    getFBUserFriendsInfo(currentUser);
                    
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
				if (user == null) {
					Log.i(StealTheCheeseApplication.LOG_TAG, "Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.i(StealTheCheeseApplication.LOG_TAG, "User signed up and logged in through Facebook!");
					try {
						user.pin(StealTheCheeseApplication.PIN_TAG);
					} catch (ParseException e) {
						Log.e(StealTheCheeseApplication.LOG_TAG, "Error pinning user info", e);
					}
					getFBUserInfo(user);
				} else {
					Log.i(StealTheCheeseApplication.LOG_TAG, "User logged in through Facebook!");
					getFBUserFriendsInfo(user);
					startMainPageActivity();
				}
			}
		});

	}
	
	
	private void getFBUserInfo(final ParseUser loggedInUser) {
		loadingText.setText("Getting user profile info...");
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				loggedInUser.put("facebookId", user.getId());
				loggedInUser.put("firstName", user.getFirstName());
				loggedInUser.put("lastName", user.getLastName());
				// Use ProfilePictureView if needed for UI
				loggedInUser.put("profilePicUrl", String.format(StealTheCheeseApplication.PROFILE_PIC_URL, user.getId()));
				loggedInUser.put("cheeseCount", getResources().getInteger(R.integer.initialCheeseCount));
				getAndSaveFBUserFriendsInfo(loggedInUser);
			}
		});
		request.executeAsync();

	}
	
	
	private void getAndSaveFBUserFriendsInfo(final ParseUser loggedInUser) {
		loadingText.setText("Getting user friends list...");
		// Returns only the list of friends which use the app also
		Request request = Request.newMyFriendsRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserListCallback() {
			@Override
			public void onCompleted(List<GraphUser> friends, Response response) {
				List<String> friendsList = new ArrayList<String>();
				if(friends.size() > 0){
					Log.i(StealTheCheeseApplication.LOG_TAG, "Friend list size: " + friends.size());
					for(GraphUser friend : friends){
						friendsList.add(friend.getId());
						/*storing data into playersFriends to pass over to Main Page Activity*/
						playersFriends.add(new User(friend.getId(), 20));
						tempFriendsList.add(friend.getId());
					}

				}
				loggedInUser.addAllUnique("friends", friendsList);
				
				loggedInUser.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException arg0) {
						ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
						userQuery.whereContainedIn("facebookId", tempFriendsList);
						userQuery.findInBackground(new FindCallback<ParseUser>() {
							public void done(List<ParseUser> allFriendsInfo, ParseException e) {
						    if (allFriendsInfo == null) {
						      Log.d(StealTheCheeseApplication.LOG_TAG, "The getFirst request failed.");
						    } else {
						    	System.out.println(allFriendsInfo);
						    	Log.d(StealTheCheeseApplication.LOG_TAG, "Retrieved the object.");
						    	try {
									ParseObject.pinAll(StealTheCheeseApplication.PIN_TAG, allFriendsInfo);
								} catch (ParseException e1) {
									Log.e(StealTheCheeseApplication.LOG_TAG, "Error pinning friends", e1);
								}
						    }
						  }
						});
						startMainPageActivity();
						
					}
				});
				
				
			}

		});
		request.executeAsync();

	}
	
	private void getFBUserFriendsInfo(final ParseUser loggedInUser) {
		loadingText.setText("Updating user friends list...");
		// Returns only the list of friends which use the app also
		Request request = Request.newMyFriendsRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserListCallback() {
			@Override
			public void onCompleted(List<GraphUser> friends, Response response) {
				List<String> friendsList = new ArrayList<String>();
				if(friends.size() > 0){
					Log.i(StealTheCheeseApplication.LOG_TAG, "Friend list size: " + friends.size());
					for(GraphUser friend : friends){
						friendsList.add(friend.getId());
						/*storing data into playersFriends to pass over to Main Page Activity*/
						playersFriends.add(new User(friend.getId(), 20));
					}

				}
			
				startMainPageActivity();

			}

		});
		request.executeAsync();

	}
	
	private void startMainPageActivity() {
		Bundle b = new Bundle();
		Intent intent = new Intent(LoginActivity.this, TheftActivity.class);		
		b.putParcelableArrayList("friends", (ArrayList<? extends Parcelable>) playersFriends);
		intent.putExtras(b);
		startActivity(intent);
	}
	
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
	
}
