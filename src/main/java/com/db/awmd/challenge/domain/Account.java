package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class Account {

	@NotNull
	@NotBlank
	private final String accountId;

	@NotNull
	@Min(value = 0, message = "Initial balance must be positive.")
	@Setter(AccessLevel.PRIVATE)
	private volatile BigDecimal balance;

	public Account(String accountId) {
		this.accountId = accountId;
		this.balance = BigDecimal.ZERO;
	}

	@JsonCreator
	public Account(@JsonProperty("accountId") String accountId, @JsonProperty("balance") BigDecimal balance) {
		this.accountId = accountId;
		this.balance = balance;
	}

	/**
	 * @param amount
	 * this method is synchronized to ensure multiple threads do not change the balance of an account simultaneously 
	 */
	public synchronized void withdraw(BigDecimal amount) {
		this.balance = this.balance.subtract(amount);
	}

	/**
	 * @param amount
	 * this method is synchronized to ensure multiple threads do not change the balance of an account simultaneously 
	 */
	public synchronized void deposit(BigDecimal amount) {
		this.balance = this.balance.add(amount);
	}
}
