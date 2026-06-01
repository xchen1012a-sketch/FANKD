const tabs = [
  {
    pagePath: '/pages/index/index',
    text: '首页',
    icon: 'home',
  },
  {
    pagePath: '/pages/stalls/index',
    text: '窗口',
    icon: 'grid',
  },
  {
    pagePath: '/pages/report/index',
    text: '上报',
    icon: 'plus',
  },
  {
    pagePath: '/pages/mine/index',
    text: '我的',
    icon: 'user',
  },
]

Component({
  data: {
    selected: 0,
    list: tabs,
  },
  methods: {
    switchTab(event) {
      const { index, path } = event.currentTarget.dataset
      wx.switchTab({ url: path })
      this.setData({ selected: Number(index) })
    },
  },
})
