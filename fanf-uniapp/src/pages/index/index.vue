<template>
  <view class="page-shell home-page">
    <view class="topbar">
      <view>
        <text class="eyebrow">一食堂试点 · 数据刚更新</text>
        <text class="page-title">中午高峰怎么吃最快?</text>
      </view>
      <view class="live-pill">
        <view class="live-dot"></view>
        <text>试点中</text>
      </view>
    </view>

    <view class="summary-grid">
      <view class="metric">
        <text>下节课倒计时</text>
        <strong>{{ rushMode ? '18 分钟' : '下节课 12:25' }}</strong>
      </view>
      <view class="metric">
        <text>食堂整体拥挤</text>
        <strong>{{ hallLoad }}</strong>
      </view>
    </view>

    <view class="recommend" v-if="best">
      <view class="recommend-head">
        <view class="recommend-copy">
          <text class="recommend-kicker">最快推荐窗口</text>
          <text class="recommend-name">{{ best.name }}</text>
          <text class="recommend-reason">{{ bestReason }}</text>
        </view>
        <view class="score">
          <text>{{ Math.round(best.score) }}</text>
          <small>推荐分</small>
        </view>
      </view>
      <view class="recommend-stats">
        <view>
          <text>总耗时</text>
          <strong>{{ minutes(best.totalTime) }}</strong>
        </view>
        <view>
          <text>预计等待</text>
          <strong>{{ minutes(best.waitTime) }}</strong>
        </view>
        <view>
          <text>步行</text>
          <strong>{{ minutes(best.walkTime) }}</strong>
        </view>
        <view>
          <text>可信</text>
          <strong>{{ trustText(best) }}</strong>
        </view>
      </view>
    </view>

    <view v-else class="recommend">
      <text class="recommend-name">正在计算</text>
      <text class="recommend-reason">根据排队、距离和出餐速度生成推荐。</text>
    </view>

    <view class="actions">
      <button class="primary-btn" id="refreshBtn" @click="refresh">刷新人流</button>
      <button class="secondary-btn" id="routeBtn" @click="goCanteen">查看路线</button>
    </view>

    <button class="toggle-row" id="rushToggle" :aria-pressed="rushMode" @click="toggleRush">
      <view>
        <strong>赶课模式</strong>
        <text>{{ rushMode ? '开启后只看能否最快吃上饭' : '关闭时兼顾口味和速度' }}</text>
      </view>
      <view :class="['switch', { active: rushMode }]"></view>
    </button>

    <view class="quick-grid">
      <button class="quick-action" @click="goCanteen">
        <view class="quick-icon">
          <wd-icon name="apps" size="36rpx" />
        </view>
        <strong>查看全部窗口</strong>
        <text>排序和等待预测</text>
      </button>
      <button class="quick-action" @click="goReport">
        <view class="quick-icon accent">
          <wd-icon name="plus" size="36rpx" />
        </view>
        <strong>现场上报人数</strong>
        <text>帮助校准推荐结果</text>
      </button>
    </view>

    <view class="section-title">
      <text class="section-heading">推荐排序</text>
      <text>{{ rushMode ? '时间优先' : '综合评分' }}</text>
    </view>
    <view class="list">
      <view
        v-for="stall in stalls"
        :key="stall.stallId"
        :class="['stall-row', { selected: best && stall.stallId === best.stallId }]"
        @click="goDetail(stall)"
      >
        <view>
          <strong>{{ stall.name }}</strong>
          <text>{{ stall.level }} · 排队 {{ stall.queueCount }} 人 · {{ minutes(stall.totalTime) }}</text>
        </view>
        <view class="wait">
          <text>{{ Math.round(stall.score) }}</text>
          <small>推荐分</small>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad, onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { getRecommend } from '../../services/api'
import { setCustomTabBarSelected } from '../../services/tabbar'

const rushMode = ref(false)
const loading = ref(false)
const best = ref(null)
const stalls = ref([])

const hallLoad = computed(() => {
  if (!stalls.value.length) return '计算中'
  const avg = stalls.value.reduce((sum, item) => sum + Number(item.queueCount || 0), 0) / stalls.value.length
  if (avg > 16) return '很挤'
  if (avg > 10) return '偏挤'
  return '可接受'
})

