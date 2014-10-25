package com.stealthecheese.viewmodel;

public class HistoryViewModel {
	private String friendName = "";
	private String imageString = "";
	
	public HistoryViewModel(String friendName, String imageString)
	{
		this.friendName = friendName;
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
	
	
}
