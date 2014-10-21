package com.stealthecheese.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
	private int cheese;
	private String imageUrl;
	private String facebookId;
	
	public User(String facebookId, int cheese, String imageUrl)
	{
		this.facebookId = facebookId;
		this.cheese = cheese;
		this.imageUrl = imageUrl;
	}
	
	public int getCheese()
	{
		return this.cheese;
	}
	
	public String getImageUrl()
	{
		return this.imageUrl;
	}
	
	public String getFacebookId()
	{
		return this.facebookId;
	}
	
	private User(Parcel in)
	{
		this.facebookId = in.readString();
        this.cheese = in.readInt();
        this.imageUrl = in.readString();
    }
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(facebookId);
		dest.writeInt(this.cheese);
		dest.writeString(this.imageUrl);
		
	}

	private void readFromParcel(Parcel in) {
		facebookId = in.readString();
	    cheese = in.readInt();
	    imageUrl = in.readString();
	}
	
   public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
	   
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }
 
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
		
}
