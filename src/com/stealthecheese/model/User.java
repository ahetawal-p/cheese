package com.stealthecheese.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
	private int cheese;
	private String facebookId;
	
	public User(String facebookId, int cheese)
	{
		this.facebookId = facebookId;
		this.cheese = cheese;
	}
	
	public int getCheese()
	{
		return this.cheese;
	}
	
	public String getFacebookId()
	{
		return this.facebookId;
	}
	
	private User(Parcel in)
	{
		this.facebookId = in.readString();
        this.cheese = in.readInt();
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
	}

	private void readFromParcel(Parcel in) {
		facebookId = in.readString();
	    cheese = in.readInt();
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
