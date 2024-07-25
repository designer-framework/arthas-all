package com.lcsc.profiling.web.api;

import com.lcsc.profiling.web.annotation.Test;
import lombok.SneakyThrows;
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
    @SneakyThrows
    @Test
    public void afterSingletonsInstantiated() {
        System.out.println(123);
        Thread.sleep(123);
    }

}
