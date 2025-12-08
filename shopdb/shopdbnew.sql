-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: shopdb
-- ------------------------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `app_order`
--

DROP TABLE IF EXISTS `app_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `app_order` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `order_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `total_amount` double DEFAULT NULL,
  `tax` double DEFAULT NULL,
  `discount` double DEFAULT NULL,
  `total` double DEFAULT NULL,
  `status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shipping_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shipping_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shipping_email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `table_number` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `idx_app_order_customer_id` (`customer_id`),
  KEY `idx_app_order_user_id` (`user_id`),
  CONSTRAINT `app_order_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
  CONSTRAINT `app_order_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`userID`),
  CONSTRAINT `fk_app_order_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_app_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`userID`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_order`
--

LOCK TABLES `app_order` WRITE;
/*!40000 ALTER TABLE `app_order` DISABLE KEYS */;
INSERT INTO `app_order` VALUES (1,1,1,'2025-09-08 16:59:26',1055000,0,0,1055000,'Pending',NULL,NULL,NULL,NULL),(2,1,1,'2025-09-16 10:39:10',30000,0,0,30000,'Pending',NULL,NULL,NULL,NULL),(3,1,1,'2025-09-22 14:51:45',45000,0,0,45000,'Pending',NULL,NULL,NULL,NULL),(4,1,1,'2025-09-24 10:31:33',20000,0,0,20000,'Pending',NULL,NULL,NULL,NULL),(5,1,1,'2025-09-24 10:34:13',10000,0,0,10000,'Pending',NULL,NULL,NULL,NULL),(6,1,1,'2025-09-24 11:27:59',545000,0,0,545000,'Pending',NULL,NULL,NULL,NULL),(7,1,1,'2025-09-24 17:15:33',545000,0,0,545000,'Pending',NULL,NULL,NULL,NULL),(8,1,1,'2025-10-11 03:11:11',80000,0,0,80000,'Pending',NULL,NULL,NULL,NULL),(10,1,1,'2025-10-13 17:13:07',193000,0,0,193000,'Pending',NULL,NULL,NULL,NULL),(11,1,1,'2025-10-13 17:50:08',580000,0,0,580000,'Pending',NULL,NULL,NULL,NULL),(12,1,1,'2025-10-20 18:16:43',681000,0,0,681000,'Pending',NULL,NULL,NULL,NULL),(13,1,1,'2025-10-20 18:18:17',2785000,0,0,2785000,'Pending',NULL,NULL,NULL,NULL),(14,1,1,'2025-10-20 18:18:58',1462000,0,0,1462000,'Pending',NULL,NULL,NULL,NULL),(15,1,1,'2025-10-27 18:59:35',537000,0,0,537000,'Pending',NULL,NULL,NULL,NULL),(16,1,1,'2025-10-28 11:57:30',926500,0,163500,926500,'Pending',NULL,NULL,NULL,'Table 1'),(19,1,1,'2025-11-03 06:57:07',38250,0,6750,38250,'Pending','Le Nguyen Vu Duy','0819032089','lenguyenvuduy123456@gmail.com','Table 1'),(21,1,1,'2025-11-22 16:07:56',742900,0,131100,742900,'Pending','Le Nguyen Vu Duy','0819032089','lenguyenvuduy123456@gmail.com','Table 1'),(22,1,1,'2025-11-22 16:09:22',76500,0,13500,76500,'Pending','Le Nguyen Vu Duy','0819032089','lenguyenvuduy123456@gmail.com','Table 1'),(23,15,1,'2025-11-25 06:39:43',117000,0,0,117000,'Pending','NDH','0812345678','ndh@gmail.com','Table 1'),(24,14,1,'2025-11-25 06:39:52',817000,0,0,817000,'Pending','NLGH','0318943032','nlgh@gmail.com','Table 1'),(25,1,1,'2025-11-25 06:44:17',2751450,0,485550,2751450,'Pending','Le Nguyen Vu Duy','0819032089','lenguyenvuduy123456@gmail.com','Table 1'),(26,1,1,'2025-11-29 20:15:55',76500,0,13500,76500,'Pending','Lê Nguyễn Vũ Duy','0819032089','lenguyenvuduy123456@gmail.com','Table 1');
/*!40000 ALTER TABLE `app_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `app_order_details`
--

