需要程序:

- JDK 8
- Gradle 2.12+
- MySQL 5.5+
- ElasticSearch 1.2.x (可选)

环境设置:

- 可有可无的环境变量SAGE\_FILES\_HOME (存放上传文件的位置, 默认值为用户目录)
- Mac/Linux可在shell配置Gradle短命令 alias grad="./gradlew"

数据库:

- 建一个数据库, 名字sage, 字符集utf8\_general\_ci
- 建一个用户, 名字sage, 密码1234, 给予所有权限

启动:

- IDE方式: 用Intellij导入项目(项目类型选Gradle)，执行Application.kt文件
- 命令方式: `./gradlew bootRun` (用了alias就是`grad bootRun`)
- 首次启动后打开 localhost:8080/z-init 完成数据初始化

开发过程:

- 后端: 修改/新增文件, 重启程序
- 前端: 修改文件, 刷新网页; 新增文件，重启程序
- 干净构建: `./gradlew clean bootRun`
