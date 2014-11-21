package com.stealthecheese.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
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
import com.stealthecheese.enums.UpdateType;
import com.stealthecheese.util.AnimationHandler;
import com.stealthecheese.util.CircularImageView;
import com.stealthecheese.util.ComparatorChain;
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
	CircularImageView userProfileImageView; 
	TextView userCheeseTextView;
	ImageView refreshImageView;
	ImageView rankingsImageView;
	TextView refreshFinishedTextView;
	ParseUser currentUser;
	private HashMap<String, Integer> localCountMap = new HashMap<String, Integer>();
	private HashMap<String, String> facebookIdFirstNameMap = new HashMap<String, String>();
	private HashMap<String, Boolean> localShowMeMap = new HashMap<String, Boolean>();
	private AnimationHandler animationHandler;
	private UpdateType updateType;
	private TextView countDown;
	private CountDownCheeseSteal countDownTimer;
	private Double timeLeft;
	
	private ConcurrentHashMap<String, String> inProgressReq = new ConcurrentHashMap<String, String>();
	
	private ComparatorChain<PlayerViewModel> chain = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_theft);
		currentUser = ParseUser.getCurrentUser();
	
		initializeUtilities();
		initializeUIControls();
		initializeCountDownTimerIfRequired();
		initializeHistoryListView(getResources());
		initializeFriendListVIew(getResources());
		updateType = UpdateType.LOGIN;
	}
	
	private void initializeCountDownTimerIfRequired() {
		Bundle extras = getIntent().getExtras();
		timeLeft = extras.getDouble("CountDown");
		beginCountDown();
	}

	private void beginCountDown() {
		if(timeLeft > 0D){
			countDown.setVisibility(View.VISIBLE);
			countDownTimer = new CountDownCheeseSteal((timeLeft.longValue())*1000, 1000);
			countDownTimer.start();
		}
	}

	private void resetCountDown() {
		timeLeft = 0D;
		countDown.setVisibility(View.GONE);
		if(countDownTimer != null ){
			countDownTimer.cancel();
			countDownTimer = null;
		} 
	}
	
	private void initializeUtilities() {
		this.animationHandler = new AnimationHandler(this);
	}
	
	@Override
	public void onBackPressed() {
			resetCountDown();
		    finish();
            super.onBackPressed();
    }
	
	
	@Override
	public void onStart() {
		StealTheCheeseApplication.setActivityisStillRunning();
		if (updateType == UpdateType.LOGIN) {
			updatePage(); 
		}
		else if (UpdateType.NOUPDATE.equals(updateType))
		{}
		else {
			updateCheeseCountData(refreshImageView);
		}
		
		updateType = UpdateType.REFRESH;
		
		super.onStart();
	}
	
	
	@Override
	public void onPause(){
		StealTheCheeseApplication.setActivityPaused();
		super.onPause();
	}
	
	@Override
	public void onResume(){
		StealTheCheeseApplication.setActivityUnPaused();
		super.onResume();
	}
	
	
	@Override
	public void onNewIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		UpdateType type = (UpdateType)extras.get("UpdateType");
		if(UpdateType.REFRESH.equals(type)){
			updateCheeseCountData(refreshImageView);
		}else if(UpdateType.REALTIME.equals(type)){
			performRealtimeUpdate(extras);
		}
		super.onNewIntent(intent);
	}

	private void performRealtimeUpdate(Bundle extras) {
		resetCountDown();
		List<HashMap<String, Object>> singleUpdateList = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> pushUpdate = new HashMap<String, Object>();
		pushUpdate.put("facebookId", (String)extras.get("ThiefId"));
		pushUpdate.put("cheeseCount", (Integer)extras.get("ThiefCheeseCount"));
		pushUpdate.put("showMe", true);
		pushUpdate.put("animateMe", (Boolean)extras.get("AnimateMe"));
		singleUpdateList.add(pushUpdate);
		
		//update localcount map
		localCountMap.put((String)extras.get("ThiefId"), (Integer)extras.get("ThiefCheeseCount"));
		localShowMeMap.put((String)extras.get("ThiefId"), true);
		
		//1. Add current user cheesecount
		localCountMap.put(currentUser.getString("facebookId"), (Integer)extras.get("CurrentUserCheeseCount"));
		
		//2. Perform Animation on userCheeseCountView
		((TextView)userCheeseTextView).setText("x " + (Integer)extras.get("CurrentUserCheeseCount"));			    	
    	View userCheeseCountContainer = findViewById(R.id.userCheeseCountContainer);
    	animationHandler.bounceCheeseCounters(userCheeseCountContainer);
    	
		refreshFriendsListview(singleUpdateList, true, null);
	}
	
	
	
	
	/* update page when logging in */
	private void updatePage() {
		try {
			List<ParseUser> friendUsers = ParseUser.getQuery()
													.fromLocalDatastore()
													.whereNotEqualTo("facebookId", currentUser.getString("facebookId"))
													.find();
			
			populateViews(friendUsers);
		
		} catch (ParseException e) {
			Log.e(StealTheCheeseApplication.LOG_TAG, "Fetch friends from localstore failed with message: " + e);
		}
	}
	
	/* update page when user refreshes */
	private void updatePage(List<HashMap<String, Object>> cheeseCounts)
	{
        populateUserView();
		refreshFriendsListview(cheeseCounts, true, null);
		populateHistoryListView();
	}
	
	/**
	 * Populates user and friends views
	 * @param friendUsers
	 */
	private void populateViews(List<ParseUser> friendUsers){
        retrieveCheeseCountsLocally();
        populateUserView();
        populateFriendsListView(friendUsers);
        populateHistoryListView();
	}
	
	
	
	private void populateHistoryListView() {
		new HistoryViewTask().execute();
	}

	
	private void retrieveCheeseCountsLocally() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("cheeseCountObj");
		query.fromLocalDatastore();
		try {
			List<ParseObject> cheeseUpdates = query.find();
			for(ParseObject cheese : cheeseUpdates){
				localCountMap.put(cheese.getString("facebookId"), cheese.getInt("cheeseCount"));
				localShowMeMap.put(cheese.getString("facebookId"), cheese.getBoolean("showMe"));
			}
			
		} catch (ParseException e) {
			Log.e(StealTheCheeseApplication.LOG_TAG, "Error getting cheese locally ", e);
		}
		
	}

	/* set display properties for user */
	private void populateUserView()
	{
		/* create dummy user properties, throw away later */
		PlayerViewModel userViewModel = new PlayerViewModel(currentUser.getString("facebookId"), 
										currentUser.getString("profilePicUrl")+"?type=large", 
										localCountMap.get(currentUser.getString("facebookId")), 
										true,
										false);
		
		/* create adapter for user view */
		userCheeseTextView = (TextView) findViewById(R.id.cheeseCountTextView);
		//userProfileImageView = (ImageView) findViewById(R.id.userProfileImageView);
		userProfileImageView = (CircularImageView) findViewById(R.id.userProfileImageView);
		userViewAdapter = new UserViewAdapter(this, userCheeseTextView, userProfileImageView);
		
		/* set display values via adapter */
		userViewAdapter.setUser(userViewModel);		
	}
	
	private void initializeFriendListVIew(Resources resources) {
		chain = populateFriendListComparators();
		friendsListView= ( ListView )findViewById( R.id.friendsListView );   
		friendsListAdapter = new FriendsListAdapter( this, friendsList, resources );
		friendsListView.setAdapter( friendsListAdapter );
	}
	
	
	private void initializeUIControls()
	{
		refreshFinishedTextView = (TextView)findViewById(R.id.refreshFinishedMessage);
		countDown = (TextView)findViewById(R.id.countDownTimer);
		
		initializeImageButtons();
	}
	
	/* hook up image button clicks */
	private void initializeImageButtons()
	{
		/* hook up refresh button to fetch data from Parse and populate views */
		refreshImageView = (ImageView)findViewById(R.id.refreshImageView);
		refreshImageView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				updateCheeseCountData(v);
			}
			
		});
		
		/* hook up rankings button to fetch ranking info from Parse and populate views */
		rankingsImageView = (ImageView)findViewById(R.id.rankingImageView);
		rankingsImageView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TheftActivity.this, RankingsActivity.class);
				updateType = UpdateType.NOUPDATE;
				startActivity(intent);			
				}
			
		});
	}
	
	/* get most updated cheese data from database and pin to localstore
	 * need to refactor these into separate class
	 */
	private void updateCheeseCountData(final View v){
		
		inProgressReq.clear();
		resetCountDown();
		
		final Map<String,Object> params = new HashMap<String,Object>();
		animationHandler.startAnimateRefresh(v);
		ParseCloud.callFunctionInBackground("getAllCheeseCounts", params, new FunctionCallback<HashMap<String, Object>>() {

			@Override
			public void done(final HashMap<String, Object> wrapper, ParseException ex) {
				if(ex == null){
					if(wrapper.containsKey("countDown")){
						timeLeft = (Double)wrapper.get("countDown");
						timeLeft +=100; //buffer time
					}
					final List<HashMap<String, Object>> cheeseCounts = (List<HashMap<String, Object>>)wrapper.get("cheeseCountList");
					List<ParseObject> allCountList = new ArrayList<ParseObject>();
					for(HashMap<String, Object> eachCount : cheeseCounts){
						String friendFacebookId = (String)eachCount.get("facebookId");
						int cheeseCount = (Integer)eachCount.get("cheeseCount");
						boolean showMe = (Boolean)eachCount.get("showMe");
						boolean animateMe = (Boolean)eachCount.get("animateMe");
						
						ParseObject tempObject = new ParseObject("cheeseCountObj");
						tempObject.put("facebookId", friendFacebookId);
						tempObject.put("cheeseCount", cheeseCount);
						tempObject.put("showMe", showMe);
						tempObject.put("animateMe", animateMe);
						
						allCountList.add(tempObject);
						
						/* update local hashmap to contain the newest mappings */
						localCountMap.put(friendFacebookId, cheeseCount);
					}
					
					
					ParseObject.pinAllInBackground(StealTheCheeseApplication.PIN_TAG, allCountList, new SaveCallback() {
						@Override
						public void done(ParseException ex) {
							updatePage(cheeseCounts);
							animationHandler.stopAnimateRefresh(v);
							AnimationListener animL = new AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {}

								@Override
								public void onAnimationRepeat(Animation animation) {}

								@Override
								public void onAnimationEnd(Animation animation) {
									//beginCountDown();
								}
							};
							animationHandler.fadeInOutView(refreshFinishedTextView, animL);
						}
					});
				}
			}
		});
	}

	
	/* set history list view adapter */
	private void initializeHistoryListView(Resources res) {
        historyListView= ( ListView )findViewById( R.id.historyListView ); 
        historyListAdapter=new HistoryListAdapter( this, historyList,res );
        historyListView.setAdapter( historyListAdapter );
	}
	
	/* set friends list view adapter and handle onClick events */
	private void populateFriendsListView(List<ParseUser> userFriends) {
		friendsList.clear();
		facebookIdFirstNameMap.clear();
		for(ParseUser friend : userFriends){
			String imageUrl = String.format(StealTheCheeseApplication.FRIEND_CHEESE_COUNT_PIC_URL, friend.getString("facebookId"));
			friendsList.add(new PlayerViewModel(friend.getString("facebookId"), 
												imageUrl,
												localCountMap.get(friend.getString("facebookId")), 
												localShowMeMap.get(friend.getString("facebookId")),
												false));
			
			facebookIdFirstNameMap.put(friend.getString("facebookId"), friend.getString("firstName"));
		}
		
		Collections.sort(friendsList, chain);
		friendsListAdapter.notifyDataSetChanged();      
	}
	
	
	
	
	public ComparatorChain<PlayerViewModel> populateFriendListComparators(){
		if(this.chain == null){
			Comparator<PlayerViewModel> compareVisibility = new Comparator<PlayerViewModel>() {
				@Override
				public int compare(PlayerViewModel lhs, PlayerViewModel rhs) {
					return lhs.getShowMe().compareTo(rhs.getShowMe());
				}
			};
			Comparator<PlayerViewModel> compareCounts = new Comparator<PlayerViewModel>() {
				@Override
				public int compare(PlayerViewModel lhs, PlayerViewModel rhs) {
					return lhs.getCheese().compareTo(rhs.getCheese());
				}
			};
			
			ComparatorChain<PlayerViewModel> comparatorChain = new ComparatorChain<PlayerViewModel>();
			comparatorChain.addComparator(compareVisibility, true);
			comparatorChain.addComparator(compareCounts, true);
			return comparatorChain;
		}
		return this.chain;
		
	}

	@SuppressWarnings("unchecked")
	private void refreshFriendsListview(List<HashMap<String, Object>> friendCheeseObjects, boolean doSort, String victimId) {
		new RefreshFriendsViewTask(doSort, victimId).execute(friendCheeseObjects);
	}
	
	
	class RefreshFriendsViewTask extends AsyncTask<List<HashMap<String, Object>>, Void, Void> {
		
		private boolean enableSorting; 
		private String victimId;
		
		public RefreshFriendsViewTask(boolean doSort, String friendFBId) {
			this.enableSorting = doSort;
			this.victimId = friendFBId;
		}

		@Override
		protected Void doInBackground(List<HashMap<String, Object>>... friendCheeseObjects) {
			if(friendCheeseObjects[0] == null || friendCheeseObjects[0].size() == 0){
				return null;
			}
			for(HashMap<String, Object> eachCount : friendCheeseObjects[0]){
				String friendFacebookId = (String)eachCount.get("facebookId");
				if (friendFacebookId.equals(currentUser.getString("facebookId"))){
					
					continue;
				}
				else {
					
					//reset inProgressRequest for the victim which was clicked
					if(friendFacebookId.equals(victimId)){
						inProgressReq.remove(victimId);
					}
					/* check match with old friendsList, diff and update */
					Boolean showMe = (Boolean)eachCount.get("showMe");
					String imageUrl = String.format(StealTheCheeseApplication.FRIEND_CHEESE_COUNT_PIC_URL, (String)eachCount.get("facebookId"));
					int cheeseCount = (Integer)eachCount.get("cheeseCount");
					boolean animateMe = (Boolean)eachCount.get("animateMe");
					
					PlayerViewModel existingFriend = findFriendInList(friendFacebookId);
					if (existingFriend == null) {
						friendsList.add(new PlayerViewModel(friendFacebookId, imageUrl , localCountMap.get(friendFacebookId), showMe, animateMe));
					} else {
						existingFriend.setCheese(cheeseCount);
						existingFriend.setAnimateMe(animateMe);
						if(!inProgressReq.containsKey(existingFriend.getFacebookId())){
							existingFriend.setShowMe(showMe);
						}else {
							existingFriend.setShowMe(false);
						}
					}
					
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			if(this.enableSorting){
				Collections.sort(friendsList, chain);
			}
			friendsListAdapter.notifyDataSetChanged();
		}
		
	}
	
	private PlayerViewModel findFriendInList(String facebookId) {
		for (PlayerViewModel friend : friendsList) {
			if (facebookId.equals(friend.getFacebookId())) {
				return friend;
			}
		}
		
		return null;
	}
	
	
	
	
	public void onCheeseTheft(View friendImageClicked, int position, ImageView movedCheeseImg, TextView cheeseCounter){
    	/* lock list item so you can't click it again before verifying > 0 */
		friendsListAdapter.lockImageClick((ImageView)friendImageClicked, cheeseCounter);
		
		String friendFacebookId = getFriendFacebookId(position);
		inProgressReq.put(friendFacebookId, "Y");
		
		/* display animation and start cheese theft async process */
		animationHandler.animateCheeseTheft(friendImageClicked, movedCheeseImg, cheeseCounter, userProfileImageView, 0, 0);
    	performCheeseTheft(friendFacebookId, (ImageView)friendImageClicked, cheeseCounter);
	}

	
	private void performCheeseTheft(final String friendFacebookId, final ImageView friendImageClicked, final TextView cheeseCounter) {
		final Map<String,Object> params = new HashMap<String,Object>();
		params.put("victimFacebookId", friendFacebookId);
		params.put("thiefFacebookId", currentUser.getString("facebookId"));
		
		resetCountDown();
		ParseCloud.callFunctionInBackground("onCheeseTheft", params, new FunctionCallback<HashMap<String, Object>>() {
	        public void done(HashMap<String, Object> wrapper, ParseException e) {
	        	if (e == null){   
	        	  if(wrapper.containsKey("countDown")){
						timeLeft = (Double)wrapper.get("countDown");
						timeLeft +=100; //buffer time
					}
	        	  
	        	  localCountMap.clear();
	        	  List<HashMap<String, Object>> allUpdates = (List<HashMap<String, Object>>)wrapper.get("cheeseCountList");
			    	for(HashMap<String, Object> eachCount : allUpdates){
			    		localCountMap.put((String)eachCount.get("facebookId"), (Integer)eachCount.get("cheeseCount"));
			    	}
			    	
					int currentCheesCount = localCountMap.get(currentUser.getString("facebookId"));
					int frndCurrentCheeseCount = localCountMap.get(friendFacebookId);
					
			    	cheeseCounter.setText(Integer.toString(frndCurrentCheeseCount));
					((TextView)userCheeseTextView).setText("x " + Integer.toString(currentCheesCount));			    	
			    	
		    		View userCheeseCountContainer = findViewById(R.id.userCheeseCountContainer);
		    		animationHandler.bounceCheeseCounters(userCheeseCountContainer, cheeseCounter);
		    		
		    		refreshFriendsListview(allUpdates, false, friendFacebookId);
		    		
		    		/* populate theft history asynchronously after friend cheese counts are updated */
		    		populateHistoryListView();
	          } else {
	        	/* if friend has no cheese, update cheese count to 0 and display message */
	  			Log.e(StealTheCheeseApplication.LOG_TAG, "Cheese theft failed with message: ", e);
	  			Toast theftFailedToast = Toast.makeText(getApplicationContext(), R.string.cheese_theft_failed_message, Toast.LENGTH_SHORT);
	  			theftFailedToast.setGravity(Gravity.CENTER, 0, 0);
	  			theftFailedToast.show();
	  			List<HashMap<String, Object>> failedList = new ArrayList<HashMap<String,Object>>();
	  			HashMap<String, Object> failedRequest = new HashMap<String, Object>();
	  			failedRequest.put("facebookId", friendFacebookId);
	  			failedRequest.put("cheeseCount", 0);
	  			failedRequest.put("showMe", false);
	  			failedRequest.put("animateMe", false);
	  			failedList.add(failedRequest);
	  			localCountMap.put(friendFacebookId, 0);
	  			refreshFriendsListview(failedList, false, friendFacebookId);
	  		  }
	        	//beginCountDown(); perf issues on continuous clicks
	       }
	    });
		}
	
	private String getFriendFacebookId(int position) {
		String facebookId;
		try {
			facebookId = friendsList.get(position).getFacebookId();
		}
		catch (Exception ex){
			Log.e(StealTheCheeseApplication.LOG_TAG, "Cannot find facebook Id of friend in list");
			facebookId = "";
		}
		
		return facebookId;
	}

	
	
	class HistoryViewTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			ParseQuery<ParseObject> histQuery = ParseQuery.getQuery("thefthistory");
			histQuery.whereEqualTo("victimFBId", currentUser.get("facebookId"));
			histQuery.orderByDescending("createdAt");
			histQuery.setLimit(5);
			
			try {
				List<ParseObject> objects = histQuery.find();
				historyList.clear();
				List<ParseObject> lastTenTrans = objects;
				int visible = ((View)historyListView.getParent()).getVisibility();
				for(ParseObject trans : lastTenTrans){
					String firstName = facebookIdFirstNameMap.get(trans.getString("thiefFBId"));
					historyList.add(new HistoryViewModel(firstName));
				}
				if(lastTenTrans.size() > 0 && (View.VISIBLE != visible)){
					return true;
				}
				
			} catch (ParseException e) {
				Log.e(StealTheCheeseApplication.LOG_TAG, "Error getting histview ", e);
			}
			return false;
		}
		
		
		@Override
		protected void onPostExecute(Boolean result){
			if(result){
				((View)historyListView.getParent()).setVisibility(View.VISIBLE);
				animationHandler.fadeIn((View)historyListView.getParent());
			}
			historyListAdapter.notifyDataSetChanged();
		}
	}
	
	
	
	public class CountDownCheeseSteal extends CountDownTimer {
		
		public CountDownCheeseSteal(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millis) {
			countDown.setText(String.format("%02d:%02d:%02d", 
					TimeUnit.MILLISECONDS.toHours(millis),
					TimeUnit.MILLISECONDS.toMinutes(millis) -  
					TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), 
					TimeUnit.MILLISECONDS.toSeconds(millis) - 
					TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))); 
    		
		}

		@Override
		public void onFinish() {
			countDown.setVisibility(View.GONE);
			updateCheeseCountData(refreshImageView);
		}
		
		
		

	}
	
	
	
	
	@Override
	public void onDestroy() {
		System.out.println("Called destory...");
		ParseObject.unpinAllInBackground(StealTheCheeseApplication.PIN_TAG);
		StealTheCheeseApplication.setActivityisStopping();
		super.onDestroy();
		
	}
	
}
