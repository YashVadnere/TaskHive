package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.PaymentGetDto;
import com.example.TaskHive.dto.PaymentPostDto;
import com.example.TaskHive.dto.PaymentResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PaymentService
{
    PaymentGetDto checkoutProducts(PaymentPostDto dto, UserDetails userDetails);

    String success(String username, String paymentName, HttpServletResponse response);

    String cancel(String username, String paymentName, HttpServletResponse response);

    List<PaymentResponseDto> getPaymentHistory(UserDetails userDetails);
}
