# 智能排课系统

面向中小学的智能自/手动排课软件，支持拖拽排课、冲突实时检测、禁止排课约束、多学期/多班级管理，以及 Excel / PDF 课表导出

基于 Vue3 + Vite + Element-Plus + Spring Boot + MyBatis + MySQL 的智能排课系统。

## 在线体验
https://337547038.github.io/ak-smart-scheduling

## 技术栈

### 前端
- Vue 3（Composition API）
- Vite
- Element Plus UI框架
- Vue Router 4
- Pinia 状态管理
- Axios

### 后端
- Spring Boot 2.7.18
- MyBatis
- MySQL 8.0
- Druid 连接池
- JWT 身份认证
- BCrypt 密码加密

## 功能模块

| 功能 | 说明 | 权限 |
|------|------|------|
| 登录认证 | 管理员/教师双角色登录 | 全部 |
| 首页Dashboard | 数据统计、快捷入口 | 全部 |
| 班级管理 | 班级CRUD、班主任分配 | 管理员 |
| 老师管理 | 教师CRUD、初始密码 | 管理员 |
| 课时管理 | 时间段配置（星期、节次、起止时间） | 管理员 |
| 一键排课 | 智能算法自动排课（主科优先、冲突检测） | 管理员 |
| 课表查看 | 按班级/教师两种维度查看周课表 | 全部 |
| 排课变更 | 调课/换老师/取消课程，自动检测冲突 | 管理员 |
| 变更记录 | 查看所有排课变更历史 | 管理员 |
| 我的课表 | 教师个人课表及授课统计 | 教师 |

## 项目结构

```
smart-scheduling-system/
├── sql/                        # 数据库脚本
│   └── smart_scheduling.sql
├── backend/                    # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/school/scheduling/
│       │   ├── config/         # 配置类（CORS、JWT拦截器）
│       │   ├── controller/     # 控制器层
│       │   ├── entity/         # 实体类
│       │   ├── mapper/         # MyBatis Mapper
│       │   ├── service/        # 业务逻辑层
│       │   ├── dto/            # 数据传输对象
│       │   ├── common/         # 通用返回
│       │   └── util/           # 工具类（JWT）
│       └── resources/
│           ├── application.yml
│           └── mapper/         # MyBatis XML
└── frontend/                   # Vue3 前端
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── api/                # API 接口
        ├── router/             # 路由
        ├── stores/             # Pinia 状态
        ├── utils/              # 工具（axios）
        ├── layout/             # 布局组件
        └── views/              # 页面视图
```

## 快速启动

### 1. 准备数据库
```bash
# 登录MySQL，执行初始化脚本
mysql -u root -p < sql/smart_scheduling.sql
```
默认数据库名 `smart_scheduling`，如MySQL账号密码不同请修改 `backend/src/main/resources/application.yml`。

### 2. 启动后端
```bash
cd backend
# 使用Maven启动
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/smart-scheduling-1.0.0.jar
```
后端服务启动在 http://localhost:8080/api

### 3. 启动前端
```bash
cd frontend
npm install
npm run dev
```
前端访问 http://localhost:5173

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 教师 | teacher1 | teacher123 |
| 教师 | teacher2 | teacher123 |
| 教师 | teacher3 | teacher123 |

## 一键排课算法说明

1. **优先级策略**：语文、数学、英语三科优先安排在上午时段（12点前）
2. **冲突检测**：
   - 同一班级同一时间段不能有两门课
   - 同一教师同一时间段不能有两门课
3. **均衡分布**：根据各课程 `weeklyHours`（每周课时数）进行安排
4. **随机均衡**：在满足约束前提下使用随机策略，使课程分布更自然

## 调课规则

- 调课时段不能与该班级已有课冲突
- 调课时段不能与该教师已有课冲突
- 更换教师时，新教师该时段不能已有课
- 每次变更均会记录操作人、变更原因及时间
