package com.stealthecheese.model;

public class Transaction {
	
	public int cheese;
	public String fromUserId;
	public String toUserId;
	
	public Transaction (String fromUserId, String toUserId, int cheese)
	{
		this.fromUserId = fromUserId;
		this.toUserId = toUserId;
		this.cheese = cheese;
	}

}
