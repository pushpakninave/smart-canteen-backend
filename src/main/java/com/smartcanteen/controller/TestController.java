package com.smartcanteen.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/welcome")
    public String greet(HttpServletRequest req){
        return req.getSession().getId();
    }
    
}