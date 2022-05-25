package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransferRequest;
import com.db.awmd.challenge.exception.AccountInfoNotFoundException;
import com.db.awmd.challenge.exception.InsufficientFundsException;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	/**
	 * @param account method to create an account using given account detail
	 */
	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	/**
	 * @param accountId
	 * @return Account method to fetch account detail by accountId
	 */
	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	/**
	 * @param transferRequest 
	 * method to transfer the amount from payer to payee account
	 */
	public void transferAmount(AmountTransferRequest transferRequest) {
		String accountFromId = transferRequest.getAccountFrom();
		String accountToId = transferRequest.getAccountTo();
		BigDecimal amount = transferRequest.getAmount();
		Account accountFrom = getAccount(accountFromId);
		Account accountTo = getAccount(accountToId);
		// To ensure that multiple threads can not withdraw from same account simultaneously,
		// a lock is acquired on the account to be withdrawn
		// This ensures that multiple threads can withdraw from different accounts simultaneously
		// Also, a lock is acquired on the payee account so that any other thread do not
		// change the payee account state at this point
		synchronized (accountFrom) {
			log.debug("1st lock acquired by "+ Thread.currentThread().getName());
			synchronized (accountTo) {
				log.debug("2nd lock acquired by "+ Thread.currentThread().getName());
				log.info("Initiating trasfer from account- "+accountFromId+" to account- "+accountToId);
				withdrawAmount(accountFrom, amount);
				try {
					depositAmount(accountTo, amount);
				} catch (Exception exception) {
					// If amount is withdrawn from payer account but deposit to payee account fails,
					// the given amount should be deposited back to the payer account
					depositAmount(accountFrom, amount);
					throw new AccountInfoNotFoundException(exception.getMessage());
				}
			}
		}
	}

	/**
	 * @param amount
	 * @param account
	 */
	private void checkBalance(BigDecimal amount, Account account) {
		if (account.getBalance().compareTo(amount) < 0) {
			throw new InsufficientFundsException("Insufficient balance in the account!!");
		}
	}

	/**
	 * @param accountFrom
	 * @param amount
	 */
	private void withdrawAmount(Account accountFrom, BigDecimal amount) {
		checkBalance(amount, accountFrom);
		accountFrom.withdraw(amount);
		this.accountsRepository.updateAccount(accountFrom);
		log.info("An amount of "+amount+" successfully withdrawn from account - "+accountFrom.getAccountId());
	}

	/**
	 * @param accountTo
	 * @param amount
	 */
	private void depositAmount(Account accountTo, BigDecimal amount) {
		accountTo.deposit(amount);
		this.accountsRepository.updateAccount(accountTo);
		log.info("An amount of "+amount+" successfully deposited to account - "+accountTo.getAccountId());
	}

	/*
	 * public void transferAmount(AmountTransferRequest transferRequest) { String
	 * accountFrom = transferRequest.getAccountFrom(); String accountTo =
	 * transferRequest.getAccountTo(); BigDecimal amount =
	 * transferRequest.getAmount(); accountsRepository.withdrawAmount(accountFrom,
	 * amount); try { accountsRepository.depositAmount(accountTo, amount); } catch
	 * (Exception exception) { //If amount is withdrawn from payer account but
	 * deposit to payee account fails, // the given amount should be deposited back
	 * to the payer account accountsRepository.depositAmount(accountFrom, amount);
	 * throw new AccountInfoNotFoundException(exception.getMessage()); } }
	 */

}
