-- 1. 회원 (Member) 테이블
CREATE TABLE member (
                        member_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        username VARCHAR2(100) NOT NULL UNIQUE,
                        password VARCHAR2(255) NOT NULL,
                        name VARCHAR2(100) NOT NULL,
                        role VARCHAR2(50),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 계좌 (Account) 테이블
CREATE TABLE account (
                         account_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         username VARCHAR2(100) NOT NULL,
                         balance NUMBER DEFAULT 0 NOT NULL,
                         CONSTRAINT fk_account_member FOREIGN KEY (username) REFERENCES member(username) ON DELETE CASCADE
);

-- 3. 거래 내역 (TransactionHistory) 테이블
CREATE TABLE transaction_history (
                                     history_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     from_account_id NUMBER,
                                     to_account_id NUMBER,
                                     amount NUMBER NOT NULL,
                                     status VARCHAR2(50),
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);