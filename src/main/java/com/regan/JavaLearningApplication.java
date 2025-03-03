package com.regan;

import com.regan.demo.SendEmail;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
public class JavaLearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaLearningApplication.class, args);

//		send();
    }


    public static void send() {
        String add = new File("").getAbsolutePath() + File.separator + "test.properties";
        System.out.println();
        Properties properties = new Properties();
        try {
            InputStream is = new FileInputStream(add);
            properties.load(is);
            SendEmail.Send(properties.getProperty("email"), properties.getProperty("password"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
