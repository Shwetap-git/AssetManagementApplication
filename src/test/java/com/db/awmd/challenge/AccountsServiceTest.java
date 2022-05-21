package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransferRequest;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientFundsException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class AccountsServiceTest {

	@Autowired
	private AccountsService accountsService;

	@Test
	public void addAccount() throws Exception {
		Account account = new Account("Id-123", new BigDecimal(1000));
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueId);
		this.accountsService.createAccount(account);

		try {
			this.accountsService.createAccount(account);
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
		}

	}
	
	@Test
	public void transferAmount() throws Exception {
		String idFrom = "Id3-" + System.currentTimeMillis();
		BigDecimal amount = new BigDecimal(1000);
		Account accountFrom = new Account(idFrom, amount);
		this.accountsService.createAccount(accountFrom);
		
		String idTo = "Id4-" + System.currentTimeMillis();
		Account accountTo = new Account(idTo);
		this.accountsService.createAccount(accountTo);
		
		this.accountsService.transferAmount(new AmountTransferRequest(idFrom, idTo, amount));

		assertThat(this.accountsService.getAccount(idTo).getBalance()).isEqualTo(amount);
	}
	
	@Test
	public void transferAmounty_insufficientFund() throws Exception {
		String idFrom = "Id1-" + System.currentTimeMillis();
		Account accountFrom = new Account(idFrom);
		this.accountsService.createAccount(accountFrom);
		
		String idTo = "Id2-" + System.currentTimeMillis();
		Account accountTo = new Account(idTo);
		this.accountsService.createAccount(accountTo);
		
		try {
			this.accountsService.transferAmount(new AmountTransferRequest(idFrom, idTo, new BigDecimal(1000)));
			fail("Should have failed when payer has insuffiecient funds for the amount transfer");
		} catch (InsufficientFundsException ex) {
			assertThat(ex.getMessage()).isEqualTo("Insufficient balance in the account!!");
		}

	}

	@Test
	public void transferAmount_concurrencyCheck() throws Exception {
		
		this.accountsService.createAccount(new Account("Id-012", new BigDecimal(1000)));
		this.accountsService.createAccount(new Account("Id-013", new BigDecimal(1000)));
		this.accountsService.createAccount(new Account("Id-014", new BigDecimal(1000)));
		
		Runnable runnable = ()->{
			AmountTransferRequest req = new AmountTransferRequest("Id-012","Id-013",new BigDecimal(100));
			this.accountsService.transferAmount(req);
		};
		
		Runnable runnable1 = ()->{
			AmountTransferRequest req = new AmountTransferRequest("Id-012","Id-014",new BigDecimal(200));
			this.accountsService.transferAmount(req);
		};
		
		Runnable runnable2 = ()->{
			AmountTransferRequest req = new AmountTransferRequest("Id-013","Id-014",new BigDecimal(100));
			this.accountsService.transferAmount(req);
		};
		
		Thread t1 = new Thread(runnable);
		Thread t2 = new Thread(runnable1);
		Thread t3 = new Thread(runnable);
		Thread t4 = new Thread(runnable1);
		Thread t5 = new Thread(runnable2);
		t1.start(); t2.start(); t3.start(); t4.start();t5.start();
		t1.join(); t2.join();t3.join();t4.join(); t5.join();	
		assertThat(this.accountsService.getAccount("Id-012").getBalance()).isEqualTo(new BigDecimal(400));
	}
	
}
