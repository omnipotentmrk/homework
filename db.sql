CREATE TABLE `user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `reg_dt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `spread_money_event` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned NOT NULL,
  `room_id` char(3) NOT NULL,
  `token` char(3) DEFAULT NULL,
  `target_member_cnt` int(5) unsigned NOT NULL,
  `currency` char(3) NOT NULL,
  `total_amount` varchar(10) NOT NULL,
  `reg_dt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `udx_token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `spread_money_distribution` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `spread_money_event_id` int(11) unsigned NOT NULL,
  `amount` varchar(10) NOT NULL,
  `distributed_yn` char(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`),
  KEY `idx_spread_money_event_id` (`spread_money_event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `spread_money_received` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `spread_money_distribution_id` int(11) unsigned NOT NULL,
  `user_id` int(11) unsigned NOT NULL,
  `reg_dt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `spread_money_distribution_id_user_id` (`spread_money_distribution_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DELIMITER $$

CREATE FUNCTION `rand_string`(length SMALLINT(3)) RETURNS varchar(100) CHARSET utf8
BEGIN
    SET @returnStr = '';
    SET @allowedChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*_-';
    SET @i = 0;

    WHILE (@i < length) DO
        SET @returnStr = CONCAT(@returnStr, substring(@allowedChars, FLOOR(RAND() * LENGTH(@allowedChars) + 1), 1));
        SET @i = @i + 1;
    END WHILE;

    RETURN @returnStr;
END$$

CREATE TRIGGER spread_money_event_before_insert
    BEFORE INSERT ON `spread_money_event`
    FOR EACH ROW
    BEGIN
        SET @spread_money_event_id = 1;
    WHILE (@spread_money_event_id IS NOT NULL) DO
        SET NEW.token = rand_string(3);
        SET @spread_money_event_id = (SELECT id FROM `spread_money_event` WHERE `token` = NEW.token);
    END WHILE;
END$$