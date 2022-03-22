
# select * from INFORMATION_SCHEMA.TABLE_CONSTRAINTS where CONSTRAINT_TYPE = 'FOREIGN KEY';

use pimcs;
SET foreign_key_checks = 0;
drop table answer;
-- -----------------------------------------------------
-- Table `pimcs`.`answer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`answer` (
                                                `id` INT NOT NULL AUTO_INCREMENT,
                                                `comment` BLOB NULL,
                                                `createdAt` DATETIME NULL,
                                                `questionId` INT NOT NULL,
                                                PRIMARY KEY (`id`),

    INDEX `fk_answer_question1_idx` (`questionId` ASC) VISIBLE,

    CONSTRAINT `fk_answer_question1`
    FOREIGN KEY (`questionId`)
    REFERENCES `pimcs`.`question` (`id`)
    ON DELETE cascade
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;

SET foreign_key_checks = 1;
-- -----------------------------------------------------
-- Table `pimcs`.`answer`
-- -----------------------------------------------------