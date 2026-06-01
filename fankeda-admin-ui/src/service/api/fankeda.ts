import { request } from '../request';

export type CrowdLevel = '人少' | '一般' | '较挤' | '拥挤' | '爆满';

export interface Stall {
  stallId: number;
  name: string;
  type: string;
  posX: number;
  posY: number;
  serveSpeed: number;
  distance: number;
  avgPrep: number;
  rating: number;
  queueCount: number;
  waitTime: number;
  walkTime: number;
  totalTime: number;
  level: CrowdLevel;
}

export interface QueueSnapshot {
  id: number;
  stallId: number;
  stallName: string;
  queueCount: number;
  reporterId: number | null;
  createdAt: string;
}

export interface RecommendStall {
  stallId: number;
  name: string;
  score: number;
  level: CrowdLevel;
  totalTime: number;
  waitTime: number;
  walkTime: number;
  prepTime: number;
  queueCount: number;
  distance: number;
  rating: number;
}

export interface RecommendResult {
  best: RecommendStall;
  stalls: RecommendStall[];
}

export interface QueueReportPayload {
  stallId: number;
  queueCount: number;
}

export interface AdminStallPayload {
  name: string;
  type: string;
  posX: number;
  posY: number;
  serveSpeed: number;
  distance: number;
  avgPrep: number;
  rating: number;
}

export interface AdminFeedback {
  id: number;
  userId: number;
  userNickname: string;
  stallId: number;
  stallName: string;
  isAccurate: boolean;
  createdAt: string;
}

export interface AdminUser {
  id: number;
  openid: string;
  nickname: string;
  classEndTime: string | null;
  reportCount: number;
  timeSaved: number;
  createdAt: string;
}

export function fetchStalls() {
  return request<Stall[]>({ url: '/stalls' });
}

export function fetchLatestQueues() {
  return request<QueueSnapshot[]>({ url: '/queue/latest' });
}

export function fetchRecommend(rushMode = false) {
  return request<RecommendResult>({ url: '/recommend', params: { rushMode } });
}

export function reportQueue(data: QueueReportPayload) {
  return request<QueueSnapshot>({ url: '/queue/report', method: 'post', data });
}

export function createAdminStall(data: AdminStallPayload) {
  return request<Stall>({ url: '/admin/stalls', method: 'post', data });
}

export function updateAdminStall(stallId: number, data: AdminStallPayload) {
  return request<Stall>({ url: `/admin/stalls/${stallId}`, method: 'put', data });
}

export function deleteAdminStall(stallId: number) {
  return request<boolean>({ url: `/admin/stalls/${stallId}`, method: 'delete' });
}

export function fetchAdminFeedback() {
  return request<AdminFeedback[]>({ url: '/admin/feedback' });
}

export function fetchAdminUsers() {
  return request<AdminUser[]>({ url: '/admin/users' });
}
