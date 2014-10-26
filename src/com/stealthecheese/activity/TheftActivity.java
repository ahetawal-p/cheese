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
import com.stealthecheese.model.User;
import com.stealthecheese.util.Animations;
import com.stealthecheese.viewmodel.HistoryViewModel;
import com.stealthecheese.viewmodel.PlayerViewModel;

public class TheftActivity extends Activity {
	ListView historyListView;
	ListView friendsListView;
	ArrayList<HistoryViewModel> historyList;
	ArrayList<PlayerViewModel> friendsList;
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
      
        //initializeHistoryListView(res);
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
		friendsList = new ArrayList<PlayerViewModel>();	
		for(ParseUser friend : userFriends){
			String imageUrl = String.format(StealTheCheeseApplication.FRIEND_CHEESE_COUNT_PIC_URL, friend.getString("facebookId"));
			friendsList.add(new PlayerViewModel(friend.getString("facebookId"), imageUrl , localCountMap.get(friend.getString("facebookId"))));
		}
		
        friendsListView= ( ListView )findViewById( R.id.friendsListView );   
        friendsListAdapter = new FriendsListAdapter( this, friendsList,res );
        friendsListView.setAdapter( friendsListAdapter );
        
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
    	int currentCheesCount = localCountMap.get(currentUser.getString("facebookId"));
		int frndCurrentCheeseCount = localCountMap.get(friendFacebookId);
		int updatedCurrentCount = currentCheesCount + 1;
		int updateFriendCheeseCount = frndCurrentCheeseCount - 1;
		
    	animateCheeseTheft(friendImageClicked, movedCheeseImg, cheeseCounter, updatedCurrentCount, updateFriendCheeseCount);
    	
    	updateTheftTransactionData(friendFacebookId, updatedCurrentCount, updateFriendCheeseCount);
    	
    	
	}
	
	
	
	private void updateTheftTransactionData(String friendFacebookId, 
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
						getAllFriendsCheeseUpdates();
					}else {
						Log.e(StealTheCheeseApplication.LOG_TAG, "Error saving theft updates", ex);
					}
					
				}
			});
			
		} catch (ParseException e) {
			Log.e(StealTheCheeseApplication.LOG_TAG, "Error in updating theft data", e);
		}
		
		
	}
	
	
	/**
	 * This only for hackathon.
	 * Since all the functions happen in main thread, we will need to move this 
	 * to cloud code for multiple fast clicks
	 */
	protected void getAllFriendsCheeseUpdates() {
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
	
	
	
//	private void populateFriendsListview(List<User> friends)
//	{
//		friendsList = new ArrayList<PlayerViewModel>(friends.size());	
//		for(User friend : friends)
//		{
//			String imageUrl = String.format(StealTheCheeseApplication.FRIEND_CHEESE_COUNT_PIC_URL, friend.getFacebookId());
//			friendsList.add(new PlayerViewModel(friend.getFacebookId(), imageUrl , friend.getCheese()));
//		}
//		
//	}
	
	/* temp code to populate dummy history list */
	private void populateHistoryListview(List<User> friends)
	{
		historyList = new ArrayList<HistoryViewModel>();	
		for(User friend : friends)
		{
			String imageUrl = String.format(StealTheCheeseApplication.FRIEND_CHEESE_COUNT_PIC_URL, friend.getFacebookId());
			historyList.add(new HistoryViewModel(friend.getFacebookId(),  imageUrl));
		}
		
	}

	
}
