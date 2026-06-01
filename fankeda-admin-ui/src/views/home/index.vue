<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import type { DataTableColumns } from 'naive-ui';
import { fetchLatestQueues, fetchRecommend, fetchStalls } from '@/service/api';
import type { QueueSnapshot, RecommendResult, Stall } from '@/service/api/fankeda';

defineOptions({
  name: 'Home'
});

const loading = ref(false);
const stalls = ref<Stall[]>([]);
const queues = ref<QueueSnapshot[]>([]);
const recommend = ref<RecommendResult | null>(null);

const stats = computed(() => {
  const { totalQueue, sumWait, crowded } = stalls.value.reduce(
    (acc, item) => ({
      totalQueue: acc.totalQueue + item.queueCount,
      sumWait: acc.sumWait + item.waitTime,
      crowded: acc.crowded + (item.level === '较挤' || item.level === '拥挤' ? 1 : 0)
    }),
    { totalQueue: 0, sumWait: 0, crowded: 0 }
  );
  const avgWait = stalls.value.length ? sumWait / stalls.value.length : 0;
  const best = recommend.value?.best;

  return [
    {
      label: '营业窗口',
      value: stalls.value.length,
      suffix: '个',
      icon: 'mdi:storefront-outline',
      color: '#2563eb'
    },
    {
      label: '当前排队',
      value: totalQueue,
      suffix: '人',
      icon: 'mdi:account-group-outline',
      color: '#16a34a'
    },
    {
      label: '平均等待',
      value: avgWait.toFixed(1),
      suffix: '分钟',
      icon: 'mdi:timer-outline',
      color: '#f59e0b'
    },
    {
      label: '较挤窗口',
      value: crowded,
      suffix: '个',
      icon: 'mdi:alert-circle-outline',
      color: '#dc2626'
    },
    {
      label: '推荐窗口',
      value: best?.name || '-',
      suffix: '',
      icon: 'mdi:star-outline',
      color: '#7c3aed'
    }
  ];
});

const queueColumns: DataTableColumns<Stall> = [
  { title: '窗口', key: 'name', minWidth: 120 },
  { title: '品类', key: 'type', minWidth: 120 },
  { title: '排队人数', key: 'queueCount', align: 'center', width: 100 },
  { title: '拥挤程度', key: 'level', align: 'center', width: 100 },
  { title: '等待时间', key: 'waitTime', align: 'center', width: 110, render: row => `${row.waitTime} 分钟` },
  { title: '总耗时', key: 'totalTime', align: 'center', width: 110, render: row => `${row.totalTime} 分钟` },
  { title: '评分', key: 'rating', align: 'center', width: 90 }
];

const latestColumns: DataTableColumns<QueueSnapshot> = [
  { title: '窗口', key: 'stallName', minWidth: 120 },
  { title: '排队人数', key: 'queueCount', align: 'center', width: 110 },
  { title: '上报用户', key: 'reporterId', align: 'center', width: 110, render: row => row.reporterId || '-' },
  { title: '更新时间', key: 'createdAt', minWidth: 180, render: row => row.createdAt.replace('T', ' ') }
];

async function loadData() {
  loading.value = true;

  try {
    const [stallResult, queueResult, recommendResult] = await Promise.all([
      fetchStalls(),
      fetchLatestQueues(),
      fetchRecommend(false)
    ]);

    if (!stallResult.error && stallResult.data) {
      stalls.value = stallResult.data;
    }

    if (!queueResult.error && queueResult.data) {
      queues.value = queueResult.data;
    }

    if (!recommendResult.error && recommendResult.data) {
      recommend.value = recommendResult.data;
    }
  } finally {
    loading.value = false;
  }
}

onMounted(loadData);
</script>

<template>
  <NSpace vertical :size="16">
    <div class="flex items-center justify-between gap-12px">
      <div>
        <h1 class="m-0 text-24px font-700">饭刻达数据看板</h1>
        <p class="m-0 mt-6px text-14px text-gray-500">食堂窗口排队、等待时间和推荐结果实时概览</p>
      </div>
      <NButton type="primary" :loading="loading" @click="loadData">
        <template #icon>
          <SvgIcon icon="mdi:refresh" />
        </template>
        刷新
      </NButton>
    </div>

    <NGrid cols="1 s:2 l:5" responsive="screen" :x-gap="16" :y-gap="16">
      <NGi v-for="item in stats" :key="item.label">
        <NCard :bordered="false" class="card-wrapper">
          <div class="flex items-center justify-between gap-12px">
            <div>
              <p class="m-0 text-13px text-gray-500">{{ item.label }}</p>
              <div class="mt-8px flex items-end gap-4px">
                <strong class="text-26px line-height-none">{{ item.value }}</strong>
                <span class="pb-2px text-13px text-gray-500">{{ item.suffix }}</span>
              </div>
            </div>
            <div class="size-42px flex-center rd-8px text-24px text-white" :style="{ backgroundColor: item.color }">
              <SvgIcon :icon="item.icon" />
            </div>
          </div>
        </NCard>
      </NGi>
    </NGrid>

    <NGrid responsive="screen" item-responsive :x-gap="16" :y-gap="16">
      <NGi span="24 l:15">
        <NCard title="窗口实时队列" :bordered="false" class="card-wrapper">
          <NDataTable :loading="loading" :columns="queueColumns" :data="stalls" :pagination="false" />
        </NCard>
      </NGi>
      <NGi span="24 l:9">
        <NCard title="推荐排序" :bordered="false" class="card-wrapper">
          <NList v-if="recommend?.stalls.length" hoverable clickable>
            <NListItem v-for="item in recommend.stalls" :key="item.stallId">
              <div class="flex items-center justify-between gap-12px">
                <div>
                  <p class="m-0 font-600">{{ item.name }}</p>
                  <p class="m-0 mt-4px text-13px text-gray-500">
                    {{ item.level }} · {{ item.totalTime }} 分钟 · {{ item.queueCount }} 人
                  </p>
                </div>
                <NTag :bordered="false" type="success">评分 {{ item.score.toFixed(0) }}</NTag>
              </div>
            </NListItem>
          </NList>
          <NEmpty v-else description="暂无推荐数据" />
        </NCard>
      </NGi>
    </NGrid>

    <NCard title="最新上报" :bordered="false" class="card-wrapper">
      <NDataTable :loading="loading" :columns="latestColumns" :data="queues" :pagination="false" />
    </NCard>
  </NSpace>
</template>
