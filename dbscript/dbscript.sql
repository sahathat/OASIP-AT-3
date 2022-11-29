-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema booking
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema booking
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `booking` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `booking` ;

-- -----------------------------------------------------
-- Table `booking`.`Category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booking`.`Category` (
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
-- Table `booking`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booking`.`user` (
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
AUTO_INCREMENT = 137
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `booking`.`Confirm_User`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booking`.`Confirm_User` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `token` VARCHAR(100) NOT NULL,
  `expireDate` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `userId` INT NOT NULL,
  `role` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `token_UNIQUE` (`token` ASC) VISIBLE,
  UNIQUE INDEX `userId_UNIQUE` (`userId` ASC) VISIBLE,
  INDEX `fk_Confirm_User_user_idx` (`userId` ASC) VISIBLE,
  CONSTRAINT `fk_Confirm_User_user`
    FOREIGN KEY (`userId`)
    REFERENCES `booking`.`user` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `booking`.`Event`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booking`.`Event` (
  `eventId` INT NOT NULL AUTO_INCREMENT,
  `bookingName` VARCHAR(100) CHARACTER SET 'utf8mb3' NOT NULL,
  `bookingEmail` VARCHAR(100) CHARACTER SET 'utf8mb3' NOT NULL,
  `eventCategoryId` INT NOT NULL,
  `eventStartTime` DATETIME NOT NULL,
  `eventDuration` INT NOT NULL,
  `eventNotes` VARCHAR(500) CHARACTER SET 'utf8mb3' NULL DEFAULT NULL,
  `eventFile` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`eventId`),
  INDEX `fk_AppoachTime_Catagory1_idx` (`eventCategoryId` ASC) VISIBLE,
  CONSTRAINT `fk_AppoachTime_Catagory1`
    FOREIGN KEY (`eventCategoryId`)
    REFERENCES `booking`.`Category` (`eventCategoryId`))
ENGINE = InnoDB
AUTO_INCREMENT = 197
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `booking`.`Lecturer_Mapping`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booking`.`Lecturer_Mapping` (
  `mappingId` INT NOT NULL AUTO_INCREMENT,
  `lecturerEmail` VARCHAR(50) NOT NULL,
  `categoryId` INT NOT NULL,
  PRIMARY KEY (`lecturerEmail`, `categoryId`),
  UNIQUE INDEX `id_UNIQUE` (`mappingId` ASC) VISIBLE,
  INDEX `fk_Lecturer_Mapping_Category1_idx` (`categoryId` ASC) VISIBLE,
  CONSTRAINT `fk_Lecturer_Mapping_Category1`
    FOREIGN KEY (`categoryId`)
    REFERENCES `booking`.`Category` (`eventCategoryId`))
ENGINE = InnoDB
AUTO_INCREMENT = 11
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
