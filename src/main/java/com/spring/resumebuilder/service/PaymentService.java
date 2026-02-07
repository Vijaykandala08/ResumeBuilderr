package com.spring.resumebuilder.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.spring.resumebuilder.dto.AuthResponse;
import com.spring.resumebuilder.model.Payment;
import com.spring.resumebuilder.respository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.spring.resumebuilder.util.AppConstants.PREMIUM;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final AuthService authService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;


    public Payment createOrder(Object principal, String planType) throws RazorpayException {
        //Initial Step
        AuthResponse authResponse = authService.getProfile(principal);
        // step 1 : Initialize the razorpay client
        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId,razorpayKeySecret);
        //step 2: Prepare the JSON object to pass the razorpay
        int amount = 99900; // Amount in paise
        String currency = "INR";
        String receipt = PREMIUM + "_" + UUID.randomUUID().toString().substring(0, 8);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount",amount);
        orderRequest.put("currency",currency);
        orderRequest.put("receipt",receipt);
        //step 3 : Call the razorpay API to create order
        Order razorpayOrder =  razorpayClient.orders.create(orderRequest);
        log.info("razorpayOrder:{}",razorpayOrder);
        //step 4 : Save the order details
        Payment newPayment = Payment.builder()
                .userId(authResponse.getId())
                .razorpayOrderId(razorpayOrder.get("id"))
                .amount(amount)
                .currency(currency)
                .planType(planType)
                .receipt(receipt)
                .build();
        //step 5: return the result
        paymentRepository.save(newPayment);
        return newPayment;

    }
}
