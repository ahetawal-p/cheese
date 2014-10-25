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

public class TheftActivity extends Activity {
	ListView historyListView;
	ListView friendsListView;
	ArrayList<HistoryViewModel> historyList;
	ArrayList<FriendViewModel> friendsList;
	HistoryListAdapter historyListAdapter;
	FriendsListAdapter friendsListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_theft);		

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
		friendsList = new ArrayList<FriendViewModel>(friends.size());	
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
