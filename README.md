docker run --name=mysql01 \
  --detach \
  --restart=always \
  -p 23306:3306 \
  -e MYSQL_DATABASE=spring-batch \
  -e MYSQL_ROOT_PASSWORD=passw0rd \
  -e MYSQL_USER=batch \
  -e MYSQL_PASSWORD=testing \
  mysql:8 

docker run --name=mysql02 \
  --detach \
  --restart=always \
  -p 23307:3306 \
  -e MYSQL_DATABASE=db1 \
  -e MYSQL_RANDOM_ROOT_PASSWORD=yes \
  -e MYSQL_USER=user02 \
  -e MYSQL_PASSWORD=testing \
  mysql:8 

docker run --name=mysql03 \
  --detach \
  --restart=always \
  -p 23308:3306 \
  -e MYSQL_DATABASE=db2 \
  -e MYSQL_RANDOM_ROOT_PASSWORD=yes \
  -e MYSQL_USER=user03 \
  -e MYSQL_PASSWORD=testing \
  mysql:8
  
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
