package com.example.travelhana.Service;

import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserServiceImpl userService;
    public void payment(String accesssToken, PaymentDto paymentDto) {
       User user =  userService.getUser(accesssToken);
       int userId = user.getId();

    }
}
