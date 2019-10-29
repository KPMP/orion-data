const MongoClient = require('mongodb').MongoClient;
const assert = require('assert');

const url = 'mongodb://localhost:27017';
const dbName = 'dataLake';
var stateMap = new Map();
var missingStates = new Array();
	
const createStateMap = function(db, callback) {
	var stateCollection = db.collection("state");
	
	stateCollection.find({}).toArray(function(err, docs) {
		assert.equal(null, err);
		docs.forEach(function(doc) {
			var packageId = doc.packageId;
			var docList = stateMap.get(packageId);
			if (docList === undefined) {
				docList= [];
			}
			docList.push(doc);
			stateMap.set(packageId, docList);
		});
		callback(stateMap);
	});
}

const determineMissingStates= function(stateMap, db, callback) {
	var packageCollection = db.collection("packages");
	
	
	packageCollection.find({}).toArray(function(err, docs) {
		assert.equal(null, err);
		docs.forEach(function(doc) {
			let packageId = doc._id;
			if (doc.largeFilesChecked === true) {
				if (!stateMap.has(packageId)) {
					console.log("********************");
					console.log("Large file upload is mising the corresponding METADATA_RECEIVED state: " + doc._id);
					console.log("Please address manually");
					console.log("********************");
				}
			} else if (doc.inError === true) {
				if (!stateMap.has(packageId)) {
					var state = {  'packageId' : packageId, 'state': 'UPLOAD_FAILED', codicil: 'backfilling errors',  stateChangeDate: doc.createdAt};
					missingStates.push(state);
				}
			} else if (!stateMap.has(packageId)) {
				var state = { 'packageId' : packageId, 'state': 'UPLOAD_SUCCEEDED', codicil: '',  stateChangeDate: doc.createdAt};
				missingStates.push(state);
			}
		});
		callback();
	});
	
} 

const insertStates = function(db, callback) {
	var stateCollection = db.collection("state");
	stateCollection.insertMany(missingStates);
	callback();
}

MongoClient.connect(url, {'forceServerObjectId' : true}, function(err, client) {
	
	assert.equal(null, err);
	console.log("successfully connected to server");
	
	const db = client.db(dbName);
	
	createStateMap(db, function() {
		determineMissingStates(stateMap, db, function() {
			insertStates(db, function() {
				client.close();
			});
		});
	});
	
});

