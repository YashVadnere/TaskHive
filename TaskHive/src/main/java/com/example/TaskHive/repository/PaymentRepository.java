package com.example.TaskHive.repository;

import com.example.TaskHive.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>
{
    Optional<Payment> findByName(String paymentName);
}
