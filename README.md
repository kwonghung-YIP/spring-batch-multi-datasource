# Run this example with docker-compose

```bash
wget --no-cache https://raw.githubusercontent.com/kwonghung-YIP/spring-batch-multi-datasource/master/docker-compose.yml
docker-compose up
```

# Deploy MySQL databases for local development

```bash
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
```
