[轻境界](https://www.qingjingjie.com/) 新一代SNS社区：后端基于Kotlin + Spring Boot + Ebean ORM， 前端基于JS + jQuery + Bootstrap。

### 开发环境安装指南

依赖软件：

- JDK 8
- MySQL 5.5+
- ElasticSearch 2/3 (可选)

数据库：

1. 用MySQL执行`database-and-user.sql`和`db-create-tables.sql`
2. 如果想生成测试数据，可打开http://localhost:8080/z-init

启动：

- IDE方式: 用Intellij导入项目(项目类型选Gradle)，执行`src/main/kotlin/sage/Application.kt`文件
- 命令方式: `./gradlew web bootRun`

开发过程：

- 后端: 修改代码, 重启程序
- 前端: 执行`./gradlew web`, 刷新网页
- 干净构建: 执行`./gradlew clean web bootRun`

Gradle命令解释：

- clean: 清理build目录的文件
- web: 把css, js文件合并(除了库文件)
- webmin: 把css, js文件合并&压缩
- build: 编译&打包jar
- bootRun: 编译&启动程序(不打包)

### 版权声明

仅供参考学习，禁止未授权的商业使用。