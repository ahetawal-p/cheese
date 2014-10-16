package Utilities;

public class TransformerManager {

	public UserTransformer userTransformer;
	public CheeseTransformer cheeseTransformer;
	public ActivityTransformer activityTransformer;
	
	public TransformerManager()
	{
		this.userTransformer = new UserTransformer();
		this.cheeseTransformer = new CheeseTransformer();
		this.activityTransformer = new ActivityTransformer();
	}
}
