<template>
  <view class="page-shell report-page">
    <view class="mini-hero">
      <text>现场排队上报</text>
      <strong>看到几个人排队，就帮大家校准一次</strong>
    </view>

    <view class="detail-card">
      <picker class="stall-picker" :range="stallNames" :value="selectedIndex" @change="onStallChange">
        <view class="picker-row">
          <view>
            <text>选择窗口</text>
            <strong>{{ selectedStall ? selectedStall.name : '请选择' }}</strong>
          </view>
          <wd-icon name="arrow-right" size="30rpx" color="#7b91a5" />
        </view>
      </picker>

      <view class="current-panel" v-if="selectedStall">
        <view>
          <text>当前预测</text>
          <strong>{{ selectedStall.level }} · {{ minutes(selectedStall.totalTime) }}</strong>
        </view>
        <view>
          <text>接口人数</text>
          <strong>{{ selectedStall.queueCount }} 人</strong>
        </view>
      </view>

      <view class="reporter">
        <view>
          <strong>当前排队人数</strong>
          <text>建议只数正在队列里等待的人。</text>
        </view>
        <view class="stepper">
          <button @click="stepQueue(-1)">-</button>
          <text>{{ queueCount }}</text>
          <button @click="stepQueue(1)">+</button>
        </view>
        <button class="submit-report" :disabled="!selectedStall || submitting" @click="submitReport">
          {{ submitting ? '提交中' : '提交上报' }}
        </button>
      </view>
    </view>

    <view class="section-title">
      <text class="section-heading">常用窗口</text>
      <text>点选后快速上报</text>
    </view>

    <view class="list">
      <view
        v-for="stall in sortedStalls"
        :key="stall.stallId"
        :class="['stall-row', { selected: selectedStall && stall.stallId === selectedStall.stallId }]"
        @click="selectStall(stall)"
      >
        <view>
          <strong>{{ stall.name }}</strong>
          <text>{{ stall.type }} · 当前 {{ stall.queueCount }} 人 · {{ minutes(stall.waitTime) }}</text>
        </view>
        <text :class="['status-pill', levelClass(stall.level)]">{{ stall.level }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { getStalls, reportQueue } from '../../services/api'
import { setCustomTabBarSelected } from '../../services/tabbar'

const stalls = ref([])
const selectedIndex = ref(0)
const queueCount = ref(0)
const submitting = ref(false)
const shownOnce = ref(false)

const stallNames = computed(() => stalls.value.map((stall) => stall.name))
const selectedStall = computed(() => stalls.value[selectedIndex.value] || null)
const sortedStalls = computed(() =>
  [...stalls.value].sort((a, b) => Number(a.totalTime || 0) - Number(b.totalTime || 0))
)

function minutes(value) {
  return `${Math.max(1, Math.round(Number(value || 0)))} 分钟`
}

function levelClass(level) {
  if (level === '人少') return 'level-low'
  if (level === '一般') return 'level-mid'
  if (level === '较挤') return 'level-high'
  return 'level-full'
}

function onStallChange(event) {
  selectedIndex.value = Number(event.detail.value || 0)
  queueCount.value = Number(selectedStall.value?.queueCount || 0)
}

function selectStall(stall) {
  const index = stalls.value.findIndex((item) => item.stallId === stall.stallId)
  selectedIndex.value = Math.max(0, index)
  queueCount.value = Number(stall.queueCount || 0)
}

function stepQueue(delta) {
  queueCount.value = Math.min(60, Math.max(0, Number(queueCount.value) + delta))
}

async function loadData() {
  stalls.value = await getStalls()
  if (selectedStall.value) {
    queueCount.value = Number(selectedStall.value.queueCount || 0)
  }
}

async function submitReport() {
  if (!selectedStall.value || submitting.value) return
  submitting.value = true
  await reportQueue(selectedStall.value.stallId, Number(queueCount.value))
  uni.showToast({ title: '已上报', icon: 'success' })
  await loadData()
  submitting.value = false
}

onLoad(loadData)
onShow(() => {
  setCustomTabBarSelected(2)
  if (shownOnce.value) {
    loadData()
    return
  }
  shownOnce.value = true
})
</script>

<style scoped>
.mini-hero {
  padding: 34rpx;
  border-radius: 32rpx;
  background: linear-gradient(145deg, #2f80ed 0%, #2d95a6 56%, #35b779 100%);
  color: #ffffff;
  box-shadow: 0 16rpx 44rpx rgba(47, 128, 237, 0.16);
}

.mini-hero text,
.mini-hero strong {
  display: block;
}

.mini-hero text {
  color: rgba(255, 255, 255, 0.72);
  font-size: 24rpx;
  font-weight: 750;
}

.mini-hero strong {
  margin-top: 12rpx;
  font-size: 42rpx;
  line-height: 1.18;
}

.detail-card {
  margin-top: 22rpx;
  padding: 26rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  border-radius: 30rpx;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 8rpx 26rpx rgba(46, 103, 143, 0.06);
}

.picker-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 92rpx;
}

.picker-row text,
.picker-row strong {
  display: block;
}

.picker-row text {
  color: #7b91a5;
  font-size: 22rpx;
  font-weight: 800;
}

.picker-row strong {
  margin-top: 6rpx;
  color: #173149;
  font-size: 32rpx;
  font-weight: 900;
}

.current-panel {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14rpx;
  margin-top: 18rpx;
}

.current-panel view {
  padding: 18rpx;
  border-radius: 22rpx;
  background: rgba(247, 251, 255, 0.9);
}

.current-panel text,
.current-panel strong {
  display: block;
}

.current-panel text {
  color: #7b91a5;
  font-size: 22rpx;
  font-weight: 800;
}

.current-panel strong {
  margin-top: 6rpx;
  color: #173149;
  font-size: 27rpx;
  font-weight: 900;
}

.reporter {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 20rpx;
  align-items: center;
  margin-top: 22rpx;
  padding: 22rpx;
  border-radius: 26rpx;
  background: rgba(237, 248, 255, 0.72);
}

.reporter strong,
.reporter text {
  display: block;
}

.reporter strong {
  color: #173149;
  font-size: 28rpx;
}

.reporter text {
  margin-top: 6rpx;
  color: #5f7182;
  font-size: 23rpx;
  line-height: 1.35;
}

.stepper {
  display: grid;
  grid-template-columns: 70rpx 82rpx 70rpx;
  overflow: hidden;
  border: 1rpx solid rgba(36, 87, 122, 0.12);
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.9);
}

