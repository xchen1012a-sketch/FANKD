<template>
  <view class="page-shell profile-settings-page">
    <view class="settings-card">
      <text>个人设置</text>
      <strong>调整下课时间</strong>
      <view class="settings-row">
        <view>
          <strong>默认下课时间</strong>
          <picker mode="time" :value="classEndTime" @change="onTimeChange">
            <text>{{ classEndTime }} · 点击选择</text>
          </picker>
        </view>
        <wd-icon name="time-line" size="30rpx" color="#24577a" />
      </view>
      <button :disabled="saving" @click="saveProfile">{{ saving ? '保存中' : '保存设置' }}</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getProfile, updateProfile } from '../../services/api'

const classEndTime = ref('12:10')
const saving = ref(false)

function onTimeChange(event) {
  classEndTime.value = event.detail.value
}

async function saveProfile() {
  if (saving.value) return
  saving.value = true
  await updateProfile(classEndTime.value)
  uni.showToast({ title: '已保存', icon: 'success' })
  saving.value = false
}

onLoad(async () => {
  const profile = await getProfile()
  classEndTime.value = profile.classEndTime || '12:10'
})
</script>

<style scoped>
.settings-card {
  padding: 30rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  border-radius: 32rpx;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 8rpx 26rpx rgba(46, 103, 143, 0.06);
}

.settings-card > text,
.settings-card > strong {
  display: block;
}

.settings-card > text {
  color: #5f7182;
  font-size: 24rpx;
  font-weight: 800;
}

.settings-card > strong {
  margin-top: 8rpx;
  color: #173149;
  font-size: 40rpx;
  font-weight: 900;
}

.settings-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  margin-top: 26rpx;
  padding: 24rpx;
  border-radius: 26rpx;
  background: rgba(247, 251, 255, 0.9);
}

.settings-row strong,
.settings-row text {
  display: block;
}

.settings-row strong {
  color: #173149;
  font-size: 28rpx;
}

.settings-row text {
  margin-top: 8rpx;
  color: #5f7182;
  font-size: 24rpx;
}

button {
  height: 88rpx;
  margin-top: 24rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #24577a, #1f6c83);
  color: #ffffff;
  font-size: 28rpx;
  font-weight: 850;
  line-height: 88rpx;
}

button[disabled] {
  opacity: 0.58;
}
</style>
