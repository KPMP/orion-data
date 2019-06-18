package org.kpmp.packages;

import java.io.IOException;

import org.kpmp.Application;
import org.kpmp.GenerateUploadReport;
import org.kpmp.RegenerateZipFiles;
import org.kpmp.WebConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;

import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;

@Configuration
@ComponentScan(basePackages = { "org.kpmp.packages", "org.kpmp.users", "org.kpmp",
		"org.kpmp.forms" }, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
				PackageController.class, PackageService.class, CustomPackageRepository.class, Application.class,
				GenerateUploadReport.class, RegenerateZipFiles.class, WebConfig.class }))
@EnableMongoRepositories(basePackages = { "org.kpmp.packages", "org.kpmp.users", "org.kpmp.forms", "org.kpmp" })
public class TestMongoConfig {

	private static final String MONGO_DB_URL = "localhost";
	private static final String MONGO_DB_NAME = "embeded_db";

	@Value("${embedded.mongodb.version:latest}")
	private String mongoVersion;

	@Bean
	public MongoTemplate mongoTemplate() throws IOException {
		EmbeddedMongoFactoryBean mongo = new EmbeddedMongoFactoryBean();
		mongo.setBindIp(MONGO_DB_URL);
		if (!mongoVersion.equals("latest")) {
			mongo.setVersion(mongoVersion);
		}
		MongoClient mongoClient = mongo.getObject();

		MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, MONGO_DB_NAME);
		return mongoTemplate;
	}

}
