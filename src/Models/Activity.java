package Models;

public class Activity {
	
	public int cheese;
	public String fromUserId;
	public String toUserId;
	
	public Activity (String fromUserId, String toUserId, int cheese)
	{
		this.fromUserId = fromUserId;
		this.toUserId = toUserId;
		this.cheese = cheese;
	}

}
