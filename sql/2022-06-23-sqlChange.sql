-- MySQL Workbench Forward Engineering

drop database if exists pimcs;
create database pimcs;

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema pimcs
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema pimcs
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `pimcs` DEFAULT CHARACTER SET utf8 ;
USE `pimcs` ;

-- -----------------------------------------------------
-- Table `pimcs`.`business_category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`BusinessCategory` (
                                                          `id` INT NOT NULL AUTO_INCREMENT,
                                                          `categoryName` VARCHAR(30) NOT NULL,
                                                          PRIMARY KEY (`id`))
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`company`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`Company` (
                                                 `id` INT NOT NULL AUTO_INCREMENT,
                                                 `companyCode` VARCHAR(30) NOT NULL,
                                                 `businessCategoryId` INT  NULL,
                                                 `companyName` VARCHAR(45) NULL,
                                                 `companyAddress` VARCHAR(60) NULL,
                                                 `contactPhone` varchar(20) NULL,
                                                 `createdAt` DATETIME NULL,
                                                 `ceoEmail` VARCHAR(60) NULL,
                                                 `ceoName` VARCHAR(60) NULL,
                                                 PRIMARY KEY (`id`),
                                                 INDEX `fk_company_business_category1_idx` (`businessCategoryId` ASC) VISIBLE,
                                                 CONSTRAINT `fk_company_business_category1`
                                                     FOREIGN KEY (`businessCategoryId`)
                                                         REFERENCES `pimcs`.`BusinessCategory` (`id`)
                                                         ON DELETE set null)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`User` (
                                              `email` VARCHAR(60) NOT NULL,
                                              `companyId` INT NOT NULL,
                                              `password` VARCHAR(100) NULL,
                                              `name` VARCHAR(30) NULL,
                                              `phone` CHAR(11) NULL,
                                              `department` VARCHAR(45) NULL,
                                              `createdAt` DATETIME NULL,
                                              `enabled` TINYINT(1) NULL,
                                              `updatedate` DATETIME NULL,
                                              PRIMARY KEY (`email`),
                                              INDEX `fk_user_company1_idx` (`companyId` ASC) VISIBLE,
                                              CONSTRAINT `fk_user_company1`
                                                  FOREIGN KEY (`companyId`)
                                                      REFERENCES `pimcs`.`Company` (`id`)
                                                      ON DELETE cascade)
    ENGINE = InnoDB;
-- -----------------------------------------------------
-- Table `pimcs`.`product_category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`ProductCategory` (
                                                         `id` INT NOT NULL AUTO_INCREMENT,
                                                         `categoryName` VARCHAR(45) NULL,
                                                         `companyId` INT NOT NULL,
                                                         PRIMARY KEY (`id`),
                                                         INDEX `fk_product_category_company1_idx` (`companyId` ASC) VISIBLE,
                                                         CONSTRAINT `fk_product_category_company1`
                                                             FOREIGN KEY (`companyId`)
                                                                 REFERENCES `pimcs`.`Company` (`id`)
                                                                 ON DELETE cascade)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`product`
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `pimcs`.`Product` (
                                                 `id` INT NOT NULL AUTO_INCREMENT,
                                                 `productCode` VARCHAR(45) NOT NULL,
                                                 `productCategoryId` INT  NULL,
                                                 `companyId` INT NOT NULL,
                                                 `productImage` VARCHAR(255) NULL,
                                                 `productWeight` INT NULL,
                                                 `productName` VARCHAR(45) NULL,
                                                 `createdAt` datetime NULL,
                                                 `updatedate` datetime NUll,
                                                 PRIMARY KEY (`id`),
                                                 INDEX `fk_product_product_category1_idx` (`productCategoryId` ASC) VISIBLE,
                                                 INDEX `fk_product_company1_idx` (`companyId` ASC) VISIBLE,
                                                 CONSTRAINT `fk_product_product_category1`
                                                     FOREIGN KEY (`productCategoryId`)
                                                         REFERENCES `pimcs`.`ProductCategory` (`id`)
                                                         ON DELETE set null,
                                                 CONSTRAINT `fk_product_company1`
                                                     FOREIGN KEY (`companyId`)
                                                         REFERENCES `pimcs`.`Company` (`id`)
                                                         ON DELETE cascade)
    ENGINE = InnoDB;



