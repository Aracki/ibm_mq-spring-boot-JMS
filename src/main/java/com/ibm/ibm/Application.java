package com.ibm.ibm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.out.println("TRUST_STORE: " + System.getenv("TRUST_STORE"));
        System.out.println("TRUST_STORE_TYPE: " + System.getenv("TRUST_STORE_TYPE"));
        System.out.println("KEY_STORE: " + System.getenv("KEY_STORE"));
        SpringApplication.run(Application.class, args);
    }
}
