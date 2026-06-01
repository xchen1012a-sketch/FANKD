<script setup lang="ts">
import { computed, h, onMounted, ref } from 'vue';
import type { DataTableColumns } from 'naive-ui';
import { NTag } from 'naive-ui';
import { fetchAdminFeedback } from '@/service/api';
import type { AdminFeedback } from '@/service/api/fankeda';

defineOptions({
  name: 'Feedback'
});

const loading = ref(false);
const rows = ref<AdminFeedback[]>([]);

const feedbackStats = computed(() => {
  const { accurate, inaccurate } = rows.value.reduce(
    (acc, item) => ({
      accurate: acc.accurate + (item.isAccurate ? 1 : 0),
      inaccurate: acc.inaccurate + (item.isAccurate ? 0 : 1)
    }),
    { accurate: 0, inaccurate: 0 }
  );
  const rate = rows.value.length ? `${Math.round((accurate / rows.value.length) * 100)}%` : '-';
  return { inaccurate, rate };
});

const inaccurateCount = computed(() => feedbackStats.value.inaccurate);
const accurateRate = computed(() => feedbackStats.value.rate);

const columns: DataTableColumns<AdminFeedback> = [
  { title: '反馈编号', key: 'id', align: 'center', width: 100 },
  { title: '用户', key: 'userNickname', minWidth: 120 },
  { title: '窗口', key: 'stallName', minWidth: 130 },
  {
    title: '是否准确',
    key: 'isAccurate',
    align: 'center',
    width: 120,
    render: row =>
      h(
        NTag,
        {
          bordered: false,
          type: row.isAccurate ? 'success' : 'warning'
        },
        { default: () => (row.isAccurate ? '准确' : '不准确') }
      )
  },
  { title: '提交时间', key: 'createdAt', minWidth: 180, render: row => row.createdAt.replace('T', ' ') }
];

async function loadData() {
  loading.value = true;

  try {
    const { error, data } = await fetchAdminFeedback();
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
        <h1 class="m-0 text-24px font-700">用户反馈</h1>
        <p class="m-0 mt-6px text-14px text-gray-500">管理小程序用户对推荐准确性的反馈</p>
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
          <p class="m-0 text-13px text-gray-500">反馈总数</p>
          <strong class="mt-8px block text-28px">{{ rows.length }}</strong>
        </NCard>
      </NGi>
      <NGi>
        <NCard :bordered="false" class="card-wrapper">
          <p class="m-0 text-13px text-gray-500">不准确反馈</p>
          <strong class="mt-8px block text-28px">{{ inaccurateCount }}</strong>
        </NCard>
      </NGi>
      <NGi>
        <NCard :bordered="false" class="card-wrapper">
          <p class="m-0 text-13px text-gray-500">准确率</p>
          <strong class="mt-8px block text-28px">{{ accurateRate }}</strong>
        </NCard>
      </NGi>
    </NGrid>

    <NCard title="反馈列表" :bordered="false" class="card-wrapper">
      <NDataTable :loading="loading" :columns="columns" :data="rows" :pagination="{ pageSize: 8 }" />
    </NCard>
  </NSpace>
</template>
