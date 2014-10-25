package com.stealthecheese.viewmodel;


public class PlayerViewModel {
	private int cheese = 0;
	private String facebookId;
	private String imageString;
	
	public PlayerViewModel(String facebookId, String imageString, int cheese)
	{
		this.facebookId = facebookId;
		this.imageString = imageString;
		this.cheese = cheese;
	}
	
	public String getImageString()
	{
		return this.imageString;
	}
	
	public String getFacebookId()
	{
		return this.facebookId;
	}
	
	public int getCheese()
	{
		return this.cheese;
	}
}