const bestReason = computed(() => {
  if (!best.value) return '正在根据窗口数据生成推荐。'
  if (rushMode.value) {
    return `${best.value.name} 总耗时约 ${minutes(best.value.totalTime)}，适合下节课前快速就餐。`
  }
  return `综合排队、距离、出餐速度和口味评分，${best.value.name} 是当前最稳选择。`
})

function minutes(value) {
  return `${Math.max(1, Math.round(Number(value || 0)))} 分钟`
}

function trustText(stall) {
  const queue = Number(stall.queueCount || 0)
  if (queue <= 7) return '高'
  if (queue <= 12) return '中'
  return '偏低'
}

async function refresh() {
  loading.value = true
  const data = await getRecommend(rushMode.value)
  best.value = data.best || null
  stalls.value = data.stalls || []
  loading.value = false
}

function toggleRush() {
  rushMode.value = !rushMode.value
  refresh()
}

function goReport() {
  uni.switchTab({ url: '/pages/report/index' })
}

function goCanteen() {
  uni.switchTab({ url: '/pages/stalls/index' })
}

function goDetail(stall) {
  uni.navigateTo({ url: `/pages/stalls/detail?id=${stall.stallId}` })
}

onLoad(refresh)
onShow(() => {
  setCustomTabBarSelected(0)
  if (!loading.value) refresh()
})
onPullDownRefresh(async () => {
  await refresh()
  uni.stopPullDownRefresh()
})
</script>

<style scoped>
.home-page {
  padding-top: 24rpx;
}

.topbar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24rpx;
}

.eyebrow {
  display: block;
  margin-bottom: 8rpx;
  color: #5f7182;
  font-size: 24rpx;
  font-weight: 700;
}

.page-title {
  display: block;
  color: #173149;
  font-size: 46rpx;
  font-weight: 900;
  line-height: 1.15;
}

.live-pill {
  display: inline-flex;
  align-items: center;
  gap: 12rpx;
  min-width: 132rpx;
  padding: 14rpx 20rpx;
  border-radius: 999rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  background: rgba(255, 255, 255, 0.72);
  color: #18724f;
  font-size: 24rpx;
  font-weight: 800;
}

.live-dot {
  width: 14rpx;
  height: 14rpx;
  border-radius: 50%;
  background: #35b779;
  box-shadow: 0 0 0 10rpx rgba(53, 183, 121, 0.17);
}

.summary-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18rpx;
  margin: 32rpx 0 24rpx;
}

.metric {
  min-height: 148rpx;
  padding: 24rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.74);
  box-shadow: 0 8rpx 26rpx rgba(46, 103, 143, 0.06);
}

.metric text {
  display: block;
  color: #5f7182;
  font-size: 24rpx;
  font-weight: 700;
}

.metric strong {
  display: block;
  margin-top: 10rpx;
  color: #173149;
  font-size: 40rpx;
  line-height: 1.1;
}

.recommend {
  position: relative;
  overflow: hidden;
  padding: 32rpx;
  border-radius: 30rpx;
  background:
    radial-gradient(circle at 92% 10%, rgba(255, 255, 255, 0.22), transparent 25%),
    linear-gradient(145deg, #2a6688 0%, #2e918f 54%, #35b779 100%);
  color: #ffffff;
  box-shadow: 0 16rpx 44rpx rgba(36, 87, 122, 0.16);
}

.recommend::after {
  content: "";
  position: absolute;
  right: -46rpx;
  bottom: -58rpx;
  width: 180rpx;
  height: 180rpx;
  border-radius: 50%;
  border: 34rpx solid rgba(255, 255, 255, 0.08);
}

.recommend-head {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20rpx;
}

.recommend-copy {
  flex: 1;
  min-width: 0;
}

.recommend-kicker,
.recommend-reason {
  display: block;
  color: rgba(255, 255, 255, 0.76);
  font-size: 26rpx;
  line-height: 1.45;
}

.recommend-name {
  display: block;
  margin: 6rpx 0 8rpx;
  font-size: 46rpx;
  font-weight: 900;
  line-height: 1.16;
}

.score {
  position: relative;
  z-index: 1;
  display: grid;
  place-items: center;
  width: 124rpx;
  height: 124rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.36);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.16);
  text-align: center;
  backdrop-filter: blur(10px);
}

