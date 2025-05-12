package io.bootify.contact_searcher.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("io.bootify.contact_searcher")
@EnableJpaRepositories("io.bootify.contact_searcher")
@EnableTransactionManagement
public class DomainConfig {
}
