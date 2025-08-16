package com.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentDTO {
    @NotNull(message = "Bill ID is required")
    private Long billId;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // CASH, ONLINE, CHEQUE, etc.
    
    private String transactionId;
    private String remarks;
}
