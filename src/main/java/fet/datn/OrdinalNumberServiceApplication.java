package fet.datn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrdinalNumberServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrdinalNumberServiceApplication.class, args);
        System.out.println("Started [ordinal-number-service] server");
    }
}
