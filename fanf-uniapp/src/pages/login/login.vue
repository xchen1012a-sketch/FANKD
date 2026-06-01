<template>
  <view class="login-page">
    <wd-toast />
    <view class="brand-area">
      <view class="brand-mark">
        <text>饭</text>
      </view>
      <text class="brand-name">饭刻达</text>
      <text class="brand-copy">校园午高峰智能就餐助手</text>
    </view>

    <view class="login-panel">
      <view class="status-row">
        <view class="status-dot"></view>
        <text>一食堂试点 · 排队推荐实时更新</text>
      </view>

      <view class="value-list">
        <view class="value-item">
          <text class="value-index">01</text>
          <text>按排队、距离和出餐速度推荐最快窗口</text>
        </view>
        <view class="value-item">
          <text class="value-index">02</text>
          <text>赶课模式自动过滤总耗时过长的窗口</text>
        </view>
        <view class="value-item">
          <text class="value-index">03</text>
          <text>登录后可上报排队人数并校准推荐结果</text>
        </view>
      </view>

      <wd-button
        custom-class="wot-login-button"
        block
        :loading="loading"
        :disabled="loading"
        @click="handleLogin"
      >
        {{ loading ? '登录中' : '微信一键登录' }}
      </wd-button>

      <wd-button
        custom-class="wot-browse-button"
        block
        plain
        @click="enterApp"
      >
        先看看推荐
      </wd-button>

      <text v-if="errorMessage" class="error-text">{{ errorMessage }}</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { useToast } from '@wot-ui/ui'
import { login } from '../../services/api'

const loading = ref(false)
const errorMessage = ref('')
const toast = useToast()

function enterApp() {
  uni.switchTab({ url: '/pages/index/index' })
}

async function handleLogin() {
  if (loading.value) return
  loading.value = true
  errorMessage.value = ''
  try {
    await login()
    toast.success('登录成功')
    enterApp()
  } catch (error) {
    errorMessage.value = error.message || '登录失败，请稍后再试'
    toast.error(errorMessage.value)
  } finally {
    loading.value = false
  }
}

</script>

<style scoped>
.login-page {
  min-height: 100vh;
  padding: 96rpx 32rpx 48rpx;
  background:
    radial-gradient(circle at 12% -4%, rgba(120, 198, 255, 0.28), transparent 36%),
    linear-gradient(180deg, #f6fbff 0%, #f4fbff 52%, #eff8f3 100%);
}

.brand-area {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  min-height: 360rpx;
  padding-top: 28rpx;
}

.brand-mark {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 116rpx;
  height: 116rpx;
  border-radius: 30rpx;
  background: linear-gradient(135deg, #24577a, #35b779);
  color: #ffffff;
  font-size: 52rpx;
  font-weight: 900;
  box-shadow: 0 14rpx 34rpx rgba(36, 87, 122, 0.18);
}

.brand-name {
  display: block;
  margin-top: 34rpx;
  color: #173149;
  font-size: 64rpx;
  font-weight: 900;
  line-height: 1.08;
}

.brand-copy {
  display: block;
  margin-top: 18rpx;
  color: #5f7182;
  font-size: 30rpx;
  line-height: 1.45;
}

.login-panel {
  padding: 34rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.82);
  border-radius: 30rpx;
  background: rgba(255, 255, 255, 0.76);
  box-shadow: 0 10rpx 30rpx rgba(23, 49, 73, 0.07);
}

.status-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  color: #24577a;
  font-size: 26rpx;
  font-weight: 700;
  line-height: 1.3;
}

.status-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 999rpx;
  background: #35b779;
}

.value-list {
  margin-top: 30rpx;
}

.value-item {
  display: grid;
  grid-template-columns: 64rpx 1fr;
  gap: 18rpx;
  min-height: 72rpx;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #edf4f8;
  color: #173149;
  font-size: 28rpx;
  line-height: 1.45;
}

.value-item:last-child {
  border-bottom: 0;
}

.value-index {
  color: #35b779;
  font-size: 24rpx;
  font-weight: 900;
  line-height: 1.6;
}

:deep(.wot-login-button) {
  margin-top: 30rpx;
  height: 88rpx !important;
  border-radius: 999rpx !important;
  background: linear-gradient(135deg, #24577a, #1f6c83) !important;
  border-color: transparent !important;
  font-size: 30rpx !important;
  font-weight: 800 !important;
  box-shadow: 0 8rpx 22rpx rgba(36, 87, 122, 0.18) !important;
}

:deep(.wot-browse-button) {
  margin-top: 16rpx;
  height: 88rpx !important;
  border-radius: 999rpx !important;
  color: #24577a !important;
  border-color: rgba(36, 87, 122, 0.18) !important;
  background: rgba(255, 255, 255, 0.78) !important;
  font-size: 30rpx !important;
  font-weight: 800 !important;
}

.error-text {
  display: block;
  margin-top: 18rpx;
  color: #b8232d;
  font-size: 24rpx;
  line-height: 1.4;
}
</style>
