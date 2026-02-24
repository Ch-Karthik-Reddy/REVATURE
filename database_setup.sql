-- =============================================
-- RevPay Database Setup Script for Oracle 10g XE
-- =============================================

-- Sequences
CREATE SEQUENCE seq_users START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_business_profiles START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_payment_methods START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_transactions START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_payment_requests START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_invoices START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_loans START WITH 1 INCREMENT BY 1 NOCACHE;

-- =============================================
-- 1. USERS TABLE
-- =============================================
CREATE TABLE users (
    user_id         NUMBER PRIMARY KEY,
    email           VARCHAR2(255) NOT NULL UNIQUE,
    phone_number    VARCHAR2(20),
    password_hash   VARCHAR2(255) NOT NULL,
    transaction_pin VARCHAR2(20),
    full_name       VARCHAR2(100),
    role            VARCHAR2(20) DEFAULT 'PERSONAL' NOT NULL
);

CREATE OR REPLACE TRIGGER trg_users_id
BEFORE INSERT ON users FOR EACH ROW
BEGIN
    IF :NEW.user_id IS NULL THEN
        SELECT seq_users.NEXTVAL INTO :NEW.user_id FROM DUAL;
    END IF;
END;
/

-- =============================================
-- 2. WALLETS TABLE
-- =============================================
CREATE TABLE wallets (
    user_id     NUMBER PRIMARY KEY,
    balance     NUMBER(15,2) DEFAULT 0.00,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id)
        REFERENCES users(user_id) ON DELETE CASCADE
);

