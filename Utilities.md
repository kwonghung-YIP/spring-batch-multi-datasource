docker run \
  -it --rm --link mysql01 -v `pwd`/schema-mysql-card-db.sql:/tmp/test.sql \
  mysql:8 mysql --host=mysql01 --port=3306 -ubatch -ptesting < test.sql

docker run \
  -it --rm --link mysql02 \
  mysql:8 mysql --host=mysql02 --port=3306 -uuser02 -ptesting

docker run \
  -it --rm \
  mysql:8 mysql --help

docker run \
  -it --rm --network host \
  mysql:8 mysql --host=localhost --port=23306 -ubatch -ptesting

docker run \
  -it --rm --network host \
  mysql:8 mysql --host=192.168.79.129 --port=23306 -ubatch -ptesting
  
INSERT INTO BATCH_STEP_EXECUTION_SEQ values(0,0);
INSERT INTO BATCH_JOB_EXECUTION_SEQ values(0,0);
INSERT INTO BATCH_JOB_SEQ values(0,0);  

https://github.com/spring-projects/spring-batch/blob/master/spring-batch-core/src/main/resources/org/springframework/batch/core/schema-mysql.sql
