use pimcs;
SET foreign_key_checks = 0;
drop table question;
-- -----------------------------------------------------
-- Table `pimcs`.`question`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`question` (
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
    REFERENCES `pimcs`.`user` (`email`)
    ON DELETE cascade
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_question_company1`
    FOREIGN KEY (`companyId`)
    REFERENCES `pimcs`.`company` (`id`)
    ON DELETE cascade
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;

SET foreign_key_checks = 1;
-- -----------------------------------------------------
-- Table `pimcs`.`answer`
-- -----------------------------------------------------