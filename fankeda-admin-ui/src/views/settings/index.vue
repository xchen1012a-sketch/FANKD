<script setup lang="ts">
defineOptions({
  name: 'Settings'
});

const serviceItems = [
  { label: '前端端口', value: '81', icon: 'mdi:web' },
  { label: '后端服务', value: import.meta.env.VITE_SERVICE_BASE_URL, icon: 'mdi:server' },
  { label: '成功状态码', value: import.meta.env.VITE_SERVICE_SUCCESS_CODE, icon: 'mdi:check-circle-outline' },
  { label: '路由模式', value: import.meta.env.VITE_ROUTER_HISTORY_MODE || 'history', icon: 'mdi:routes' }
];

const configGroups = [
  {
    title: '推荐算法权重',
    status: '代码内置',
    desc: '普通模式和赶时间模式目前写在 Java RecommendService 中，后续可迁移到数据库配置。'
  },
  {
    title: '窗口基础数据',
    status: '内存数据',
    desc: 'MVP 阶段由 FankedaStore 初始化，后续接 MySQL 后支持后台增删改查。'
  },
  {
    title: '反馈处理规则',
    status: '自动调整',
    desc: '用户反馈“不准确”时会降低窗口出餐速度，后台审核接口待补。'
  }
];
</script>

<template>
  <NSpace vertical :size="16">
    <div>
      <h1 class="m-0 text-24px font-700">系统配置</h1>
      <p class="m-0 mt-6px text-14px text-gray-500">查看当前管理端和后端服务配置，规划后续可配置项</p>
    </div>

    <NGrid cols="1 s:2 l:4" responsive="screen" :x-gap="16" :y-gap="16">
      <NGi v-for="item in serviceItems" :key="item.label">
        <NCard :bordered="false" class="card-wrapper">
          <div class="flex items-start justify-between gap-12px">
            <div class="min-w-0">
              <p class="m-0 text-13px text-gray-500">{{ item.label }}</p>
              <strong class="mt-8px block break-all text-18px">{{ item.value }}</strong>
            </div>
            <SvgIcon :icon="item.icon" class="shrink-0 text-30px text-primary" />
          </div>
        </NCard>
      </NGi>
    </NGrid>

    <NCard title="配置项规划" :bordered="false" class="card-wrapper">
      <NSpace vertical :size="12">
        <div
          v-for="item in configGroups"
          :key="item.title"
          class="flex flex-wrap items-center justify-between gap-12px rd-8px bg-gray-100 p-14px dark:bg-dark"
        >
          <div>
            <h3 class="m-0 text-16px font-700">{{ item.title }}</h3>
            <p class="m-0 mt-6px text-13px text-gray-500">{{ item.desc }}</p>
          </div>
          <NTag :bordered="false" type="info">{{ item.status }}</NTag>
        </div>
      </NSpace>
    </NCard>

    <NCard :bordered="false" class="card-wrapper">
      <div class="flex flex-wrap items-center justify-between gap-16px">
        <div>
          <NTag :bordered="false" type="warning">后端待接入</NTag>
          <h2 class="m-0 mt-12px text-20px font-700">在线配置保存接口还没做</h2>
          <p class="m-0 mt-8px text-14px text-gray-500">
            后续可以新增 GET/PUT /api/admin/settings，把算法权重、营业状态、拥挤阈值放进 MySQL。
          </p>
        </div>
        <NButton disabled>
          <template #icon>
            <SvgIcon icon="mdi:content-save-outline" />
          </template>
          保存待接入
        </NButton>
      </div>
    </NCard>
  </NSpace>
</template>
