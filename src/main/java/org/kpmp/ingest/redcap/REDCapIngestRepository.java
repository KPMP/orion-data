package org.kpmp.ingest.redcap;

import java.util.Date;

import org.bson.Document;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoCollection;

@Component
public class REDCapIngestRepository {

	private static final String REDCAP_COLLECTION = "redcap";
	private MongoTemplate mongoTemplate;

	@Autowired
	public REDCapIngestRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public void saveDump(JSONObject jsonDump) {
		Document document = Document.parse(jsonDump.toString());
		document.put("created_at", new Date());
		MongoCollection<Document> collection = mongoTemplate.getCollection(REDCAP_COLLECTION);
		collection.insertOne(document);
	}

}
