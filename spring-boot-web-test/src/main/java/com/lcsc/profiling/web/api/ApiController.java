package com.lcsc.profiling.web.api;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ApiController implements SmartInitializingSingleton {

    @RequestMapping("/test")
    public void test() {
        System.out.println("");
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            System.out.println(214);
            Thread.sleep(1232);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
