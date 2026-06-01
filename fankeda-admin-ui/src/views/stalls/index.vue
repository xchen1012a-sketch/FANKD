<script setup lang="ts">
import { computed, h, onMounted, reactive, ref } from 'vue';
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui';
import { NButton, NTag } from 'naive-ui';
import { createAdminStall, deleteAdminStall, fetchStalls, updateAdminStall } from '@/service/api';
import type { AdminStallPayload, Stall } from '@/service/api/fankeda';

defineOptions({
  name: 'Stalls'
});

const loading = ref(false);
const submitting = ref(false);
const modalVisible = ref(false);
const editingId = ref<number | null>(null);
const keyword = ref('');
const stalls = ref<Stall[]>([]);
const formRef = ref<FormInst | null>(null);

const DEFAULT_STALL: AdminStallPayload = {
  name: '',
  type: '',
  posX: 0,
  posY: 0,
  serveSpeed: 1,
  distance: 0,
  avgPrep: 1,
  rating: 4
};

const model = reactive<AdminStallPayload>({ ...DEFAULT_STALL });

const rules: FormRules = {
  name: [{ required: true, message: '请输入窗口名称', trigger: ['blur', 'input'] }],
  type: [{ required: true, message: '请输入窗口品类', trigger: ['blur', 'input'] }],
  posX: [{ required: true, type: 'number', message: '请输入 X 坐标', trigger: ['blur', 'change'] }],
  posY: [{ required: true, type: 'number', message: '请输入 Y 坐标', trigger: ['blur', 'change'] }],
  serveSpeed: [{ required: true, type: 'number', message: '请输入出餐速度', trigger: ['blur', 'change'] }],
  distance: [{ required: true, type: 'number', message: '请输入距离', trigger: ['blur', 'change'] }],
  avgPrep: [{ required: true, type: 'number', message: '请输入平均备餐时间', trigger: ['blur', 'change'] }],
  rating: [{ required: true, type: 'number', message: '请输入评分', trigger: ['blur', 'change'] }]
};

const filteredStalls = computed(() => {
  const value = keyword.value.trim().toLowerCase();

  if (!value) {
    return stalls.value;
  }

  return stalls.value.filter(item => {
    return item.name.toLowerCase().includes(value) || item.type.toLowerCase().includes(value);
  });
});

const overview = computed(() => {
  const { totalQueue, sumServeSpeed, sumRating, busyCount } = stalls.value.reduce(
    (acc, item) => ({
      totalQueue: acc.totalQueue + item.queueCount,
      sumServeSpeed: acc.sumServeSpeed + item.serveSpeed,
      sumRating: acc.sumRating + item.rating,
      busyCount: acc.busyCount + (item.level === '较挤' || item.level === '爆满' ? 1 : 0)
    }),
    { totalQueue: 0, sumServeSpeed: 0, sumRating: 0, busyCount: 0 }
  );
  const count = stalls.value.length;
  const avgServeSpeed = count ? sumServeSpeed / count : 0;
  const avgRating = count ? sumRating / count : 0;

  return [
    { label: '营业窗口', value: count, suffix: '个', icon: 'mdi:storefront-outline', color: '#2563eb' },
    { label: '总排队人数', value: totalQueue, suffix: '人', icon: 'mdi:account-group-outline', color: '#16a34a' },
    {
      label: '平均出餐速度',
      value: avgServeSpeed.toFixed(1),
      suffix: '人/分钟',
      icon: 'mdi:speedometer',
      color: '#7c3aed'
    },
    { label: '平均评分', value: avgRating.toFixed(1), suffix: '分', icon: 'mdi:star-outline', color: '#f59e0b' },
    { label: '拥挤窗口', value: busyCount, suffix: '个', icon: 'mdi:alert-circle-outline', color: '#dc2626' }
  ];
});

const columns: DataTableColumns<Stall> = [
  { title: '窗口', key: 'name', minWidth: 120, fixed: 'left' },
  { title: '品类', key: 'type', minWidth: 120 },
  { title: '排队', key: 'queueCount', align: 'center', width: 90, render: row => `${row.queueCount} 人` },
  {
    title: '拥挤程度',
    key: 'level',
    align: 'center',
    width: 110,
    render: row =>
      h(
        NTag,
        {
          bordered: false,
          type: row.level === '人少' ? 'success' : row.level === '一般' ? 'info' : 'warning'
        },
        { default: () => row.level }
      )
  },
  { title: '等待', key: 'waitTime', align: 'center', width: 100, render: row => `${row.waitTime} 分钟` },
  { title: '步行', key: 'walkTime', align: 'center', width: 100, render: row => `${row.walkTime} 分钟` },
  { title: '总耗时', key: 'totalTime', align: 'center', width: 100, render: row => `${row.totalTime} 分钟` },
  { title: '距离', key: 'distance', align: 'center', width: 90, render: row => `${row.distance} 米` },
  { title: '评分', key: 'rating', align: 'center', width: 80 },
  {
    title: '操作',
    key: 'actions',
    align: 'center',
    width: 170,
    render: row =>
      h('div', { class: 'flex-center gap-8px' }, [
        h(
          NButton,
          {
            size: 'small',
            onClick: () => openEdit(row)
          },
          { default: () => '编辑' }
        ),
        h(
          NButton,
          {
            size: 'small',
            type: 'error',
            ghost: true,
            onClick: () => handleDelete(row)
          },
          { default: () => '删除' }
        )
      ])
  }
];

