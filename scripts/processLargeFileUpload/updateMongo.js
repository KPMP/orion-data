const MongoClient = require('mongodb').MongoClient;
const assert = require('assert');

const url = 'mongodb://localhost:27017';
const dbName = 'dataLake';

MongoClient.connect(url, { useUnifiedTopology: true, 'forceServerObjectId' : true }, function(err, client) {
	assert.equal(null, err);
	console.log("Successfully connected to mongo");

	const db = client.db(dbName);

	process.argv.forEach((val, index) => {
  		console.log(`${index}: ${val}`)
	})
});
