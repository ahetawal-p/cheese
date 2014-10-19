package repositories;

import java.util.ArrayList;
import java.util.List;

import utilities.TransformerManager;

import models.Transaction;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class TransactionDAOImpl implements TransactionDAO{
	
	private TransformerManager transformerManager;
	public TransactionDAOImpl(TransformerManager transformerManager)
	{
		this.transformerManager = transformerManager;
	}
	
	public void createTransaction(Transaction transaction)
	{
		ParseObject transactionParseObject = transformerManager.transactionTransformer
															.ReverseTransform(transaction);
		try
		{
			transactionParseObject.save();
		//transactionParseObject.saveEventually();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	
	public List<Transaction> getTransactions(String userId)
	{
		final List<Transaction> userTransactions = new ArrayList<Transaction>();
		
		//get activities where user either stole cheese or got stolen
		ParseQuery<ParseObject> fromUser = ParseQuery.getQuery("transaction");
		fromUser.whereEqualTo("fromUserId", userId);
		
		ParseQuery<ParseObject> toUser = ParseQuery.getQuery("transaction");
		toUser.whereEqualTo("toUserId", userId);
		
		List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
		queries.add(fromUser);
		queries.add(toUser);
		
		ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
		mainQuery.findInBackground(new FindCallback<ParseObject>() {
			  public void done(List<ParseObject> results, ParseException e) {
			        if (e == null) {
			        	userTransactions.addAll(transformerManager.transactionTransformer.Transform(results));
			        } else {
			        	//log error
			        }
			  }

			});
		
		return userTransactions;
	}

}
