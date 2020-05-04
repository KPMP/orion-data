const MongoClient = require("mongodb").MongoClient;
const assert = require("assert");
const filesystem = require("fs");
const path = require("path");
const { v4: uuidGenerator } = require("uuid");

const url = "mongodb://localhost:27017";
const dbName = "dataLake";

const updatePackage = function(packageId, files, db, callback) {
	
	var packages = db.collection("packages");
        packages.findOneAndUpdate({ "_id": packageId }, {$set: { files: files, regenerateZip: true }}, {new: true}, function(err, doc) {
		if(err) {
			console.log("Hit err: " + err);
		} else {
			console.log("Successfully updated");
		}
		callback();
	});
}


MongoClient.connect(url, { useUnifiedTopology: true, 'forceServerObjectId' : true }, function(err, client) {
	assert.equal(null, err);
	console.log("Successfully connected to mongo");

	const db = client.db(dbName);

	const args = process.argv.slice(2)
	const packageId = args[0]
	
	const packageDir = "package_" + packageId
	const directory = path.join("/data/dataLake", packageDir);
	var files = filesystem.readdirSync(directory);
	let fileInfos = [];
 	files.forEach(file => {
		const filePath = path.join(directory, file);
		let stats = filesystem.statSync(filePath);
		let fileInfo = {};
		fileInfo.fileName= file;
		fileInfo.size=stats["size"];
		fileInfo._id = uuidGenerator();
		fileInfos.push(fileInfo);
	});
	
	updatePackage(packageId, fileInfos , db, function() {
		client.close();
	});
});
