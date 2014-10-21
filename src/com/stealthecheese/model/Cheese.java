package com.stealthecheese.model;

public class Cheese {
	public String  userId;
	public int cheese;
	
	public Cheese(String userId, int cheese)
	{
		this.userId = userId;
		this.cheese = cheese;
	}
	
	public void setValues(String userId, int cheese)
	{
		this.userId = userId;
		this.cheese = cheese;
	}
	
	public void setValues(Cheese newCheese)
	{
		setValues(newCheese.userId, newCheese.cheese);
	}
}
