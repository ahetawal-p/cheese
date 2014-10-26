package com.stealthecheese.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.stealthecheese.R;
import com.stealthecheese.adapter.FriendsListAdapter;
import com.stealthecheese.adapter.HistoryListAdapter;
import com.stealthecheese.adapter.UserViewAdapter;
import com.stealthecheese.application.StealTheCheeseApplication;
import com.stealthecheese.util.Animations;
import com.stealthecheese.viewmodel.HistoryViewModel;
import com.stealthecheese.viewmodel.PlayerViewModel;

public class TheftActivity extends Activity {
	ListView historyListView;
	ListView friendsListView;
	ArrayList<HistoryViewModel> historyList = new ArrayList<HistoryViewModel>();
	ArrayList<PlayerViewModel> friendsList = new ArrayList<PlayerViewModel>();
	HistoryListAdapter historyListAdapter;
	FriendsListAdapter friendsListAdapter;
	UserViewAdapter userViewAdapter;
	View userProfileImageView; 
	View userCheeseTextView;
	ParseUser currentUser;
	private HashMap<String, Integer> localCountMap = new HashMap<String, Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_theft);
		
		initializeHistoryListView(getResources());
		initializeFriendListVIew(getResources());
		
		updatePage();
	}
	
	

	private void updatePage()
	{
		currentUser = ParseUser.getCurrentUser();
		
		try {
			List<ParseUser> friendUsers = ParseUser.getQuery()
													.fromLocalDatastore()
													.whereNotEqualTo("facebookId", currentUser.getString("facebookId"))
													.find();
			
			populateViews(friendUsers);
		} 
		catch (ParseException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(StealTheCheeseApplication.LOG_TAG, "Fetch friends from localstore failed with message: " + e.toString());
		}
	}
	
	/**
	 * Populates user and friends views
	 * @param friendUsers
	 */
	private void populateViews(List<ParseUser> friendUsers)
	{
        Resources res = getResources();
        
        retrieveCheeseCountsLocally();
        
        populateUserView();
        
        populateFriendsListView(friendUsers, res);
      
        populateHistoryListView(res);
	}
	
	
	
	
	private void populateHistoryListView(Resources res) {
		historyList.clear();
		ParseQuery<ParseObject> histQuery = ParseQuery.getQuery("thefthistory");
		histQuery.whereEqualTo("victimFBId", currentUser.get("facebookId"));
		histQuery.setLimit(10);
		histQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> lastTenTrans, ParseException err) {
				if(err == null){
					for(ParseObject trans : lastTenTrans){
						String fname = retrieveFriendFirstName(trans.getString("thiefFBId"));
						historyList.add(new HistoryViewModel(fname));
					}
					historyListAdapter.notifyDataSetChanged();
				}else {
					Log.e(StealTheCheeseApplication.LOG_TAG, "Error getting hsitory", err);
				}
				
			}
			
			
		});
		
	}



	private void retrieveCheeseCountsLocally() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("cheese");
		query.fromLocalDatastore();
		try {
			List<ParseObject> cheeseUpdates = query.find();
			for(ParseObject cheese : cheeseUpdates){
				localCountMap.put(cheese.getString("facebookId"), cheese.getInt("cheeseCount"));
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/* set display properties for user */
	private void populateUserView()
	{
		/* create dummy user properties, throw away later */
		PlayerViewModel userViewModel = new PlayerViewModel(currentUser.getString("facebookId"), 
										currentUser.getString("profilePicUrl")+"?type=large", 
										localCountMap.get(currentUser.getString("facebookId")));
		
		/* create adapter for user view */
		System.out.println(currentUser.getString("facebookId"));
		userCheeseTextView = findViewById(R.id.cheeseCountTextView);
		userProfileImageView = findViewById(R.id.userProfileImageView);
		userViewAdapter = new UserViewAdapter(this, userCheeseTextView, userProfileImageView);
		
		/* set display values via adapter */
		userViewAdapter.setUser(userViewModel);		
	}
	
	private void initializeFriendListVIew(Resources resources) {
		friendsListView= ( ListView )findViewById( R.id.friendsListView );   
		friendsListAdapter = new FriendsListAdapter( this, friendsList, resources );
		friendsListView.setAdapter( friendsListAdapter );

	}
	
	/* set history list view adapter */
	private void initializeHistoryListView(Resources res)
	{
        historyListView= ( ListView )findViewById( R.id.historyListView ); 
        historyListAdapter=new HistoryListAdapter( this, historyList,res );
        historyListView.setAdapter( historyListAdapter );
	}
	
	/* set friends list view adapter and handle onClick events */
	private void populateFriendsListView(List<ParseUser> userFriends, Resources res)
	{
		friendsList.clear();
		for(ParseUser friend : userFriends){
			String imageUrl = String.format(StealTheCheeseApplication.FRIEND_CHEESE_COUNT_PIC_URL, friend.getString("facebookId"));
			friendsList.add(new PlayerViewModel(friend.getString("facebookId"), imageUrl , localCountMap.get(friend.getString("facebookId"))));
		}
		
		friendsListAdapter.notifyDataSetChanged();
        
	}
	
	/**
	 * TODO: 
	 * 1. Add check for grayed out cheese/not enough cheeses
	 * @param friendImageClicked
	 * @param position
	 * @param movedCheeseImg
	 */
	public void onCheeseTheft(View friendImageClicked, int position, ImageView movedCheeseImg, TextView cheeseCounter){
    	String friendFacebookId = getFriendFacebookId(position);
    	
    	fecthLatestCheeseDataForTrans(friendFacebookId);
		
    	int currentCheesCount = localCountMap.get(currentUser.getString("facebookId"));
		int frndCurrentCheeseCount = localCountMap.get(friendFacebookId);
		
		
		int updatedCurrentCount = currentCheesCount + 1;
		int updateFriendCheeseCount = frndCurrentCheeseCount - 1;
		
    	animateCheeseTheft(friendImageClicked, movedCheeseImg, cheeseCounter, updatedCurrentCount, updateFriendCheeseCount);
    	
    	updateTheftTransactionData(friendFacebookId, updatedCurrentCount, updateFriendCheeseCount);
    	
    	
	}

	private void fecthLatestCheeseDataForTrans(String friendFacebookId) {
		try{
    	//Get current counts from Parse
    	ParseQuery<ParseObject> query = ParseQuery.getQuery("cheese");
		query.whereContainedIn("facebookId", Arrays.asList(new String[]{currentUser.getString("facebookId"),friendFacebookId} ));
		final List<ParseObject> allUpdates = query.find();
		for(ParseObject cheeseCount : allUpdates){
			localCountMap.put(cheeseCount.getString("facebookId"), cheeseCount.getInt("cheeseCount"));
		}
		ParseObject.saveAllInBackground(allUpdates, new SaveCallback() {
			@Override
			public void done(ParseException ex) {
				if(ex ==null){
					ParseUser.pinAllInBackground(StealTheCheeseApplication.PIN_TAG, allUpdates);
				}else {
					Log.e(StealTheCheeseApplication.LOG_TAG, "Error saving theft updates", ex);
				}
				
			}
		});
		
    	}catch(ParseException ex){
    		Log.e(StealTheCheeseApplication.LOG_TAG, "Error", ex);
    	}
	}
	
	
	
	private void updateTheftTransactionData(final String friendFacebookId, 
									int updatedCurrentCount, 
									int updateFriendCheeseCount) {
		try {
			
			//1. Update current user count
			
			localCountMap.put(currentUser.getString("facebookId"), updatedCurrentCount);
			
			//2. Update friends cheese count
			
			localCountMap.put(friendFacebookId, updateFriendCheeseCount);
			
			ParseQuery<ParseObject> query = ParseQuery.getQuery("cheese");
			query.whereContainedIn("facebookId", Arrays.asList(new String[]{currentUser.getString("facebookId"),friendFacebookId} ));
			query.fromLocalDatastore();
			final List<ParseObject> allUpdates = query.find();
			for(ParseObject cheeseCount : allUpdates){
				cheeseCount.put("cheeseCount", localCountMap.get(cheeseCount.get("facebookId")));
			}
			
			ParseObject.saveAllInBackground(allUpdates, new SaveCallback() {
				@Override
				public void done(ParseException ex) {
					if(ex ==null){
						ParseUser.pinAllInBackground(StealTheCheeseApplication.PIN_TAG, allUpdates);
						
						insertHistoryData(friendFacebookId);
						
						
						getAllFriendsCheeseUpdates(friendFacebookId);
					}else {
						Log.e(StealTheCheeseApplication.LOG_TAG, "Error saving theft updates", ex);
					}
					
				}

				
			});
			
		} catch (ParseException e) {
			Log.e(StealTheCheeseApplication.LOG_TAG, "Error in updating theft data", e);
		}
		
		
	}
	
	
	
	private void insertHistoryData(String friendFacebookId) {
		ParseObject histQuery = new ParseObject("thefthistory");
		histQuery.put("victimFBId", friendFacebookId);
		histQuery.put("thiefFBId", currentUser.get("facebookId"));
		histQuery.saveInBackground();
	}
	
	
	
	/**
	 * This only for hackathon.
	 * Since all the functions happen in main thread, we will need to move this 
	 * to cloud code for multiple fast clicks
	 * @param friendFacebookId 
	 */
	protected void getAllFriendsCheeseUpdates(String friendFacebookId) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("cheese");
		query.whereContainedIn("facebookId", currentUser.getList("friends"));
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> allFriendsInfo, ParseException e) {
		    if (allFriendsInfo == null) {
		      Log.d(StealTheCheeseApplication.LOG_TAG, "Update for friends failed");
		    } else {
		    	Log.d(StealTheCheeseApplication.LOG_TAG, "Retrieved the object.");
		    	for(ParseObject cheese : allFriendsInfo){
					localCountMap.put(cheese.getString("facebookId"), cheese.getInt("cheeseCount"));
				}
		    	ParseObject.pinAllInBackground(StealTheCheeseApplication.PIN_TAG, allFriendsInfo);
		    	
		    	
		    	
		    	
		    	
		    	
		    	updatePage();
			}
		  }
		});
		
	}

	private void animateCheeseTheft(View viewItemClicked, 
							final ImageView movedCheeseImg, final TextView cheeseCounter, 
							final int updatedCurrentCount, 
							final int updateFriendCheeseCount) {
		AnimationListener animL = new AnimationListener() {
		    @Override
		    public void onAnimationStart(Animation animation) {}

		    @Override
		    public void onAnimationRepeat(Animation animation) {}

		    @Override
		    public void onAnimationEnd(Animation animation) {
		        movedCheeseImg.setVisibility(View.GONE);
		        wobbleImageView(userProfileImageView);
		        
				cheeseCounter.setText(Integer.toString(updateFriendCheeseCount));
				((TextView)userCheeseTextView).setText("x " + Integer.toString(updatedCurrentCount));
		        
		        
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
	    Animation a = anim.fromAtoB(origPos[0],origPos[1], destPos[0], destPos[1], animL, 500);
	    movedCheeseImg.setVisibility(View.VISIBLE);
	    movedCheeseImg.startAnimation(a);
		
	}
	
    private void wobbleImageView(View imageView)
    {
        Animation animationWobble  = AnimationUtils.loadAnimation(TheftActivity.this, R.anim.wobble);
        imageView.startAnimation(animationWobble);
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(250);
    }
    

	
	private String getFriendFacebookId(int position)
	{
		String facebookId;
		try
		{
			facebookId = friendsList.get(position).getFacebookId();
			
		}
		catch (Exception ex)
		{
			Log.e(StealTheCheeseApplication.LOG_TAG, "Cannot find facebook Id of friend in list");
			facebookId = "";
		}
		
		return facebookId;
	}

	
	private String retrieveFriendFirstName(String facebookId)  {
		ParseQuery<ParseUser> friendDetails = ParseUser.getQuery();
		friendDetails.fromLocalDatastore();
		friendDetails.whereEqualTo("facebookId", facebookId);
		try {
			List<ParseUser> friends = friendDetails.find();

			if(friends.size()>0){
				return friends.get(0).getString("firstName");
			}else {
				Log.e(StealTheCheeseApplication.LOG_TAG, "Cannot find friend in local datastore");
			}
		} catch (ParseException e) {
			Log.e(StealTheCheeseApplication.LOG_TAG, "Cannot find friend in local datastore", e);
		}
		return null;
	}
	
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
	}
	
}
