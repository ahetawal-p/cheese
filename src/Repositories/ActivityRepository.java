package Repositories;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import Models.Activity;
import Models.Cheese;
import Utilities.TransformerManager;

public class ActivityRepository {
	
	private TransformerManager transformerManager;
	public ActivityRepository(TransformerManager transformerManager)
	{
		this.transformerManager = transformerManager;
	}
	
	public void createActivity(Activity activity)
	{
		ParseObject activityParseObject = transformerManager.activityTransformer
															.ReverseTransform(activity);
		activityParseObject.saveEventually();
	}
	
	public List<Activity> getActivities(String userId)
	{
		final List<Activity> userActivities = new ArrayList<Activity>();
		
		//get activities where user either stole cheese or got stolen
		ParseQuery<ParseObject> fromUser = ParseQuery.getQuery("activity");
		fromUser.whereEqualTo("fromUserId", userId);
		
		ParseQuery<ParseObject> toUser = ParseQuery.getQuery("activity");
		toUser.whereEqualTo("toUserId", userId);
		
		List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
		queries.add(fromUser);
		queries.add(toUser);
		
		ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
		mainQuery.findInBackground(new FindCallback<ParseObject>() {
			  public void done(List<ParseObject> results, ParseException e) {
			        if (e == null) {
			        	userActivities.addAll(transformerManager.activityTransformer.Transform(results));
			        } else {
			        	//log error
			        }
			  }

			});
		
		return userActivities;
	}

}
