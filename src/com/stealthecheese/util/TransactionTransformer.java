package com.stealthecheese.util;

import java.util.ArrayList;
import java.util.List;


import com.parse.ParseObject;
import com.stealthecheese.model.Transaction;

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
