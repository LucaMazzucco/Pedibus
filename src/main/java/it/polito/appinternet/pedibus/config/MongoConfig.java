package it.polito.appinternet.pedibus.config;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "it.polito.appinternet.pedibus.repository")
public class MongoConfig{
    @Bean
    public MongoClient mongo() throws Exception {
        return new MongoClient("localhost");
    }
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), "pedibus");
    }
}
