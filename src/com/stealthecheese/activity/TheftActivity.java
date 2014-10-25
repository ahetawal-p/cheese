package com.stealthecheese.activity;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ListView;

import com.stealthecheese.R;
import com.stealthecheese.adapter.FriendsListAdapter;
import com.stealthecheese.adapter.HistoryListAdapter;
import com.stealthecheese.model.User;
import com.stealthecheese.viewmodel.FriendViewModel;
import com.stealthecheese.viewmodel.HistoryViewModel;

import com.stealthecheese.R;
import com.stealthecheese.adapter.FriendsListAdapter;
import com.stealthecheese.adapter.HistoryListAdapter;
import com.stealthecheese.adapter.UserViewAdapter;
import com.stealthecheese.application.StealTheCheeseApplication;
import com.stealthecheese.model.User;
import com.stealthecheese.viewmodel.PlayerViewModel;
import com.stealthecheese.viewmodel.HistoryViewModel;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_theft);		

		fetchProperties(savedInstanceState);
        Resources res = getResources();
        initializeUserProperties();
        initializeHistoryListView(res);
        initializeFriendsListView(res);
	}
	
	/* set display properties for user */
	private void initializeUserProperties()
	{
		/* create dummy user properties, throw away later */
		PlayerViewModel userViewModel = new PlayerViewModel("", "", 99);
		
		/* create adapter for user view */
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
	private void initializeFriendsListView(Resources res)
	{
        friendsListView= ( ListView )findViewById( R.id.friendsListView );   
        friendsListAdapter = new FriendsListAdapter( this, friendsList,res );
        friendsListView.setAdapter( friendsListAdapter );
//        friendsListView.setOnItemClickListener(new OnItemClickListener() 
//        {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
//            {
//            	String facebookId = getFriendFacebookId(position);
//            	animateCheeseTheft(view);
//            	Toast.makeText(getApplicationContext(), "Stealing cheese from user: " + facebookId, 3).show();
//            	updateCheeseCount();
//            }
//        });
	}
	
	/* handle user click on friends image view */
	public void onImageClicked(View imageViewClicked, int position)
	{
    	String facebookId = getFriendFacebookId(position);
    	animateCheeseTheft(imageViewClicked);
    	Toast.makeText(getApplicationContext(), "Stealing cheese from user: " + facebookId, 3).show();
    	updateCheeseCount();
	}
	
	private void animateCheeseTheft(View viewItemClicked)
	{
		/* destination position */
		int[] destPos = new int[2];
		userProfileImageView.getLocationOnScreen(destPos);
		
		/* original position of cheese, want it to start at victim's image position */
		int [] origPos = new int[2];
		viewItemClicked.getLocationOnScreen(origPos);
		
		/* calculate deltas to determine how much to move */
		int deltaX = destPos[0] - origPos[0];
		int deltaY = destPos[1] - origPos[1];
		
		ImageView cheeseAnimationImageView = (ImageView)findViewById(R.id.cheeseAnimationImageView);
		Animation animationSet = new TranslateAnimation(origPos[0], deltaX, origPos[1], deltaY);
		animationSet.setDuration(1000);
		cheeseAnimationImageView.setVisibility(View.VISIBLE);
		cheeseAnimationImageView.startAnimation(animationSet);
		cheeseAnimationImageView.setVisibility(View.GONE);
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
	
	private void updateCheeseCount()
	{
		updateUserCheeseCount();
	}
	
	private void updateUserCheeseCount()
	{
		userViewAdapter.setCheeseCount(90);
	}
	
	private void fetchProperties(Bundle bundle)
	{
		Bundle data = this.getIntent().getExtras();
		List<User> friends = data.getParcelableArrayList("friends");
		populateFriendsListview(friends);
		populateHistoryListview(friends);

	}
	
	private void populateFriendsListview(List<User> friends)
	{
		friendsList = new ArrayList<PlayerViewModel>(friends.size());	
		for(User friend : friends)
		{
			String imageUrl = String.format(StealTheCheeseApplication.FRIEND_CHEESE_COUNT_PIC_URL, friend.getFacebookId());
			friendsList.add(new PlayerViewModel(friend.getFacebookId(), imageUrl , friend.getCheese()));
		}
		
	}
	
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
