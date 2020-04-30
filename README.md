#### 安装教程

1. 搭建 mqtt 服务器  
```
$ docker run -d --name emqx -p 1883:1883 -p 8083:8083 -p 8883:8883 -p 8084:8084 -p 18083:18083 emqx/emqx:v4.0.0
```
[emqx使用文档](https://docs.emqx.io/broker/v4/cn/)  

2. 启动 publisher 和 subscriber  

3. 发布和订阅