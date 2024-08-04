package com.lcsc.profiling.web.api;

import com.lcsc.profiling.web.annotation.Test;
import com.lcsc.profiling.web.feign.TestFeign;
import lombok.SneakyThrows;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class ApiController implements SmartInitializingSingleton {

    @Autowired
    private TestFeign testFeign;

    @RequestMapping("/test")
    public String test(String test, MultipartFile multipartFile) {
        return testFeign.test(test);
    }

    @RequestMapping("/test1")
    public String test1(String test1) {
        System.out.println(test1);
        return test1;
    }

    @Override
    @SneakyThrows
    @Test
    public void afterSingletonsInstantiated() {
        System.out.println(123);
        Thread.sleep(123);
    }

}
