/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50639
 Source Host           : localhost:3306
 Source Schema         : lkf_mybatis_db

 Target Server Type    : MySQL
 Target Server Version : 50639
 File Encoding         : 65001

 Date: 02/08/2018 09:12:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for author
-- ----------------------------
DROP TABLE IF EXISTS `author`;
CREATE TABLE `author` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `USERNAME` varchar(200) DEFAULT NULL,
  `PASSWORD` varchar(200) DEFAULT NULL,
  `EMAIL` varchar(200) DEFAULT NULL,
  `BIO` varchar(200) DEFAULT NULL,
  `FAVOURITE_SECTION` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `AUTHOR_INDEX` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of author
-- ----------------------------
BEGIN;
INSERT INTO `author` VALUES (1, 'alien', 'alien', '461857202@qq.com', NULL, 'java io');
COMMIT;

-- ----------------------------
-- Table structure for blog
-- ----------------------------
DROP TABLE IF EXISTS `blog`;
CREATE TABLE `blog` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `TITLE` varchar(200) DEFAULT NULL,
  `AUTHOR_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `BLOG_INDEX` (`ID`),
  KEY `BLOG_AUTHOR_FG` (`AUTHOR_ID`),
  CONSTRAINT `BLOG_AUTHOR_FG` FOREIGN KEY (`AUTHOR_ID`) REFERENCES `author` (`ID`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of blog
-- ----------------------------
BEGIN;
INSERT INTO `blog` VALUES (1, 'Mybatis tutorial', 1);
COMMIT;

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `BLOG_ID` int(11) DEFAULT NULL,
  `AUTHOR_ID` int(11) DEFAULT NULL,
  `CREATED_ON` date DEFAULT NULL,
  `SECTION` varchar(200) DEFAULT NULL,
  `SUBJECT` varchar(200) DEFAULT NULL,
  `DRAFT` varchar(200) DEFAULT NULL,
  `BODY` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `POST_INDEX` (`ID`),
  KEY `POST_BLOG_FG` (`BLOG_ID`),
  CONSTRAINT `POST_BLOG_FG` FOREIGN KEY (`BLOG_ID`) REFERENCES `blog` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of post
-- ----------------------------
BEGIN;
INSERT INTO `post` VALUES (1, 1, 1, '2015-05-16', 'Mybatis introduction', 'Mybatis', 'Mybatis series draft', 'How to lean mybatis ?');
COMMIT;

-- ----------------------------
-- Table structure for post_comment
-- ----------------------------
DROP TABLE IF EXISTS `post_comment`;
CREATE TABLE `post_comment` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `POST_ID` int(11) DEFAULT NULL,
  `NAME` varchar(200) DEFAULT NULL,
  `COMMENT_TEXT` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `POST_COMMENT_INDEX` (`ID`),
  KEY `POST_COMMENT_POST_FG` (`POST_ID`),
  CONSTRAINT `POST_COMMENT_POST_FG` FOREIGN KEY (`POST_ID`) REFERENCES `post` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of post_comment
-- ----------------------------
BEGIN;
INSERT INTO `post_comment` VALUES (1, 1, 'comment', 'Keep updating');
COMMIT;

-- ----------------------------
-- Table structure for post_tag
-- ----------------------------
DROP TABLE IF EXISTS `post_tag`;
CREATE TABLE `post_tag` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `POST_ID` int(11) NOT NULL,
  `TAG_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `POST_TAG_INDEX` (`ID`),
  KEY `POST_TAG_INDEX2` (`POST_ID`),
  KEY `POST_TAG_INDEX3` (`TAG_ID`),
  CONSTRAINT `POST_TAG_POST` FOREIGN KEY (`POST_ID`) REFERENCES `post` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `POST_TAG_TAG` FOREIGN KEY (`TAG_ID`) REFERENCES `tag` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of post_tag
-- ----------------------------
BEGIN;
INSERT INTO `post_tag` VALUES (1, 1, 1);
INSERT INTO `post_tag` VALUES (2, 1, 2);
INSERT INTO `post_tag` VALUES (3, 1, 5);
COMMIT;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tag
-- ----------------------------
BEGIN;
INSERT INTO `tag` VALUES (1, 'Mybatis');
INSERT INTO `tag` VALUES (2, 'Java');
INSERT INTO `tag` VALUES (3, 'JavaScript');
INSERT INTO `tag` VALUES (4, 'Web');
INSERT INTO `tag` VALUES (5, 'ORM framework');
INSERT INTO `tag` VALUES (6, NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
