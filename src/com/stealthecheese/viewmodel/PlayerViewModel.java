package com.stealthecheese.viewmodel;


public class PlayerViewModel {
	private Integer cheese = 0;
	private String facebookId;
	private String imageString;
	private Boolean showMe;
	
	public PlayerViewModel(String facebookId, String imageString, Integer cheese, Boolean showMe)
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
	
	public void setShowMe(Boolean showMe)
	{
		this.showMe = showMe;
	}
	
	public String getImageString()
	{
		return this.imageString;
	}
	
	public void setImageString(String imageString)
	{
		this.imageString = imageString;
	}
	
	public String getFacebookId()
	{
		return this.facebookId;
	}
	
	public Integer getCheese()
	{
		return this.cheese;
	}
	
	public void setCheese(Integer cheese)
	{
		this.cheese = cheese;
	}
	
}
