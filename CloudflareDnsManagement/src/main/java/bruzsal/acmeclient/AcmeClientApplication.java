package bruzsal.acmeclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"bruzsal.dnsmanagement", "bruzsal.acmeclient"})
public class AcmeClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcmeClientApplication.class);
    }

}
