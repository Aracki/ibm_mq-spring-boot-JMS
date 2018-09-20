package com.ibm.ibm;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    @JmsListener(destination = "testTopic")
    public void receiveMessage(String message) {
        System.out.println("Received <" + message + ">");
    }
}
