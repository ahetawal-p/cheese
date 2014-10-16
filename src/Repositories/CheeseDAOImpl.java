package Repositories;

import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import Models.Cheese;
import Utilities.TransformerManager;

public class CheeseDAOImpl implements CheeseDAO {
	
	public TransformerManager transformerManager;
	
	public CheeseDAOImpl(TransformerManager transformerManager)
	{
		this.transformerManager = transformerManager;
	}
	
	public Cheese getCheese(String userId)
	{
		final Cheese cheese = null;
		ParseQuery<ParseObject> query = ParseQuery.getQuery("cheese");
		query.whereEqualTo("userId", userId);
		
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done( final List<ParseObject> cheeseObjs, ParseException e) {
		        if (e == null) {
		        	Cheese cheeseObj = transformerManager.cheeseTransformer.Transform(cheeseObjs.get(0));
		        	cheese.setValues(cheeseObj);
		        } else {
		        	//log error
		        }
		    }
		});
		
		return cheese;
	}
	
	public void saveCheese(Cheese cheese)
	{
		ParseObject parseCheeseObj = transformerManager.cheeseTransformer.ReverseTransform(cheese);
		parseCheeseObj.saveEventually();
	}
}
