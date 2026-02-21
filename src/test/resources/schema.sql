-- 기존에 남아있을 수 있는 객체들을 안전하게 초기화합니다.
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS member;
DROP SEQUENCE IF EXISTS account_seq;

-- 1. Account(계좌) 테이블 PK 생성을 위한 오라클 시퀀스
CREATE SEQUENCE account_seq START WITH 100000 INCREMENT BY 1;

-- 2. Member(회원) 테이블 생성
-- MemberMapper에서 useGeneratedKeys="true"를 사용하므로 자동 증가(AUTO_INCREMENT)를 적용합니다.
CREATE TABLE member (
                        member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        name VARCHAR(50) NOT NULL,
                        role VARCHAR(20) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Account(계좌) 테이블 생성
CREATE TABLE account (
                         account_id BIGINT PRIMARY KEY,
                         username VARCHAR(50) NOT NULL,
                         balance BIGINT DEFAULT 0 NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);