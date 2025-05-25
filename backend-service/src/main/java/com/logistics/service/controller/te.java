package com.logistics.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class te {

    @GetMapping("/test")
    public String test() {
        return "API测试成功";
    }
}
