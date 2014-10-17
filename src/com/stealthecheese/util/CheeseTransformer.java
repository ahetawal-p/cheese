package com.stealthecheese.util;


import com.parse.ParseObject;
import com.stealthecheese.model.Cheese;

public class CheeseTransformer {

	public ParseObject ReverseTransform(Cheese cheese)
	{
		ParseObject cheeseParseObj = new ParseObject("cheese");
		cheeseParseObj.put("userId", cheese.userId);
		cheeseParseObj.put("cheese", cheese.cheese);
		
		return cheeseParseObj;
	}
	
	public Cheese Transform(ParseObject cheeseParseObj)
	{
		String userId = cheeseParseObj.getString("userId");
		int cheese = cheeseParseObj.getInt("cheese");
		
		return new Cheese(userId, cheese);
	}
}
