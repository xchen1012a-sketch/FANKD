<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import type { DataTableColumns } from 'naive-ui';
import { fetchAdminUsers } from '@/service/api';
import type { AdminUser } from '@/service/api/fankeda';

defineOptions({
  name: 'Users'
});

const loading = ref(false);
const rows = ref<AdminUser[]>([]);

const userStats = computed(() =>
  rows.value.reduce(
    (acc, item) => ({
      totalReports: acc.totalReports + item.reportCount,
      totalTimeSaved: acc.totalTimeSaved + item.timeSaved,
      classEndConfigured: acc.classEndConfigured + (item.classEndTime ? 1 : 0)
    }),
    { totalReports: 0, totalTimeSaved: 0, classEndConfigured: 0 }
  )
);

const totalReports = computed(() => userStats.value.totalReports);
const totalTimeSaved = computed(() => userStats.value.totalTimeSaved);
const classEndConfigured = computed(() => userStats.value.classEndConfigured);

const columns: DataTableColumns<AdminUser> = [
  { title: '用户ID', key: 'id', align: 'center', width: 90 },
  { title: '昵称', key: 'nickname', minWidth: 120 },
  { title: 'OpenID', key: 'openid', minWidth: 220 },
  { title: '下课时间', key: 'classEndTime', align: 'center', width: 120, render: row => row.classEndTime || '-' },
  { title: '上报次数', key: 'reportCount', align: 'center', width: 120 },
  { title: '节省时间', key: 'timeSaved', align: 'center', width: 120, render: row => `${row.timeSaved} 分钟` },
  { title: '创建时间', key: 'createdAt', minWidth: 180, render: row => row.createdAt.replace('T', ' ') }
];

async function loadData() {
  loading.value = true;

  try {
    const { error, data } = await fetchAdminUsers();
    if (!error && data) {
      rows.value = data;
    }
  } finally {
    loading.value = false;
  }
}

onMounted(loadData);
</script>

<template>
  <NSpace vertical :size="16">
    <div class="flex flex-wrap items-center justify-between gap-12px">
      <div>
        <h1 class="m-0 text-24px font-700">小程序用户</h1>
        <p class="m-0 mt-6px text-14px text-gray-500">查看用户画像、使用行为和推荐偏好</p>
      </div>
      <NButton type="primary" :loading="loading" @click="loadData">
        <template #icon>
          <SvgIcon icon="mdi:refresh" />
        </template>
        刷新
      </NButton>
    </div>

    <NGrid cols="1 m:3" responsive="screen" :x-gap="16" :y-gap="16">
      <NGi>
        <NCard :bordered="false" class="card-wrapper">
          <p class="m-0 text-13px text-gray-500">用户数</p>
          <strong class="mt-8px block text-28px">{{ rows.length }}</strong>
        </NCard>
      </NGi>
      <NGi>
        <NCard :bordered="false" class="card-wrapper">
          <p class="m-0 text-13px text-gray-500">队列上报</p>
          <strong class="mt-8px block text-28px">{{ totalReports }}</strong>
        </NCard>
      </NGi>
      <NGi>
        <NCard :bordered="false" class="card-wrapper">
          <p class="m-0 text-13px text-gray-500">已设下课时间</p>
          <strong class="mt-8px block text-28px">{{ classEndConfigured }}</strong>
        </NCard>
      </NGi>
    </NGrid>

    <NCard title="用户列表" :bordered="false" class="card-wrapper">
      <NDataTable :loading="loading" :columns="columns" :data="rows" :pagination="{ pageSize: 8 }" :scroll-x="970" />
    </NCard>
  </NSpace>
</template>
