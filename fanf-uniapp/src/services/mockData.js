const baseStalls = [
  {
    stallId: 1,
    name: '快餐米饭',
    type: '盖浇饭 / 小炒',
    posX: 20,
    posY: 58,
    serveSpeed: 2.3,
    distance: 128,
    avgPrep: 2.5,
    rating: 4.4,
    queueCount: 8,
  },
  {
    stallId: 2,
    name: '面食窗口',
    type: '汤面 / 拌面',
    posX: 50,
    posY: 26,
    serveSpeed: 1.7,
    distance: 102,
    avgPrep: 3.5,
    rating: 4.6,
    queueCount: 9,
  },
  {
    stallId: 3,
    name: '轻食套餐',
    type: '沙拉 / 鸡胸',
    posX: 50,
    posY: 58,
    serveSpeed: 1.5,
    distance: 96,
    avgPrep: 2.2,
    rating: 4.1,
    queueCount: 5,
  },
  {
    stallId: 4,
    name: '麻辣香锅',
    type: '自选称重',
    posX: 80,
    posY: 58,
    serveSpeed: 1.1,
    distance: 168,
    avgPrep: 7,
    rating: 4.8,
    queueCount: 13,
  },
  {
    stallId: 5,
    name: '包子粥铺',
    type: '包子 / 粥',
    posX: 20,
    posY: 26,
    serveSpeed: 2.9,
    distance: 54,
    avgPrep: 1.3,
    rating: 4,
    queueCount: 6,
  },
  {
    stallId: 6,
    name: '预制套餐',
    type: '固定套餐',
    posX: 80,
    posY: 26,
    serveSpeed: 3.2,
    distance: 88,
    avgPrep: 1.8,
    rating: 4.2,
    queueCount: 10,
  },
]

const round1 = (value) => Math.round(value * 10) / 10

const levelFromQueue = (queueCount) => {
  if (queueCount <= 6) return '人少'
  if (queueCount <= 10) return '一般'
  if (queueCount <= 14) return '较挤'
  return '爆满'
}

const scoreStall = (stall, rushMode) => {
  const waitTime = round1(stall.queueCount / stall.serveSpeed)
  const walkTime = round1(stall.distance / 80)
  const prepTime = stall.avgPrep
  const totalTime = round1(waitTime + walkTime + prepTime)
  const timeScore = Math.max(0, 100 - totalTime * 5)
  const crowdScore = Math.max(0, 100 - (stall.queueCount / 26) * 100)
  const distanceScore = Math.max(0, 100 - stall.distance / 2.8)
  const tasteScore = stall.rating * 20
  const weights = rushMode
    ? { time: 0.52, crowd: 0.22, distance: 0.18, taste: 0.08 }
    : { time: 0.35, crowd: 0.2, distance: 0.18, taste: 0.27 }
  const score = round1(
    timeScore * weights.time +
      crowdScore * weights.crowd +
      distanceScore * weights.distance +
      tasteScore * weights.taste
  )

  return {
    ...stall,
    score,
    level: levelFromQueue(stall.queueCount),
    totalTime,
    waitTime,
    walkTime,
    prepTime,
  }
}

export function getMockStalls() {
  return baseStalls.map((stall) => scoreStall(stall, false))
}

export function getMockRecommend(rushMode = false) {
  const stalls = baseStalls
    .map((stall) => scoreStall(stall, rushMode))
    .filter((stall) => !rushMode || stall.totalTime <= 14)
    .sort((a, b) => b.score - a.score)

  return {
    best: stalls[0],
    stalls,
  }
}

export function getMockQueues() {
  return baseStalls.map((stall, index) => ({
    id: index + 1,
    stallId: stall.stallId,
    stallName: stall.name,
    queueCount: stall.queueCount,
    reporterId: null,
    createdAt: new Date().toISOString(),
  }))
}

export function getMockProfile() {
  return {
    id: 1,
    openid: 'mock-openid-demo',
    nickname: '饭刻达用户',
    classEndTime: '12:10',
    reportCount: 3,
    timeSaved: 21,
    createdAt: new Date().toISOString(),
  }
}
