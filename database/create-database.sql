CREATE TABLE PRICE_DATA(TICKER VARCHAR(10), DATE DATE, CLOSE_PRICE NUMERIC(10,2), PRIMARY KEY(TICKER, DATE));
CREATE TABLE ASSET(TICKER VARCHAR(10) PRIMARY KEY, NAME VARCHAR(100), ISIN VARCHAR(12), ASSET_CLASS VARCHAR(20) NOT NULL);