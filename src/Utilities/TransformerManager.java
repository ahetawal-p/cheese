package Utilities;

public class TransformerManager {

	public UserTransformer userTransformer;
	public CheeseTransformer cheeseTransformer;
	public TransactionTransformer transactionTransformer;
	
	public TransformerManager()
	{
		this.userTransformer = new UserTransformer();
		this.cheeseTransformer = new CheeseTransformer();
		this.transactionTransformer = new TransactionTransformer();
	}
}
