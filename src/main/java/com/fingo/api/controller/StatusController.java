package com.fingo.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/status")
public class StatusController {
    @GetMapping
    public String verificarStatus() {
        return "FinGo API funcionando!";
    }
}