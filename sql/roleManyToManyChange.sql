USE `pimcs` ;

-- -----------------------------------------------------
-- Table `test`.`role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`role` (
    `name` VARCHAR(40) NOT NULL,
    PRIMARY KEY (`name`));





-- -----------------------------------------------------
-- Table `test`.`user_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`user_role` (
                                                   `id` INT NOT NULL AUTO_INCREMENT,
                                                   `role_name` VARCHAR(40) NOT NULL,
    `user_email` VARCHAR(40) NOT NULL,
    PRIMARY KEY (`id`, `user_email`),
    INDEX `fk_table1_role_idx` (`role_name` ASC) VISIBLE,
    INDEX `fk_table1_user1_idx` (`user_email` ASC) VISIBLE,
    CONSTRAINT `fk_table1_role`
    FOREIGN KEY (`role_name`)
    REFERENCES `pimcs`.`role` (`name`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_table1_user1`
    FOREIGN KEY (`user_email`)
    REFERENCES `pimcs`.`user` (`email`)
    ON DELETE NO ACTION);
