package com.example.stealthecheese;

import repositories.CheeseDAO;
import repositories.CheeseDAOImpl;
import repositories.TransactionDAO;
import repositories.TransactionDAOImpl;
import repositories.UserDAO;
import repositories.UserDAOImpl;
import utilities.TransformerManager;


public class UnitOfWork {
	
	public UserDAO userDAO;
	public TransactionDAO transactionDAO;
	public CheeseDAO cheeseDAO;
	
	public UnitOfWork()
	{
		TransformerManager transformerManager = new TransformerManager();
		this.userDAO = new UserDAOImpl(transformerManager);
		this.transactionDAO = new TransactionDAOImpl(transformerManager);
		this.cheeseDAO = new CheeseDAOImpl(transformerManager);
	}
	
}
