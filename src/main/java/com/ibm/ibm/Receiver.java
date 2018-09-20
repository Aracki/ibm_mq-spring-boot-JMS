package com.ibm.ibm;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

    @JmsListener(destination = "testtopic")
    public void receiveMessage(String msg) {
        System.out.println("Received <" + msg + ">");
    }
}
