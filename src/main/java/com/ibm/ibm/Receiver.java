package com.ibm.ibm;

import com.ibm.mq.jms.MQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;

@Component
public class Receiver {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Value(value = "${file.location}")
    private String fileLocation;

    @Value(value = "${queue.name}")
    private String queueName;

    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;

    @PostConstruct
    public void testJMS() {
//   TODO enable this for testing JMS
//     sendTestMsgs();
    }

    @JmsListener(destination = "${queue.name}")
    public void receiveMessage(String message) {
        if (StringUtils.isEmpty(fileLocation)) {
            System.out.println("Please specify correct file location in application.properties file.");
            return;
        }
        System.out.println("Received <" + message + ">");

        Path filePath = Paths.get(fileLocation + LocalDate.now());
        try {
            if (Files.notExists(filePath)) {
                System.out.println("Writing message to a file...");
                writeToFile(filePath, message, StandardOpenOption.CREATE);
            } else {
                writeToFile(filePath, message, StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            System.out.println("Error occurred: " + e.getMessage());
            return;
        }
        System.out.println("Done");
    }

    private void writeToFile(Path path, String message, StandardOpenOption openOption) throws IOException {
        Files.write(path, message.getBytes(), openOption);
        Files.write(path, LINE_SEPARATOR.getBytes(), StandardOpenOption.APPEND);
    }

    private void sendTestMsgs() {
        try {
            for (int i = 0; i < 10; i++) {
                jmsMessagingTemplate.convertAndSend(new MQQueue(queueName), "testMessage" + i);
            }
            System.out.println("Messages sent.");
        } catch (JMSException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }
}