-- =============================================
-- 3. BUSINESS_PROFILES TABLE
-- =============================================
CREATE TABLE business_profiles (
    business_id     NUMBER PRIMARY KEY,
    user_id         NUMBER NOT NULL,
    business_name   VARCHAR2(255),
    address         CLOB,
    CONSTRAINT fk_business_user FOREIGN KEY (user_id)
        REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE OR REPLACE TRIGGER trg_business_id
BEFORE INSERT ON business_profiles FOR EACH ROW
BEGIN
    IF :NEW.business_id IS NULL THEN
        SELECT seq_business_profiles.NEXTVAL INTO :NEW.business_id FROM DUAL;
    END IF;
END;
/

-- =============================================
-- 4. PAYMENT_METHODS TABLE
-- =============================================
CREATE TABLE payment_methods (
    method_id               NUMBER PRIMARY KEY,
    user_id                 NUMBER NOT NULL,
    card_number_encrypted   VARCHAR2(255),
    card_type               VARCHAR2(50),
    expiry_date             DATE,
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id)
        REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE OR REPLACE TRIGGER trg_method_id
BEFORE INSERT ON payment_methods FOR EACH ROW
BEGIN
    IF :NEW.method_id IS NULL THEN
        SELECT seq_payment_methods.NEXTVAL INTO :NEW.method_id FROM DUAL;
    END IF;
END;
/

-- =============================================
-- 5. TRANSACTIONS TABLE
-- =============================================
CREATE TABLE transactions (
    transaction_id      NUMBER PRIMARY KEY,
    sender_id           NUMBER,
    receiver_id         NUMBER,
    amount              NUMBER(15,2) NOT NULL,
    transaction_type    VARCHAR2(50) NOT NULL,
    status              VARCHAR2(50) NOT NULL,
    txn_timestamp       TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_trans_sender FOREIGN KEY (sender_id)
        REFERENCES users(user_id) ON DELETE SET NULL,
    CONSTRAINT fk_trans_receiver FOREIGN KEY (receiver_id)
        REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE OR REPLACE TRIGGER trg_transaction_id
BEFORE INSERT ON transactions FOR EACH ROW
BEGIN
    IF :NEW.transaction_id IS NULL THEN
        SELECT seq_transactions.NEXTVAL INTO :NEW.transaction_id FROM DUAL;
    END IF;
END;
/

-- =============================================
-- 6. PAYMENT_REQUESTS TABLE
-- =============================================
CREATE TABLE payment_requests (
    request_id      NUMBER PRIMARY KEY,
    requester_id    NUMBER NOT NULL,
    payer_id        NUMBER NOT NULL,
    amount          NUMBER(15,2) NOT NULL,
    status          VARCHAR2(50) DEFAULT 'PENDING',
    CONSTRAINT fk_request_requester FOREIGN KEY (requester_id)
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_request_payer FOREIGN KEY (payer_id)
        REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE OR REPLACE TRIGGER trg_request_id
BEFORE INSERT ON payment_requests FOR EACH ROW
BEGIN
    IF :NEW.request_id IS NULL THEN
        SELECT seq_payment_requests.NEXTVAL INTO :NEW.request_id FROM DUAL;
    END IF;
END;
/

-- =============================================
-- 7. INVOICES TABLE
-- =============================================
CREATE TABLE invoices (
    invoice_id      NUMBER PRIMARY KEY,
    business_id     NUMBER NOT NULL,
    customer_email  VARCHAR2(255) NOT NULL,
    amount          NUMBER(15,2) NOT NULL,
    description     CLOB,
    status          VARCHAR2(50) DEFAULT 'PENDING',
    created_at      TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_invoice_business FOREIGN KEY (business_id)
        REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE OR REPLACE TRIGGER trg_invoice_id
BEFORE INSERT ON invoices FOR EACH ROW
BEGIN
    IF :NEW.invoice_id IS NULL THEN
        SELECT seq_invoices.NEXTVAL INTO :NEW.invoice_id FROM DUAL;
    END IF;
END;
/

-- =============================================
-- 8. LOANS TABLE
-- =============================================
CREATE TABLE loans (
    loan_id         NUMBER PRIMARY KEY,
    user_id         NUMBER NOT NULL,
    amount          NUMBER(15,2) NOT NULL,
    reason          CLOB,
    status          VARCHAR2(50) DEFAULT 'PENDING',
    applied_at      TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_loan_user FOREIGN KEY (user_id)
        REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE OR REPLACE TRIGGER trg_loan_id
BEFORE INSERT ON loans FOR EACH ROW
BEGIN
    IF :NEW.loan_id IS NULL THEN
        SELECT seq_loans.NEXTVAL INTO :NEW.loan_id FROM DUAL;
    END IF;
END;
/

-- =============================================
-- INDEXES
-- =============================================
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_trans_sender ON transactions(sender_id);
CREATE INDEX idx_trans_receiver ON transactions(receiver_id);
CREATE INDEX idx_trans_timestamp ON transactions(txn_timestamp);
CREATE INDEX idx_invoice_business ON invoices(business_id);
CREATE INDEX idx_invoice_status ON invoices(status);
CREATE INDEX idx_loan_user ON loans(user_id);
CREATE INDEX idx_loan_status ON loans(status);

-- =============================================
-- TRIGGER: Auto-create wallet on user registration
-- =============================================
CREATE OR REPLACE TRIGGER trg_create_wallet_on_user
AFTER INSERT ON users FOR EACH ROW
BEGIN
    INSERT INTO wallets (user_id, balance) VALUES (:NEW.user_id, 0.00);
END;
/

-- =============================================
-- PROCEDURE: Transfer money between two users
-- =============================================
CREATE OR REPLACE PROCEDURE sp_transfer_money (
    p_sender_id   IN NUMBER,
    p_receiver_id IN NUMBER,
    p_amount      IN NUMBER
)
AS
    v_sender_balance NUMBER(15,2);
BEGIN
    IF p_amount <= 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Transfer amount must be greater than zero.');
    END IF;

    SELECT balance INTO v_sender_balance
    FROM wallets WHERE user_id = p_sender_id FOR UPDATE;

    IF v_sender_balance < p_amount THEN
        RAISE_APPLICATION_ERROR(-20002,
            'Insufficient funds. Available: ' || v_sender_balance);
    END IF;

    UPDATE wallets SET balance = balance - p_amount WHERE user_id = p_sender_id;
    UPDATE wallets SET balance = balance + p_amount WHERE user_id = p_receiver_id;

    INSERT INTO transactions (sender_id, receiver_id, amount, transaction_type, status)
    VALUES (p_sender_id, p_receiver_id, p_amount, 'TRANSFER', 'SUCCESS');

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20003, 'Wallet not found for user_id: ' || p_sender_id);
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- =============================================
-- FUNCTION: Get wallet balance for a user
-- =============================================
CREATE OR REPLACE FUNCTION fn_get_wallet_balance (
    p_user_id IN NUMBER
)
RETURN NUMBER
AS
    v_balance NUMBER(15,2);
BEGIN
    SELECT balance INTO v_balance
    FROM wallets WHERE user_id = p_user_id;
    RETURN v_balance;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END;
/

COMMIT;
EXIT;
