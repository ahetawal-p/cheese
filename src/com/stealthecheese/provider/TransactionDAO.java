package com.stealthecheese.provider;

import java.util.List;

import com.stealthecheese.model.Transaction;

public interface TransactionDAO {
	
	void createTransaction(Transaction activity);	
	List<Transaction> getTransactions(String userId);
}
