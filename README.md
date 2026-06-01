# 饭刻达 FANKD

饭刻达是一个校园智能就餐助手 MVP，面向学生午高峰排队时间不透明、赶课时间紧的问题，提供窗口排队上报、拥挤热力、最省时窗口推荐和管理后台维护能力。

## 项目结构

```text
.
├── fankeda/             # Spring Boot 后端 API
├── fanf-uniapp/         # uni-app Vue3 小程序/H5 前端
├── fankeda-admin-ui/    # Vue3 + Vite + Naive UI 管理后台
├── README.md
└── .gitignore
```

## 核心功能

- 智能推荐：按排队人数、步行距离、备餐时间和评分计算推荐排序。
- 赶课模式：提高时间权重，并过滤总耗时过高的窗口。
- 众包上报：学生可上报各窗口实时排队人数。
- 反馈校准：用户反馈推荐是否准确，用于修正窗口出餐速度。
- 管理后台：支持窗口管理、队列数据、用户和反馈查看。

## 技术栈

- 后端：Java 25、Spring Boot 4、Spring Data JPA、Flyway、MySQL、H2 测试库
- 小程序端：uni-app、Vue3、Vite、Wot UI
- 管理后台：Vue3、TypeScript、Vite、Naive UI、UnoCSS、ECharts、pnpm workspace

## 后端启动

准备 MySQL 数据库：

```sql
CREATE DATABASE fankeda DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

配置环境变量，或复制示例配置：

```bash
cd fankeda
copy config\application-local.example.properties config\application-local.properties
```

`application-local.properties` 只保留在本地，不要提交到仓库。

启动后端：

```bash
cd fankeda
.\mvnw.cmd spring-boot:run
```

运行测试：

```bash
cd fankeda
.\mvnw.cmd test
```

后端默认地址：`http://localhost:8080`。数据库表由 Flyway 迁移文件自动创建。

## 小程序端启动

```bash
cd fanf-uniapp
npm install
npm run dev:h5
```

微信小程序开发：

```bash
npm run dev:mp-weixin
```

默认 API 地址为 `http://localhost:8080`，可通过 `VITE_API_BASE_URL` 覆盖。

## 管理后台启动

```bash
cd fankeda-admin-ui
pnpm install
pnpm dev
```

开发服务默认端口为 `81`，接口地址配置在 `.env.test` / `.env.prod` 中，默认指向 `http://localhost:8080/api`。

构建：

```bash
pnpm build
```

## 主要接口

后端统一返回：

```json
{ "code": 0, "msg": "ok", "data": {} }
```

常用接口：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录并获取 token |
| GET | `/api/stalls` | 获取窗口与排队信息 |
| GET | `/api/recommend?rushMode=false` | 获取推荐排序 |
| POST | `/api/queue/report` | 上报排队人数 |
| GET | `/api/queue/latest` | 获取最新排队快照 |
| POST | `/api/feedback` | 提交推荐反馈 |
| GET | `/api/user/profile` | 获取用户资料 |
| PUT | `/api/user/profile` | 更新用户资料 |
| GET/POST/PUT/DELETE | `/api/admin/*` | 管理后台接口 |

## 上传到 GitHub

本仓库已按以下规则清理并忽略开发缓存：

- 不提交 `node_modules/`
- 不提交 Maven `target/`
- 不提交本地 IDE 配置
- 不提交本地数据库密码配置
- 不提交本地 AI 工具状态目录

首次推送示例：

```bash
git init
git remote add origin https://github.com/xchen1012a-sketch/FANKD.git
git add .
git commit -m "Initial commit"
git branch -M main
git push -u origin main
```

