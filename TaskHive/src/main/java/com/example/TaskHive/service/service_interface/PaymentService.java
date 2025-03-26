package com.example.TaskHive.service.service_interface;

import com.example.TaskHive.dto.PaymentGetDto;
import com.example.TaskHive.dto.PaymentPostDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService
{
    PaymentGetDto checkoutProducts(PaymentPostDto dto, UserDetails userDetails);

    String success(String username, String paymentName, HttpServletResponse response);

    String cancel(String username, String paymentName, HttpServletResponse response);
}
