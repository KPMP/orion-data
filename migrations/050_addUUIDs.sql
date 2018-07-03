ALTER TABLE `orion`.`file_submissions` 
ADD COLUMN `uuid` VARCHAR(50) NOT NULL AFTER `updated_at`;

ALTER TABLE `orion`.`upload_package` 
ADD COLUMN `uuid` VARCHAR(50) NOT NULL AFTER `updated_at`;
