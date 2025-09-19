# 登录注册功能集成说明

## 概述
已成功将项目备份3.1网络初步中的登录注册功能集成到主程序finalProj中。现在应用程序启动时会首先显示登录注册界面，登录成功后才会跳转到主界面。

## 新增文件
- `src/main/java/com/stardewbombers/model/Player.java` - 玩家数据模型
- `src/main/java/com/stardewbombers/db/Config.java` - 配置管理类
- `src/main/java/com/stardewbombers/db/Database.java` - 数据库连接管理
- `src/main/java/com/stardewbombers/service/PlayerService.java` - 玩家服务接口
- `src/main/java/com/stardewbombers/service/impl/PlayerServiceImpl.java` - 玩家服务实现
- `src/main/java/com/stardewbombers/ui/LoginRegisterView.java` - 登录注册界面
- `src/main/java/com/stardewbombers/ui/SimpleLoginView.java` - 简化版登录界面
- `src/main/resources/app.properties` - 数据库配置文件

## 修改文件
- `pom.xml` - 添加了MySQL、HikariCP、BCrypt、slf4j-simple等数据库相关依赖
- `src/main/java/com/stardewbombers/StardewBombersApp.java` - 修改启动流程，先显示登录界面

## 测试脚本
- `test_login_integration.bat` - 集成测试脚本
- `test_database.bat` - 数据库连接测试脚本
- `run_with_login.bat` - 带登录功能的启动脚本

## 数据库配置
数据库连接信息在 `src/main/resources/app.properties` 中配置：
```
DB_HOST=192.168.207.87
DB_PORT=3306
DB_NAME=users
DB_USER=root
DB_PASSWORD=ning749A
```

## 功能说明
1. **注册功能**：用户可以注册新账号，需要提供昵称、手机号和密码
2. **登录功能**：用户可以使用手机号和密码登录
3. **自动跳转**：登录成功后自动跳转到主界面
4. **数据持久化**：玩家数据保存在MySQL数据库中

## 运行方式

### 方式1：使用Maven（推荐）
```bash
# 下载依赖
mvn dependency:copy-dependencies

# 编译项目
mvn clean compile

# 运行项目
mvn javafx:run
```

### 方式2：使用批处理文件
```bash
# 运行集成测试
test_login_integration.bat

# 或直接运行
run_with_login.bat
```

## 注意事项
1. 确保MySQL数据库服务正在运行
2. 确保数据库连接配置正确
3. 首次运行时会自动创建数据表
4. 新注册用户会获得初始的种子奖励

## 测试步骤
1. 启动应用程序
2. 在注册标签页注册一个新用户
3. 切换到登录标签页，使用注册的账号登录
4. 验证登录成功后是否跳转到主界面
5. 测试主界面的所有功能是否正常工作
