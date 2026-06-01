# 饭刻达 — 校园智能就餐助手 MVP 落地计划书

## 一、项目概述

**产品名称**：饭刻达（FanKeDa）

**一句话定位**：校园午高峰智能就餐推荐小程序，用排队预测和赶课推荐帮学生最快吃上饭。

**解决的问题**：

- 学生午高峰不知道哪个窗口人少
- 赶课时没时间对比，只能盲选排队
- 食堂拥挤信息不透明，决策成本高

**创新方法**：奥斯本检核表法（借用、组合、颠倒三问）

---

## 二、核心功能（MVP 范围）

| 功能     | 说明                           | 优先级 |
| ------ | ---------------------------- | --- |
| 智能推荐   | 根据排队、距离、出餐速度、口味加权评分，推荐最省时窗口  | P0  |
| 赶课模式   | 开关式，开启后提高时间权重，过滤掉总耗时>14分钟的窗口 | P0  |
| 众包上报   | 学生手动上报窗口排队人数，数据实时更新          | P0  |
| 食堂热力图  | 用颜色标注各窗口拥挤程度（绿/黄/橙/红）        | P1  |
| 反馈校准   | "准/不准"按钮，校准窗口出餐速度参数          | P1  |
| 用户统计   | 累计省时、上报次数、预测命中率              | P2  |
| 下课时间设置 | 手动设置下节课时间，赶课模式自动生效           | P2  |

**不在 MVP 范围**：

- 课程表自动导入（二期）
- 多食堂切换（二期）
- 拼单/点餐功能（二期）
- 硬件数据采集（三期）

---

## 三、推荐算法

### 核心公式

```
总耗时 = 排队等待 + 步行时间 + 备餐时间

其中：
  排队等待 = 排队人数 ÷ 出餐速度（人/分钟）
  步行时间 = 距离（米）÷ 步行速度（70米/分钟）
  备餐时间 = 窗口平均备餐时间（分钟）

推荐分 = 时间分×权重₁ + 拥挤分×权重₂ + 距离分×权重₃ + 口味分×权重₄
```

### 权重分配

| 维度  | 普通模式 | 赶课模式 |
| --- | ---- | ---- |
| 时间分 | 35%  | 52%  |
| 拥挤分 | 20%  | 22%  |
| 距离分 | 18%  | 18%  |
| 口味分 | 27%  | 8%   |

### 评分计算

```
时间分 = max(0, 100 - 总耗时 × 系数)
拥挤分 = max(0, 100 - (排队人数/26) × 系数)
距离分 = max(0, 100 - 距离/2.8)
口味分 = 评分(5分制) × 20
```

### 赶课模式过滤

开启后，总耗时 > 14 分钟的窗口自动排除（14 = 下课间隔18分钟 - 4分钟缓冲）。

### 反馈校准

用户反馈"不准"时，该窗口出餐速度下调 0.12 人/分钟，影响后续推荐。

---

## 四、技术架构

```
┌─────────────────┐     HTTP/JSON     ┌──────────────────────┐
│   UniApp 前端    │ ◄──────────────► │  Spring Boot 后端     │
│  (微信小程序)    │                   │  (若依 RuoYi 框架)    │
└─────────────────┘                   │                      │
                                      │  ┌──────────────┐    │
┌─────────────────┐     HTTP/JSON     │  │ Recommend    │    │
│   管理后台       │ ◄──────────────► │  │ Service      │    │
│  (Vue3+Element)  │                   │  └──────────────┘    │
└─────────────────┘                   │  ┌──────────────┐    │
                                      │  │ MySQL 数据库  │    │
                                      │  └──────────────┘    │
                                      └──────────────────────┘
```

### 技术选型

| 层      | 技术                         | 理由              |
| ------ | -------------------------- | --------------- |
| 后端框架   | Spring Boot 3.2 (若依 RuoYi) | 主流、内置权限管理、代码生成器 |
| ORM    | MyBatis-Plus               | 简化 CRUD，少写 SQL  |
| 数据库    | MySQL 8                    | 成熟稳定，学校常见       |
| 小程序前端  | UniApp Vue3                | 一套代码→微信小程序      |
| 管理后台前端 | Vue3 + Element Plus (若依内置) | 表格、图表、权限开箱即用    |
| 认证     | JWT                        | 无状态，前后端分离友好     |

---

## 五、数据库设计

### 表结构（5 张表）

