#####################################################
# 本文档记录在amd64和arm设备上启动docker容器所需要的所有脚本
# 相对于readme.md中的内容，是更加偏向测试向的，所有docker命令脚本都会记录在这个文件中
#####################################################
# amd64设备
# ElasticSearch
docker run --name skywalking-es --network=bridge-skywalking -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e ES_JAVA_OPTS="-Xms128m -Xmx512m" -d elasticsearch:7.6.2
# SkyWalking
docker run --name skywalking-server --network=bridge-skywalking -p 1234:1234 -p 11800:11800 -p 12800:12800 -e SW_STORAGE=elasticsearch7 -e SW_STORAGE_ES_CLUSTER_NODES=skywalking-es:9200 -d apache/skywalking-oap-server:8.3.0-es7
# UI
docker run --name skywalking-ui --network=bridge-skywalking -p 8080:8080 -e SW_OAP_ADDRESS=skywalking-server:12800 -d apache/skywalking-ui:8.3.0

# arm设备