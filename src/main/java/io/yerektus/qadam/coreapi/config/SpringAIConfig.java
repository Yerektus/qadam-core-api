package io.yerektus.qadam.coreapi.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class SpringAIConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    /**
     * Explicit DataSource bean required because WebFlux + R2DBC suppresses
     * Spring Boot's DataSourceAutoConfiguration, so JdbcTemplate is never
     * created automatically — even with spring-boot-starter-jdbc on the classpath.
     * PgVectorStore needs a JdbcTemplate to perform vector operations.
     */
    @Bean
    public DataSource vectorStoreDataSource() {
        return DataSourceBuilder.create()
                .url(datasourceUrl)
                .username(datasourceUsername)
                .password(datasourcePassword)
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource vectorStoreDataSource) {
        return new JdbcTemplate(vectorStoreDataSource);
    }

    @Bean
    public PgVectorStore vectorStore(EmbeddingModel embeddingModel, JdbcTemplate jdbcTemplate) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(1536)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .initializeSchema(false)
                .build();
    }
}
