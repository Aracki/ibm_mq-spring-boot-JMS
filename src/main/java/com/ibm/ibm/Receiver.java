package com.ibm.ibm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    @JmsListener(destination = "${queue.name}")
    public void receiveMessage(String message) {
        if (StringUtils.isEmpty(fileLocation)) {
            System.out.println("Please specify correct file location in application.properties file.");
            return;
        }

        String encodedMsg = null;
        try {
            encodedMsg = new String(message.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("UnsupportedEncodingException: " + e.getMessage());
            return;
        }

        System.out.println("Received message: " + message);
        System.out.println("Encoded message:" + encodedMsg);

        Path filePath = Paths.get(fileLocation + LocalDate.now());
        try {
            if (Files.notExists(filePath)) {
                System.out.println("Writing message to a file...");
                writeToFile(filePath, encodedMsg, StandardOpenOption.CREATE);
            } else {
                writeToFile(filePath, encodedMsg, StandardOpenOption.APPEND);
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
}
