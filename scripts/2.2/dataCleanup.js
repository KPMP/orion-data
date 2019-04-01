var institutions=["Broad (Michigan/Broad/Princeton TIS)", "Michigan (Michigan/Broad/Princeton TIS)", "Princeton (Michigan/Broad/Princeton TIS)"]
db.packages.update({"institution": {$in:institutions}}, {$set: {"tisName": "Michigan/Broad/Princeton"}}, false, true)

institutions = ["EMBL (UTHSA/EMBL/PNNL/UCSD TIS)", "PNNL (UTHSA/EMBL/PNNL/UCSD TIS)", "UCSD (UTHSA/EMBL/PNNL/UCSD TIS)", "UTHSA (UTHSA/EMBL/PNNL/UCSD TIS)"]
db.packages.update({"institution": {$in:institutions}}, {$set: {"tisName": "UTHSA/EMBL/PNNL/UCSD"}}, false, true)

institutions = ["Indiana (IU/OSU TIS)", "OSU (IU/OSU TIS)"]
db.packages.update({"institution": {$in:institutions}}, {$set: {"tisName": "IU/OSU"}}, false, true)

institutions = ["Stanford (UCSF/Stanford TIS)", "UCSF (UCSF/Stanford TIS)"]
db.packages.update({"institution": {$in:institutions}}, {$set: {"tisName": "UCSF/Stanford"}}, false, true)

institutions = ["UCSD (UCSD/WashU TIS)", "WashU (UCSD/WashU TIS)"]
db.packages.update({"institution": {$in:institutions}}, {$set: {"tisName": "UCSD/WashU"}}, false, true)

db.packages.update({ "isError": { $exists: false } }, {$set: {"regenerateZip": true}}, false, true)
db.packages.update({ packageType:"Bulk RNA-Seq" },{ $set:{ packageType:"Bulk total/mRNA", regenerateZip:true }}, false, true)
db.packages.update({ packageType:"Sub-segment RNA-Seq" },{ $set:{ packageType:"Sub-segmental RNA-Seq", regenerateZip:true }}, false, true)
