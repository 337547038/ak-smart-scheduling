import request from '@/utils/request'

// 登录
export function login(data) {
  return request({ url: '/auth/login', method: 'post', data })
}

// 班级管理
export function getClassList() {
  return request({ url: '/class/list', method: 'get' })
}
export function addClass(data) {
  return request({ url: '/class/add', method: 'post', data })
}
export function updateClass(data) {
  return request({ url: '/class/update', method: 'put', data })
}
export function deleteClass(id) {
  return request({ url: `/class/delete/${id}`, method: 'delete' })
}

// 老师管理
export function getTeacherList() {
  return request({ url: '/teacher/list', method: 'get' })
}
export function addTeacher(data) {
  return request({ url: '/teacher/add', method: 'post', data })
}
export function updateTeacher(data) {
  return request({ url: '/teacher/update', method: 'put', data })
}
export function deleteTeacher(id) {
  return request({ url: `/teacher/delete/${id}`, method: 'delete' })
}

// 课程管理
export function getCourseList() {
  return request({ url: '/course/list', method: 'get' })
}

// 课时管理
export function getTimeSlotList() {
  return request({ url: '/timeslot/list', method: 'get' })
}
export function addTimeSlot(data) {
  return request({ url: '/timeslot/add', method: 'post', data })
}
export function updateTimeSlot(data) {
  return request({ url: '/timeslot/update', method: 'put', data })
}
export function deleteTimeSlot(id) {
  return request({ url: `/timeslot/delete/${id}`, method: 'delete' })
}

// 排课
export function getScheduleList(params) {
  return request({ url: '/schedule/list', method: 'get', params })
}
export function autoSchedule() {
  return request({ url: '/schedule/auto', method: 'post' })
}
export function clearSchedule() {
  return request({ url: '/schedule/clear', method: 'post' })
}
export function adjustSchedule(data) {
  return request({ url: '/schedule/adjust', method: 'post', data })
}
export function getScheduleChanges() {
  return request({ url: '/schedule/changes', method: 'get' })
}
