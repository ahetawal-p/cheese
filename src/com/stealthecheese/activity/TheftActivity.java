package com.stealthecheese.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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
	 * Popluates user and friends views
	 * @param friendUsers
	 */
	private void populateViews(List<ParseUser> friendUsers)
	{
        Resources res = getResources();
        
        populateUserView();
        
        populateFriendsListView(friendUsers, res);
      
        //initializeHistoryListView(res);
	}
	
	/* set display properties for user */
	private void populateUserView()
	{
		/* create dummy user properties, throw away later */
		PlayerViewModel userViewModel = new PlayerViewModel(currentUser.getString("facebookId"), 
										currentUser.getString("profilePicUrl")+"?type=large", 
										currentUser.getInt("cheeseCount"));
		
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
		for(ParseUser friend : userFriends)
		{
			String imageUrl = String.format(StealTheCheeseApplication.FRIEND_CHEESE_COUNT_PIC_URL, friend.getString("facebookId"));
			friendsList.add(new PlayerViewModel(friend.getString("facebookId"), imageUrl , friend.getInt("cheeseCount")));
		}
		
        friendsListView= ( ListView )findViewById( R.id.friendsListView );   
        friendsListAdapter = new FriendsListAdapter( this, friendsList,res );
        friendsListView.setAdapter( friendsListAdapter );
	}
	
	/**
	 * TODO: 
	 * 1. Add bulging animation and vibration when you steal
	 * 2. Add check for greyed out cheese/not enough cheeses
	 * @param friendImageClicked
	 * @param position
	 * @param movedCheeseImg
	 */
	public void onCheeseTheft(View friendImageClicked, int position, ImageView movedCheeseImg){
    	String friendFacebookId = getFriendFacebookId(position);
    	animateCheeseTheft(friendImageClicked, movedCheeseImg);
    	updateTheftTransactionData(friendFacebookId);
	}
	
	
	
	private void updateTheftTransactionData(String friendFacebookId) {
		try {
			final List<ParseUser> batchedRequestList = new ArrayList<ParseUser>();
			//1. Update current user count
			int currentCheesCount = currentUser.getInt("cheeseCount");
			currentUser.put("cheeseCount", currentCheesCount+1);
			batchedRequestList.add(currentUser);
			
			//2. Update friend cheese count
			ParseQuery<ParseUser> findFriendQuery = ParseUser.getQuery();
			findFriendQuery.fromLocalDatastore();
			findFriendQuery.whereEqualTo("facebookId", friendFacebookId);
			
			List<ParseUser> friends = findFriendQuery.find();
			ParseUser currFriend = friends.get(0);
			int frndCurrentCheeseCount = currFriend.getInt("cheeseCount");
			currFriend.put("cheeseCount", frndCurrentCheeseCount-1);
			//batchedRequestList.add(currFriend);
			
			ParseUser.saveAllInBackground(batchedRequestList, new SaveCallback() {
				@Override
				public void done(ParseException ex) {
						if(ex ==null){
							ParseUser.pinAllInBackground(StealTheCheeseApplication.PIN_TAG, batchedRequestList);
							getAllFriendsUpdates();
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
	protected void getAllFriendsUpdates() {
		ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
		userQuery.whereContainedIn("facebookId", currentUser.getList("friends"));
		userQuery.findInBackground(new FindCallback<ParseUser>() {
			public void done(List<ParseUser> allFriendsInfo, ParseException e) {
		    if (allFriendsInfo == null) {
		      Log.d(StealTheCheeseApplication.LOG_TAG, "Update for friends failed");
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
		
	}

	private void animateCheeseTheft(View viewItemClicked, final ImageView movedCheeseImg) {
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
