package com.PIMCS.PIMCS.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;


@Configuration
@PropertySource("classpath:application.properties")
@Data
public class AWSConfig {
    @Value("${aws.accesskey}")
    private String ACCESS_KEY;

    @Value("${aws.secretKey}")
    private String SECRET_KEY;

    @Value("${aws.endPoint}")
    private String END_POINT;


    @Bean
    public AWSCredentials AWSCredentials() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        return awsCredentials;
    }

    @Bean
    AwsClientBuilder.EndpointConfiguration EndpointConfiguration(){

        return new AwsClientBuilder.EndpointConfiguration(END_POINT, "ap-northeast-2");
    }
    @Bean
    public AmazonDynamoDB AmazonDynamoDB() {

        return  AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(AWSCredentials()))
                .withEndpointConfiguration(EndpointConfiguration()).build();
    }

    @Bean
    public DynamoDBMapper DynamoDBMapper(){
        return new DynamoDBMapper(AmazonDynamoDB(), DynamoDBMapperConfig.DEFAULT);
    }

    public static class LocalDateTimeConverter implements DynamoDBTypeConverter<Date, LocalDateTime> {
        @Override
        public Date convert(LocalDateTime source) {
            return Date.from(source.toInstant(ZoneOffset.UTC));
        }

        @Override
        public LocalDateTime unconvert(Date source) {
            return source.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
        }
    }
}
