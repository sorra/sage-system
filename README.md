[轻境界](https://www.qingjingjie.com/) 新一代SNS社区：后端基于Kotlin + Spring Boot + Ebean ORM， 前端基于JS + jQuery + Bootstrap。

### 开发环境安装指南

依赖软件:

- JDK 8
- MySQL 5.5+
- ElasticSearch 2/3 (可选)

环境设置:

- (可选) 环境变量SAGE\_FILES\_HOME (存放上传文件的位置, 默认值为用户目录)
- (可选) Linux/Mac可在shell配置Gradle短命令 alias grad="./gradlew"

数据库:

1. 执行项目根目录的database-and-user.sql
2. 启动程序，项目根目录会生成db-create-tables.sql，执行它
3. 如果想生成测试数据，可打开http://localhost:8080/z-init

启动:

- IDE方式: 用Intellij导入项目(项目类型选Gradle)，执行Application.kt文件
- 命令方式: `./gradlew web bootRun` (若有alias就是`grad web bootRun`)

开发过程:

- 后端: 修改代码, 重启程序
- 前端: 执行`./gradlew web`, 刷新网页
- 干净构建: 执行`./gradlew clean web bootRun`

Gradle命令解释:

- clean: 清理build目录的文件
- web: 把css, js文件合并(除了库文件)
- webmin: 把css, js文件合并&精简
- build: 编译&打包jar
- bootRun: 编译&启动程序(不打包)

版权声明：仅供参考学习，禁止未授权的商业使用。