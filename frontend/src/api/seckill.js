import request from '@/utils/request'

// 获取秒杀活动列表
export const getSeckillList = () => {
  return request({
    url: '/seckill/list',
    method: 'get'
  })
}

// 获取秒杀活动详情
export const getSeckillDetail = (id) => {
  return request({
    url: `/seckill/${id}`,
    method: 'get'
  })
}

// 参与秒杀
export const doSeckill = (activityId) => {
  return request({
    url: `/seckill/${activityId}`,
    method: 'post'
  })
}

// 检查用户是否已参与秒杀
export const checkUserSeckill = (activityId) => {
  return request({
    url: `/seckill/check/${activityId}`,
    method: 'get'
  })
}

