package com.deliguy.order_service.DTO;

import lombok.Data;

@Data
public class PaymentRequest {
    private String paymentMethod; // CARD, CASH
    private String cardNumber; // optional - for card payments
    private String cardExpiry; // optional
    private String cardCvc; // optional
}
