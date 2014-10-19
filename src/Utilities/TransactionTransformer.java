package utilities;

import java.util.ArrayList;
import java.util.List;

import models.Transaction;


import com.parse.ParseObject;

public class TransactionTransformer {
	
	public ParseObject ReverseTransform(Transaction transaction)
	{
		ParseObject transactionParseObj = new ParseObject("transaction");
		transactionParseObj.put("fromUserId", transaction.fromUserId);
		transactionParseObj.put("toUserId", transaction.toUserId);
		transactionParseObj.put("allowedToSteal", transaction.allowedToSteal);
		
		return transactionParseObj;
	}
	
	public Transaction Transform(ParseObject transactionParseObj)
	{
		String fromUserId = transactionParseObj.getString("fromUserId");
		String toUserId = transactionParseObj.getString("toUserId");
		Boolean allowedToSteal = transactionParseObj.getBoolean("allowedToSteal");
		
		return new Transaction(fromUserId, toUserId, allowedToSteal);
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
