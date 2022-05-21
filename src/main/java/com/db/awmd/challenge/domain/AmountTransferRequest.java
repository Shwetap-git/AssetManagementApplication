package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmountTransferRequest {
	@NotNull
	@NotBlank(message = "please mention the Payer account")
	private String accountFrom;

	@NotNull
	@NotBlank(message = "please mention the Payee account")
	private String accountTo;

	@Min(value = 0, message = "Initial balance must be positive.")
	private BigDecimal amount;

}
