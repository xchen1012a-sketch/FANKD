<template>
  <view class="page-shell mine-page">
    <view class="profile-card">
      <view class="profile-top">
        <view class="avatar">{{ avatarText }}</view>
        <view class="profile-main">
          <strong>{{ profile.nickname || '饭刻达用户' }}</strong>
          <text>一食堂试点 · 午餐高峰助手</text>
        </view>
        <text class="badge">MVP</text>
      </view>
      <view class="profile-stats">
        <view>
          <strong>{{ profile.reportCount || 0 }}</strong>
          <text>我的上报</text>
        </view>
        <view>
          <strong>{{ profile.timeSaved || 0 }}</strong>
          <text>节省分钟</text>
        </view>
        <view>
          <strong>{{ classEndTime }}</strong>
          <text>下课时间</text>
        </view>
      </view>
    </view>

    <view class="section-title">
      <text class="section-heading">我的设置</text>
      <text>影响推荐时间</text>
    </view>

    <view class="list">
      <view class="settings-row">
        <view>
          <strong>下课时间设置</strong>
          <picker mode="time" :value="classEndTime" @change="onTimeChange">
            <text>{{ classEndTime }} · 点击调整</text>
          </picker>
        </view>
        <wd-icon name="time-line" size="30rpx" color="#24577a" />
      </view>
      <view class="settings-row" @click="goProfile">
        <view>
          <strong>个人设置</strong>
          <text>修改下课时间和基础偏好。</text>
        </view>
        <wd-icon name="arrow-right" size="30rpx" color="#7b91a5" />
      </view>
      <view class="settings-row" @click="goFeedback">
        <view>
          <strong>推荐反馈</strong>
          <text>反馈某个窗口的预测是否准确。</text>
        </view>
        <wd-icon name="arrow-right" size="30rpx" color="#7b91a5" />
      </view>
    </view>

    <button class="save-button" :disabled="saving" @click="saveProfile">
      {{ saving ? '保存中' : '保存下课时间' }}
    </button>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { getProfile, updateProfile } from '../../services/api'
import { setCustomTabBarSelected } from '../../services/tabbar'

const profile = ref({})
const classEndTime = ref('12:10')
const saving = ref(false)
const shownOnce = ref(false)

const avatarText = computed(() => (profile.value.nickname || '饭').slice(0, 1))

function onTimeChange(event) {
  classEndTime.value = event.detail.value
}

async function saveProfile() {
  if (saving.value) return
  saving.value = true
  profile.value = await updateProfile(classEndTime.value)
  uni.showToast({ title: '已保存', icon: 'success' })
  saving.value = false
}

function goProfile() {
  uni.navigateTo({ url: '/subPages/profile/index' })
}

function goFeedback() {
  uni.navigateTo({ url: '/subPages/feedback/index' })
}

async function loadData() {
  profile.value = await getProfile()
  classEndTime.value = profile.value.classEndTime || '12:10'
}

onLoad(loadData)
onShow(() => {
  setCustomTabBarSelected(3)
  if (shownOnce.value) {
    loadData()
    return
  }
  shownOnce.value = true
})
</script>

<style scoped>
.profile-card {
  overflow: hidden;
  padding: 32rpx;
  border-radius: 32rpx;
  background: linear-gradient(145deg, #24577a 0%, #2f8f91 54%, #35b779 100%);
  color: #ffffff;
  box-shadow: 0 16rpx 44rpx rgba(36, 87, 122, 0.16);
}

.profile-top {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.avatar {
  display: grid;
  place-items: center;
  width: 96rpx;
  height: 96rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.2);
  border-radius: 34rpx;
  background: rgba(255, 255, 255, 0.18);
  color: #ffffff;
  font-size: 40rpx;
  font-weight: 900;
}

.profile-main {
  flex: 1;
  min-width: 0;
}

.profile-main strong,
.profile-main text {
  display: block;
}

.profile-main strong {
  font-size: 36rpx;
  font-weight: 900;
}

.profile-main text {
  margin-top: 6rpx;
  color: rgba(255, 255, 255, 0.72);
  font-size: 24rpx;
}

.badge {
  flex: 0 0 auto;
  padding: 10rpx 16rpx;
  border-radius: 999rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.16);
  background: rgba(255, 255, 255, 0.16);
  color: #ffffff;
  font-size: 22rpx;
  font-weight: 850;
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14rpx;
  margin-top: 28rpx;
}

.profile-stats view {
  min-height: 110rpx;
  padding: 16rpx 10rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.11);
  border-radius: 22rpx;
  background: rgba(255, 255, 255, 0.11);
  text-align: center;
}

.profile-stats strong,
.profile-stats text {
  display: block;
}

.profile-stats strong {
  font-size: 32rpx;
  font-weight: 900;
}

.profile-stats text {
  margin-top: 8rpx;
  color: rgba(255, 255, 255, 0.7);
  font-size: 22rpx;
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

.settings-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  min-height: 104rpx;
  padding: 24rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 8rpx 26rpx rgba(46, 103, 143, 0.06);
}

.settings-row strong,
.settings-row text {
  display: block;
}

.settings-row strong {
  color: #173149;
  font-size: 28rpx;
  font-weight: 900;
}

.settings-row text {
  margin-top: 8rpx;
  color: #5f7182;
  font-size: 24rpx;
  line-height: 1.4;
}

.save-button {
  height: 88rpx;
  margin-top: 22rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #24577a, #1f6c83);
  color: #ffffff;
  font-size: 28rpx;
  font-weight: 850;
  line-height: 88rpx;
  box-shadow: 0 8rpx 22rpx rgba(36, 87, 122, 0.18);
}

.save-button[disabled] {
  opacity: 0.58;
}
</style>
