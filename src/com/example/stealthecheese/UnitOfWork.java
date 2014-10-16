package com.example.stealthecheese;

import Repositories.CheeseDAO;
import Repositories.TransactionDAO;
import Repositories.TransactionDAOImpl;
import Repositories.CheeseDAOImpl;
import Repositories.UserDAO;
import Repositories.UserDAOImpl;
import Utilities.TransformerManager;


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
