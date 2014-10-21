package com.stealthecheese.viewmodel;

public class HistoryViewModel {
	private String friendName = "";
	private String stoleCheeseMesage = "stole your cheese";
	private String imageString = "";
	
	public HistoryViewModel(String friendName, String stoleCheeseMessage, String imageString)
	{
		this.friendName = friendName;
		this.stoleCheeseMesage = stoleCheeseMessage;
		this.imageString = imageString;
	}
	
	public String getFriendName()
	{
		return this.friendName;
	}
	
	public String getImageString()
	{
		return this.imageString;
	}
	
	public String getStoleCheeseMessage()
	{
		return this.stoleCheeseMesage;
	}
	
}
