drop table if exists pimcs.;
drop table if exists pimcs.role;
-- -----------------------------------------------------
-- Table `test`.`role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pimcs`.`role` (
    `name` VARCHAR(40) NOT NULL,
    PRIMARY KEY (`name`));


INSERT INTO pimcs.role VALUES("User");
INSERT INTO pimcs.role VALUES("UserManagement");
INSERT INTO pimcs.role VALUES("MatManagement");
INSERT INTO pimcs.role VALUES("CategoryManagement");


-- -----------------------------------------------------
-- Table `test`.``

CREATE TABLE IF NOT EXISTS `pimcs`.`` (
   `id` INT NOT NULL AUTO_INCREMENT,
   `role_name` VARCHAR(40) NOT NULL,
    `user_email` VARCHAR(40) NOT NULL,
    PRIMARY KEY (`id`, `user_email`),
    INDEX `fk_table1_role_idx` (`role_name` ASC) VISIBLE,
    INDEX `fk_table1_user1_idx` (`user_email` ASC) VISIBLE,
    CONSTRAINT `fk_table1_role`
    FOREIGN KEY (`role_name`)
    REFERENCES `pimcs`.`role` (`name`)
    ON DELETE cascade
    ON UPDATE cascade,
    CONSTRAINT `fk_table1_user1`
    FOREIGN KEY (`user_email`)
    REFERENCES `pimcs`.`user` (`email`)
    ON DELETE cascade);