-- -----------------------------------------------------
-- Table `pimcs`.`mat`
-- -----------------------------------------------------

-- set FOREIGN_KEY_CHECKS = 1;
-- drop table mat;
CREATE TABLE IF NOT EXISTS `pimcs`.`Mat` (
                                             `id` INT NOT NULL AUTO_INCREMENT,
                                             `productId` INT  NULL,
                                             `companyId` INT NOT NULL,
                                             `serialNumber` VARCHAR(255) NOT NULL,
                                             `calcMethod` TINYINT(1) NULL,
                                             `threshold` INT NULL,
                                             `inventoryWeight` INT NULL,
                                             `recentlyNoticeDate` DATETIME NULL,
                                             `isSendEmail` TINYINT(1) NULL,
                                             `matLocation` VARCHAR(45) NULL,
                                             `productOrderCnt` INT NULL,
                                             `boxWeight` INT NULL,
                                             `battery` INT NULL,
                                             `communicationStatus` INT default 1,
                                             `createdAt` DATETIME NULL,
                                             `updatedate` DATETIME NULL,

                                             PRIMARY KEY (`id`),
                                             UNIQUE INDEX `serial_number_UNIQUE` (`serialNumber` ASC) VISIBLE,
                                             INDEX `fk_mat_product1_idx` (`productId` ASC) VISIBLE,
                                             INDEX `fk_mat_company1_idx` (`companyId` ASC) VISIBLE,
                                             CONSTRAINT `fk_mat_product1`
                                                 FOREIGN KEY (`productId`)
                                                     REFERENCES `pimcs`.`Product` (`id`)
                                                     ON DELETE set null
                                                     ON UPDATE NO ACTION,
                                             CONSTRAINT `fk_mat_company1`
                                                 FOREIGN KEY (`companyId`)
                                                     REFERENCES `pimcs`.`Company` (`id`)
                                                     ON DELETE cascade
                                                     ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`notice_email`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`NoticeEmail` (
                                                     `id` INT NOT NULL AUTO_INCREMENT,
                                                     `email` VARCHAR(45) NOT NULL,
                                                     `matId` INT NOT NULL,
                                                     PRIMARY KEY (`id`),
                                                     UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
                                                     INDEX `fk_notice_email_mat1_idx` (`matId` ASC) VISIBLE,
                                                     CONSTRAINT `fk_notice_email_mat1`
                                                         FOREIGN KEY (`matId`)
                                                             REFERENCES `pimcs`.`Mat` (`id`)
                                                             ON DELETE NO ACTION
                                                             ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`mat_category`
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `pimcs`.`MatCategory` (
                                                     `id` INT NOT NULL AUTO_INCREMENT,
                                                     `matCategoryName` VARCHAR(45) NOT NULL,
                                                     `mappingSerialCode` VARCHAR(45) NULL,
                                                     `matPrice` INT NULL,
                                                     `matInformation` BLOB NULL,
                                                     `maxWeight` INT NULL,
                                                     PRIMARY KEY (`id`),
                                                     UNIQUE INDEX `mat_category_UNIQUE` (`matCategoryName` ASC) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`mat_device`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`MatDevice` (
                                                   `id` INT NOT NULL AUTO_INCREMENT,
                                                   `serialNumber` VARCHAR(40) NOT NULL,
                                                   `matCategory` VARCHAR(45)  NULL,
                                                   PRIMARY KEY (`id`),
                                                   INDEX `fk_mat_device_mat_category1_idx` (`matCategory` ASC) VISIBLE,
                                                   UNIQUE INDEX `serial_number_UNIQUE` (`serialNumber` ASC) VISIBLE,
                                                   CONSTRAINT `fk_mat_device_mat_category1`
                                                       FOREIGN KEY (`matCategory`)
                                                           REFERENCES `pimcs`.`MatCategory` (`matCategoryName`)
                                                           ON DELETE set null
                                                           ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------

-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS `pimcs`.`MatOrder` (
                                                  `id` INT NOT NULL AUTO_INCREMENT,
                                                  `userEmail` VARCHAR(60) NOT NULL,
                                                  `companyId` INT NULL,
                                                  `totalCnt` Int NULL,
                                                  `totalPrice` Int NULL,
                                                  `deliveryAddress` VARCHAR(60) NULL,
                                                  `postCode` VARCHAR(40) NULL,
                                                  `depositStatus` INT NULL,
                                                  `hopeDeliveryDate` DATETIME NULL,
                                                  `depositerName` VARCHAR(45) NULL,
                                                  `createdAt` DATETIME NULL,
                                                  `deliveryStatus` INT NULL,
                                                  `deliveryCode` VARCHAR(45) NULL,
                                                  PRIMARY KEY (`id`),
                                                  INDEX `fk_matOrder_user1_idx` (`userEmail` ASC) VISIBLE,
                                                  INDEX `fk_matOrder_company1_idx` (`companyId` ASC) VISIBLE,
                                                  CONSTRAINT `fk_matOrder_user1`
                                                      FOREIGN KEY (`userEmail`)
                                                          REFERENCES `pimcs`.`User` (`email`)
                                                          ON DELETE CASCADE
                                                          ON UPDATE NO ACTION,
                                                  CONSTRAINT `fk_matOrder_company1`
                                                      FOREIGN KEY (`companyId`)
                                                          REFERENCES `pimcs`.`Company` (`id`)
                                                          ON DELETE set null
                                                          ON UPDATE NO ACTION)
    ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `pimcs`.`mat_category_order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`MatCategoryOrder` (
                                                          `id` INT NOT NULL AUTO_INCREMENT,
                                                          `orderId` INT NOT NULL,
                                                          `orderCnt` INT NOT NULL,
                                                          `pricePerDevice` INT NOT NULL,
                                                          `matCategoryName` VARCHAR(20) NOT NULL,
                                                          INDEX `fk_mat_device_order_order1_idx` (`orderId` ASC) VISIBLE,
                                                          PRIMARY KEY (`id`),
                                                          CONSTRAINT `fk_mat_device_order_order1`
                                                              FOREIGN KEY (`orderId`)
                                                                  REFERENCES `pimcs`.`MatOrder` (`id`)
                                                                  ON DELETE cascade
                                                                  ON UPDATE NO ACTION
                                                          )
    ENGINE = InnoDB;





-- -----------------------------------------------------
-- Table `pimcs`.`question`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`Question` (
                                                  `id` INT NOT NULL AUTO_INCREMENT,
                                                  `userEmail` VARCHAR(60) NOT NULL,
                                                  `companyId` INT NOT NULL,
                                                  `isSecret` TINYINT(1) NULL,
                                                  `title` VARCHAR(200) NULL,
                                                  `content` BLOB NULL,
                                                  `createdAt` DATETIME NULL,
                                                  PRIMARY KEY (`id`),
                                                  INDEX `fk_question_user1_idx` (`userEmail` ASC) VISIBLE,
                                                  INDEX `fk_question_company1_idx` (`companyId` ASC) VISIBLE,
                                                  CONSTRAINT `fk_question_user1`
                                                      FOREIGN KEY (`userEmail`)
                                                          REFERENCES `pimcs`.`User` (`email`)
                                                          ON DELETE cascade
                                                          ON UPDATE NO ACTION,
                                                  CONSTRAINT `fk_question_company1`
                                                      FOREIGN KEY (`companyId`)
                                                          REFERENCES `pimcs`.`Company` (`id`)
                                                          ON DELETE cascade
                                                          ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`answer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`Answer` (
                                                `id` INT NOT NULL AUTO_INCREMENT,
                                                `comment` BLOB NULL,
                                                `createdAt` DATETIME NULL,
                                                `questionId` INT NOT NULL,
                                                PRIMARY KEY (`id`),
                                                INDEX `fk_answer_question1_idx` (`questionId` ASC) VISIBLE,
                                                CONSTRAINT `fk_answer_question1`
                                                    FOREIGN KEY (`questionId`)
                                                        REFERENCES `pimcs`.`Question` (`id`)
                                                        ON DELETE cascade
                                                        ON UPDATE NO ACTION)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`Role` (
                                              `id` INT NOT NULL AUTO_INCREMENT,
                                              `name` VARCHAR(45) NULL,
                                              PRIMARY KEY (`id`),
                                              UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.``
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`UserRole` (
                                                  `id` INT NOT NULL AUTO_INCREMENT,
                                                  `userEmail` VARCHAR(60) NOT NULL,
                                                  `roleId` INT NOT NULL,
                                                  PRIMARY KEY (`id`, `roleId`),
                                                  INDEX `fk_user_role_user1_idx` (`userEmail` ASC) VISIBLE,
                                                  INDEX `fk_user_role_role1_idx` (`roleId` ASC) VISIBLE,
                                                  CONSTRAINT `fk_user_role_user1`
                                                      FOREIGN KEY (`userEmail`)
                                                          REFERENCES `pimcs`.`User` (`email`)
                                                          ON DELETE cascade
                                                          ON UPDATE NO ACTION,
                                                  CONSTRAINT `fk_user_role_role1`
                                                      FOREIGN KEY (`roleId`)
                                                          REFERENCES `pimcs`.`Role` (`id`)
                                                          ON DELETE cascade
                                                          ON UPDATE NO ACTION)
    ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `pimcs`.`OrderMailFrame` (
                                                        `id` INT NOT NULL AUTO_INCREMENT,
                                                        `greeting` BLOB NULL,
                                                        `managerInfo` BLOB NULL,
                                                        `createdAt` DATETIME NULL,
                                                        PRIMARY KEY (`id`))
    ENGINE = InnoDB;

-- 기기 소유확인 테이블
CREATE TABLE IF NOT EXISTS `pimcs`.`OwnDevice` (
                                                         `id` INT NOT NULL AUTO_INCREMENT,
                                                         `createdAt` DATETIME NULL,
                                                         `serialNumber` VARCHAR(45) NULL,
                                                         `companyId` INT NOT NULL,
                                                         PRIMARY KEY (`id`),
                                                         INDEX `fk_ownDevice_company1_idx` (`companyId` ASC) VISIBLE,
                                                         INDEX `fk_ownDevice_serialNumber_company1_idx` (`serialNumber` ASC) VISIBLE,
                                                         CONSTRAINT `fk_ownDevice_company1`
                                                             FOREIGN KEY (`companyId`)
                                                                 REFERENCES `pimcs`.`Company` (`id`)
                                                                 ON DELETE cascade)

    ENGINE = InnoDB;
CREATE TABLE IF NOT EXISTS `pimcs`.`SendHistory` (
                                                         `id` INT NOT NULL AUTO_INCREMENT,
                                                         `createdAt` DATETIME NULL,
                                                         `history` BLOB NULL,
                                                         `matOrderId` INT NOT NULL,
                                                         PRIMARY KEY (`id`),
                                                         INDEX `fk_matOrderId_company1_idx` (`matOrderId` ASC) VISIBLE,
                                                         CONSTRAINT `fk_matOrderId_company1`
                                                             FOREIGN KEY (`matOrderId`)
                                                                 REFERENCES `pimcs`.`MatOrder` (`id`)
                                                                 ON DELETE cascade)

    ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;