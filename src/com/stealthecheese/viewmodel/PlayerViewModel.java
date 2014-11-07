package com.stealthecheese.viewmodel;


public class PlayerViewModel {
	private int cheese = 0;
	private String facebookId;
	private String imageString;
	private Boolean showMe;
	
	public PlayerViewModel(String facebookId, String imageString, int cheese, Boolean showMe)
	{
		this.facebookId = facebookId;
		this.imageString = imageString;
		this.cheese = cheese;
		this.showMe = showMe;
	}
	
	public Boolean getShowMe()
	{
		return this.showMe;
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
