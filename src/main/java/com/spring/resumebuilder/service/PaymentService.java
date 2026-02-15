package com.spring.resumebuilder.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.spring.resumebuilder.dto.AuthResponse;
import com.spring.resumebuilder.exception.ResourceNotFoundException;
import com.spring.resumebuilder.model.Payment;
import com.spring.resumebuilder.model.User;
import com.spring.resumebuilder.respository.PaymentRepository;
import com.spring.resumebuilder.respository.Userrespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.spring.resumebuilder.util.AppConstants.PREMIUM;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final AuthService authService;
    private final Userrespository  userrespository;

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
        log.info("newPayment:{}",newPayment);
        paymentRepository.save(newPayment);
        return newPayment;

    }

    public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws RazorpayException {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id",razorpayOrderId);
            attributes.put("razorpay_payment_id",razorpayPaymentId);
            attributes.put("razorpay_signature",razorpaySignature);

            boolean isValidSignature =  Utils.verifyPaymentSignature(attributes,razorpayKeySecret);

            if(!isValidSignature){
                throw new RazorpayException("Invalid payment signature");
            }
                //upgrade the payment status
                Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                        .orElseThrow(()->new RazorpayException("Payment not found"));

                if("paid".equals(payment.getStatus())){
                    throw new RazorpayException("Payment is already paid");
                }

                payment.setRazorpayPaymentId(razorpayPaymentId);
                payment.setRazorpaySignature(razorpaySignature);
                payment.setStatus("paid");
                paymentRepository.save(payment);

                //upgrade the user subscription
                upgradeUserSubscription(payment.getUserId(),payment.getPlanType());
                return true;


    }

    private void upgradeUserSubscription(String userId, String planType) {
        User existingUser = userrespository.findById(userId)
                        .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        existingUser.setSubscriptionPlan(planType);
        userrespository.save(existingUser);

        log.info("user {} upgraded to {} plan ",userId,planType);

    }

    public List<Payment> getUserPayments(Object principal) {
        //step 1: Get the current profile
        AuthResponse authResponse = authService.getProfile(principal);

        //step 2: Call the respository finder method
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(authResponse.getId());

    }

    public Payment getPaymentDetails(String orderId) {
        //step 1: Call the repository finder method
        return paymentRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(()->new RuntimeException("Payment not found"));
    }
}
