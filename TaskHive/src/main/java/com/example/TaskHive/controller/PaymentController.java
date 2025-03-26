package com.example.TaskHive.controller;

import com.example.TaskHive.dto.PaymentGetDto;
import com.example.TaskHive.dto.PaymentPostDto;
import com.example.TaskHive.service.service_implementation.PaymentServiceImplementation;
import com.example.TaskHive.service.service_interface.PaymentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PaymentController
{
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentServiceImplementation paymentService)
    {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<PaymentGetDto> checkoutProducts(
            @RequestBody PaymentPostDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return new ResponseEntity<>(paymentService.checkoutProducts(dto, userDetails), HttpStatus.OK);
    }

    @GetMapping("/success")
    public ResponseEntity<String> success(
            @RequestParam("username") String username,
            @RequestParam("payment") String paymentName,
            HttpServletResponse response
    ) {
        return new ResponseEntity<>(paymentService.success(username, paymentName, response), HttpStatus.OK);
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> cancel(
            @RequestParam("username") String username,
            @RequestParam("payment") String paymentName,
            HttpServletResponse response
    ) {
        return new ResponseEntity<>(paymentService.cancel(username, paymentName, response), HttpStatus.OK);
    }

}
