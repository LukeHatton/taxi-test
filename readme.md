<mark>本项目为skywalking测试工程</mark>

### 需要安装的其他程序

1. ElasticSearch
2. redis
3. mysql
4. skywalking oap server
5. skywalking ui

### docker安装脚本——以ElasticSearch作为后端存储
首先要创建用户定义网络，上述的几个容器全部都要连接到这个网络中
```shell
docker network create bridge-skywalking
```

#### ElasticSearch

```shell
docker run --name skywalking-es --network=bridge-skywalking \
-p 9200:9200 -p 9300:9300 \
-e "discovery.type=single-node" -e ES_JAVA_OPTS="-Xms128m -Xmx512m" \
-d elasticsearch:7.16.2
```

修改ES配置

```shell
# 修改容器中/usr/share/elasticsearch/config/elasticsearch.yml文件，添加如下配置
# 集群服务名称
cluster.name: "skywalking-es"
# 开启跨域
http.cors.enabled: true
# 允许的跨域域名：允许所有
http.cors.allow-origin: "*"
# 外部可访问ip
network.host: 0.0.0.0
# 最小主节点个数
discovery.zen.minimum_master_nodes: 1
```
重启容器，使修改的配置生效

```shell
docker restart skywalking-es
```

#### ElasticSearch HQ
如果使用的是7及7以前版本的ES，可以安装这个ElasticSearch的GUI工具

> ElasticSearch HQ因为ES版本迭代过快，已经停止了对于8及更高版本的适配

```shell
docker run --name elastic-hq --network=bridge-skywalking \
-p 5000:5000 \
-d elastichq/elasticsearch-hq
```

#### redis

主要是作为应用缓存，之后在skywalking gui中可以查看是否能够检测到应用程序对于redis的依赖

```shell
# 因为最新的7版本的redis可能在使用上有修改的地方，还是使用上一个大版本的redis
docker run --name  skywalking-redis \
--network=bridge-skywalking \
-p 6379:6379 \
-d redis:6.2.7
```

#### mysql

主要作为应用数据库，之后在skywalking gui中可以查看是否能够检测到应用程序对于mysql的依赖

mysql的版本可以自由选择（arm或amd64），最好使用8以上的版本

```shell
docker run --name skywalking-mysql -p 3306:3306 -p 33060:33060 \
--network=bridge-skywalking \
-e MYSQL_ROOT_PASSWORD=password -e TZ=Asia/Shanghai \
-d arm64v8/mysql:oracle
```

#### skywalking oap server

这是skywalking的服务端

```shell
docker run --name skywalking-server \
--network=bridge-skywalking \
-p 1234:1234 -p 11800:11800 -p 12800:12800 \
-e SW_STORAGE=elasticsearch \
-e SW_STORAGE_ES_CLUSTER_NODES=skywalking-es:9200 \
-d apache/skywalking-oap-server:8.9.1
```

#### skywalking ui

这是skywalking的网页gui服务

```shell
docker run --name skywalking-ui \
--network=bridge-skywalking \
-p 8080:8080 \
-e SW_OAP_ADDRESS=skywalking-server:12800 \
-d apache/skywalking-ui:8.9.1
```

全部5个容器启动成功后，查看localhost:8080（配置的skywalking ui的映射端口），如果有显示，就说明
ES和skywalking配置成功了。关于mysql和redis，可以使用客户端连接看一下是否成功启动了

### docker安装脚本——以MySQL作为后端存储

> 在m1芯片的macbook上启动后发现，skywalking ui页面中一片空白，什么都没有显示
> 
> 推测有两个可能的原因：
> 1. 操作系统原因
> 2. skywalking与mysql配置原因
> 
> 通过在amd64设备上部署docker容器来进行排查
> 
> 经过测试发现，在amd64上部署的使用mysql作为后端存储的skywalking服务正常启动了，也创建了所有的表（macbook也创建了），到这一步都全部正常
> 
> 启动skywalking-ui，在这一步上不同架构的平台出现了不同的效果：容器成功启动并没有报错，查看控制台，两边都有报错，但是amd64的可以看到页面输出，
> 而arm芯片的macbook上则是一片空白
> 
> **至此，基本可以确定是操作系统的问题了**

创建用户定义网络

```shell
docker network create mysql-test
```

启动mysql

```shell
docker run --name skywalking-mysql-test -p 3306:3306 -p 33060:33060 --network=mysql-test -e MYSQL_ROOT_PASSWORD=password -e TZ=Asia/Shanghai -d mysql:latest
```

启动redis

```shell
docker run --name  skywalking-redis-test --network=mysql-test -p 6379:6379 -d redis:6.2.7
```

创建skywalking-oap-server，并以mysql作为后端存储

```shell
docker container create --name skywalking-server-test --network=mysql-test -p 1234:1234 -p 11800:11800 -p 12800:12800 -e SW_STORAGE=mysql -e SW_JDBC_URL="jdbc:mysql://skywalking-mysql-test:3306/swtest?rewriteBatchedStatements=true" -e SW_DATA_SOURCE_USER=root -e SW_DATA_SOURCE_PASSWORD=password -e TZ=Asia/Shanghai apache/skywalking-oap-server:8.9.1
```

拷贝mysql驱动到skywalking-oap-server

```shell
docker cp "C:\Users\consi\.m2\repository\mysql\mysql-connector-java\8.0.29\mysql-connector-java-8.0.29.jar" skywalking-server-test:/skywalking/oap-libs/mysql-connector-java-8.0.29.jar
docker start skywalking-server-test
```

启动skywalking-ui

```shell
docker run --name skywalking-ui-test --network=mysql-test -p 8080:8080 -e SW_OAP_ADDRESS="skywalking-server-test:12800" -e TZ=Asia/Shanghai -d apache/skywalking-ui:8.9.1
```






