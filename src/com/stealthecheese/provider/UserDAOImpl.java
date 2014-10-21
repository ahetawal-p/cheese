package com.stealthecheese.provider;

import com.stealthecheese.util.TransformerManager;

public class UserDAOImpl implements UserDAO {
	
	private TransformerManager transformerManager;
	public UserDAOImpl(TransformerManager transformerManager)
	{
		this.transformerManager = transformerManager;
	}
}
