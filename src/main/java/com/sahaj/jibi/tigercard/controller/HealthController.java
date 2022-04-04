package com.sahaj.jibi.tigercard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String greeting() {
        return (new Date()).toString();
    }
}
