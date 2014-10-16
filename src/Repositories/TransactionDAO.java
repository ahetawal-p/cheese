package Repositories;

import java.util.List;
import Models.Transaction;

public interface TransactionDAO {
	
	void createTransaction(Transaction activity);	
	List<Transaction> getTransactions(String userId);
}
