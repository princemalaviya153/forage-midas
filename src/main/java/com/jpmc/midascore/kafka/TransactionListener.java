package com.jpmc.midascore.kafka;

import com.jpmc.midascore.config.GeneralProperties;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private final GeneralProperties generalProperties;
    private final TransactionService transactionService;

    public TransactionListener(GeneralProperties generalProperties, TransactionService transactionService) {
        this.generalProperties = generalProperties;
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "#{generalProperties.kafkaTopic}", groupId = "midas-group")
    public void listen(Transaction transaction) {
        transactionService.process(transaction);
    }
}