function resetModel() {
  Object.assign(model, { ...DEFAULT_STALL });
}

function openCreate() {
  editingId.value = null;
  resetModel();
  modalVisible.value = true;
}

function openEdit(row: Stall) {
  editingId.value = row.stallId;
  Object.assign(model, {
    name: row.name,
    type: row.type,
    posX: row.posX,
    posY: row.posY,
    serveSpeed: row.serveSpeed,
    distance: row.distance,
    avgPrep: row.avgPrep,
    rating: row.rating
  });
  modalVisible.value = true;
}

async function loadData() {
  loading.value = true;

  try {
    const { error, data } = await fetchStalls();
    if (!error && data) {
      stalls.value = data;
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

  submitting.value = true;

  try {
    const payload = { ...model };
    const result = editingId.value ? await updateAdminStall(editingId.value, payload) : await createAdminStall(payload);

    if (!result.error) {
      window.$message?.success(editingId.value ? '窗口已更新' : '窗口已新增');
      modalVisible.value = false;
      await loadData();
    }
  } finally {
    submitting.value = false;
  }
}

function handleDelete(row: Stall) {
  window.$dialog?.warning({
    title: '删除窗口',
    content: `确认删除「${row.name}」吗？删除后该窗口的队列快照也会清理。`,
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      const { error } = await deleteAdminStall(row.stallId);
      if (!error) {
        window.$message?.success('窗口已删除');
        await loadData();
      }
    }
  });
}

onMounted(loadData);
</script>

<template>
  <NSpace vertical :size="16">
    <div class="flex flex-wrap items-center justify-between gap-12px">
      <div>
        <h1 class="m-0 text-24px font-700">窗口管理</h1>
        <p class="m-0 mt-6px text-14px text-gray-500">维护食堂窗口基础信息、实时队列和耗时指标</p>
      </div>
      <NSpace :size="12">
        <NInput v-model:value="keyword" clearable placeholder="搜索窗口或品类" class="w-220px" />
        <NButton @click="openCreate">
          <template #icon>
            <SvgIcon icon="mdi:plus" />
          </template>
          新增窗口
        </NButton>
        <NButton type="primary" :loading="loading" @click="loadData">
          <template #icon>
            <SvgIcon icon="mdi:refresh" />
          </template>
          刷新
        </NButton>
      </NSpace>
    </div>

    <NGrid cols="1 s:2 l:5" responsive="screen" :x-gap="16" :y-gap="16">
      <NGi v-for="item in overview" :key="item.label">
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

    <NCard title="窗口列表" :bordered="false" class="card-wrapper">
      <NDataTable
        :loading="loading"
        :columns="columns"
        :data="filteredStalls"
        :pagination="{ pageSize: 8 }"
        :scroll-x="1130"
      />
    </NCard>

    <NModal v-model:show="modalVisible" preset="card" :title="editingId ? '编辑窗口' : '新增窗口'" class="w-620px">
      <NForm ref="formRef" :model="model" :rules="rules" label-placement="top">
        <NGrid cols="1 m:2" responsive="screen" :x-gap="16">
          <NGi>
            <NFormItem label="窗口名称" path="name">
              <NInput v-model:value="model.name" placeholder="例如：快餐米饭" />
            </NFormItem>
          </NGi>
          <NGi>
            <NFormItem label="品类" path="type">
              <NInput v-model:value="model.type" placeholder="例如：盖浇饭/小炒" />
            </NFormItem>
          </NGi>
          <NGi>
            <NFormItem label="地图 X 坐标" path="posX">
              <NInputNumber v-model:value="model.posX" class="w-full" :min="0" :max="100" />
            </NFormItem>
          </NGi>
          <NGi>
            <NFormItem label="地图 Y 坐标" path="posY">
              <NInputNumber v-model:value="model.posY" class="w-full" :min="0" :max="100" />
            </NFormItem>
          </NGi>
          <NGi>
            <NFormItem label="出餐速度（人/分钟）" path="serveSpeed">
              <NInputNumber v-model:value="model.serveSpeed" class="w-full" :min="0.1" :step="0.1" />
            </NFormItem>
          </NGi>
          <NGi>
            <NFormItem label="距离（米）" path="distance">
              <NInputNumber v-model:value="model.distance" class="w-full" :min="0" />
            </NFormItem>
          </NGi>
          <NGi>
            <NFormItem label="平均备餐时间（分钟）" path="avgPrep">
              <NInputNumber v-model:value="model.avgPrep" class="w-full" :min="0" :step="0.1" />
            </NFormItem>
          </NGi>
          <NGi>
            <NFormItem label="评分" path="rating">
              <NInputNumber v-model:value="model.rating" class="w-full" :min="0" :max="5" :step="0.1" />
            </NFormItem>
          </NGi>
        </NGrid>
      </NForm>
      <template #footer>
        <div class="flex justify-end gap-12px">
          <NButton @click="modalVisible = false">取消</NButton>
          <NButton type="primary" :loading="submitting" @click="handleSubmit">保存</NButton>
        </div>
      </template>
    </NModal>
  </NSpace>
</template>
