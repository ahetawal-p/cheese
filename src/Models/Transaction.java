package Models;

public class Transaction {
	
	public int cheese;
	public String fromUserId;
	public String toUserId;
	
	public Transaction (String fromUserId, String toUserId)
	{
		this.fromUserId = fromUserId;
		this.toUserId = toUserId;
	}

}
