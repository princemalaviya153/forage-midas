package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.jpmc.midascore.foundation.Incentive;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRepository;
    private final RestTemplate restTemplate;

    public TransactionService(UserRepository userRepository,
            TransactionRecordRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    public void process(Transaction tx) {
        UserRecord sender = userRepository.findById(tx.getSenderId()).orElse(null);
        UserRecord recipient = userRepository.findById(tx.getRecipientId()).orElse(null);

        // 1. Validate sender and recipient
        if (sender == null || recipient == null) {
            return;
        }

        // 2. Validate sender has enough balance
        BigDecimal amount = BigDecimal.valueOf(tx.getAmount());
        if (sender.getBalance().compareTo(amount) < 0) {
            return; // Reject transaction
        }

        // 4. Hit Incentives API
        Incentive incentive = restTemplate.postForObject("http://localhost:8080/incentive", tx, Incentive.class);
        float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0.0f;

        // 5. Adjust balances
        sender.setBalance(sender.getBalance().subtract(amount));
        recipient.setBalance(recipient.getBalance().add(amount).add(BigDecimal.valueOf(incentiveAmount)));

        // 6. Save users and transaction
        userRepository.save(sender);
        userRepository.save(recipient);

        TransactionRecord record = new TransactionRecord(amount, sender, recipient, incentiveAmount);
        transactionRepository.save(record);
    }
}