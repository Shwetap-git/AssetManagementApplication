package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

public interface AccountsRepository {

	/**
	 * @param account
	 * @throws DuplicateAccountIdException method to create a new account with given
	 *                                     account detail
	 */
	void createAccount(Account account) throws DuplicateAccountIdException;

	/**
	 * @param accountId
	 * @return Account information method to get the account detail by accountId
	 */
	Account getAccount(String accountId);

	/**
	 * 
	 */
	void clearAccounts();

	
	/**
	 * @param account
	 * @return
	 */
	Account updateAccount(Account account);

	/**
	 * @param accountFrom
	 * @param amount      method to withdraw the given amount from the given account
	 */
	/*
	 * void withdrawAmount(Account accountFrom, BigDecimal amount);
	 * 
	 *//**
		 * @param accountId
		 * @param amount    method to withdraw the given amount from the given account
		 *//*
			 * void depositAmount(String accountId, BigDecimal amount);
			 */
}
