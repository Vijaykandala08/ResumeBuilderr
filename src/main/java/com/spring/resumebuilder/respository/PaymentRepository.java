package com.spring.resumebuilder.respository;

import com.spring.resumebuilder.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment,String> {
}
