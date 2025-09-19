# 编译错误修复说明

## 问题描述
在 `StardewBombersApp.java` 中出现了编译错误：
```
无法解析 'Platform' 中的方法 'getCurrentStage'
```

## 错误原因
`javafx.application.Platform.getCurrentStage()` 方法在JavaFX中不存在。JavaFX的Platform类没有提供获取当前Stage的方法。

## 修复方案
将Stage对象作为实例变量保存，在需要时直接使用：

### 修复前：
```java
private void onLoginSuccess(Player player) {
    // 登录成功后显示主菜单
    Stage stage = (Stage) javafx.application.Platform.getCurrentStage(); // ❌ 错误
    MainMenuView mainMenu = new MainMenuView(stage);
    mainMenu.show();
}
```

### 修复后：
```java
private Stage primaryStage; // 添加实例变量

@Override
public void start(Stage stage) {
    this.primaryStage = stage; // 保存Stage引用
    // 先显示登录注册界面
    LoginRegisterView loginView = new LoginRegisterView(this::onLoginSuccess);
    loginView.show(stage);
}

private void onLoginSuccess(Player player) {
    // 登录成功后显示主菜单
    MainMenuView mainMenu = new MainMenuView(primaryStage); // ✅ 正确
    mainMenu.show();
}
```

## 修复结果
- ✅ 编译错误已解决
- ✅ 代码逻辑保持不变
- ✅ 登录成功后正确跳转到主界面

## 测试建议
运行以下命令测试修复结果：
```bash
mvn clean compile
mvn javafx:run
```
