package com.stealthecheese.viewmodel;


public class RankingViewModel {
	private Integer cheese = 0;
	private Integer ranking;
	private String imageString;
	private String firstName;
	private Boolean isUser;
	
	public RankingViewModel(String firstName, String imageString, Integer cheese, Integer ranking, Boolean isUser)
	{
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