```sql
-- 窗口表（预置 6 条）
CREATE TABLE stall (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  type VARCHAR(100),
  pos_x INT, pos_y INT,
  serve_speed DOUBLE,
  distance INT,
  avg_prep DOUBLE,
  rating DOUBLE DEFAULT 4.0
);

-- 排队快照（众包上报）
CREATE TABLE queue_snapshot (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  stall_id BIGINT NOT NULL,
  queue_count INT NOT NULL,
  reporter_id BIGINT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户表
CREATE TABLE `user` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  openid VARCHAR(100) NOT NULL UNIQUE,
  nickname VARCHAR(50),
  class_end_time VARCHAR(10),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 反馈表
CREATE TABLE feedback (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  stall_id BIGINT NOT NULL,
  is_accurate TINYINT NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 统计表
CREATE TABLE report_stat (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  report_count INT DEFAULT 0,
  time_saved INT DEFAULT 0,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 预置数据（一食堂 6 窗口）

| 窗口   | 品类     | 出餐速度   | 距离   | 备餐时间 | 评分  |
| ---- | ------ | ------ | ---- | ---- | --- |
| 快餐米饭 | 盖浇饭/小炒 | 2.3人/分 | 128m | 2.5分 | 4.4 |
| 面食窗口 | 汤面/拌面  | 1.7人/分 | 102m | 3.5分 | 4.6 |
| 轻食套餐 | 沙拉/鸡胸  | 1.5人/分 | 96m  | 2.2分 | 4.1 |
| 麻辣香锅 | 自选称重   | 1.1人/分 | 168m | 7.0分 | 4.8 |
| 包子粥铺 | 包子/粥   | 2.9人/分 | 54m  | 1.3分 | 4.0 |
| 预制套餐 | 固定套餐   | 3.2人/分 | 88m  | 1.8分 | 4.2 |

---

## 六、API 接口设计

| #   | Method | Path                | 说明        | 请求体/参数                  |
| --- | ------ | ------------------- | --------- | ----------------------- |
| 1   | POST   | `/api/auth/login`   | 微信登录      | `{code}` → JWT          |
| 2   | GET    | `/api/stalls`       | 窗口列表+最新排队 | —                       |
| 3   | GET    | `/api/recommend`    | 推荐结果      | `?rushMode=false`       |
| 4   | POST   | `/api/queue/report` | 上报排队人数    | `{stallId, queueCount}` |
| 5   | GET    | `/api/queue/latest` | 最新快照      | —                       |
| 6   | POST   | `/api/feedback`     | 反馈        | `{stallId, isAccurate}` |
| 7   | GET    | `/api/user/profile` | 个人信息      | —                       |
| 8   | PUT    | `/api/user/profile` | 更新设置      | `{classEndTime}`        |

### 接口示例

**GET /api/recommend?rushMode=false 响应**：

```json
{
  "code": 0,
  "msg": "ok",
  "data": {
    "best": {
      "stallId": 5,
      "name": "包子粥铺",
      "score": 86,
      "totalTime": 4,
      "waitTime": 2,
      "walkTime": 1,
      "prepTime": 1.3
    },
    "stalls": [
      {"stallId": 5, "name": "包子粥铺", "score": 86, "level": "人少"},
      {"stallId": 3, "name": "轻食套餐", "score": 79, "level": "人少"},
      {"stallId": 1, "name": "快餐米饭", "score": 72, "level": "一般"},
      {"stallId": 6, "name": "预制套餐", "score": 68, "level": "一般"},
      {"stallId": 2, "name": "面食窗口", "score": 55, "level": "较挤"},
      {"stallId": 4, "name": "麻辣香锅", "score": 31, "level": "爆满"}
    ]
  }
}
```

---

## 七、管理后台（基于若依 RuoYi）

### 为什么选若依

若依是国内最流行的 Spring Boot 开源管理框架，内置：

- 用户/角色/权限管理（RBAC）
- 菜单配置 + 动态路由
- 数据表格 + 分页 + 搜索
- 代码生成器（自动生成 Entity/Mapper/Service/Controller/Vue 页面）
- 日志管理、定时任务、系统监控

**对本项目的价值**：不用从零搭建后台，只需在若依基础上新增业务模块。

### 管理后台功能

| 模块   | 功能         | 说明                 |
| ---- | ---------- | ------------------ |
| 窗口管理 | CRUD 窗口数据  | 编辑窗口名称、出餐速度、距离等参数  |
| 排队数据 | 查看上报记录     | 按时间/窗口筛选，导出 Excel  |
| 用户管理 | 查看小程序用户    | 绑定 openid、查看上报统计   |
| 数据看板 | ECharts 图表 | 各窗口排队趋势、推荐命中率、活跃用户 |
| 反馈管理 | 查看准/不准反馈   | 按窗口统计反馈比例          |
| 系统设置 | 推荐算法参数     | 调整权重系数、步行速度等       |

### 接入方式

若依是标准的 Spring Boot + Vue 前后端分离架构：

```
若依后端 (ruoyi-admin)
├── 新增 com.ruoyi.fankeda 包
│   ├── controller/StallController.java
│   ├── controller/QueueController.java
│   ├── controller/RecommendController.java
│   ├── service/...
│   └── mapper/...
└── 新增业务菜单配置

