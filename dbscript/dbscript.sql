-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema test
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema test
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `test` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `test` ;

-- -----------------------------------------------------
-- Table `test`.`Category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`Category` (
  `eventCategoryId` INT NOT NULL AUTO_INCREMENT,
  `eventCategoryName` VARCHAR(100) CHARACTER SET 'utf8mb3' NOT NULL,
  `eventCategoryDescription` VARCHAR(500) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `eventCategoryDuration` INT NOT NULL,
  PRIMARY KEY (`eventCategoryId`),
  UNIQUE INDEX `eventCatagoryName_UNIQUE` (`eventCategoryName` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `test`.`Event`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`Event` (
  `eventId` INT NOT NULL AUTO_INCREMENT,
  `bookingName` VARCHAR(100) CHARACTER SET 'utf8mb3' NOT NULL,
  `bookingEmail` VARCHAR(100) CHARACTER SET 'utf8mb3' NOT NULL,
  `eventCategoryId` INT NOT NULL,
  `eventStartTime` DATETIME NOT NULL,
  `eventDuration` INT NOT NULL,
  `eventNotes` VARCHAR(500) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  PRIMARY KEY (`eventId`),
  INDEX `fk_AppoachTime_Catagory1_idx` (`eventCategoryId` ASC) VISIBLE,
  CONSTRAINT `fk_AppoachTime_Catagory1`
    FOREIGN KEY (`eventCategoryId`)
    REFERENCES `test`.`Category` (`eventCategoryId`))
ENGINE = InnoDB
AUTO_INCREMENT = 163
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `test`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(50) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `role` VARCHAR(10) NOT NULL,
  `createdOn` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedOn` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UNIQUE` (`name` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 121
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `test`.`Lecturer_Mapping`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `test`.`Lecturer_Mapping` (
  `id` VARCHAR(45) NULL,
  `eventId` INT NOT NULL,
  `userId` INT NOT NULL,
  INDEX `fk_Event_has_user_user1_idx` (`userId` ASC) VISIBLE,
  INDEX `fk_Event_has_user_Event1_idx` (`eventId` ASC) VISIBLE,
  CONSTRAINT `fk_Event_has_user_Event1`
    FOREIGN KEY (`eventId`)
    REFERENCES `test`.`Event` (`eventId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Event_has_user_user1`
    FOREIGN KEY (`userId`)
    REFERENCES `test`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
