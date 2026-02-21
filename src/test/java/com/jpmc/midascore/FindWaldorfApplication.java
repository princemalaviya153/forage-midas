package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FindWaldorfApplication {

    public static void main(String[] args) {
        System.setProperty("spring.kafka.consumer.auto-offset-reset", "earliest");
        // Ensure application exits after run
        SpringApplication.run(FindWaldorfApplication.class, args).close();
    }

    @Bean
    public CommandLineRunner runQuery(UserRepository userRepository) {
        return args -> {
            UserRecord waldorf = userRepository.findByName("waldorf");
            if (waldorf != null) {
                System.out.println("=============== ANSWER ===============");
                System.out.println("Waldorf's balance: " + waldorf.getBalance());
                System.out.println("======================================");
            } else {
                System.out.println("Waldorf not found in the DB!");
            }
        };
    }
}
