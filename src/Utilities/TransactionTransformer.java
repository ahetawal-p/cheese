package Utilities;

import java.util.ArrayList;
import java.util.List;

import Models.Transaction;

import com.parse.ParseObject;

public class TransactionTransformer {
	
	public ParseObject ReverseTransform(Transaction activity)
	{
		ParseObject activityParseObj = new ParseObject("activity");
		activityParseObj.put("fromUserId", activity.fromUserId);
		activityParseObj.put("toUserId", activity.toUserId);
		activityParseObj.put("cheese", activity.cheese);
		
		return activityParseObj;
	}
	
	public Transaction Transform(ParseObject activityParseObj)
	{
		String fromUserId = activityParseObj.getString("fromUserId");
		String toUserId = activityParseObj.getString("toUserId");
		int cheese = activityParseObj.getInt("cheese");
		
		return new Transaction(fromUserId, toUserId, cheese);
	}
	
	public List<Transaction> Transform(List<ParseObject> activityParseObjs)
	{
		List<Transaction> activities = new ArrayList<Transaction>();
		for (final ParseObject activityParseObj : activityParseObjs)
		{
			activities.add(Transform(activityParseObj));
		}
		
		return activities;
	}
}
