package com.example.stealthecheese;

import java.util.ArrayList;
import java.util.List;

import viewModels.FriendViewModel;
import viewModels.HistoryViewModel;

import models.Transaction;
import adapters.FriendsListAdapter;
import adapters.HistoryListAdapter;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends Activity {

	ListView historyListView;
	ListView friendsListView;
	ArrayList<HistoryViewModel> historyList;
	ArrayList<FriendViewModel> friendsList;
	HistoryListAdapter historyListAdapter;
	FriendsListAdapter friendsListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Test parse code
		//UnitOfWork uow = new UnitOfWork();
		//Transaction newTransaction = new Transaction("user2", "user1", false);
		//uow.transactionDAO.createTransaction(newTransaction);

		populateHistoryListview();

        Resources res =getResources();
        historyListView= ( ListView )findViewById( R.id.historyListView );  // List defined in XML ( See Below )
        
        /**************** Create Custom Adapter *********/
        historyListAdapter=new HistoryListAdapter( this, historyList,res );
        historyListView.setAdapter( historyListAdapter );
        
        populateFriendsListview();
        friendsListView= ( ListView )findViewById( R.id.friendsListView );  // List defined in XML ( See Below )
        
        /**************** Create Custom Adapter *********/
        friendsListAdapter = new FriendsListAdapter( this, friendsList,res );
        friendsListView.setAdapter( friendsListAdapter );
	}

	//Temp code to populate dummy friend list
	private void populateFriendsListview()
	{
		friendsList = new ArrayList<FriendViewModel>();
		friendsList.add(new FriendViewModel("",4));
		friendsList.add(new FriendViewModel("",12));
		friendsList.add(new FriendViewModel("",10));
		friendsList.add(new FriendViewModel("",2));
		friendsList.add(new FriendViewModel("",3));
		friendsList.add(new FriendViewModel("",3));
		friendsList.add(new FriendViewModel("",5));
		friendsList.add(new FriendViewModel("",6));
		friendsList.add(new FriendViewModel("",2));
		friendsList.add(new FriendViewModel("",3));
		friendsList.add(new FriendViewModel("",7));
		friendsList.add(new FriendViewModel("",9));
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
	
    public void onItemClick(int mPosition)
    {
//        ListModel tempValues = ( ListModel ) CustomListViewValuesArr.get(mPosition);
//
//
//       // SHOW ALERT                  
//
//        Toast.makeText(CustomListView,
//                ""+tempValues.getCompanyName()
//                  +" Image:"+tempValues.getImage() +"Url:"+tempValues.getUrl(),
//                Toast.LENGTH_LONG)
//        .show();
    }
	

}
