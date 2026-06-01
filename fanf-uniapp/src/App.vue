<script>
export default {
  onLaunch() {
    console.log('FanKeDa launch')
  },
}
</script>

<style>
page {
  min-height: 100%;
  background:
    radial-gradient(circle at 18% -4%, rgba(120, 198, 255, 0.26), transparent 34%),
    radial-gradient(circle at 92% 6%, rgba(53, 183, 121, 0.14), transparent 32%),
    linear-gradient(180deg, #f4fbff 0%, #f7fbff 42%, #eff8f3 100%);
  color: #173149;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

/* #ifdef H5 */
@media (min-width: 520px) {
  body {
    background:
      radial-gradient(circle at 18% -4%, rgba(120, 198, 255, 0.26), transparent 34%),
      radial-gradient(circle at 92% 6%, rgba(53, 183, 121, 0.14), transparent 32%),
      linear-gradient(180deg, #f4fbff 0%, #f7fbff 42%, #eff8f3 100%);
  }

  uni-page,
  uni-page-wrapper,
  uni-page-body {
    width: 430px !important;
    max-width: 430px !important;
    margin-right: auto;
    margin-left: auto;
  }

  uni-page-head .uni-page-head,
  uni-tabbar .uni-tabbar {
    right: auto !important;
    left: 50% !important;
    width: 430px !important;
    max-width: 430px !important;
    transform: translateX(-50%);
  }

  uni-page-head .uni-page-head {
    color: #173149 !important;
    background: rgba(247, 251, 255, 0.82) !important;
    backdrop-filter: saturate(180%) blur(18px);
    box-shadow: 0 1rpx 0 rgba(36, 87, 122, 0.06);
  }

  uni-page-head .uni-page-head__title {
    color: #173149 !important;
  }

  uni-page-head .uni-page-head-bd {
    width: 290px !important;
  }

  uni-tabbar .uni-tabbar__item {
    width: 25% !important;
  }

  uni-tabbar .uni-tabbar {
    overflow: hidden;
    border-radius: 30rpx 30rpx 0 0;
    background: rgba(255, 255, 255, 0.86) !important;
    backdrop-filter: saturate(180%) blur(18px);
    box-shadow: 0 -12rpx 36rpx rgba(36, 87, 122, 0.1);
  }
}
/* #endif */

view,
text,
button,
input,
picker {
  box-sizing: border-box;
}

button {
  margin: 0;
  padding: 0;
  border: 0;
  border-radius: 0;
  background: transparent;
  line-height: 1.4;
  transition: transform 0.16s ease, opacity 0.16s ease, box-shadow 0.16s ease;
}

button::after {
  border: 0;
}

button:active {
  opacity: 0.9;
  transform: scale(0.985);
}

.page-shell {
  min-height: 100vh;
  padding: 32rpx 28rpx 190rpx;
}

.section {
  margin-top: 28rpx;
}

.section-title {
  display: block;
  margin-bottom: 16rpx;
  color: #173149;
  font-size: 34rpx;
  font-weight: 700;
  line-height: 1.25;
}

.panel {
  border: 1rpx solid rgba(186, 211, 229, 0.54);
  border-radius: 26rpx;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: 0 10rpx 30rpx rgba(46, 103, 143, 0.06);
}

.muted {
  color: #5f7182;
}

.primary-button,
.secondary-button,
.danger-button {
  min-height: 88rpx;
  padding: 0 28rpx;
  border-radius: 999rpx;
  font-size: 30rpx;
  font-weight: 700;
  line-height: 88rpx;
  text-align: center;
}

.primary-button {
  background: #24577a;
  color: #ffffff;
}

.secondary-button {
  border: 1rpx solid rgba(36, 87, 122, 0.18);
  background: #ffffff;
  color: #24577a;
}

.danger-button {
  background: #ef5d60;
  color: #ffffff;
}

.button-row {
  display: flex;
  gap: 16rpx;
}

.button-row button {
  flex: 1;
}

.status-pill {
  min-width: 112rpx;
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  font-size: 24rpx;
  font-weight: 700;
  line-height: 1.2;
  text-align: center;
}

.level-low {
  background: rgba(53, 183, 121, 0.14);
  color: #147a4b;
}

.level-mid {
  background: rgba(255, 209, 102, 0.22);
  color: #8a5a00;
}

.level-high {
  background: rgba(255, 138, 91, 0.2);
  color: #aa3e18;
}

.level-full {
  background: rgba(239, 93, 96, 0.16);
  color: #b8232d;
}
</style>
