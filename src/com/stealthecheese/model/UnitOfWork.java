package com.stealthecheese.model;

import com.stealthecheese.provider.CheeseDAO;
import com.stealthecheese.provider.CheeseDAOImpl;
import com.stealthecheese.provider.TransactionDAO;
import com.stealthecheese.provider.TransactionDAOImpl;
import com.stealthecheese.provider.UserDAO;
import com.stealthecheese.provider.UserDAOImpl;
import com.stealthecheese.util.TransformerManager;



public class UnitOfWork {
	
	public UserDAO userDAO;
	public TransactionDAO activityDAO;
	public CheeseDAO cheeseDAO;
	
	public UnitOfWork()
	{
		TransformerManager transformerManager = new TransformerManager();
		this.userDAO = new UserDAOImpl(transformerManager);
		this.activityDAO = new TransactionDAOImpl(transformerManager);
		this.cheeseDAO = new CheeseDAOImpl(transformerManager);
	}
	
}
