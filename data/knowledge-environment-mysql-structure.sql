-- Adminer 4.7.7 MySQL dump

SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;

SET NAMES utf8mb4;

CREATE DATABASE `knowledge_environment` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `knowledge_environment`;

DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
  `file_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL,
  `file_name` text COLLATE utf8_unicode_ci NOT NULL,
  `package_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL,
  `access` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `file_size` bigint(20) DEFAULT NULL,
  `protocol` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `metadata_type_id` int(11) DEFAULT NULL,
  `release_ver` float DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


DROP TABLE IF EXISTS `file_participant`;
CREATE TABLE `file_participant` (
  `file_id` varchar(100) NOT NULL,
  `participant_id` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `metadata_type`;
CREATE TABLE `metadata_type` (
  `metadata_type_id` int(11) NOT NULL,
  `experimental_strategy` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `data_type` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `data_category` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `data_format` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `platform` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `workflow_type` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `access` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `kpmp_data_type` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


DROP TABLE IF EXISTS `participant`;
CREATE TABLE `participant` (
  `participant_id` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `age_binned` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `sex` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `tissue_source` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `protocol` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `sample_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `tissue_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `clinical_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`clinical_data`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


-- 2020-08-04 03:48:20