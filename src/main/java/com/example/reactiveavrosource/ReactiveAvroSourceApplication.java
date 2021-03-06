package com.example.reactiveavrosource;

import com.avro.document.Attachment;
import com.avro.document.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.schema.registry.avro.AvroSchemaMessageConverter;
import org.springframework.cloud.schema.registry.avro.AvroSchemaServiceManagerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SpringBootApplication
public class ReactiveAvroSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveAvroSourceApplication.class, args);
    }

    @Bean
    public Supplier<Flux<Message<Document>>> produce() {
        return () -> Flux.interval(Duration.ofMillis(1000))
                         .map(id -> mapDocument(id))
                         .map(document -> MessageBuilder.withPayload(document).setHeader("payloadId", document.getId$1()).build())
                         .log();
    }

    @Bean
    public MessageConverter avroDocumentMessageConverter() {
        AvroSchemaMessageConverter converter = new AvroSchemaMessageConverter(MimeType.valueOf("avro/bytes"), new AvroSchemaServiceManagerImpl());
        converter.setSchema(Document.getClassSchema());
        return converter;
    }

    private Document mapDocument(Long id) {
        Document document = new Document();
        document.setId$1(id.toString());
        document.setText("sample text");

        List<Attachment> attachments = new ArrayList<>();
        Attachment attachment = new Attachment();
        attachment.setUrl("http://sample.com/2019/12/abc.jpg");
        attachment.setType("photo");
        attachments.add(attachment);

        document.setAttachments(attachments);

        return document;
    }

}
