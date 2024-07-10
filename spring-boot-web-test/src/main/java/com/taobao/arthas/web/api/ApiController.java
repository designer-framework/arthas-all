package com.taobao.arthas.web.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ApiController {

    @RequestMapping("/test")
    public void test() {
        System.out.println("");
    }

}
