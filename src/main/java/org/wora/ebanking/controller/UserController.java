package org.wora.ebanking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    @GetMapping("/myLoans")
    public String getUserLoans() {
        return "User's loan information";
    }

    @GetMapping("/myCards")
    public String getUserCards() {
        return "User's bank cards details";
    }

    @GetMapping("/myAccount")
    public String getUserAccount() {
        return "User's account information";
    }

    @GetMapping("/myBalance")
    public String getUserBalance() {
        return "User's total account balance";
    }
}
