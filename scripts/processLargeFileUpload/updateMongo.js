const MongoClient = require('mongodb').MongoClient;
const assert = require('assert');

const url = 'mongodb://localhost:27017';
const dbName = 'dataLake';

MongoClient.connect(url, { useUnifiedTopology: true, 'forceServerObjectId' : true }, function(err, client) {
	assert.equal(null, err);
	console.log("Successfully connected to mongo");

	const db = client.db(dbName);

	const args = process.argv.slice(2)
	const packageId = args[0]
	
	var packages = db.collection("packages");
	packages.findOne({ "_id": packageId }, function(err, document) {
		assert.equal(null, err);
		console.log(document);
	}); 
});
