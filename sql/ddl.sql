-- 영화 테이블
CREATE TABLE movies
(
    id              BIGINT UNSIGNED       NOT NULL AUTO_INCREMENT COMMENT '영화 ID',
    title           VARCHAR(255) NOT NULL COMMENT '영화 제목',
    release_date    TIMESTAMP    NOT NULL COMMENT '영화 개봉일',
    running_time    INTEGER      NOT NULL COMMENT '영화 상영 시간',
    thumbnail_image VARCHAR(255) COMMENT '영화 썸네일 이미지 URL',
    genre           ENUM ('ACTION', 'HORROR', 'ROMANCE', 'SF') NOT NULL COMMENT '영화 장르',
    grade           ENUM ('ALL', 'TWELVE', 'FIFTEEN', 'ADULT') NOT NULL COMMENT '영화 등급',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    created_by      VARCHAR(255) COMMENT '생성자',
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '수정 일시',
    updated_by      VARCHAR(255) COMMENT '수정자',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_title_genre ON movies (title, genre);


-- 극장 테이블
CREATE TABLE theaters
(
    id            BIGINT UNSIGNED      NOT NULL AUTO_INCREMENT COMMENT '극장 ID',
    name          VARCHAR(255) NOT NULL COMMENT '극장 이름',
    created_at    TIMESTAMP DEFAULT NOW() COMMENT '생성 일시',
    created_by    VARCHAR(255) COMMENT '생성자',
    updated_at    TIMESTAMP DEFAULT NOW() COMMENT '수정 일시',
    updated_by    VARCHAR(255) COMMENT '수정자',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 상영 테이블
CREATE TABLE screenings
(
    id           BIGINT UNSIGNED       NOT NULL AUTO_INCREMENT COMMENT '상영 ID',
    movie_id     BIGINT UNSIGNED       NOT NULL COMMENT '영화 ID',
    theater_id   BIGINT UNSIGNED       NOT NULL COMMENT '상영관 ID',
    theater_name VARCHAR(100) NOT NULL COMMENT '상영관 이름',
    start_time   TIME(6)      NOT NULL COMMENT '상영 시작 시간',
    end_time     TIME(6)      NOT NULL COMMENT '상영 종료 시간',
    created_at   TIMESTAMP DEFAULT NOW() COMMENT '생성 일시',
    created_by   VARCHAR(255) COMMENT '생성자',
    updated_at   TIMESTAMP DEFAULT NOW() COMMENT '수정 일시',
    updated_by   VARCHAR(255) COMMENT '수정자',
    PRIMARY KEY (id),
    FOREIGN KEY (movie_id) REFERENCES movies (id),
    FOREIGN KEY (theater_id) REFERENCES theaters (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_start_time ON screenings (start_time);


-- 좌석 테이블
CREATE TABLE seats
(
    id         BIGINT UNSIGNED       NOT NULL AUTO_INCREMENT COMMENT '좌석 ID',
    seat_code  VARCHAR(255) NOT NULL COMMENT '좌석 코드',
    theater_id BIGINT UNSIGNED       NOT NULL COMMENT '극장 ID',
    created_at TIMESTAMP DEFAULT NOW() COMMENT '생성 일시',
    created_by VARCHAR(255) COMMENT '생성자',
    updated_at TIMESTAMP DEFAULT NOW() COMMENT '수정 일시',
    updated_by VARCHAR(255) COMMENT '수정자',
    PRIMARY KEY (id),
    FOREIGN KEY (theater_id) REFERENCES theaters (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 예약 테이블
CREATE TABLE reservations
(
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '예약 ID',
    screening_id BIGINT UNSIGNED NOT NULL COMMENT '상영 ID (foreign key)',
    seat_id      BIGINT UNSIGNED NOT NULL COMMENT '좌석 ID (foreign key)',
    created_at   TIMESTAMP DEFAULT NOW() COMMENT '생성 일시',
    created_by   VARCHAR(255) COMMENT '생성자',
    updated_at   TIMESTAMP DEFAULT NOW() COMMENT '수정 일시',
    updated_by   VARCHAR(255) COMMENT '수정자',
    PRIMARY KEY (id),
    FOREIGN KEY (screening_id) REFERENCES screenings (id),
    FOREIGN KEY (seat_id) REFERENCES seats (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
