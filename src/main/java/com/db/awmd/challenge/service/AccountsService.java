package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransferRequest;
import com.db.awmd.challenge.exception.AccountInfoNotFoundException;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;

@Service
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	/**
	 * @param account
	 * method to create an account using given account detail
	 */
	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	/**
	 * @param accountId
	 * @return Account
	 * method to fetch account detail by accountId
	 */
	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	/**
	 * @param transferRequest
	 * method to transfer the amount from payer to payee account
	 */
	public void transferAmount(AmountTransferRequest transferRequest) {
		String accountFrom = transferRequest.getAccountFrom();
		String accountTo = transferRequest.getAccountTo();
		BigDecimal amount = transferRequest.getAmount();
		accountsRepository.withdrawAmount(accountFrom, amount);
		try {
			accountsRepository.depositAmount(accountTo, amount);
		} catch (Exception exception) {
			//If amount is withdrawn from payer account but deposit to payee account fails,
			// the given amount should be deposited back to the payer account 
			accountsRepository.depositAmount(accountFrom, amount);
			throw new AccountInfoNotFoundException(exception.getMessage());
		}
	}

}
