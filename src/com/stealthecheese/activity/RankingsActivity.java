package com.stealthecheese.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.stealthecheese.R;
import com.stealthecheese.adapter.RankingsListAdapter;
import com.stealthecheese.application.StealTheCheeseApplication;
import com.stealthecheese.enums.UpdateType;
import com.stealthecheese.viewmodel.RankingViewModel;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ListView;

public class RankingsActivity extends Activity {
	ArrayList<RankingViewModel> rankingsList = new ArrayList<RankingViewModel>();
	RankingsListAdapter rankingsListAdapter;
	ListView rankingsListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rankings);
		fetchRankingsData();
	}

	private void fetchRankingsData()
	{
		final Map<String,Object> params = new HashMap<String,Object>();
		ParseCloud.callFunctionInBackground("onGetRankings", params, new FunctionCallback<List<HashMap<String, Object>>>() {

			@Override
			public void done(final List<HashMap<String, Object>> playerRankingList, ParseException ex) {
				if(ex == null){
					rankingsList.clear();
					for(HashMap<String, Object> playerRanking : playerRankingList){
						String playerFacebookId = (String)playerRanking.get("facebookId");
						int cheeseCount = (Integer)playerRanking.get("cheeseCount");
						int ranking = (Integer)playerRanking.get("ranking");
						String firstName = (String)playerRanking.get("firstName");
						String imageUrl = String.format(StealTheCheeseApplication.FRIEND_CHEESE_COUNT_PIC_URL, playerFacebookId);
						Boolean isUser = playerFacebookId.equals(ParseUser.getCurrentUser().getString("facebookId"));
						RankingViewModel rankingViewModel = new RankingViewModel(firstName, imageUrl, cheeseCount, ranking, isUser);
						rankingsList.add(rankingViewModel);
					}
					
					Collections.sort(rankingsList, new RankingsComparator());
					rankingsList.subList(0, 10);
					initializeRankingsListView(getResources());
				}
			}
		});
	}
	
	private void initializeRankingsListView(Resources res)
	{
		rankingsListView= ( ListView )findViewById( R.id.rankingsListView );   
		rankingsListAdapter = new RankingsListAdapter( this, rankingsList, res );
		rankingsListView.setAdapter( rankingsListAdapter );
	}
	
	class RankingsComparator implements Comparator<RankingViewModel> {
	    public int compare(RankingViewModel ranking1, RankingViewModel ranking2) {
	        return ranking1.getRanking().compareTo(ranking2.getRanking());
	    }
	}
	
	/*
	public void onBackPressed(){
		Intent newIntent = new Intent(this, TheftActivity.class);
		newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		newIntent.putExtra("UpdateType", UpdateType.NOUPDATE);
        this.startActivity(newIntent);	}
	*/
}
