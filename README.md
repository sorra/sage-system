程序:
JDK 8
Maven 3
Tomcat 7/8
MySQL 5
  ElasticSearch (可选)

环境变量:
JAVA_HOME
M2_HOME
TOMCAT_HOME
PATH加上M2_HOME/bin:$JAVA_HOME/bin
  SAGE_FILES_HOME (可选, 默认值为用户目录)

数据库:
建一个数据库, 名字sage, 字符集utf8_general_ci
建一个用户, 名字sage, 密码1234, 给予所有权限

脚本(Linux/Mac为.sh, Windows为.bat):
./build.sh 构建+部署
./web-build.sh 前端构建+部署(无需重启server)
./run.sh 启动server(Ctrl+C关闭)
首次启动后打开 localhost:8080/z-init 完成数据初始化

开发过程:
后端 - 写了代码, shutdown, build+run (./build.sh && ./run.sh)
前端 - 写了代码(css, js, httl), 执行web-build, server可保持开启


```
brew install maven
brew install tomcat

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home
export TOMCAT_HOME=/Library/Tomcat

```
