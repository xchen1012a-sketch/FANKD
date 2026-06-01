<script setup lang="ts">
import { computed, h, onMounted, ref } from 'vue';
import type { DataTableColumns } from 'naive-ui';
import { NTag } from 'naive-ui';
import { fetchRecommend } from '@/service/api';
import type { RecommendResult, RecommendStall } from '@/service/api/fankeda';

defineOptions({
  name: 'Recommend'
});

const loading = ref(false);
const rushMode = ref(false);
const recommend = ref<RecommendResult | null>(null);

const weightText = computed(() => {
  return rushMode.value ? '赶时间模式：耗时 52% / 拥挤 22% / 距离 18% / 口味 8%' : '普通模式：耗时 35% / 拥挤 20% / 距离 18% / 口味 27%';
});

const best = computed(() => recommend.value?.best || null);
const rows = computed(() => recommend.value?.stalls || []);

const columns: DataTableColumns<RecommendStall> = [
  { title: '排名', key: 'rank', align: 'center', width: 70, render: (_row, index) => index + 1 },
  { title: '窗口', key: 'name', minWidth: 130 },
  {
    title: '综合分',
    key: 'score',
    align: 'center',
    width: 100,
    render: row =>
      h(
        NTag,
        {
          bordered: false,
          type: row.score >= 70 ? 'success' : row.score >= 50 ? 'warning' : 'error'
        },
        { default: () => row.score.toFixed(0) }
      )
  },
  { title: '拥挤程度', key: 'level', align: 'center', width: 110 },
  { title: '排队', key: 'queueCount', align: 'center', width: 90, render: row => `${row.queueCount} 人` },
  { title: '等待', key: 'waitTime', align: 'center', width: 90, render: row => `${row.waitTime} 分` },
  { title: '步行', key: 'walkTime', align: 'center', width: 90, render: row => `${row.walkTime} 分` },
  { title: '出餐', key: 'prepTime', align: 'center', width: 90, render: row => `${row.prepTime} 分` },
  { title: '总耗时', key: 'totalTime', align: 'center', width: 100, render: row => `${row.totalTime} 分` },
  { title: '距离', key: 'distance', align: 'center', width: 90, render: row => `${row.distance} 米` },
  { title: '评分', key: 'rating', align: 'center', width: 80 }
];

async function loadData() {
  loading.value = true;

  try {
    const { error, data } = await fetchRecommend(rushMode.value);
    if (!error && data) {
      recommend.value = data;
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
        <h1 class="m-0 text-24px font-700">推荐分析</h1>
        <p class="m-0 mt-6px text-14px text-gray-500">对比推荐排序、耗时拆分和算法权重</p>
      </div>
      <NSpace align="center" :size="12">
        <span class="text-14px text-gray-500">赶时间</span>
        <NSwitch v-model:value="rushMode" @update:value="loadData" />
        <NButton type="primary" :loading="loading" @click="loadData">
          <template #icon>
            <SvgIcon icon="mdi:refresh" />
          </template>
          刷新
        </NButton>
      </NSpace>
    </div>

    <NGrid responsive="screen" item-responsive :x-gap="16" :y-gap="16">
      <NGi span="24 l:8">
        <NCard :bordered="false" class="card-wrapper">
          <div class="flex items-start justify-between gap-12px">
            <div>
              <p class="m-0 text-13px text-gray-500">当前最优推荐</p>
              <h2 class="m-0 mt-8px text-28px font-700">{{ best?.name || '-' }}</h2>
              <p class="m-0 mt-6px text-14px text-gray-500">
                {{ best ? `${best.level} / ${best.queueCount} 人排队` : '暂无推荐数据' }}
              </p>
            </div>
            <div class="size-48px flex-center rd-8px bg-primary text-26px text-white">
              <SvgIcon icon="mdi:star-outline" />
            </div>
          </div>
          <NDivider />
          <NGrid cols="3" :x-gap="12">
            <NGi>
              <p class="m-0 text-12px text-gray-500">综合分</p>
              <strong class="mt-4px block text-22px">{{ best?.score.toFixed(0) || '-' }}</strong>
            </NGi>
            <NGi>
              <p class="m-0 text-12px text-gray-500">总耗时</p>
              <strong class="mt-4px block text-22px">{{ best?.totalTime || '-' }} 分</strong>
            </NGi>
            <NGi>
              <p class="m-0 text-12px text-gray-500">距离</p>
              <strong class="mt-4px block text-22px">{{ best?.distance || '-' }} 米</strong>
            </NGi>
          </NGrid>
        </NCard>
      </NGi>

      <NGi span="24 l:16">
        <NCard title="算法权重" :bordered="false" class="card-wrapper">
          <p class="m-0 text-14px text-gray-600 dark:text-gray-300">{{ weightText }}</p>
          <NGrid class="mt-16px" cols="2 m:4" responsive="screen" :x-gap="12" :y-gap="12">
            <NGi>
              <div class="rd-8px bg-gray-100 p-12px dark:bg-dark">
                <p class="m-0 text-12px text-gray-500">等待时间</p>
                <strong class="mt-6px block text-20px">{{ best?.waitTime || '-' }} 分</strong>
              </div>
            </NGi>
            <NGi>
              <div class="rd-8px bg-gray-100 p-12px dark:bg-dark">
                <p class="m-0 text-12px text-gray-500">步行时间</p>
                <strong class="mt-6px block text-20px">{{ best?.walkTime || '-' }} 分</strong>
              </div>
            </NGi>
            <NGi>
              <div class="rd-8px bg-gray-100 p-12px dark:bg-dark">
                <p class="m-0 text-12px text-gray-500">出餐时间</p>
                <strong class="mt-6px block text-20px">{{ best?.prepTime || '-' }} 分</strong>
              </div>
            </NGi>
            <NGi>
              <div class="rd-8px bg-gray-100 p-12px dark:bg-dark">
                <p class="m-0 text-12px text-gray-500">口味评分</p>
                <strong class="mt-6px block text-20px">{{ best?.rating || '-' }} 分</strong>
              </div>
            </NGi>
          </NGrid>
        </NCard>
      </NGi>
    </NGrid>

    <NCard title="推荐排序明细" :bordered="false" class="card-wrapper">
      <NDataTable :loading="loading" :columns="columns" :data="rows" :pagination="false" :scroll-x="1120" />
    </NCard>
  </NSpace>
</template>
