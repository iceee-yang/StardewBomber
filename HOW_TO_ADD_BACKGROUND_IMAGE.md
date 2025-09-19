# 如何添加背景图片

## 步骤说明

### 1. 准备图片文件
- 将您想要作为背景的图片保存到电脑上
- 确保图片格式为 **PNG**
- 建议尺寸：500x400像素或更高

### 2. 重命名图片文件
将图片文件重命名为：`pixel_frame_background.png`

### 3. 复制到项目目录
将重命名后的图片文件复制到以下位置：
```
C:\Users\ningshuaiming\Desktop\finnal\finalProj\src\main\resources\login\pixel_frame_background.png
```

### 4. 测试效果
运行以下命令测试：
```bash
test_pixel_art_background.bat
```

## 当前状态

- ✅ 登录界面已准备好接收背景图片
- ✅ 程序会自动检测并加载背景图片
- ✅ 如果没有图片，会显示像素艺术风格渐变背景
- ✅ 控制台会显示背景加载状态

## 文件路径确认

请确认图片文件放在正确位置：
```
finalProj/
└── src/
    └── main/
        └── resources/
            └── login/
                └── pixel_frame_background.png  ← 您的图片文件应该在这里
```

## 调试信息

程序启动时会在控制台显示：
- `未找到背景图片文件，使用像素艺术风格背景` - 没有图片文件
- `成功加载背景图片: 宽度x高度` - 成功加载图片
- `背景图片无效，使用像素艺术风格背景` - 图片文件损坏

## 注意事项

1. **文件名必须完全匹配**：`pixel_frame_background.png`
2. **文件格式必须是PNG**
3. **文件位置必须正确**
4. **重新编译后才会生效**
