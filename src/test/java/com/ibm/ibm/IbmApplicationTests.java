package com.ibm.ibm;

import com.ibm.mq.jms.MQQueue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.JMSException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IbmApplicationTests {

    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;

    @Test
    public void contextLoads() {

        try {
            for (int i = 0; i < 10; i++) {
                jmsMessagingTemplate.convertAndSend(new MQQueue("testQueue"), "testMessage" + i);
            }
            System.out.println("Messages sent.");
        } catch (JMSException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

}
