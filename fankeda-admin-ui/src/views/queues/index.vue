<script setup lang="ts">
import { computed, h, onMounted, reactive, ref } from 'vue';
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui';
import { NTag } from 'naive-ui';
import { fetchLatestQueues, fetchStalls, reportQueue } from '@/service/api';
import type { QueueSnapshot, Stall } from '@/service/api/fankeda';

defineOptions({
  name: 'Queues'
});

const loading = ref(false);
const submitting = ref(false);
const formRef = ref<FormInst | null>(null);
const stalls = ref<Stall[]>([]);
const queues = ref<QueueSnapshot[]>([]);

const model = reactive({
  stallId: null as number | null,
  queueCount: 0
});

const rules: FormRules = {
  stallId: [{ required: true, type: 'number', message: '请选择窗口', trigger: ['blur', 'change'] }],
  queueCount: [{ required: true, type: 'number', message: '请输入排队人数', trigger: ['blur', 'change'] }]
};

const stallOptions = computed(() => {
  return stalls.value.map(item => ({
    label: `${item.name} / ${item.type}`,
    value: item.stallId
  }));
});

const queueStats = computed(() => {
  if (!queues.value.length) {
    return { totalQueue: 0, busiest: null as QueueSnapshot | null, lastUpdated: '-' };
  }

  const result = queues.value.reduce(
    (acc, item) => ({
      totalQueue: acc.totalQueue + item.queueCount,
      busiest: item.queueCount > acc.busiest.queueCount ? item : acc.busiest,
      lastUpdated: item.createdAt > acc.lastUpdated ? item.createdAt : acc.lastUpdated
    }),
    { totalQueue: 0, busiest: queues.value[0], lastUpdated: '' }
  );

  return { totalQueue: result.totalQueue, busiest: result.busiest, lastUpdated: result.lastUpdated.replace('T', ' ') };
});

const totalQueue = computed(() => queueStats.value.totalQueue);
const busiestQueue = computed(() => queueStats.value.busiest);
const lastUpdated = computed(() => queueStats.value.lastUpdated);

const columns: DataTableColumns<QueueSnapshot> = [
  { title: '窗口', key: 'stallName', minWidth: 140 },
  {
    title: '排队人数',
    key: 'queueCount',
    align: 'center',
    width: 120,
    render: row =>
      h(
        NTag,
        {
          bordered: false,
          type: row.queueCount <= 6 ? 'success' : row.queueCount <= 12 ? 'warning' : 'error'
        },
        { default: () => `${row.queueCount} 人` }
      )
  },
  { title: '上报用户', key: 'reporterId', align: 'center', width: 120, render: row => row.reporterId || '后台/匿名' },
  { title: '更新时间', key: 'createdAt', minWidth: 180, render: row => row.createdAt.replace('T', ' ') }
];

async function loadData() {
  loading.value = true;

  try {
    const [stallResult, queueResult] = await Promise.all([fetchStalls(), fetchLatestQueues()]);

    if (!stallResult.error && stallResult.data) {
      stalls.value = stallResult.data;
      if (!model.stallId && stallResult.data[0]) {
        model.stallId = stallResult.data[0].stallId;
      }
    }

    if (!queueResult.error && queueResult.data) {
      queues.value = queueResult.data;
    }
  } finally {
    loading.value = false;
  }
}

async function handleSubmit() {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }

  if (!model.stallId) {
    return;
  }

  submitting.value = true;

  try {
    const { error } = await reportQueue({
      stallId: model.stallId,
      queueCount: model.queueCount
    });

    if (!error) {
      window.$message?.success('排队人数已上报');
      await loadData();
    }
  } finally {
    submitting.value = false;
  }
}

onMounted(loadData);
</script>

<template>
  <NSpace vertical :size="16">
    <div class="flex flex-wrap items-center justify-between gap-12px">
      <div>
        <h1 class="m-0 text-24px font-700">排队上报</h1>
        <p class="m-0 mt-6px text-14px text-gray-500">人工校准窗口排队人数，供小程序推荐算法实时使用</p>
      </div>
      <NButton :loading="loading" @click="loadData">
        <template #icon>
          <SvgIcon icon="mdi:refresh" />
        </template>
        刷新
      </NButton>
    </div>

    <NGrid responsive="screen" item-responsive :x-gap="16" :y-gap="16">
      <NGi span="24 m:8">
        <NCard :bordered="false" class="card-wrapper">
          <div class="flex items-center justify-between gap-12px">
            <div>
              <p class="m-0 text-13px text-gray-500">当前总排队</p>
              <div class="mt-8px flex items-end gap-4px">
                <strong class="text-30px line-height-none">{{ totalQueue }}</strong>
                <span class="pb-2px text-13px text-gray-500">人</span>
              </div>
            </div>
            <SvgIcon icon="mdi:account-group-outline" class="text-38px text-primary" />
          </div>
        </NCard>
      </NGi>
      <NGi span="24 m:8">
        <NCard :bordered="false" class="card-wrapper">
          <div class="flex items-center justify-between gap-12px">
            <div>
              <p class="m-0 text-13px text-gray-500">最拥挤窗口</p>
              <div class="mt-8px">
                <strong class="text-24px line-height-none">{{ busiestQueue?.stallName || '-' }}</strong>
                <p class="m-0 mt-6px text-13px text-gray-500">{{ busiestQueue?.queueCount || 0 }} 人排队</p>
              </div>
            </div>
            <SvgIcon icon="mdi:alert-circle-outline" class="text-38px text-warning" />
          </div>
        </NCard>
      </NGi>
      <NGi span="24 m:8">
        <NCard :bordered="false" class="card-wrapper">
          <div class="flex items-center justify-between gap-12px">
            <div>
              <p class="m-0 text-13px text-gray-500">最近更新</p>
              <strong class="mt-8px block text-18px line-height-tight">{{ lastUpdated }}</strong>
            </div>
            <SvgIcon icon="mdi:clock-outline" class="text-38px text-info" />
          </div>
        </NCard>
      </NGi>
    </NGrid>

    <NGrid responsive="screen" item-responsive :x-gap="16" :y-gap="16">
      <NGi span="24 l:9">
        <NCard title="人工上报" :bordered="false" class="card-wrapper">
          <NForm ref="formRef" :model="model" :rules="rules" label-placement="top">
            <NFormItem label="窗口" path="stallId">
              <NSelect v-model:value="model.stallId" :options="stallOptions" filterable placeholder="请选择窗口" />
            </NFormItem>
            <NFormItem label="排队人数" path="queueCount">
              <NInputNumber v-model:value="model.queueCount" class="w-full" :min="0" :max="200" />
            </NFormItem>
            <NButton type="primary" block :loading="submitting" @click="handleSubmit">
              <template #icon>
                <SvgIcon icon="mdi:upload-outline" />
              </template>
              提交上报
            </NButton>
          </NForm>
        </NCard>
      </NGi>
      <NGi span="24 l:15">
        <NCard title="最新上报记录" :bordered="false" class="card-wrapper">
          <NDataTable :loading="loading" :columns="columns" :data="queues" :pagination="false" />
        </NCard>
      </NGi>
    </NGrid>
  </NSpace>
</template>
