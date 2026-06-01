<template>
  <view class="page-shell stalls-page">
    <view class="mini-hero">
      <view>
        <text>一食堂窗口</text>
        <strong>按你现在最关心的方式排序</strong>
      </view>
      <view class="hero-badge">
        <text>{{ stalls.length }}</text>
        <small>个窗口</small>
      </view>
    </view>

    <view class="sort-tabs">
      <button
        v-for="item in sortOptions"
        :key="item.key"
        :class="{ active: sortKey === item.key }"
        @click="sortKey = item.key"
      >
        {{ item.label }}
      </button>
    </view>

    <view class="empty-state" v-if="!sortedStalls.length">
      <strong>还没有窗口数据</strong>
      <text>后端接口未返回时会自动使用本地试点数据。</text>
    </view>

    <view class="list" v-else>
      <view
        v-for="(stall, index) in sortedStalls"
        :key="stall.stallId"
        class="stall-row"
        @click="goDetail(stall)"
      >
        <view class="rank" :class="{ fastest: sortKey === 'fastest' && index === 0 }">
          {{ index + 1 }}
        </view>
        <view class="stall-main">
          <view class="stall-title">
            <strong>{{ stall.name }}</strong>
            <text :class="['status-pill', levelClass(stall.level)]">{{ stall.level }}</text>
          </view>
          <text class="stall-meta">{{ stall.type }} · 排队 {{ stall.queueCount }} 人 · 评分 {{ rating(stall) }}</text>
          <view class="metrics">
            <view>
              <text>等待</text>
              <strong>{{ minutes(stall.waitTime) }}</strong>
            </view>
            <view>
              <text>步行</text>
              <strong>{{ Math.round(Number(stall.distance || 0)) }}m</strong>
            </view>
            <view>
              <text>总耗时</text>
              <strong>{{ minutes(stall.totalTime) }}</strong>
            </view>
          </view>
        </view>
        <wd-icon name="arrow-right" size="30rpx" color="#7b91a5" />
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad, onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { getStalls } from '../../services/api'
import { setCustomTabBarSelected } from '../../services/tabbar'

const stalls = ref([])
const sortKey = ref('fastest')
const shownOnce = ref(false)

const sortOptions = [
  { key: 'fastest', label: '最快' },
  { key: 'fewest', label: '人少' },
  { key: 'nearest', label: '距离近' },
  { key: 'rating', label: '评分高' },
]

const sortedStalls = computed(() => {
  const list = [...stalls.value]
  if (sortKey.value === 'fewest') return list.sort((a, b) => Number(a.queueCount || 0) - Number(b.queueCount || 0))
  if (sortKey.value === 'nearest') return list.sort((a, b) => Number(a.distance || 0) - Number(b.distance || 0))
  if (sortKey.value === 'rating') return list.sort((a, b) => Number(b.rating || 0) - Number(a.rating || 0))
  return list.sort((a, b) => Number(a.totalTime || 0) - Number(b.totalTime || 0))
})

function minutes(value) {
  return `${Math.max(1, Math.round(Number(value || 0)))} 分钟`
}

function rating(stall) {
  return Number(stall.rating || 0).toFixed(1)
}

function levelClass(level) {
  if (level === '人少') return 'level-low'
  if (level === '一般') return 'level-mid'
  if (level === '较挤') return 'level-high'
  return 'level-full'
}

function goDetail(stall) {
  uni.navigateTo({ url: `/pages/stalls/detail?id=${stall.stallId}` })
}

async function refresh() {
  stalls.value = await getStalls()
}

onLoad(refresh)
onShow(() => {
  setCustomTabBarSelected(1)
  if (shownOnce.value) {
    refresh()
    return
  }
  shownOnce.value = true
})
onPullDownRefresh(async () => {
  await refresh()
  uni.stopPullDownRefresh()
})
</script>

<style scoped>
.mini-hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 22rpx;
  overflow: hidden;
  padding: 34rpx;
  border-radius: 32rpx;
  background: linear-gradient(145deg, #24577a 0%, #2f8f91 58%, #35b779 100%);
  color: #ffffff;
  box-shadow: 0 16rpx 44rpx rgba(36, 87, 122, 0.16);
}

.mini-hero text {
  display: block;
  color: rgba(255, 255, 255, 0.72);
  font-size: 24rpx;
  font-weight: 750;
}

.mini-hero strong {
  display: block;
  margin-top: 12rpx;
  font-size: 44rpx;
  line-height: 1.16;
}

.hero-badge {
  display: grid;
  place-items: center;
  flex: 0 0 auto;
  width: 124rpx;
  height: 124rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.22);
  border-radius: 36rpx;
  background: rgba(255, 255, 255, 0.15);
}

.hero-badge text {
  color: #ffffff;
  font-size: 42rpx;
  font-weight: 900;
  line-height: 1;
}

.hero-badge small {
  margin-top: 6rpx;
  color: rgba(255, 255, 255, 0.72);
  font-size: 21rpx;
  font-weight: 750;
}

.sort-tabs {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10rpx;
  margin: 24rpx 0;
  padding: 8rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.72);
  box-shadow: 0 8rpx 26rpx rgba(46, 103, 143, 0.06);
}

.sort-tabs button {
  min-height: 70rpx;
  border-radius: 999rpx;
  color: #5f7182;
  font-size: 24rpx;
  font-weight: 850;
  line-height: 70rpx;
}

.sort-tabs button.active {
  background: #ffffff;
  color: #18724f;
  box-shadow: 0 8rpx 22rpx rgba(36, 87, 122, 0.1);
}

.empty-state {
  padding: 34rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.76);
  text-align: center;
}

.empty-state strong,
.empty-state text {
  display: block;
}

.empty-state strong {
  color: #173149;
  font-size: 30rpx;
}

.empty-state text {
  margin-top: 10rpx;
  color: #5f7182;
  font-size: 24rpx;
}

.list {
  display: grid;
  gap: 16rpx;
}

.stall-row {
  display: grid;
  grid-template-columns: 54rpx 1fr auto;
  align-items: center;
  gap: 18rpx;
  padding: 24rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 8rpx 26rpx rgba(46, 103, 143, 0.06);
}

.rank {
  display: grid;
  place-items: center;
  width: 54rpx;
  height: 54rpx;
  border-radius: 18rpx;
  background: rgba(36, 87, 122, 0.08);
  color: #24577a;
  font-size: 24rpx;
  font-weight: 900;
}

.rank.fastest {
  background: rgba(53, 183, 121, 0.15);
  color: #18724f;
}

.stall-main {
  min-width: 0;
}

.stall-title {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.stall-title strong {
  flex: 1;
  min-width: 0;
  color: #173149;
  font-size: 30rpx;
  font-weight: 900;
}

.stall-meta {
  display: block;
  margin-top: 8rpx;
  color: #5f7182;
  font-size: 24rpx;
  line-height: 1.35;
}

.metrics {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10rpx;
  margin-top: 18rpx;
}

.metrics view {
  padding: 14rpx 10rpx;
  border-radius: 18rpx;
  background: rgba(247, 251, 255, 0.9);
}

.metrics text,
.metrics strong {
  display: block;
}

.metrics text {
  color: #7b91a5;
  font-size: 20rpx;
  font-weight: 800;
}

.metrics strong {
  margin-top: 4rpx;
  color: #173149;
  font-size: 23rpx;
  font-weight: 900;
}
</style>
