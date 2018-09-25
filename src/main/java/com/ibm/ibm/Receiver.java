package com.ibm.ibm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Component
public class Receiver {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Value(value = "${file.location}")
    private String fileLocation;

    @JmsListener(destination = "${queue.name}")
    public void receiveMessage(String message) {
        if (StringUtils.isEmpty(fileLocation)) {
            System.out.println("Please specify correct file location in application.properties file.");
            return;
        }
        System.out.println("Received <" + message + ">");

        Path file = Paths.get(fileLocation);
        try {
            if (Files.notExists(file)) {
                System.out.println("Writing message to a file...");
                writeToFile(file, message, StandardOpenOption.CREATE);
            } else {
                writeToFile(file, message, StandardOpenOption.APPEND);
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
