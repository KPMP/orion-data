const MongoClient = require("mongodb").MongoClient;
const assert = require("assert");
const filesystem = require("fs");
const path = require("path");
const { v4: uuidGenerator } = require("uuid");

const url = "mongodb://mongodb:27017";
const dbName = "dataLake";

const updatePackage = function(packageId, files, db, callback) {
	
	var packages = db.collection("packages");
        packages.findOneAndUpdate({ "_id": packageId }, {$set: { files, regenerateZip: true }}, {returnOriginal: true}, function(err, doc) {
		if(err) {
			throw err;
		} else if (typeof doc === "undefined") {
			throw "Unable to find package with id ${packageId}";
		} else {
			// eslint-disable-next-line no-console
			console.log("Successfully updated");
		}
		callback();
	});
};


MongoClient.connect(url, { useUnifiedTopology: true, "forceServerObjectId" : true }, function(err, client) {
	assert.equal(null, err);

	const db = client.db(dbName);

	const args = process.argv.slice(2);
	const packageId = args[0];
	
	const packageDir = "package_" + packageId;
	const directory = path.join("/data/dataLake", packageDir);
	var files = filesystem.readdirSync(directory);
	let fileInfos = [];
	files.forEach(function(file) {
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
