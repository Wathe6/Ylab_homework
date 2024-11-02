package io.envoi.service;

import io.envoi.model.HelloWorld;
import org.springframework.stereotype.Service;

@Service
public class HelloWorldService {
    public HelloWorld sayHello() {
        return new HelloWorld("Hello World!");
    }
}
