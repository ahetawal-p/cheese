package repositories;

import java.util.List;

import models.Transaction;

public interface TransactionDAO {
	
	void createTransaction(Transaction transaction);	
	List<Transaction> getTransactions(String userId);
}
