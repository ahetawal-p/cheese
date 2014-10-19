package models;

public class Transaction {
	
	public int cheese;
	public String fromUserId;
	public String toUserId;
	public Boolean allowedToSteal;
	
	public Transaction (String fromUserId, String toUserId, Boolean allowedToSteal)
	{
		this.fromUserId = fromUserId;
		this.toUserId = toUserId;
		this.allowedToSteal = allowedToSteal;
	}

}
