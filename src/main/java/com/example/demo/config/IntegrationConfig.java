package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.function.Function;

@Configuration
public class IntegrationConfig {

    private final String outputDirectory = "journaux";

    @Bean
    public MessageChannel inputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel journalChannel() {
        return new DirectChannel();
    }

    @Bean
    public Function<String, String> transformer() {
        return payload -> "[Journal]: " + payload + " - " + System.currentTimeMillis();
    }

    @Bean
    public MessageHandler fileWritingMessageHandler() {
        File directory = new File(outputDirectory);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Impossible de créer le répertoire pour les journaux : " + outputDirectory);
        }

        FileWritingMessageHandler handler = new FileWritingMessageHandler(directory);
        handler.setFileExistsMode(FileExistsMode.APPEND);
        handler.setAppendNewLine(true);
        handler.setFileNameGenerator(message -> "journal_" + System.currentTimeMillis() + ".txt");
        return handler;
    }

    @PostConstruct
    public void configureJournalFlow() {
        MessageChannel journalChannel = journalChannel();
        Function<String, String> transformer = transformer();
        MessageHandler fileWritingMessageHandler = fileWritingMessageHandler();

        ((DirectChannel) journalChannel).subscribe(message -> {
            try {
                String payload = (String) message.getPayload();
                String transformedMessage = transformer.apply(payload);

                org.springframework.messaging.Message<String> transformed =
                        MessageBuilder.withPayload(transformedMessage).build();

                fileWritingMessageHandler.handleMessage(transformed);
            } catch (Exception e) {
                System.err.println("Erreur lors du traitement du message : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
