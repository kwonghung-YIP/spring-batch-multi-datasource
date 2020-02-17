drop table if exists ACCOUNT;

create table ACCOUNT (
  ACCOUNT_NO varchar(20) not null primary key,
  BALANCE decimal(10,2) not null default 0,
  CREDIT_LIMIT decimal(10,2) not null check (CREDIT_LIMIT > 0),
  LAST_TX_REF int,
  LAST_TX_DATETIME datetime(4),
  constraint CREDIT_LIMIT_CHK check (BALANCE * -1 <= CREDIT_LIMIT)
);