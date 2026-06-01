<template>
  <view class="page-shell detail-page">
    <view class="detail-hero" v-if="stall">
      <view class="hero-top">
        <view>
          <text>{{ stall.type }}</text>
          <strong>{{ stall.name }}</strong>
        </view>
        <view class="recommend-badge" v-if="isRecommended">推荐</view>
        <view class="recommend-badge muted-badge" v-else>可选</view>
      </view>
      <view class="hero-metrics">
        <view>
          <text>排队</text>
          <strong>{{ stall.queueCount }}人</strong>
        </view>
        <view>
          <text>等待</text>
          <strong>{{ minutes(stall.waitTime) }}</strong>
        </view>
        <view>
          <text>总耗时</text>
          <strong>{{ minutes(stall.totalTime) }}</strong>
        </view>
      </view>
    </view>

    <view class="detail-card" v-if="stall">
      <view class="detail-row">
        <text>拥挤程度</text>
        <strong>{{ stall.level }}</strong>
      </view>
      <view class="detail-row">
        <text>窗口评分</text>
        <strong>{{ rating(stall) }}</strong>
      </view>
      <view class="detail-row">
        <text>步行距离</text>
        <strong>{{ Math.round(Number(stall.distance || 0)) }}m</strong>
      </view>
      <view class="detail-row">
        <text>出餐速度</text>
        <strong>{{ stall.serveSpeed }}人/分</strong>
      </view>
    </view>

    <view class="feedback-panel" v-if="stall">
      <view>
        <strong>这次推荐准吗?</strong>
        <text>你的反馈会用于校准后续排队预测。</text>
      </view>
      <view class="feedback-actions">
        <button :disabled="submitting" @click="submitFeedback(true)">准确</button>
        <button :disabled="submitting" @click="submitFeedback(false)">不准确</button>
      </view>
    </view>

    <view class="empty-state" v-else>
      <strong>未找到窗口</strong>
      <text>返回窗口列表重新选择。</text>
      <button @click="goBack">返回列表</button>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getStalls, sendFeedback } from '../../services/api'

const stall = ref(null)
const bestId = ref(null)
const submitting = ref(false)

const isRecommended = computed(() => stall.value && Number(stall.value.stallId) === Number(bestId.value))

function minutes(value) {
  return `${Math.max(1, Math.round(Number(value || 0)))} 分钟`
}

function rating(item) {
  return Number(item.rating || 0).toFixed(1)
}

async function loadData(options = {}) {
  const stalls = await getStalls()
  const sorted = [...stalls].sort((a, b) => Number(a.totalTime || 0) - Number(b.totalTime || 0))
  bestId.value = sorted[0]?.stallId || null
  stall.value = stalls.find((item) => Number(item.stallId) === Number(options.id)) || sorted[0] || null
}

async function submitFeedback(isAccurate) {
  if (!stall.value || submitting.value) return
  submitting.value = true
  await sendFeedback(stall.value.stallId, isAccurate)
  uni.showToast({ title: isAccurate ? '已记录准确' : '已记录不准', icon: 'success' })
  submitting.value = false
}

function goBack() {
  uni.switchTab({ url: '/pages/stalls/index' })
}

onLoad(loadData)
</script>

<style scoped>
.detail-hero {
  overflow: hidden;
  padding: 34rpx;
  border-radius: 32rpx;
  background: linear-gradient(145deg, #24577a 0%, #2d95a6 56%, #35b779 100%);
  color: #ffffff;
  box-shadow: 0 16rpx 44rpx rgba(36, 87, 122, 0.16);
}

.hero-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 22rpx;
}

.hero-top text {
  display: block;
  color: rgba(255, 255, 255, 0.72);
  font-size: 24rpx;
  font-weight: 750;
}

.hero-top strong {
  display: block;
  margin-top: 10rpx;
  font-size: 48rpx;
  line-height: 1.12;
}

.recommend-badge {
  flex: 0 0 auto;
  padding: 12rpx 20rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.22);
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.16);
  color: #ffffff;
  font-size: 24rpx;
  font-weight: 900;
}

.muted-badge {
  opacity: 0.76;
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14rpx;
  margin-top: 28rpx;
}

.hero-metrics view {
  padding: 18rpx 12rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.12);
}

.hero-metrics text,
.hero-metrics strong {
  display: block;
}

.hero-metrics text {
  color: rgba(255, 255, 255, 0.66);
  font-size: 22rpx;
}

.hero-metrics strong {
  margin-top: 6rpx;
  font-size: 28rpx;
  font-weight: 900;
}

.detail-card,
.feedback-panel,
.empty-state {
  margin-top: 22rpx;
  padding: 28rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 8rpx 26rpx rgba(46, 103, 143, 0.06);
}

.detail-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 72rpx;
  border-bottom: 1rpx solid rgba(36, 87, 122, 0.07);
}

.detail-row:last-child {
  border-bottom: 0;
}

.detail-row text {
  color: #5f7182;
  font-size: 26rpx;
}

.detail-row strong {
  color: #173149;
  font-size: 28rpx;
  font-weight: 900;
}

.feedback-panel strong,
.feedback-panel text,
.empty-state strong,
.empty-state text {
  display: block;
}

.feedback-panel strong,
.empty-state strong {
  color: #173149;
  font-size: 30rpx;
  font-weight: 900;
}

.feedback-panel text,
.empty-state text {
  margin-top: 8rpx;
  color: #5f7182;
  font-size: 24rpx;
  line-height: 1.4;
}

.feedback-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16rpx;
  margin-top: 22rpx;
}

.feedback-actions button,
.empty-state button {
  height: 84rpx;
  border-radius: 999rpx;
  background: rgba(36, 87, 122, 0.08);
  color: #24577a;
  font-size: 28rpx;
  font-weight: 850;
  line-height: 84rpx;
}

.feedback-actions button:first-child {
  background: linear-gradient(135deg, #35b779, #20a46f);
  color: #ffffff;
}

.empty-state {
  text-align: center;
}

.empty-state button {
  margin-top: 22rpx;
}
</style>
