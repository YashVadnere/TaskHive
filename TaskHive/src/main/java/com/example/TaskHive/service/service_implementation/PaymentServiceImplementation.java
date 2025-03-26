package com.example.TaskHive.service.service_implementation;

import com.example.TaskHive.dto.PaymentGetDto;
import com.example.TaskHive.dto.PaymentPostDto;
import com.example.TaskHive.entity.ActivePlan;
import com.example.TaskHive.entity.Payment;
import com.example.TaskHive.entity.PaymentStatus;
import com.example.TaskHive.entity.User;
import com.example.TaskHive.exceptions.Mismatch;
import com.example.TaskHive.exceptions.PaymentProcessingException;
import com.example.TaskHive.exceptions.ResourceNotFound;
import com.example.TaskHive.repository.PaymentRepository;
import com.example.TaskHive.repository.UserRepository;
import com.example.TaskHive.service.service_interface.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImplementation implements PaymentService
{
    @Value("${spring.stripe.secret-key}")
    private String secretKey;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentServiceImplementation(
            UserRepository userRepository,
            PaymentRepository paymentRepository
    ) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentGetDto checkoutProducts(PaymentPostDto dto, UserDetails userDetails)
    {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Payment payment = new Payment();

        payment.setName(dto.getName());
        payment.setQuantity(1L);
        payment.setAmount(dto.getAmount());
        payment.setCurrency(dto.getCurrency());
        payment.setPlan(dto.getPlan());
        payment.setPaymentStatus(PaymentStatus.PAYMENT_PENDING);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setUser(user);
        user.getPayments().add(payment);

        userRepository.save(user);
        paymentRepository.save(payment);

        Stripe.apiKey = secretKey;
        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(dto.getName())
                .build();

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(dto.getCurrency() == null ? "USD" : dto.getCurrency())
                .setUnitAmount(dto.getAmount()*100)
                .setProductData(productData)
                .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(priceData)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/api/v1/success?username=" + userDetails.getUsername()+"&payment="+payment.getName())
                .setCancelUrl("http://localhost:8080/api/v1/cancel?username=" + userDetails.getUsername()+"&payment="+payment.getName())
                .addLineItem(lineItem)
                .build();

        Session session = null;

        try {
            session = Session.create(params);

        } catch (StripeException e) {
            throw new PaymentProcessingException("Error creating Stripe session" + e.getMessage());
        }

        return new PaymentGetDto("SUCCESS", "Payment session created", session.getId(), session.getUrl());
    }

    @Override
    public String success(String username, String paymentName, HttpServletResponse response)
    {
        System.out.println("Success Called");

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Payment payment = paymentRepository.findByName(paymentName)
                .orElseThrow(() -> new ResourceNotFound("Payment not found"));

        if(!payment.getUser().getUserId().equals(user.getUserId()))
        {
            throw new Mismatch("Payment does not belong to user");
        }

        payment.setPaymentStatus(PaymentStatus.PAYMENT_SUCCESS);
        if(payment.getAmount().equals(1000L))
        {
            user.setActivePlan(ActivePlan.MONTHLY);
            user.setPlanEndsAt(LocalDateTime.now().plusMonths(1));
            user.setProjectLimit(Long.MAX_VALUE);
            userRepository.save(user);
            paymentRepository.save(payment);
        }
        else if (payment.getAmount().equals(10000L))
        {
            user.setActivePlan(ActivePlan.ANNUALLY);
            user.setPlanEndsAt(LocalDateTime.now().plusYears(1));
            user.setProjectLimit(Long.MAX_VALUE);
            userRepository.save(user);
            paymentRepository.save(payment);
        }
//        try
//        {
//            response.sendRedirect("");
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        return "Success";
    }

    @Override
    public String cancel(String username, String paymentName, HttpServletResponse response)
    {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Payment payment = paymentRepository.findByName(paymentName)
                .orElseThrow(() -> new ResourceNotFound("Payment not found"));

        payment.setPaymentStatus(PaymentStatus.PAYMENT_FAILED);
        paymentRepository.save(payment);
//        try
//        {
//            response.sendRedirect("");
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        return "Failure";
    }
}