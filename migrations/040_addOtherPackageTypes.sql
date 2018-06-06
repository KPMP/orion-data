CREATE TABLE `upload_package_to_package_type_other` (
  `upload_package_id` INT UNSIGNED NOT NULL,
  `package_type_other_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`upload_package_id`, `package_type_other_id`)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `package_type_other` (
  `id` INT UNSIGNED NOT NULL,
  `package_type` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE case_demographics RENAME TO upload_package;

ALTER TABLE `upload_package_to_package_type_other` 
	ADD INDEX `upload_package_to_ptother_package_type_other_foreign_idx` (`package_type_other_id` ASC);
ALTER TABLE `upload_package_to_package_type_other` 
	ADD CONSTRAINT `upload_package_to_ptother_package_type_other_id_foreign`
	  FOREIGN KEY (`package_type_other_id`)
	  REFERENCES `package_type_other` (`id`)
	ADD CONSTRAINT `upload_package_to_ptother_upload_package_id_foreign`
	  FOREIGN KEY (`upload_package_id`)
	  REFERENCES `upload_package` (`id`);


ALTER TABLE `file_submissions` 
	DROP FOREIGN KEY `file_submissions_case_id_foreign`;
ALTER TABLE `file_submissions` 
	CHANGE COLUMN `case_id` `upload_package_id` INT(10) UNSIGNED NOT NULL ;
ALTER TABLE `file_submissions` 
	ADD CONSTRAINT `file_submissions_case_id_foreign`
	  FOREIGN KEY (`upload_package_id`)
	  REFERENCES `upload_package` (`id`);