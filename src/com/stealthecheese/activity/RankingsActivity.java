package com.stealthecheese.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.stealthecheese.R;
import com.stealthecheese.adapter.RankingsListAdapter;
import com.stealthecheese.application.StealTheCheeseApplication;
import com.stealthecheese.enums.UpdateType;
import com.stealthecheese.util.CircleTransform;
import com.stealthecheese.viewmodel.RankingViewModel;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RankingsActivity extends Activity {
	ArrayList<RankingViewModel> rankingsList = new ArrayList<RankingViewModel>();
	RankingsListAdapter rankingsListAdapter;
	ListView rankingsListView;
	TextView userRankingTextView;
	ImageView backButtonImageView;
	ParseUser currentUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rankings);
		currentUser = ParseUser.getCurrentUser();
		initializeUIControls();
		fetchRankingsData();
	}

	private void initializeUIControls()
	{
		userRankingTextView = (TextView)findViewById(R.id.userRankingTextView);
		backButtonImageView = (ImageView)findViewById(R.id.backButtonImageView);
		backButtonImageView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();	
				}
			
		});
	}
	
	private void fetchRankingsData()
	{
		final Map<String,Object> params = new HashMap<String,Object>();
		ParseCloud.callFunctionInBackground("onGetRankings", params, new FunctionCallback<List<HashMap<String, Object>>>() {

			@Override
			public void done(final List<HashMap<String, Object>> playerRankingList, ParseException ex) {
				if(ex == null){
					rankingsList.clear();
					int userRanking = 0;
					int userCheeseCount = 0;
					for(HashMap<String, Object> playerRanking : playerRankingList){
						String playerFacebookId = (String)playerRanking.get("facebookId");
						int cheeseCount = (Integer)playerRanking.get("cheeseCount");
						int ranking = (Integer)playerRanking.get("ranking");
						String firstName = (String)playerRanking.get("firstName");
						String imageUrl = String.format(StealTheCheeseApplication.FRIEND_CHEESE_COUNT_PIC_URL, playerFacebookId);
						Boolean isUser = playerFacebookId.equals(currentUser.getString("facebookId"));
						
						if (isUser)
						{
							userRanking = ranking;
							userCheeseCount = cheeseCount;
						}
						
						RankingViewModel rankingViewModel = new RankingViewModel(playerFacebookId, firstName, imageUrl, cheeseCount, ranking, isUser);
						rankingsList.add(rankingViewModel);
					}
					
					populateUserRanking(userRanking, userCheeseCount);
					rankingsList = deDupe(rankingsList);
					Collections.sort(rankingsList, new RankingsComparator());
					rankingsList = new ArrayList<RankingViewModel>(rankingsList.subList(0, 9));
					initializeRankingsListView(getResources());
				}
			}
		});
	}
	
	/* deduplicate array list */
	private ArrayList<RankingViewModel> deDupe(List<RankingViewModel> recs) {

	    Set<String> tags = new HashSet<String>();
	    List<RankingViewModel> result = new ArrayList<RankingViewModel>();

	    for(RankingViewModel rec : recs) {
	        if(!tags.contains(rec.getFacebookId())) {
	            result.add(rec);
	            tags.add(rec.getFacebookId());
	        }
	    }

	    return (ArrayList<RankingViewModel>) result;
	}
	
	private void populateUserRanking(int ranking, int cheeseCount)
	{
		userRankingTextView.setText(getOrdinal(ranking));
		
		if (ranking > 10)
		{
			View userGreaterThanTenView = findViewById(R.id.userGreaterThanTenRanking);
			userGreaterThanTenView.setVisibility(View.VISIBLE);
			TextView userNameTextView = (TextView)userGreaterThanTenView.findViewById(R.id.playerNameTextview);
			TextView userCheeseCountTextView = (TextView)userGreaterThanTenView.findViewById(R.id.cheeseCountTextView);
            ImageView userImageView = (ImageView)userGreaterThanTenView.findViewById(R.id.playerImageView);
			
			/* load user image view */
			String imageUrl = String.format(StealTheCheeseApplication.FRIEND_CHEESE_COUNT_PIC_URL, currentUser.get("facebookId"));
			Transformation circleTransform = new CircleTransform();
            Picasso.with(this).load(imageUrl)
            .transform(circleTransform)
            .into(userImageView);
            
            /* load user name and cheese count */
            userNameTextView.setText(currentUser.getString("firstName"));
            String cheeseCountText = "x " + cheeseCount;
            userCheeseCountTextView.setText(cheeseCountText);
		}
	}
	
	private void initializeRankingsListView(Resources res)
	{
		rankingsListView= ( ListView )findViewById( R.id.rankingsListView );   
		rankingsListAdapter = new RankingsListAdapter( this, rankingsList, res );
		rankingsListView.setAdapter( rankingsListAdapter );
	}
	
	private String getOrdinal(int num)
	{
		  String[] suffix = {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
		  int m = num % 100;
		  return Integer.toString(num) + suffix[(m > 10 && m < 20) ? 0 : (m % 10)];
	}
	
	class RankingsComparator implements Comparator<RankingViewModel> {
	    public int compare(RankingViewModel ranking1, RankingViewModel ranking2) {
	        return ranking1.getRanking().compareTo(ranking2.getRanking());
	    }
	}
	
}
