package com.stealthecheese;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.stealthecheese.application.StealTheCheeseApplication;
import com.stealthecheese.model.User;
import com.stealthecheese.util.BitmapRetrieveTask;

import viewModels.FriendViewModel;
import viewModels.HistoryViewModel;

import adapters.FriendsListAdapter;
import adapters.HistoryListAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class MainPageActivity extends Activity {
	ListView historyListView;
	ListView friendsListView;
	ArrayList<HistoryViewModel> historyList;
	ArrayList<FriendViewModel> friendsList;
	HistoryListAdapter historyListAdapter;
	FriendsListAdapter friendsListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);		

		fetchProperties(savedInstanceState);
		populateHistoryListview();

        Resources res =getResources();
        historyListView= ( ListView )findViewById( R.id.historyListView );  // List defined in XML ( See Below )
        
        /**************** Create Custom Adapter *********/
        historyListAdapter=new HistoryListAdapter( this, historyList,res );
        historyListView.setAdapter( historyListAdapter );
        
        friendsListView= ( ListView )findViewById( R.id.friendsListView );  // List defined in XML ( See Below )
        
        /**************** Create Custom Adapter *********/
        friendsListAdapter = new FriendsListAdapter( this, friendsList,res );
        friendsListView.setAdapter( friendsListAdapter );
	}

	private void fetchProperties(Bundle bundle)
	{
		//User user = bundle.getParcelable("user");		
		Bundle data = this.getIntent().getExtras();
		List<User> friends = data.getParcelableArrayList("friends");
		populateFriendsListview(friends);
	}
	
	private void populateFriendsListview(List<User> friends)
	{
		friendsList = new ArrayList<FriendViewModel>();	
		for(User friend : friends)
		{
			friendsList.add(new FriendViewModel(friend.getFacebookId(), friend.getCheese()));
		}
		
	}
	
	//Temp code to populate dummy history list
	private void populateHistoryListview()
	{
		historyList = new ArrayList<HistoryViewModel>();
		historyList.add(new HistoryViewModel("Angelina_J", "stole your cheese", ""));
		historyList.add(new HistoryViewModel("Gandillio", "stole your cheese", ""));
		historyList.add(new HistoryViewModel("Blue_Spring", "stole your cheese", ""));
		historyList.add(new HistoryViewModel("HolaPola", "stole your cheese", ""));
	}

	
}