.score text {
  display: block;
  font-size: 36rpx;
  font-weight: 900;
  line-height: 1;
}

.score small {
  display: block;
  margin-top: 4rpx;
  color: rgba(255, 255, 255, 0.7);
  font-size: 20rpx;
  font-weight: 700;
}

.recommend-stats {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
  margin-top: 24rpx;
}

.recommend-stats view {
  padding: 18rpx 16rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.12);
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.12);
}

.recommend-stats text {
  display: block;
  color: rgba(255, 255, 255, 0.66);
  font-size: 22rpx;
}

.recommend-stats strong {
  display: block;
  margin-top: 4rpx;
  font-size: 30rpx;
}

.actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18rpx;
  margin: 24rpx 0 28rpx;
}

.primary-btn,
.secondary-btn {
  height: 88rpx;
  border-radius: 999rpx;
  font-size: 30rpx;
  font-weight: 850;
  line-height: 88rpx;
}

.primary-btn {
  background: linear-gradient(135deg, #35b779, #20a46f);
  color: #ffffff;
  box-shadow: 0 8rpx 22rpx rgba(53, 183, 121, 0.2);
}

.secondary-btn {
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  background: rgba(255, 255, 255, 0.78);
  color: #24577a;
  box-shadow: 0 8rpx 22rpx rgba(46, 103, 143, 0.06);
}

.toggle-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 28rpx;
  width: 100%;
  padding: 26rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  border-radius: 26rpx;
  background: rgba(255, 255, 255, 0.76);
  box-shadow: 0 8rpx 26rpx rgba(46, 103, 143, 0.06);
  text-align: left;
}

.toggle-row strong {
  display: block;
  color: #173149;
  font-size: 28rpx;
}

.toggle-row text {
  display: block;
  margin-top: 4rpx;
  color: #5f7182;
  font-size: 24rpx;
}

.switch {
  position: relative;
  width: 108rpx;
  height: 60rpx;
  border-radius: 999rpx;
  background: #d7e4ed;
  transition: 0.2s ease;
}

.switch::before {
  content: "";
  position: absolute;
  top: 6rpx;
  left: 6rpx;
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  background: #ffffff;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.18);
  transition: 0.2s ease;
}

.switch.active {
  background: #35b779;
}

.switch.active::before {
  transform: translateX(48rpx);
}

.quick-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18rpx;
  margin-top: 22rpx;
}

.quick-action {
  min-height: 164rpx;
  padding: 24rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  border-radius: 26rpx;
  background: rgba(255, 255, 255, 0.76);
  color: #173149;
  text-align: left;
  box-shadow: 0 8rpx 26rpx rgba(46, 103, 143, 0.06);
}

.quick-icon {
  display: grid;
  place-items: center;
  width: 68rpx;
  height: 68rpx;
  border-radius: 22rpx;
  background: rgba(47, 128, 237, 0.1);
  color: #24577a;
}

.quick-icon.accent {
  background: rgba(53, 183, 121, 0.13);
  color: #18724f;
}

.quick-action strong {
  display: block;
  margin-top: 16rpx;
  font-size: 28rpx;
}

.quick-action text {
  display: block;
  margin-top: 6rpx;
  color: #5f7182;
  font-size: 23rpx;
  line-height: 1.35;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
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
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 20rpx;
  padding: 24rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.76);
  box-shadow: 0 8rpx 26rpx rgba(46, 103, 143, 0.06);
}

.stall-row.selected {
  border-color: #35b779;
  box-shadow: 0 0 0 6rpx rgba(53, 183, 121, 0.14);
}

.stall-row strong {
  display: block;
  color: #173149;
  font-size: 28rpx;
}

.stall-row text {
  display: block;
  margin-top: 6rpx;
  color: #5f7182;
  font-size: 24rpx;
  line-height: 1.35;
}

.wait {
  align-self: center;
  color: #24577a;
  text-align: right;
}

.wait text {
  margin: 0;
  color: #24577a;
  font-size: 34rpx;
  font-weight: 900;
}

.wait small {
  display: block;
  margin-top: 4rpx;
  color: #5f7182;
  font-size: 22rpx;
  font-weight: 700;
}
</style>