.stepper button,
.stepper text {
  height: 70rpx;
  color: #24577a;
  font-size: 30rpx;
  font-weight: 900;
  line-height: 70rpx;
  text-align: center;
}

.submit-report {
  grid-column: 1 / -1;
  height: 86rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #24577a, #1f6c83);
  color: #ffffff;
  font-size: 28rpx;
  font-weight: 850;
  line-height: 86rpx;
  box-shadow: 0 8rpx 22rpx rgba(36, 87, 122, 0.18);
}

.submit-report[disabled] {
  opacity: 0.58;
}

.section-title {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 20rpx;
  margin: 36rpx 0 18rpx;
}

.section-heading {
  color: #173149;
  font-size: 32rpx;
  font-weight: 900;
}

.section-title > text:last-child {
  color: #5f7182;
  font-size: 24rpx;
  font-weight: 700;
}

.list {
  display: grid;
  gap: 16rpx;
}

.stall-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  padding: 24rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 8rpx 26rpx rgba(46, 103, 143, 0.06);
}

.stall-row.selected {
  border-color: #35b779;
  box-shadow: 0 0 0 6rpx rgba(53, 183, 121, 0.14);
}

.stall-row strong,
.stall-row text {
  display: block;
}

.stall-row strong {
  color: #173149;
  font-size: 28rpx;
  font-weight: 900;
}

.stall-row text {
  margin-top: 8rpx;
  color: #5f7182;
  font-size: 24rpx;
  line-height: 1.35;
}
</style>
