CREATE TABLE `protocol` (
  `id` INT UNSIGNED NOT NULL,
  `protocol` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `upload_package` 
	ADD COLUMN `protocol_id` INT NOT NULL AFTER `experiment_id`;	  
	  
INSERT INTO `protocol` (id, protocol) VALUES (1, 'Pilot 1');	  
INSERT INTO `protocol` (id, protocol) VALUES (2, 'Pilot 2');	  
INSERT INTO `protocol` (id, protocol) VALUES (3, 'Pilot 3');	  
INSERT INTO `protocol` (id, protocol) VALUES (4, 'Protocol v1: Pathology MOP v1');	  
INSERT INTO `protocol` (id, protocol) VALUES (5, 'Protocol v1: TIS MOP v1');	  
INSERT INTO `protocol` (id, protocol) VALUES (6, 'Other');

UPDATE upload_package set protocol_id = 6;