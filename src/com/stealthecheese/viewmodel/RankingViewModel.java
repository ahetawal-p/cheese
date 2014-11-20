package com.stealthecheese.viewmodel;


public class RankingViewModel {
	private Integer cheese = 0;
	private Integer ranking;
	private String imageString;
	private String firstName;
	private Boolean isUser;
	private String facebookId;
	
	public RankingViewModel(String facebookId, String firstName, String imageString, Integer cheese, Integer ranking, Boolean isUser)
	{
		this.facebookId = facebookId;
		this.firstName = firstName;
		this.imageString = imageString;
		this.cheese = cheese;
		this.ranking = ranking;
		this.isUser = isUser;
	}
	
	public Boolean getIsUser()
	{
		return this.isUser;
	}
	
	public String getFacebookId()
	{
		return this.imageString;
	}
	
	public void setFacebookId(String facebookId)
	{
		this.facebookId = facebookId;
	}
	
	public String getImageString()
	{
		return this.imageString;
	}
	
	public void setImageString(String imageString)
	{
		this.imageString = imageString;
	}
	
	public String getFirstName()
	{
		return this.firstName;
	}
	
	public Integer getRanking()
	{
		return this.ranking;
	}
	
	public void setRanking(Integer ranking)
	{
		this.ranking = ranking;
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