DROP TABLE IF EXISTS `app_order_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `app_order_details` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `price` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_app_order_details_order_id` (`order_id`),
  KEY `idx_app_order_details_product_id` (`product_id`),
  CONSTRAINT `app_order_details_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `app_order` (`order_id`),
  CONSTRAINT `app_order_details_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `fk_app_order_details_order` FOREIGN KEY (`order_id`) REFERENCES `app_order` (`order_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_app_order_details_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_order_details`
--

LOCK TABLES `app_order_details` WRITE;
/*!40000 ALTER TABLE `app_order_details` DISABLE KEYS */;
INSERT INTO `app_order_details` VALUES (12,5,1,1,10000),(13,6,1,1,10000),(14,6,2,1,20000),(15,6,3,1,15000),(16,6,4,1,500000),(17,7,1,1,10000),(18,7,2,1,20000),(19,7,3,1,15000),(20,7,4,1,500000),(21,8,1,1,45000),(22,8,2,1,35000),(25,10,1,1,45000),(26,10,3,4,37000),(27,11,1,1,45000),(28,11,2,1,35000),(29,11,4,1,500000),(30,12,3,3,37000),(31,12,2,2,35000),(32,12,4,1,500000),(33,13,2,1,35000),(34,13,4,5,500000),(35,13,7,5,50000),(36,14,11,6,100000),(37,14,9,4,78000),(38,14,7,3,50000),(39,14,5,2,200000),(40,15,3,1,37000),(41,15,4,1,500000),(42,16,5,1,200000),(43,16,9,5,78000),(44,16,10,3,100000),(45,16,11,2,100000),(54,19,1,1,45000),(56,21,1,4,45000),(57,21,2,2,35000),(58,21,3,2,37000),(59,21,4,1,500000),(60,21,7,1,50000),(61,22,1,2,45000),(62,23,1,1,45000),(63,23,2,1,35000),(64,23,3,1,37000),(65,24,1,1,45000),(66,24,2,1,35000),(67,24,3,1,37000),(68,24,4,1,500000),(69,24,5,1,200000),(70,25,3,1,37000),(71,25,5,16,200000),(72,26,1,2,45000);
/*!40000 ALTER TABLE `app_order_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `accumulatedPoint` float DEFAULT '0',
  `accumulated_point` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'Lê Nguyễn Vũ Duy','0819032089','lenguyenvuduy123456@gmail.com',280,NULL),(14,'NLGH','0318943032','nlgh@gmail.com',10,0),(15,'NDH','0812345678','ndh@gmail.com',10,0),(17,'Nguyen Van A','0901234567','nguyenvana@gmail.com',0,0),(18,'Nguyen Van B','0901234536','nguyenvanbb@gmail.com',0,0);
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items_backup`
--

DROP TABLE IF EXISTS `order_items_backup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items_backup` (
  `id` bigint NOT NULL DEFAULT '0',
  `price` double NOT NULL,
  `quantity` int NOT NULL,
  `order_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items_backup`
--

LOCK TABLES `order_items_backup` WRITE;
/*!40000 ALTER TABLE `order_items_backup` DISABLE KEYS */;
INSERT INTO `order_items_backup` VALUES (1,10000,1,21,1),(2,20000,1,21,2),(3,15000,1,21,3),(4,500000,1,21,4);
/*!40000 ALTER TABLE `order_items_backup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` double NOT NULL,
  `stock` int DEFAULT '0',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Lavender Lemonade Mocktail',45000,46,'/uploads/product_1_1759200804822_Refreshing Lavender Lemonade Mocktail Recipe -.jpg','2025-08-18 07:10:12','Một loại mocktail tuyệt vời với hương vị lavender nhẹ nhàng kết hợp với chanh tươi mát. Thức uống hoàn hảo cho những ngày hè nóng bức.'),(2,'Matcha Latte',35000,148,'/uploads/product_2_1751617630360_????? ??????.jpg','2025-08-18 07:10:12','abc'),(3,'Vietnamese Salted Coffee',37000,88,'/uploads/product_3_1751617623234_download.jpg','2025-08-18 07:10:12',NULL),(4,'Matcha Lava Cake',500000,75,'/uploads/product_4_1751617637060_download (2).jpg','2025-08-18 07:10:12',NULL),(5,'Smooth Brown Sugar Bourbon Cold Brew',200000,154,'/uploads/product_5_1759201027944_Quick And Smooth Brown Sugar Bourbon Cold Brew Recipe.jpg','2025-08-18 07:10:12',NULL),(7,'Thai Milk Tea Boba',50000,86,'/uploads/product_7_1759201197344_Thai Milk Tea Boba.jpg','2025-08-18 07:10:12',NULL),(8,'Refreshing Coconut Lavender Lemonade Mocktail',500000,98,'/uploads/product_8_1759201359402_Refreshing Coconut Lavender Lemonade Mocktail Recipe - The Sip Spot - Cocktails Made Easy.jpg','2025-08-18 07:10:12',NULL),(9,'Butterfly pea juice with milk',78000,188,'/uploads/product_9_1759201393441_Butterfly pea juice with milk _ Premium Photo.jpg','2025-08-18 07:10:12',NULL),(10,'Huy',100000,139,'/uploads/product_10_1762126864289_vuong-quoc-anh.jpg','2025-08-18 07:10:12',NULL),(11,'vxpj',100000,92,'/uploads/product_11_1758704575757_frame.png','2025-08-18 07:10:12',NULL);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `userID` int NOT NULL AUTO_INCREMENT,
  `userName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'user',
  `user_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`userID`),
  UNIQUE KEY `userName` (`userName`),
  UNIQUE KEY `ux_users_userName` (`userName`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','$2a$12$qRYCWqk1aNMKvL3WGUNgGuD4yYO3I30PAgy4trHHI5qudYfm6PGIu','admin@example.com','admin',''),(2,'staff','$2a$12$Brg8Bvd20eR8jXfrK0xCsuMb1hbGErQS7W7ri8/.OQfQZzj/qkxTm','staff@example.com','staff',''),(3,'staff1','$2a$12$0OgxnJxuoGzcvHHg9D7Hgeza.vm.YXNMtKbbP/7RaKhIhcNpoZlnC','staff111@gmail.com','staff',''),(4,'testt1','$2a$12$vxSS4vH/aOJQGAHAx15NWumsexqosPglDFpVAjYxBthojzusQoX7a','test1@gmail.com','staff','');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_order`
--

DROP TABLE IF EXISTS `web_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `web_order` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int DEFAULT NULL,
  `staff_id` int NOT NULL,
  `order_date` datetime NOT NULL,
  `total_amount` double NOT NULL,
  `tax` double DEFAULT '0',
  `discount` double DEFAULT '0',
  `total` double NOT NULL,
  `staff_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'pending',
  `shipping_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shipping_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `shipping_email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `table_number` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `customer_id` (`customer_id`),
  KEY `staff_id` (`staff_id`),
  KEY `idx_orders_order_date` (`order_date`),
  KEY `idx_web_order_customer_id` (`customer_id`),
  KEY `idx_web_order_user_id` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`userID`),
  CONSTRAINT `fk_web_order_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_web_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`userID`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `web_order_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
  CONSTRAINT `web_order_ibfk_2` FOREIGN KEY (`staff_id`) REFERENCES `users` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_order`
--

LOCK TABLES `web_order` WRITE;
/*!40000 ALTER TABLE `web_order` DISABLE KEYS */;
INSERT INTO `web_order` VALUES (38,1,1,'2025-09-24 03:34:02',10000,0,0,10000,NULL,NULL,'Cancelled','Le Nguyen Vu Duy','0819032089','lenguyenvuduy123456@gmail.com','Table 1'),(39,1,1,'2025-09-24 04:26:30',10000,0,0,10000,NULL,NULL,'Processing','Lê Nguyễn Vũ Duy','0819032089','Lmbd1210@gmail.com','Table 1'),(40,1,1,'2025-09-24 04:27:47',545000,0,0,545000,NULL,NULL,'Processing','Lê Nguyễn Vũ Duy','0819032089','Hchhdth@gmail.com','Table 1'),(41,1,1,'2025-09-24 10:15:14',545000,0,0,545000,NULL,NULL,'Processing','Le Nguyen Vu Duy','0819032089','lenguyenvuduy123456@gmail.com','Table 1'),(42,1,1,'2025-09-29 12:01:32',45000,0,0,45000,NULL,NULL,'Processing','Lê Nguyễn Vũ Duy','0819032089','Lnvd1210@vmail.com','Table 3'),(43,1,1,'2025-09-30 03:14:02',80000,0,0,80000,NULL,NULL,'Completed','Lê Nguyễn Vũ Duy','0819032089','Lnvd1210@gmail.com','Table 1'),(44,1,1,'2025-10-13 10:45:23',580000,0,0,580000,NULL,NULL,'Completed','Lê Nguyễn Vũ Duy','0819032089','lnvd1210@gmail.com','Table 3'),(45,1,1,'2025-11-02 23:56:06',45000,0,0,45000,NULL,NULL,'Completed','Le Nguyen Vu Duy','0819032089','lenguyenvuduy123456@gmail.com','Table 1'),(47,1,1,'2025-11-22 09:07:41',874000,0,0,874000,NULL,NULL,'Completed','Le Nguyen Vu Duy','0819032089','lenguyenvuduy123456@gmail.com','Table 1'),(48,1,1,'2025-11-22 09:08:51',90000,0,0,90000,NULL,NULL,'Completed','Le Nguyen Vu Duy','0819032089','lenguyenvuduy123456@gmail.com','Table 1'),(49,14,1,'2025-11-24 23:38:27',817000,0,0,817000,NULL,NULL,'Completed','NLGH','0318943032','nlgh@gmail.com','Table 1'),(50,15,1,'2025-11-24 23:38:59',117000,0,0,117000,NULL,NULL,'Completed','NDH','0812345678','ndh@gmail.com','Table 1'),(51,1,1,'2025-11-24 23:44:06',3237000,0,0,3237000,NULL,NULL,'Pending','Le Nguyen Vu Duy','0819032089','lenguyenvuduy123456@gmail.com','Table 1'),(52,1,1,'2025-11-24 23:49:32',45000,0,0,45000,NULL,NULL,'Pending','Lê Nguyễn Vũ Duy','0819032089','Hsbsb@gmail.com','Table 3'),(54,17,1,'2025-11-25 14:56:14',30000,0,0,30000,NULL,NULL,'Pending','Nguyen Van A','0901234567','nguyenvana@gmail.com','Table 5'),(55,17,1,'2025-11-26 11:14:32',44000,0,0,44000,NULL,NULL,'Pending','Nguyen Van A','0901234567','nguyenvana@gmail.com','Table 5'),(56,18,1,'2025-11-26 11:17:33',44000,0,0,44000,NULL,NULL,'Pending','Nguyen Van B','0901234536','nguyenvanbb@gmail.com','Table 5'),(57,1,1,'2025-11-29 13:15:20',90000,0,0,90000,NULL,NULL,'Pending','Lê Nguyễn Vũ Duy','0819032089','lenguyenvuduy123456@gmail.com','Table 1');
/*!40000 ALTER TABLE `web_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `web_order_details`
--

DROP TABLE IF EXISTS `web_order_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `web_order_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` int NOT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  KEY `idx_web_order_details_order_id` (`order_id`),
  KEY `idx_web_order_details_product_id` (`product_id`),
  CONSTRAINT `fk_web_order_details_order` FOREIGN KEY (`order_id`) REFERENCES `web_order` (`order_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_web_order_details_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `web_order_details_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `web_order` (`order_id`) ON DELETE CASCADE,
  CONSTRAINT `web_order_details_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `web_order_details`
--

LOCK TABLES `web_order_details` WRITE;
/*!40000 ALTER TABLE `web_order_details` DISABLE KEYS */;
INSERT INTO `web_order_details` VALUES (84,38,1,1,10000),(85,39,1,1,10000),(86,40,1,1,10000),(87,40,2,1,20000),(88,40,3,1,15000),(89,40,4,1,500000),(90,41,1,1,10000),(91,41,2,1,20000),(92,41,3,1,15000),(93,41,4,1,500000),(94,42,1,1,10000),(95,42,2,1,20000),(96,42,3,1,15000),(97,43,1,1,45000),(98,43,2,1,35000),(99,44,1,1,45000),(100,44,2,1,35000),(101,44,4,1,500000),(102,45,1,1,45000),(104,47,1,4,45000),(105,47,2,2,35000),(106,47,3,2,37000),(107,47,4,1,500000),(108,47,7,1,50000),(109,48,1,2,45000),(110,49,1,1,45000),(111,49,2,1,35000),(112,49,3,1,37000),(113,49,4,1,500000),(114,49,5,1,200000),(115,50,1,1,45000),(116,50,2,1,35000),(117,50,3,1,37000),(118,51,3,1,37000),(119,51,5,16,200000),(120,52,1,1,45000),(122,54,1,2,15000),(123,55,1,2,15000),(124,55,2,1,14000),(125,56,1,2,15000),(126,56,2,1,14000),(127,57,1,2,45000);
/*!40000 ALTER TABLE `web_order_details` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-02  5:27:08