若依前端 (ruoyi-ui-vue3)
├── 新增 views/fankeda/
│   ├── stall/index.vue        # 窗口管理页
│   ├── queue/index.vue        # 排队数据页
│   ├── dashboard/index.vue    # 数据看板
│   └── feedback/index.vue     # 反馈管理页
└── 新增菜单路由配置
```

### 数据看板图表

用 ECharts（若依已集成）展示：

1. **实时排队柱状图** — 各窗口当前排队人数对比
2. **排队趋势折线图** — 过去 7 天各窗口高峰时段人流
3. **推荐命中率饼图** — 用户实际去的窗口 vs 推荐窗口的吻合度
4. **上报活跃度** — 每日上报次数趋势
5. **反馈分布** — 准 vs 不准的比例

---

## 八、小程序页面设计

### 页面结构（4 页）

```
┌─────────────────────────────────────┐
│  首页（推荐）│ 食堂 │ 上报 │ 我的  │  ← 底部 TabBar
└─────────────────────────────────────┘

首页：推荐卡片 + 赶课模式开关 + 快捷入口
食堂：热力图 + 窗口列表（按等待时间排序）
上报：选窗口 + 步进器调人数 + 提交
我的：统计 + 设置下课时间 + 反馈入口
```

### 核心交互流程

```
打开小程序
    │
    ▼
首页显示推荐窗口 ──→ 开启赶课模式 ──→ 推荐结果变化
    │
    ▼
切到食堂页 ──→ 看热力图 ──→ 点击窗口查看详情
    │
    ▼
切到上报页 ──→ 选窗口 ──→ 调人数 ──→ 提交
    │
    ▼
回到首页 ──→ 推荐已刷新
```

### 配色方案（从原型移植）

```css
主色: #24577a (深蓝)
强调: #35b779 (绿)
警告: #ffd166 (黄)
危险: #ff8a5b (橙) / #ef5d60 (红)
背景: #f6fbff (浅蓝白)
文字: #173149 (深色) / #5f7182 (次要)
```

---

## 九、开发排期（10 天）

### Day 1：后端骨架（若依）

- 克隆若依 RuoYi-Vue 项目
- MySQL 建库 + 导入若依基础表 + 新建饭刻达业务表
- 预置 6 个窗口数据
- 若依项目启动验证

### Day 2：业务模块开发

- 若依代码生成器生成 stall、queue_snapshot、feedback 表的 CRUD
- 新增 RecommendService（推荐算法移植）
- 新增 RecommendController（GET /api/recommend）
- 新增 QueueController（POST report + GET latest）

### Day 3：小程序 API 适配

- 新增 AuthController（微信登录，独立于若依的管理后台登录）
- 新增小程序专用 JWT 鉴权
- 新增反馈校准逻辑
- 新增用户设置接口
- Postman 验证全部小程序 API

### Day 4：管理后台页面

- 若依菜单配置：窗口管理、排队数据、数据看板、反馈管理
- 数据看板 ECharts 图表：实时排队柱状图、上报趋势折线图
- 反馈统计图表

### Day 5：UniApp 前端骨架

- UniApp 项目初始化（Vue3）
- pages.json 配置 4 页 + tabBar
- request.js 封装（JWT 自动携带）
- 4 页基础布局

### Day 6：首页 + 食堂页

- 首页推荐卡片（调 /api/recommend）
- 赶课模式开关（切换后重新请求）
- 食堂热力图（CSS 定位色块）
- 窗口列表（按等待时间排序）

### Day 7：上报 + 我的

- 上报页步进器 + 提交
- 我的页统计展示
- 下课时间设置
- 反馈按钮（准/不准）

### Day 8：联调

- 前后端联调，走通完整流程
- 管理后台数据验证
- 边界处理（无数据、网络错误）

### Day 9：样式打磨

- 对齐原型配色方案
- 细节优化（动画、加载态、空状态）
- 微信开发者工具真机预览

### Day 10：收尾

- 完整流程测试
- 演示准备（录屏/截图）
- 文档整理

---

## 十、演示话术（1 分钟）

> "我们用奥斯本检核表法改造了食堂排队工具。核心创新有三个：
> 
> 第一，智能推荐。我们把排队时间、步行距离、出餐速度、口味评分量化成一个公式，自动算出最省时的窗口。
> 
> 第二，赶课模式。离上课近了，系统自动提高时间权重，过滤掉慢窗口，只推荐能赶上上课的选择。
> 
> 第三，众包上报。不用摄像头，学生手动上报排队人数，加上'准不准'反馈校准模型，低成本就能落地。
> 
> 目前在一食堂试点 6 个窗口，推荐算法已经在小程序里跑通了。"

---

## 十一、未来扩展

| 阶段  | 内容                       |
| --- | ------------------------ |
| 二期  | 对接教务系统自动导入课程表、多食堂切换、拼单功能 |
| 三期  | 对接刷卡系统自动获取排队数据、历史数据预测    |
| 四期  | 硬件采集（摄像头/传感器）、校园地图集成     |
