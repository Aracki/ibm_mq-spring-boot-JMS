package com.ibm.ibm;

import com.ibm.mq.ese.config.KeyStoreConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.out.println("TRUST_STORE: " + System.getenv("TRUST_STORE"));
        System.out.println("TRUST_STORE_TYPE: " + System.getenv("TRUST_STORE_TYPE"));
        System.out.println("KEY_STORE: " + System.getenv("KEY_STORE"));

        Enumeration<String> aliasenum = null;
        try {
            System.out.println("Trying to find keystore...");
            System.out.println("Default keyStore type is: " + KeyStore.getDefaultType());
            KeyStore ks = KeyStore.getInstance("JKS");
            aliasenum = ks.aliases();

            String keyAlias = null;
            if (aliasenum.hasMoreElements()) {
                keyAlias = aliasenum.nextElement();
            }
            X509Certificate cert = (X509Certificate) ks
                    .getCertificate(keyAlias);

            System.out.println("---------------------------------------------");
            System.out.println("---------------------------------------------");
            System.out.println("---------------------------------------------");
            System.out.println("Certificate alias found: " + keyAlias);
            System.out.println("Certificate serial number: " + cert.getSerialNumber().toString());
            System.out.println("Certificate toString() method: " + cert.toString());
            System.out.println("Certificate version: " + cert.getVersion());
            System.out.println("---------------------------------------------");
            System.out.println("---------------------------------------------");
            System.out.println("---------------------------------------------");

        } catch (KeyStoreException e) {
            System.out.println("Error getting keystore:");
            System.err.println(e.getMessage());
        }
        SpringApplication.run(Application.class, args);
    }
}
