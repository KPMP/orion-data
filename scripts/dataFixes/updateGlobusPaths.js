const MongoClient = require('mongodb').MongoClient;
const assert = require('assert');

const url = 'mongodb://localhost:27017';
const dbName = 'dataLake';

const updateCodicil = function(db, callback) {
    var stateCollection = db.collection('state');
    
    stateCollection.find({ state: "METADATA_RECEIVED" }).toArray(function(err, documents) {
        let asyncWrapper = new Promise((resolve, reject) => {
            documents.forEach(function(document, index, array) {
                
                console.log(document);

                let codicil = document.codicil;
                if (codicil) {
                    codicil = codicil.replace("https://app.globus.org/file-manager?origin_id=936381c8-1653-11ea-b94a-0e16720bb42f", "https://app.globus.org/file-manager?origin_id=d4560298-72ed-11ec-bdef-55fe55c2cfea");
                    stateCollection.updateOne({_id: document._id}, {$set: { codicil: codicil}});
                    console.log("updated document " + document
                    ._id);
                }
                if (index === array.length - 1) resolve();
            });
        });
        asyncWrapper.then(() => {
            callback();
        });


    });    
        
}

MongoClient.connect(url, function(err, client) {
    assert.strictEqual(null, err);

    const db = client.db(dbName);
    
    updateCodicil(db, function() {
        client.close();
    });
});

