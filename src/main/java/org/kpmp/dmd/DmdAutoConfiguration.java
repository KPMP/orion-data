package org.kpmp.dmd;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

@Configuration
@PropertySource({"classpath:application.properties"})
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "org.kpmp.dmd",
        entityManagerFactoryRef = "dmdEntityManager",
        transactionManagerRef = "dmdTransactionManager")
public class DmdAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix="spring.dmd-datasource")
    public DataSourceProperties dmdDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dmdDataSource() {
        return dmdDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean dmdEntityManager(@Qualifier("dmdDataSource") DataSource dataSource,
    EntityManagerFactoryBuilder builder) {
        final HashMap<String, Object> hibernateProperties = new HashMap<String, Object>();
        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");

        return builder
                .dataSource(dmdDataSource())
                .packages(DluPackageInventory.class)
                .properties(hibernateProperties)
                .build();
    }

    @Bean
    public PlatformTransactionManager dmdTransactionManager(
            @Qualifier("dmdEntityManagerFactory") LocalContainerEntityManagerFactoryBean dmdEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(dmdEntityManagerFactory.getObject()));

    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);

    }

}
