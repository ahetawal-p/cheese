package Repositories;

import Utilities.TransformerManager;

public class UserDAOImpl implements UserDAO {
	
	private TransformerManager transformerManager;
	public UserDAOImpl(TransformerManager transformerManager)
	{
		this.transformerManager = transformerManager;
	}
}
