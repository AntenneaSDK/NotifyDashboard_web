--DROP TABLE users IF EXISTS;

CREATE TABLE USER (
  id  INTEGER PRIMARY KEY,
  name VARCHAR(30),
  email  VARCHAR(50)
);

CREATE TABLE APP_INFO (
  ID  INTEGER PRIMARY KEY,
  GCM_REGID VARCHAR(250),
  APNS_REGID VARCHAR(250),
  APP_VERSION INTEGER,
  APP_ID VARCHAR(200),
  FIRST_INSTLL_TIME timestamp,
  LAST_UPDATE_TIME timestamp
);

CREATE TABLE DEVICE_INFO(
  ID  INTEGER PRIMARY KEY,
  DEVICE_ID VARCHAR(100),
  SW_VERSION VARCHAR(100),
  SIM_SERIAL_NUM VARCHAR(100),
  VOICEMAIL_NUM VARCHAR(100),
  SIM_OPERATOR VARCHAR(100),
  PHONE_NUM VARCHAR(100),
  PHONE_TYPE VARCHAR(10),
  NETWORK_COUNTRY VARCHAR(10),
  NETWORK_OPERATOR_ID VARCHAR(10),
  NETWORK_OPERATOR_NAME VARCHAR(100)
);