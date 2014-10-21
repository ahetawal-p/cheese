package viewModels;

import android.graphics.Bitmap;

public class FriendViewModel {
	private int cheese = 0;
	private String facebookId;
	
	public FriendViewModel(String facebookId, int cheese)
	{
		this.facebookId = facebookId;
		this.cheese = cheese;
	}
	
	
	/*public String getImageString()
	{
		return this.imageString;
	}
	
	public Bitmap getImageBitmap()
	{
		return this.imageBitmap;
	}*/
	
	public String getFacebookId()
	{
		return this.facebookId;
	}
	
	public int getCheese()
	{
		return this.cheese;
	}
}
