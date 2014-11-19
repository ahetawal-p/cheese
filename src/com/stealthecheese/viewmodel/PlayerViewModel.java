package com.stealthecheese.viewmodel;


public class PlayerViewModel {
	private Integer cheese = 0;
	private String facebookId;
	private String imageString;
	private Boolean showMe;
	private boolean animateMe;
	
	
	public PlayerViewModel(String facebookId, String imageString, Integer cheese, Boolean showMe, boolean animateMe) {
		this.facebookId = facebookId;
		this.imageString = imageString;
		this.cheese = cheese;
		this.showMe = showMe;
		this.animateMe = animateMe;
	}
	
	public boolean getAnimateMe() {
		return animateMe;
	}

	public void setAnimateMe(boolean animateMe) {
		this.animateMe = animateMe;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PlayerViewModel [cheese=");
		builder.append(cheese);
		builder.append(", facebookId=");
		builder.append(facebookId);
		builder.append(", imageString=");
		builder.append(imageString);
		builder.append(", showMe=");
		builder.append(showMe);
		builder.append(", animateMe=");
		builder.append(animateMe);
		builder.append("]");
		return builder.toString();
	}

	
	
	
	
}
