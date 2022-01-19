-- MySQL Workbench Forward Engineering
create database if not exists pimcs;

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
-- -----------------------------------------------------
-- Schema pimcs
-- -----------------------------------------------------
USE `pimcs` ;

-- -----------------------------------------------------
-- Table `pimcs`.`business_category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`business_category` (
  `category_name` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`category_name`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`company`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`company` (
  `company_code` VARCHAR(30) NOT NULL,
  `business_category_name` VARCHAR(30) NOT NULL,
  `company_name` VARCHAR(45) NULL,
  `company_address` VARCHAR(60) NULL,
  `createat` DATETIME NULL,
  `contact_phone` varchar(20) NULL,
  `ceo_email` VARCHAR(60) NULL,
  PRIMARY KEY (`company_code`),
  INDEX `fk_company_business_category1_idx` (`business_category_name` ASC) ,
  CONSTRAINT `fk_company_business_category1`
    FOREIGN KEY (`business_category_name`)
    REFERENCES `pimcs`.`business_category` (`category_name`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`user` (
  `email` VARCHAR(60) NOT NULL,
  `company_code` VARCHAR(30) NOT NULL,
  `password` VARCHAR(100) NULL,
  `name` VARCHAR(30) NULL,
  `phone` CHAR(11) NULL,
  `department` VARCHAR(45) NULL,
  `creatat` DATETIME NULL,
  `updatedate` DATETIME NULL,
  PRIMARY KEY (`email`),
  INDEX `fk_user_company_idx` (`company_code` ASC),
  CONSTRAINT `fk_user_company`
    FOREIGN KEY (`company_code`)
    REFERENCES `pimcs`.`company` (`company_code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`role` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_email` VARCHAR(60) NOT NULL ,
  `user_management` TINYINT(1) NULL ,
  `mat_management` TINYINT(1) NULL ,
  `category_management` TINYINT(1) NULL ,
  PRIMARY KEY (`id`),
  INDEX `fk_role_user1_idx` (`user_email` ASC) VISIBLE,
  CONSTRAINT `fk_role_user1`
    FOREIGN KEY (`user_email`)
    REFERENCES `pimcs`.`user` (`email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`product_category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`product_category` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `category_name` VARCHAR(45) NULL,
  `company_code` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_product_category_company1_idx` (`company_code` ASC) ,
  CONSTRAINT `fk_product_category_company1`
    FOREIGN KEY (`company_code`)
    REFERENCES `pimcs`.`company` (`company_code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`product` (
  `prod_code` VARCHAR(45) NOT NULL ,
  `company_code` VARCHAR(30) NOT NULL,
  `creatat` DATETIME NULL,
  `product_category_id` INT NOT NULL,
  `prod_image` VARCHAR(100) NULL,
  `prod_weight` INT NULL,
  `prod_name` VARCHAR(45) NULL,
  PRIMARY KEY (`prod_code`),
  INDEX `fk_product_company1_idx` (`company_code` ASC) ,
  INDEX `fk_product_product_category1_idx` (`product_category_id` ASC) ,
  CONSTRAINT `fk_product_company1`
    FOREIGN KEY (`company_code`)
    REFERENCES `pimcs`.`company` (`company_code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_product_product_category1`
    FOREIGN KEY (`product_category_id`)
    REFERENCES `pimcs`.`product_category` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`mat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`mat` (
  `serial_number` VARCHAR(40) NOT NULL,
  `prod_code` VARCHAR(45) NOT NULL,
  `creatat` DATETIME NULL,
  `calc_method` TINYINT(1) NULL ,
  `threshold` INT NULL ,
  `inventory_weight` INT NULL ,
  `recently_notice_date` DATETIME NULL ,
  `is_send_email` TINYINT(1) NULL ,
  `mat_location` VARCHAR(45) NULL ,
  `product_order_cnt` INT NULL ,
  `box_weight` INT NULL,
  `company_code` VARCHAR(30) NOT NULL,
  `battery` INT NULL,
  PRIMARY KEY (`serial_number`),
  INDEX `fk_mat_product1_idx` (`prod_code` ASC) VISIBLE,
  INDEX `fk_mat_company1_idx` (`company_code` ASC) VISIBLE,
  CONSTRAINT `fk_mat_product1`
    FOREIGN KEY (`prod_code`)
    REFERENCES `pimcs`.`product` (`prod_code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_mat_company1`
    FOREIGN KEY (`company_code`)
    REFERENCES `pimcs`.`company` (`company_code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`notice_email`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`notice_email` (
  `email` INT NOT NULL,
  `mat_serial_number` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`email`),
  INDEX `fk_notice_email_mat1_idx` (`mat_serial_number` ASC) ,
  CONSTRAINT `fk_notice_email_mat1`
    FOREIGN KEY (`mat_serial_number`)
    REFERENCES `pimcs`.`mat` (`serial_number`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`mat_category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`mat_category` (
  `mat_category` VARCHAR(45) NOT NULL,
  `mat_price` INT NULL,
  `mat_infomation` BLOB NULL,
  `max_weight` INT NULL,
  PRIMARY KEY (`mat_category`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`mat_device`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`mat_device` (
  `serial_number` VARCHAR(40) NOT NULL,
  `mat_category` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`serial_number`),
  INDEX `fk_mat_device_mat_category1_idx` (`mat_category` ASC) ,
  CONSTRAINT `fk_mat_device_mat_category1`
    FOREIGN KEY (`mat_category`)
    REFERENCES `pimcs`.`mat_category` (`mat_category`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`mat_order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`mat_order` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(60) NOT NULL,
  `delivery_address` VARCHAR(60) NULL,
  `order_phone`  CHAR(11) NULL,
  `hope_delivery_date` DATETIME NULL,
  `username` VARCHAR(45) NULL,
  `createat` DATETIME NULL,
  `deposit_status` INT NULL,
  `delivery_status` INT NULL,
  `delivery_code` VARCHAR(45) NULL,
  `deposit_name` varchar(30) not null,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_mat_order_user2`
    FOREIGN KEY (`email`)
    REFERENCES `pimcs`.`user` (`email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
  )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`mat_category_order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`mat_category_order` (
  `id` INT NOT NULL,
  `order_id` INT NULL,
  `mat_category_mat_category` VARCHAR(45) NULL,
  `order_cnt` INT NULL,
  INDEX `fk_mat_device_order_order1_idx` (`order_id` ASC) ,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_mat_device_order_order1`
    FOREIGN KEY (`order_id`)
    REFERENCES `pimcs`.`order` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_mat_device_order_mat_category1`
    FOREIGN KEY (`mat_category_mat_category`)
    REFERENCES `pimcs`.`mat_category` (`mat_category`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;




-- -----------------------------------------------------
-- Table `pimcs`.`question`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`question` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_email` VARCHAR(60) NOT NULL,
  `isSecret` TINYINT(1) NULL,
  `title` VARCHAR(200) NULL,
  `question` BLOB NULL,
  `createat` DATETIME NULL,
  `company_company_code` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_question_user1_idx` (`user_email` ASC) ,
  INDEX `fk_question_company1_idx` (`company_company_code` ASC) ,
  CONSTRAINT `fk_question_user1`
    FOREIGN KEY (`user_email`)
    REFERENCES `pimcs`.`user` (`email`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_question_company1`
    FOREIGN KEY (`company_company_code`)
    REFERENCES `pimcs`.`company` (`company_code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `pimcs`.`answer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`answer` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `answer` BLOB NULL,
  `creatat` DATETIME NULL,
  `question_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_answer_question1_idx` (`question_id` ASC) ,
  CONSTRAINT `fk_answer_question1`
    FOREIGN KEY (`question_id`)
    REFERENCES `pimcs`.`question` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
