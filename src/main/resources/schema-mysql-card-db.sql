drop procedure if exists GENERATE_CARD_TXN;

drop table if exists CARD_TXN;

drop table if exists CARD;

create table CARD (
  CARD_NO varchar(20) not null primary key,
  CREDIT_LIMIT decimal(10,2) not null
);

create table CARD_TXN (
  TX_REF int not null primary key,
  TX_DATETIME datetime(4) not null,
  CARD_NO varchar(20) not null,
  CR_AMOUNT decimal(10,2),
  DR_AMOUNT decimal(10,2),
  POST char(1) not null default 'N' check (POST in ('Y','N')),
  POST_DATETIME datetime(4),
  foreign key FK_CARD_NO(CARD_NO) references CARD(CARD_NO)
  on update cascade on delete restrict
);

delimiter //

create procedure GENERATE_CARD_TXN(
  in no_of_card decimal(10),
  in no_of_txn decimal(10)
)
begin
  declare n int;
  declare d1 date;
  declare d2 date;
  declare no_of_days int;
  declare v_card_no varchar(20);
  declare cr_amt decimal(10,2);
  declare dr_amt decimal(10,2);
  
  start transaction;
  
  set n = 1;
  while n <= no_of_card do
    insert into CARD (
      card_no, credit_limit
    ) values (
      concat(lpad(floor(rand()*9999),4,'0'),'-',lpad(floor(rand()*9999),4,'0'),'-',lpad(floor(rand()*9999),4,'0'),'-',lpad(floor(rand()*9999),4,'0')),
      round(rand()*40,0)*100+1000
    );
	set n = n + 1;
  end while;
  
  set d1 = last_day(now());
  set d2 = last_day(date_add(now(), interval -1 month));
  set no_of_days=timestampdiff(day,d2,d1);
  set n = 1;
  while n <= no_of_txn do
    
    select card_no from CARD order by rand() limit 1 into v_card_no;
  
    if rand() > 0.8 then
      set cr_amt = round(rand()*30,2)*100;
      set dr_amt = null;
    else
      set cr_amt = null;
      set dr_amt = round(rand()*500,2);
    end if;
    
    insert into CARD_TXN (
      tx_ref, tx_datetime, card_no, cr_amount, dr_amount
    ) values (
      n, date_add(date_add(d2, interval round(rand()*no_of_days) day), interval round(rand()*24*60*60) second), v_card_no, cr_amt, dr_amt
    );
    
    /*if n%1000 = 0 then
      select n;
    end if;*/
    
    set n = n + 1;
  end while;
  
  commit;
end //

delimiter ;

call GENERATE_CARD_TXN(500,10000);


