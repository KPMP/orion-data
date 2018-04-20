-- phpMyAdmin SQL Dump
-- version 4.7.7
-- https://www.phpmyadmin.net/
--
-- Host: mysql
-- Generation Time: Apr 19, 2018 at 03:29 PM
-- Server version: 10.2.13-MariaDB-10.2.13+maria~jessie
-- PHP Version: 7.1.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `orion`
--
CREATE DATABASE IF NOT EXISTS `orion` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `orion`;

-- --------------------------------------------------------

--
-- Table structure for table `case_demographics`
--

CREATE TABLE `case_demographics` (
  `id` int(10) UNSIGNED NOT NULL,
  `subject_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `experiment_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `performed_at` datetime NOT NULL,
  `version_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `device_vendors`
--

CREATE TABLE `device_vendors` (
  `id` int(10) UNSIGNED NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `file_formats`
--

CREATE TABLE `file_formats` (
  `id` int(10) UNSIGNED NOT NULL,
  `format_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `file_meta_entries`
--

CREATE TABLE `file_meta_entries` (
  `id` int(10) UNSIGNED NOT NULL,
  `entry` longtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `file_submissions`
--

CREATE TABLE `file_submissions` (
  `id` int(10) UNSIGNED NOT NULL,
  `file_path` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `case_id` int(10) UNSIGNED NOT NULL,
  `institution_id` int(10) UNSIGNED NOT NULL,
  `submitter_id` int(10) UNSIGNED NOT NULL,
  `file_meta_entry_id` int(10) UNSIGNED NOT NULL,
  `file_format_id` int(10) UNSIGNED NOT NULL,
  `device_vendor_id` int(10) UNSIGNED NOT NULL,
  `post_process_protocol_id` int(10) UNSIGNED NOT NULL,
  `magnification_level_id` int(10) UNSIGNED NOT NULL,
  `instrument_id` int(10) UNSIGNED NOT NULL,
  `viewer_id` int(10) UNSIGNED NOT NULL,
  `matrix_format_id` int(10) UNSIGNED NOT NULL,
  `file_created_at` datetime NOT NULL,
  `file_size` bigint(20) NOT NULL,
  `is_open` tinyint(1) NOT NULL,
  `is_raw` tinyint(1) NOT NULL,
  `is_post_proccessing` tinyint(1) NOT NULL,
  `is_multiplane` tinyint(1) NOT NULL,
  `is_pyramid` tinyint(1) NOT NULL,
  `spatial_res` double(8,2) NOT NULL,
  `channels` bigint(20) NOT NULL,
  `bit_depth` bigint(20) NOT NULL,
  `x_extent_pixels` bigint(20) NOT NULL,
  `y_exttent_pixels` bigint(20) NOT NULL,
  `z_plane_extent_layers` bigint(20) NOT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `institution_demographics`
--

CREATE TABLE `institution_demographics` (
  `id` int(10) UNSIGNED NOT NULL,
  `inst_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `inst_shortname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `instruments`
--

CREATE TABLE `instruments` (
  `id` int(10) UNSIGNED NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `magnification_levels`
--

CREATE TABLE `magnification_levels` (
  `id` int(10) UNSIGNED NOT NULL,
  `level` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `matrix_formats`
--

CREATE TABLE `matrix_formats` (
  `id` int(10) UNSIGNED NOT NULL,
  `version` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `migrations`
--

CREATE TABLE `migrations` (
  `id` int(10) UNSIGNED NOT NULL,
  `migration` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `batch` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `post_process_protocols`
--

CREATE TABLE `post_process_protocols` (
  `id` int(10) UNSIGNED NOT NULL,
  `protocol_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE `roles` (
  `id` int(10) UNSIGNED NOT NULL,
  `role` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `submitter_demographics`
--

CREATE TABLE `submitter_demographics` (
  `id` int(10) UNSIGNED NOT NULL,
  `first_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role_id` int(10) UNSIGNED DEFAULT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `viewers`
--

CREATE TABLE `viewers` (
  `id` int(10) UNSIGNED NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `case_demographics`
--
ALTER TABLE `case_demographics`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `device_vendors`
--
ALTER TABLE `device_vendors`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `file_formats`
--
ALTER TABLE `file_formats`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `file_meta_entries`
--
ALTER TABLE `file_meta_entries`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `file_submissions`
--
ALTER TABLE `file_submissions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `file_submissions_case_id_foreign` (`case_id`),
  ADD KEY `file_submissions_institution_id_foreign` (`institution_id`),
  ADD KEY `file_submissions_submitter_id_foreign` (`submitter_id`),
  ADD KEY `file_submissions_file_meta_entry_id_foreign` (`file_meta_entry_id`),
  ADD KEY `file_submissions_file_format_id_foreign` (`file_format_id`),
  ADD KEY `file_submissions_device_vendor_id_foreign` (`device_vendor_id`),
  ADD KEY `file_submissions_post_process_protocol_id_foreign` (`post_process_protocol_id`),
  ADD KEY `file_submissions_magnification_level_id_foreign` (`magnification_level_id`),
  ADD KEY `file_submissions_instrument_id_foreign` (`instrument_id`),
  ADD KEY `file_submissions_viewer_id_foreign` (`viewer_id`),
  ADD KEY `file_submissions_matrix_format_id_foreign` (`matrix_format_id`);

--
-- Indexes for table `institution_demographics`
--
ALTER TABLE `institution_demographics`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `instruments`
--
ALTER TABLE `instruments`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `magnification_levels`
--
ALTER TABLE `magnification_levels`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `matrix_formats`
--
ALTER TABLE `matrix_formats`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `migrations`
--
ALTER TABLE `migrations`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `post_process_protocols`
--
ALTER TABLE `post_process_protocols`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `submitter_demographics`
--
ALTER TABLE `submitter_demographics`
  ADD PRIMARY KEY (`id`),
  ADD KEY `submitter_demographics_role_id_foreign` (`role_id`);

--
-- Indexes for table `viewers`
--
ALTER TABLE `viewers`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `case_demographics`
--
ALTER TABLE `case_demographics`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `device_vendors`
--
ALTER TABLE `device_vendors`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `file_formats`
--
ALTER TABLE `file_formats`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `file_meta_entries`
--
ALTER TABLE `file_meta_entries`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `file_submissions`
--
ALTER TABLE `file_submissions`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `institution_demographics`
--
ALTER TABLE `institution_demographics`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `instruments`
--
ALTER TABLE `instruments`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `magnification_levels`
--
ALTER TABLE `magnification_levels`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `matrix_formats`
--
ALTER TABLE `matrix_formats`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `migrations`
--
ALTER TABLE `migrations`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `post_process_protocols`
--
ALTER TABLE `post_process_protocols`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `submitter_demographics`
--
ALTER TABLE `submitter_demographics`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `viewers`
--
ALTER TABLE `viewers`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `file_submissions`
--
ALTER TABLE `file_submissions`
  ADD CONSTRAINT `file_submissions_case_id_foreign` FOREIGN KEY (`case_id`) REFERENCES `case_demographics` (`id`),
  ADD CONSTRAINT `file_submissions_device_vendor_id_foreign` FOREIGN KEY (`device_vendor_id`) REFERENCES `device_vendors` (`id`),
  ADD CONSTRAINT `file_submissions_file_format_id_foreign` FOREIGN KEY (`file_format_id`) REFERENCES `file_formats` (`id`),
  ADD CONSTRAINT `file_submissions_file_meta_entry_id_foreign` FOREIGN KEY (`file_meta_entry_id`) REFERENCES `file_meta_entries` (`id`),
  ADD CONSTRAINT `file_submissions_institution_id_foreign` FOREIGN KEY (`institution_id`) REFERENCES `institution_demographics` (`id`),
  ADD CONSTRAINT `file_submissions_instrument_id_foreign` FOREIGN KEY (`instrument_id`) REFERENCES `instruments` (`id`),
  ADD CONSTRAINT `file_submissions_magnification_level_id_foreign` FOREIGN KEY (`magnification_level_id`) REFERENCES `magnification_levels` (`id`),
  ADD CONSTRAINT `file_submissions_matrix_format_id_foreign` FOREIGN KEY (`matrix_format_id`) REFERENCES `matrix_formats` (`id`),
  ADD CONSTRAINT `file_submissions_post_process_protocol_id_foreign` FOREIGN KEY (`post_process_protocol_id`) REFERENCES `post_process_protocols` (`id`),
  ADD CONSTRAINT `file_submissions_submitter_id_foreign` FOREIGN KEY (`submitter_id`) REFERENCES `submitter_demographics` (`id`),
  ADD CONSTRAINT `file_submissions_viewer_id_foreign` FOREIGN KEY (`viewer_id`) REFERENCES `viewers` (`id`);


ALTER TABLE `orion`.`submitter_demographics` 
ADD CONSTRAINT `submitter_demographics_role_id_foreign`
  FOREIGN KEY (`role_id`)
  REFERENCES `orion`.`roles` (`id`);