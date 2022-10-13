const MongoClient = require("mongodb").MongoClient;
const md5File = require('md5-file');
const assert = require("assert");
const filesystem = require("fs");
const path = require("path");
const { v4: uuidGenerator } = require("uuid");
const axios = require('axios')

const url = "mongodb://mongodb:27017";
const dmdUrl = "http://data-manager-service:5000/v1/dlu/file";
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
		uuid = uuidGenerator();
		const filePath = path.join(directory, file);
		md5checksum = md5File.sync(filePath);
		let stats = filesystem.statSync(filePath);
		let fileInfo = {};
		fileInfo.fileName= file;
		fileInfo.size=stats["size"];
		fileInfo._id = uuid;
		fileInfo.md5checksum = md5checksum;
		fileInfos.push(fileInfo);
		dmdData = {
			dluFileName: file,
			dluPackageId: packageId,
			dluFileId: uuid,
			dluFileSize: stats["size"],
			dluMd5Checksum: md5checksum,
		}
		axios.post(dmdUrl, dmdData, {})
			.then(function (response) {
				console.log(response);
			})
	});
	
	updatePackage(packageId, fileInfos , db, function() {
		client.close();
	});
});
