package repositories;

import utilities.TransformerManager;

public class UserDAOImpl implements UserDAO {
	
	private TransformerManager transformerManager;
	public UserDAOImpl(TransformerManager transformerManager)
	{
		this.transformerManager = transformerManager;
	}
}
