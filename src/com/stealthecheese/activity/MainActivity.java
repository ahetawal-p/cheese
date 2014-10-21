package com.stealthecheese.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.stealthecheese.R;
import com.stealthecheese.application.StealTheCheeseApplication;


public class MainActivity extends ActionBarActivity {
	
	private Button loginButton;
	private Button logoutButton;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Test parse code
		//UnitOfWork uow = new UnitOfWork();
		//Transaction newActivity = new Transaction("testFromuserId", "testToUserId", 5);
		//uow.activityDAO.createTransaction(newActivity);
		
		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoginButtonClicked();
			}
		});
		
		logoutButton = (Button) findViewById(R.id.logoutButton);
		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ParseFacebookUtils.getSession().closeAndClearTokenInformation();
				
				/*
				 *  Do we need to delete the current user from parse, in order to start fresh 
				 *  or just log them out and only update friend list when they log back in again
				 */
				ParseUser currentUser = ParseUser.getCurrentUser(); 
				currentUser.deleteInBackground();
				ParseUser.logOut();
				
			}
		});
		
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			// Go to the user info activity
			//showUserDetailsActivity();
		}else {
			//onLoginButtonClicked();
		}
		
		
	}

	private void onLoginButtonClicked() {

		List<String> permissions = Arrays.asList("public_profile", "user_friends");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {

				if (user == null) {
					Log.i(StealTheCheeseApplication.LOG_TAG,
							"Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.i(StealTheCheeseApplication.LOG_TAG,
							"User signed up and logged in through Facebook!");
					getFBUserInfo(user);
				} else {
					Log.i(StealTheCheeseApplication.LOG_TAG,
							"User logged in through Facebook!");

					//showUserDetailsActivity();
				}
			}
		});

	}
	
	
	private void getFBUserInfo(final ParseUser loggedInUser) {
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
					}

				}
				loggedInUser.addAllUnique("friends", friendsList);
				
				//TODO: Do we need the callback version of this save in case of new friends updated ??
				loggedInUser.saveInBackground();
			}

		});
		request.executeAsync();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
	
	
	
}
