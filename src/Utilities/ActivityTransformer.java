package Utilities;

import java.util.ArrayList;
import java.util.List;

import Models.Activity;

import com.parse.ParseObject;

public class ActivityTransformer {
	
	public ParseObject ReverseTransform(Activity activity)
	{
		ParseObject activityParseObj = new ParseObject("activity");
		activityParseObj.put("fromUserId", activity.fromUserId);
		activityParseObj.put("toUserId", activity.toUserId);
		activityParseObj.put("cheese", activity.cheese);
		
		return activityParseObj;
	}
	
	public Activity Transform(ParseObject activityParseObj)
	{
		String fromUserId = activityParseObj.getString("fromUserId");
		String toUserId = activityParseObj.getString("toUserId");
		int cheese = activityParseObj.getInt("cheese");
		
		return new Activity(fromUserId, toUserId, cheese);
	}
	
	public List<Activity> Transform(List<ParseObject> activityParseObjs)
	{
		List<Activity> activities = new ArrayList<Activity>();
		for (final ParseObject activityParseObj : activityParseObjs)
		{
			activities.add(Transform(activityParseObj));
		}
		
		return activities;
	}
}
