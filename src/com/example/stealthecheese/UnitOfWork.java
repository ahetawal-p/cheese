package com.example.stealthecheese;

import Repositories.ActivityRepository;
import Repositories.CheeseRepository;
import Repositories.UserRepository;
import Utilities.TransformerManager;


public class UnitOfWork {
	
	public UserRepository userRepository;
	public ActivityRepository activityRepository;
	public CheeseRepository cheeseRepository;
	
	public UnitOfWork()
	{
		TransformerManager transformerManager = new TransformerManager();
		this.userRepository = new UserRepository(transformerManager);
		this.activityRepository = new ActivityRepository(transformerManager);
		this.cheeseRepository = new CheeseRepository(transformerManager);
	}
	
}
