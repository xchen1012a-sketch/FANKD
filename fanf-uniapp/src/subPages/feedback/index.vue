<template>
  <view class="page-shell feedback-page">
    <view class="feedback-card">
      <view>
        <text class="eyebrow">推荐反馈</text>
        <strong>校准窗口推荐结果</strong>
        <text class="desc">选择窗口后提交本次推荐是否准确，后台会在用户反馈模块展示。</text>
      </view>
    </view>

    <view class="form-card">
      <picker :range="stallNames" :value="selectedIndex" @change="onStallChange">
        <view class="picker-row">
          <view>
            <text>反馈窗口</text>
            <strong>{{ selectedStall ? selectedStall.name : '请选择窗口' }}</strong>
          </view>
          <text class="arrow">›</text>
        </view>
      </picker>

      <view class="feedback-actions">
        <button :disabled="submitting || !selectedStall" @click="submitFeedback(true)">推荐准确</button>
        <button :disabled="submitting || !selectedStall" @click="submitFeedback(false)">推荐不准</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getStalls, sendFeedback } from '../../services/api'

const stalls = ref([])
const selectedIndex = ref(0)
const submitting = ref(false)

const stallNames = computed(() => stalls.value.map((stall) => stall.name))
const selectedStall = computed(() => stalls.value[selectedIndex.value] || null)

function onStallChange(event) {
  selectedIndex.value = Number(event.detail.value || 0)
}

async function submitFeedback(isAccurate) {
  if (!selectedStall.value || submitting.value) return
  submitting.value = true
  try {
    await sendFeedback(selectedStall.value.stallId, isAccurate)
    uni.showToast({ title: '已提交', icon: 'success' })
  } finally {
    submitting.value = false
  }
}

onLoad(async () => {
  stalls.value = await getStalls()
})
</script>

<style scoped>
.feedback-card,
.form-card {
  padding: 28rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 8rpx 26rpx rgba(46, 103, 143, 0.06);
}

.eyebrow,
.desc,
.picker-row text {
  display: block;
  color: #5f7182;
  font-size: 24rpx;
  line-height: 1.45;
}

.feedback-card strong,
.picker-row strong {
  display: block;
  margin-top: 8rpx;
  color: #173149;
  font-size: 34rpx;
  font-weight: 900;
}

.desc {
  margin-top: 12rpx;
}

.form-card {
  margin-top: 22rpx;
}

.picker-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 92rpx;
}

.arrow {
  color: #24577a !important;
  font-size: 48rpx !important;
}

.feedback-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16rpx;
  margin-top: 24rpx;
}

.feedback-actions button {
  height: 86rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #24577a, #1f6c83);
  color: #ffffff;
  font-size: 28rpx;
  font-weight: 850;
  line-height: 86rpx;
}

.feedback-actions button:last-child {
  background: rgba(255, 255, 255, 0.88);
  color: #24577a;
  border: 1rpx solid rgba(36, 87, 122, 0.18);
}

.feedback-actions button[disabled] {
  opacity: 0.58;
}
</style>
