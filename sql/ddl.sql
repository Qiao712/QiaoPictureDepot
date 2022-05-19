-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: localhost    Database: picture_depot_2
-- ------------------------------------------------------
-- Server version	8.0.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `album`
--

DROP TABLE IF EXISTS `album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `album` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '相册名称',
  `owner_id` bigint unsigned NOT NULL COMMENT '所属用户',
  `description` varchar(1000) DEFAULT NULL COMMENT '描述',
  `is_public` tinyint NOT NULL DEFAULT '0' COMMENT '是否公开',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_owner` (`owner_id`),
  CONSTRAINT `fk_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='相册';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `album`
--

LOCK TABLES `album` WRITE;
/*!40000 ALTER TABLE `album` DISABLE KEYS */;
INSERT INTO `album` VALUES (73,'hello',1,NULL,0,'2022-03-04 16:40:02','2022-03-04 16:40:02'),(74,'测试2',1,NULL,0,'2022-03-06 10:59:20','2022-03-06 10:59:20'),(75,'测试',1,NULL,0,'2022-03-31 15:31:52','2022-03-31 15:31:52');
/*!40000 ALTER TABLE `album` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `album_access`
--

DROP TABLE IF EXISTS `album_access`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `album_access` (
  `album_id` bigint unsigned NOT NULL COMMENT '指向相册',
  `friend_group_id` bigint unsigned NOT NULL COMMENT '指向好友分组',
  PRIMARY KEY (`album_id`,`friend_group_id`) USING BTREE,
  KEY `fk_friend_group_auth` (`friend_group_id`),
  CONSTRAINT `fk_accessed_album` FOREIGN KEY (`album_id`) REFERENCES `album` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_friend_group_auth` FOREIGN KEY (`friend_group_id`) REFERENCES `friend_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `album_access`
--

LOCK TABLES `album_access` WRITE;
/*!40000 ALTER TABLE `album_access` DISABLE KEYS */;
/*!40000 ALTER TABLE `album_access` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `picture_group_id` bigint unsigned NOT NULL COMMENT '被评论图组ID',
  `author_id` bigint unsigned NOT NULL COMMENT '发布者用户id',
  `parent_id` bigint unsigned DEFAULT NULL COMMENT '所属的一级评论的ID',
  `replied_id` bigint unsigned DEFAULT NULL COMMENT '被回复的评论的Id',
  `content` varchar(1000) NOT NULL COMMENT '评论内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_picture_group_replied` (`picture_group_id`),
  KEY `fk_parent_comment` (`parent_id`),
  KEY `fk_reply_to` (`replied_id`),
  KEY `fk_comment_author` (`author_id`),
  CONSTRAINT `fk_comment_author` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_parent_comment` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_picture_group_replied` FOREIGN KEY (`picture_group_id`) REFERENCES `picture_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_reply_to` FOREIGN KEY (`replied_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friend_group`
--

DROP TABLE IF EXISTS `friend_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friend_group` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` char(64) DEFAULT NULL COMMENT '为空则标识默认分组',
  `owner_id` bigint unsigned NOT NULL COMMENT '属主用户',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_friend_group` (`name`,`owner_id`),
  KEY `fk_friend_group_owner_idx` (`owner_id`),
  CONSTRAINT `fk_friend_group_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='好友分组';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend_group`
--

LOCK TABLES `friend_group` WRITE;
/*!40000 ALTER TABLE `friend_group` DISABLE KEYS */;
INSERT INTO `friend_group` VALUES (18,'测试修改分组名',1,'2022-03-29 12:20:36','2022-03-29 12:20:36'),(19,'基友',2,'2022-03-29 12:20:36','2022-03-29 12:20:36'),(20,'基友',3,'2022-03-29 12:21:48','2022-03-29 12:21:48'),(21,'新朋',1,'2022-03-29 12:22:47','2022-03-29 12:22:47'),(22,'基友',1,'2022-03-29 12:22:47','2022-03-29 12:22:47'),(23,NULL,4,'2022-04-02 17:31:28','2022-04-02 17:31:28'),(24,NULL,7,'2022-04-02 17:53:09','2022-04-02 17:53:09'),(25,NULL,3,'2022-04-02 17:56:20','2022-04-02 17:56:20'),(26,'基友',7,'2022-04-02 17:56:20','2022-04-02 17:56:20'),(29,'hhh',4,'2022-04-03 00:25:07','2022-04-03 00:25:07'),(30,'分组测试2',7,'2022-04-03 00:25:07','2022-04-03 00:25:07'),(31,'测试233',1,'2022-04-04 22:22:39','2022-04-04 22:22:39'),(32,'好基友',3,'2022-04-04 22:22:39','2022-04-04 22:22:39'),(33,'新的分组',4,'2022-04-05 22:28:29','2022-04-05 22:28:29'),(34,'18',1,'2022-04-06 21:48:28','2022-04-06 21:48:28'),(35,'测试接收申请',22,'2022-05-18 16:09:24','2022-05-18 16:09:24'),(36,'测试申请',1,'2022-05-18 16:09:24','2022-05-18 16:09:24'),(37,'测试修改',1,'2022-05-18 16:13:39','2022-05-18 16:13:39');
/*!40000 ALTER TABLE `friend_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friendship`
--

DROP TABLE IF EXISTS `friendship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friendship` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `friend_user_id` bigint unsigned NOT NULL COMMENT '朋友',
  `friend_group_id` bigint unsigned NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_friend_group` (`friend_group_id`) /*!80000 INVISIBLE */,
  KEY `fk_friend_user_idx` (`friend_user_id`),
  CONSTRAINT `fk_friend_group` FOREIGN KEY (`friend_group_id`) REFERENCES `friend_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_friend_user` FOREIGN KEY (`friend_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='好友关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friendship`
--

LOCK TABLES `friendship` WRITE;
/*!40000 ALTER TABLE `friendship` DISABLE KEYS */;
INSERT INTO `friendship` VALUES (29,1,18,'2022-03-29 12:22:47','2022-03-29 12:22:47'),(30,1,18,'2022-03-29 12:22:47','2022-03-29 12:22:47'),(31,1,33,'2022-04-02 17:31:28','2022-04-02 17:31:28'),(32,4,37,'2022-04-02 17:31:28','2022-04-02 17:31:28'),(35,7,25,'2022-04-02 17:56:20','2022-04-02 17:56:20'),(36,3,26,'2022-04-02 17:56:20','2022-04-02 17:56:20'),(37,7,29,'2022-04-03 00:25:07','2022-04-03 00:25:07'),(38,4,30,'2022-04-03 00:25:07','2022-04-03 00:25:07'),(39,3,31,'2022-04-04 22:22:39','2022-04-04 22:22:39'),(40,1,32,'2022-04-04 22:22:39','2022-04-04 22:22:39');
/*!40000 ALTER TABLE `friendship` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `sender_id` bigint unsigned DEFAULT NULL COMMENT '引发该系统消息的用户的id。',
  `receiver_id` bigint unsigned NOT NULL COMMENT '接收者id',
  `message_type` varchar(64) NOT NULL COMMENT '消息类型',
  `message_body` varchar(1024) NOT NULL COMMENT '消息 JSON格式',
  `is_acknowledged` tinyint NOT NULL DEFAULT '0' COMMENT '用户是否查看(0: 未查看; 1:已查看)',
  `expiration_time` datetime DEFAULT NULL COMMENT '过期时间(为空则以默认过期日期处理)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用作发送时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_receiver_idx` (`receiver_id`) COMMENT '外键自动创建;用于快速查询用户所受到的消息。',
  KEY `idx_create_time` (`create_time`) COMMENT '按时间对消息排序',
  KEY `fk_sender_idx` (`sender_id`),
  CONSTRAINT `fk_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_sender` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ck_is_acknowledged` CHECK ((`is_acknowledged` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统消息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (20,3,4,'Notification','{\"notification\":\"admin2拒绝了您的好友申请\"}',0,NULL,'2022-04-05 21:42:43','2022-04-05 21:42:43'),(22,4,3,'NewFriend','{\"applicantUsername\":\"admin3\",\"applicationMessage\":\"aaaaaa\",\"friendGroupName\":\"基友\"}',0,NULL,'2022-04-07 23:00:53','2022-04-07 23:00:53'),(31,2,7,'NewFriend','{\"applicantUsername\":\"admin1\",\"applicationMessage\":\"123456\",\"friendGroupName\":\"基友\"}',0,NULL,'2022-04-08 15:00:26','2022-04-08 15:00:26'),(33,2,4,'NewFriend','{\"applicantUsername\":\"admin1\",\"applicationMessage\":\"123\",\"friendGroupName\":\"123\"}',0,NULL,'2022-04-08 15:09:42','2022-04-08 15:09:42'),(38,2,1,'NewFriend','{\"applicantUsername\":\"admin1\",\"applicationMessage\":\"\",\"friendGroupName\":\"\"}',1,NULL,'2022-04-08 15:10:34','2022-04-08 15:10:34');
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `picture_group`
--

DROP TABLE IF EXISTS `picture_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `picture_group` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `album_id` bigint unsigned NOT NULL COMMENT '所属相册',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '标题',
  `description` varchar(1000) DEFAULT NULL COMMENT '描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_album` (`album_id`),
  CONSTRAINT `fk_album` FOREIGN KEY (`album_id`) REFERENCES `album` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图片组，应至少包含一张图片';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `picture_group`
--

LOCK TABLES `picture_group` WRITE;
/*!40000 ALTER TABLE `picture_group` DISABLE KEYS */;
INSERT INTO `picture_group` VALUES (97,73,'添加',NULL,'2022-05-18 22:39:01','2022-05-18 22:39:01'),(98,73,'添加2',NULL,'2022-05-19 20:52:40','2022-05-19 20:52:40'),(99,73,'添加2',NULL,'2022-05-19 20:57:11','2022-05-19 20:57:11');
/*!40000 ALTER TABLE `picture_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `picture_identity`
--

DROP TABLE IF EXISTS `picture_identity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `picture_identity` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `uri` char(128) NOT NULL COMMENT '指向图片文件',
  `md5` binary(16) NOT NULL COMMENT '图片文件hash',
  `format` char(32) DEFAULT NULL COMMENT '图片格式',
  `ref_count` int unsigned NOT NULL DEFAULT '0' COMMENT '引用计数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uri_UNIQUE` (`uri`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标识图片文件';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `picture_identity`
--

LOCK TABLES `picture_identity` WRITE;
/*!40000 ALTER TABLE `picture_identity` DISABLE KEYS */;
INSERT INTO `picture_identity` VALUES (15,'b202dd98-9d99-42a9-ba2d-a38e9ec750f2.jpg',_binary ')|�Q\"\�\�\�� �ؗ','jpg',1,'2022-05-18 22:39:01','2022-05-18 22:39:01'),(16,'0e00ecee-1788-4c1b-a808-74625a5f8c7d.png',_binary 'X\�\�\�5�J @��i�]','png',1,'2022-05-18 22:39:01','2022-05-18 22:39:01'),(17,'5bee366b-e474-4c83-a9be-c908320a366e.jpg',_binary ' �/\�\\qn�yi\��','jpg',1,'2022-05-18 22:41:02','2022-05-18 22:41:02'),(18,'06928dfe-b0e9-489f-b169-8caf9b4a4323.jpg',_binary ' �/\�\\qn�yi\��','jpg',1,'2022-05-19 20:52:40','2022-05-19 20:52:40'),(19,'c799c8b0-7da3-4004-b5a4-5c589b478af5.png',_binary '_]��	\��n�~���n','png',1,'2022-05-19 20:52:40','2022-05-19 20:52:40'),(20,'91879945-b0db-450d-bb70-9a2689477e45.gif',_binary ']S�q�\0\� ¤\�bEA','gif',1,'2022-05-19 20:52:40','2022-05-19 20:52:40'),(21,'92f5acbd-d264-4509-810e-cdc3a7b95fb8.jpg',_binary ' �/\�\\qn�yi\��','jpg',1,'2022-05-19 20:57:11','2022-05-19 20:57:11'),(22,'5ed5b580-0184-4341-ad55-a9740ea8caf6.png',_binary '_]��	\��n�~���n','png',1,'2022-05-19 20:57:11','2022-05-19 20:57:11'),(23,'615f6010-dd1d-4768-b720-8602e1554437.gif',_binary ']S�q�\0\� ¤\�bEA','gif',1,'2022-05-19 20:57:11','2022-05-19 20:57:11');
/*!40000 ALTER TABLE `picture_identity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `picture_ref`
--

DROP TABLE IF EXISTS `picture_ref`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `picture_ref` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `picture_group_id` bigint unsigned NOT NULL COMMENT '所属图组',
  `picture_id` bigint unsigned NOT NULL COMMENT '图片',
  `sequence` int unsigned NOT NULL COMMENT '图片在组内的次序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_picture_group` (`picture_group_id`),
  KEY `fk_picture` (`picture_id`),
  CONSTRAINT `fk_picture` FOREIGN KEY (`picture_id`) REFERENCES `picture_identity` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_picture_group` FOREIGN KEY (`picture_group_id`) REFERENCES `picture_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图片引用，关联图组与图片';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `picture_ref`
--

LOCK TABLES `picture_ref` WRITE;
/*!40000 ALTER TABLE `picture_ref` DISABLE KEYS */;
INSERT INTO `picture_ref` VALUES (11,97,15,1,'2022-05-18 22:39:01','2022-05-18 22:39:01'),(12,97,16,2,'2022-05-18 22:39:01','2022-05-18 22:39:01'),(13,97,17,0,'2022-05-18 22:41:02','2022-05-18 22:41:02'),(14,98,18,0,'2022-05-19 20:52:40','2022-05-19 20:52:40'),(15,98,19,1,'2022-05-19 20:52:40','2022-05-19 20:52:40'),(16,98,20,2,'2022-05-19 20:52:40','2022-05-19 20:52:40'),(17,99,21,0,'2022-05-19 20:57:11','2022-05-19 20:57:11'),(18,99,22,1,'2022-05-19 20:57:11','2022-05-19 20:57:11'),(19,99,23,2,'2022-05-19 20:57:11','2022-05-19 20:57:11');
/*!40000 ALTER TABLE `picture_ref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` char(32) NOT NULL COMMENT '角色名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'admin','2022-01-20 16:39:29','2022-01-20 16:39:29'),(2,'normal','2022-01-20 16:39:49','2022-01-20 16:39:49');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `username` char(32) NOT NULL COMMENT '用户名',
  `password` char(128) NOT NULL COMMENT '密码',
  `role_id` int unsigned NOT NULL DEFAULT '0' COMMENT '角色',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `avatar` blob COMMENT '头像',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  KEY `fk_role_idx` (`role_id`),
  CONSTRAINT `fk_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','123456',1,'2022-01-18 13:19:28','2022-01-18 13:19:28',NULL),(2,'admin1','123456',1,'2022-02-07 20:30:14','2022-02-07 20:30:14',NULL),(3,'admin2','123456',1,'2022-01-18 13:19:40','2022-01-18 13:19:40',NULL),(4,'admin3','123456',1,'2022-01-18 13:19:43','2022-01-18 13:19:43',NULL),(5,'testUser1','123456',2,'2022-01-20 21:54:25','2022-01-20 21:54:25',NULL),(7,'test123','123456',2,'2022-01-20 22:47:42','2022-01-20 22:47:42',NULL),(14,'test666','123456',2,'2022-01-20 22:57:48','2022-01-20 22:57:48',NULL),(16,'test555','123456',2,'2022-01-20 23:11:14','2022-01-20 23:11:14',NULL),(17,'test222','123456',2,'2022-01-20 23:12:19','2022-01-20 23:12:19',NULL),(18,'qiao','123456',2,'2022-03-02 15:10:17','2022-03-02 15:10:17',NULL),(20,'test456','123456',2,'2022-03-04 16:45:32','2022-03-04 16:45:32',NULL),(22,'test1','12345678',2,'2022-05-18 15:45:57','2022-05-18 15:45:57',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-05-19 21:13:19
