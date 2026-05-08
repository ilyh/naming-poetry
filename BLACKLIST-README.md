# 黑名单配置说明

## 概述
黑名单已经从硬编码改为外置配置，支持热加载，修改后无需重启服务。

## 配置文件位置
- 配置文件：`backend/src/main/resources/application-blacklist.properties`
- 格式：`bad.chars=字符1,字符2,字符3,...`

## 修改黑名单

### 方法1：直接编辑配置文件
1. 编辑 `application-blacklist.properties` 文件
2. 修改 `bad.chars` 的值
3. 调用热加载接口（推荐）

### 方法2：通过API热加载
```bash
# 获取当前黑名单
curl http://localhost:8080/api/admin/blacklist

# 重新加载黑名单配置
curl -X POST http://localhost:8080/api/admin/blacklist/reload
```

### 方法3：命令行工具
```bash
# 启动项目时添加参数
java -jar naming-poetry.jar --update-blacklist
```

## 注意事项
1. 字符之间用英文逗号分隔
2. 不需要空格
3. 系统会自动过滤空值
4. 修改后需要调用热加载接口才生效
5. 热加载接口需要管理员权限