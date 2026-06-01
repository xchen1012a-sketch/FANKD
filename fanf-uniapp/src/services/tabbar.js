export function setCustomTabBarSelected(index) {
  // #ifdef MP-WEIXIN
  const page = typeof getCurrentPages === 'function'
    ? getCurrentPages().at(-1)
    : null
  if (page && typeof page.getTabBar === 'function' && page.getTabBar()) {
    page.getTabBar().setData({ selected: index })
  }
  // #endif
}
