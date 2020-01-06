新一代博客和SNS社区：后端基于Kotlin + Spring Boot + Ebean ORM， 前端基于JS + jQuery + Bootstrap。

### 开发环境安装指南

依赖软件：

- JDK 8
- MySQL 5.6
- ElasticSearch 7.5.1 (可选)

数据库：

1. 初安装，用MySQL执行`db-1st-setup.sql`和`db-create-tables.sql`
2. 每次改变表结构，会更新`db-create-tables.sql`，请手动处理其中的变更
3. 如果想快速生成测试数据，可打开http://localhost:8080/z-init

启动：

- IDE方式: 运行`./gradlew web`，然后用Intellij导入项目(项目类型选Gradle)，执行`src/main/kotlin/sage/Application.kt`文件
- 命令方式: `./gradlew web bootRun`

开发过程：

- 后端: 修改代码, 重启应用
- 前端: 执行`./gradlew web`, 刷新网页
- 干净构建: 执行`./gradlew clean web bootRun`

Gradle命令解释：

- clean: 清理build目录的文件
- web: 把css, js文件合并(除了库文件)
- webmin: 把css, js文件合并&压缩
- build: 编译&打包jar
- bootRun: 编译&启动(不打包)

### 生产环境使用指南

请谨慎考虑用于生产环境，本项目仅提供信息，对任何风险概不负责！

部署：

- 将Gradle build打包生成的jar放到服务器上，把production-files目录下所有文件也放到与jar同一路径
- 在jar所在目录执行run-keepalive.sh以启动应用
- 同理，执行stop-keepalive.sh以停止应用

自定义设置：

- 默认设置项位于"设置文件"sage-settings.properties (位于src/main/resources目录)
- 需自定义设置，可在用户home目录下创建一个同名的"设置文件"(优先于默认设置项)
- 需自定义设置，也可在应用启动命令中附加JVM系统属性(优先于所有设置文件)，但注意避免包含密码等敏感信息
- 详情可参考代码Settings.kt

密码安全(以数据库为例)：

- 请不要使用默认密码！
- 请把数据库密码放入自定义"设置文件"，限制该文件的读写权限，只许特定用户访问（也可在应用启动后删除此文件，下次部署再重新创建）
- 请不要在应用启动命令中包含密码等敏感信息，因为这可能暴露到进程列表等信息中

### 版权声明

仅供参考学习，禁止未授权的商业使用。