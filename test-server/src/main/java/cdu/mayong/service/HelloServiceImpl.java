package cdu.mayong.service;

import cdu.mayong.service.Hello;
import cdu.mayong.service.HelloService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(Hello hello) {
        log.info("HelloServiceImpl收到: {}.", hello.getMsg());
        String result = "Hello description is " + hello.getDesc();
        log.info("HelloServiceImpl返回: {}.", result);
        return result;
    }
}
