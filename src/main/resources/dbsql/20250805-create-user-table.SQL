-- rap.`user` definition

CREATE TABLE `user` (
                        `id` bigint NOT NULL,
                        `user_id` varchar(100) DEFAULT NULL,
                        `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
                        `password` varchar(100) DEFAULT NULL,
                        `icon` varchar(100) DEFAULT NULL,
                        `create_user` varchar(100) DEFAULT NULL,
                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `update_user` varchar(100) DEFAULT NULL,
                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
                        `remark1` varchar(100) DEFAULT NULL,
                        `remark2` varchar(100) DEFAULT NULL,
                        `remark3` varchar(100) DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        KEY `user_user_id_IDX` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;