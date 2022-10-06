package com.gmall.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeataController {

    @GetMapping("/seata")
    public String getSeataData() {
        return "Seata Server";
    }

}
