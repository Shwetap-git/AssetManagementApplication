
package com.db.awmd.challenge.repository;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountInfoNotFoundException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public Account getAccount(String accountId) {
		Account account = accounts.get(accountId);
		if (Objects.isNull(account)) {
			throw new AccountInfoNotFoundException("This Account does not exist");
		}
		return account;
	}

	@Override
	public void clearAccounts() {
		accounts.clear();
	}

	@Override
	public Account updateAccount(Account account) {
		return accounts.replace(account.getAccountId(), account);
	}

	/*
	 * @Override public void withdrawAmount(String accountId, BigDecimal amount) {
	 * Account accountFrom = getAccount(accountId); //To ensure that multiple
	 * threads can not withdraw from same account simultaneously, //a lock is
	 * acquired on the account to be withdrawn //Also, this ensures that multiple
	 * threads can withdraw from different accounts simultaneously
	 * synchronized(accountFrom) { Account account = accountFrom;
	 * checkBalance(amount, account); account.withdraw(amount);
	 * updateAccount(account); } }
	 */

	/*
	 * @Override public void depositAmount(String accountId, BigDecimal amount) {
	 * //This method is not synchronized as multiple threads should be able to
	 * deposit to an account simultaneously Account account = getAccount(accountId);
	 * account.deposit(amount); updateAccount(account); }
	 */

}
