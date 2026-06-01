import { getMockProfile, getMockQueues, getMockRecommend, getMockStalls } from './mockData'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const TOKEN_KEY = 'FANKEDA_TOKEN'
const USER_KEY = 'FANKEDA_USER'

function getToken() {
  return uni.getStorageSync(TOKEN_KEY)
}

export function hasToken() {
  return Boolean(getToken())
}

export function getStoredUser() {
  return uni.getStorageSync(USER_KEY) || null
}

function setSession(session) {
  if (!session) return
  if (session.token) {
    uni.setStorageSync(TOKEN_KEY, session.token)
  }
  if (session.user) {
    uni.setStorageSync(USER_KEY, session.user)
  }
}

function request({ url, method = 'GET', data, auth = false }) {
  const headers = {}
  const token = getToken()
  if (auth && token) {
    headers.Authorization = `Bearer ${token}`
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: `${API_BASE_URL}${url}`,
      method,
      data,
      header: headers,
      success(response) {
        const body = response.data
        if (response.statusCode >= 200 && response.statusCode < 300 && body && body.code === 0) {
          resolve(body.data)
          return
        }
        reject(new Error((body && body.msg) || `HTTP ${response.statusCode}`))
      },
      fail(error) {
        reject(error)
      },
    })
  })
}

async function withFallback(loader, fallback) {
  try {
    return await loader()
  } catch (error) {
    console.warn('[FanKeDa] API fallback:', error.message || error)
    return fallback()
  }
}

async function getLoginCode() {
  return new Promise((resolve) => {
    if (!uni.login) {
      resolve(`h5-${Date.now()}`)
      return
    }
    uni.login({
      success(result) {
        resolve(result.code || `mock-${Date.now()}`)
      },
      fail() {
        resolve(`mock-${Date.now()}`)
      },
    })
  })
}

async function ensureLogin() {
  const token = getToken()
  if (token) return token
  const session = await login()
  return session.token
}

export async function login() {
  return withFallback(
    async () => {
      const code = await getLoginCode()
      const session = await request({
        url: '/api/auth/login',
        method: 'POST',
        data: { code },
      })
      setSession(session)
      return session
    },
    () => {
      const session = {
        token: `mock-token-${Date.now()}`,
        user: getMockProfile(),
      }
      setSession(session)
      return session
    }
  )
}

export async function getRecommend(rushMode = false) {
  return withFallback(
    () => request({ url: `/api/recommend?rushMode=${rushMode ? 'true' : 'false'}` }),
    () => getMockRecommend(rushMode)
  )
}

export async function getStalls() {
  return withFallback(
    () => request({ url: '/api/stalls' }),
    () => getMockStalls()
  )
}

export async function reportQueue(stallId, queueCount) {
  await ensureLogin()
  return withFallback(
    () =>
      request({
        url: '/api/queue/report',
        method: 'POST',
        auth: true,
        data: { stallId, queueCount },
      }),
    () => ({
      id: Date.now(),
      stallId,
      queueCount,
      reporterId: 1,
      createdAt: new Date().toISOString(),
    })
  )
}

export async function getLatestQueues() {
  return withFallback(
    () => request({ url: '/api/queue/latest' }),
    () => getMockQueues()
  )
}

export async function sendFeedback(stallId, isAccurate) {
  await ensureLogin()
  return withFallback(
    () =>
      request({
        url: '/api/feedback',
        method: 'POST',
        auth: true,
        data: { stallId, isAccurate },
      }),
    () => ({
      id: Date.now(),
      userId: 1,
      stallId,
      accurate: isAccurate,
      adjustedServeSpeed: null,
      createdAt: new Date().toISOString(),
    })
  )
}

export async function getProfile() {
  await ensureLogin()
  return withFallback(
    () => request({ url: '/api/user/profile', auth: true }),
    () => uni.getStorageSync(USER_KEY) || getMockProfile()
  )
}

export async function updateProfile(classEndTime) {
  await ensureLogin()
  return withFallback(
    async () => {
      const user = await request({
        url: '/api/user/profile',
        method: 'PUT',
        auth: true,
        data: { classEndTime },
      })
      uni.setStorageSync(USER_KEY, user)
      return user
    },
    () => {
      const user = {
        ...(uni.getStorageSync(USER_KEY) || getMockProfile()),
        classEndTime,
      }
      uni.setStorageSync(USER_KEY, user)
      return user
    }
  )
}
