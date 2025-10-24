import request from '@/utils/request'

// 获取订单列表
export const getOrderList = (params) => {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

// 获取订单详情
export const getOrderDetail = (id) => {
  return request({
    url: `/order/${id}`,
    method: 'get'
  })
}

// 取消订单
export const cancelOrder = (id) => {
  return request({
    url: `/order/cancel/${id}`,
    method: 'post'
  })
}

// 支付订单
export const payOrder = (id) => {
  return request({
    url: `/order/pay/${id}`,
    method: 'post'
  })
}

// 生成支付二维码
export const generatePaymentQRCode = (id) => {
  return request({
    url: `/order/qrcode/${id}`,
    method: 'post'
  })
}

// 查询支付状态
export const checkPaymentStatus = (paymentId) => {
  return request({
    url: `/order/payment/status/${paymentId}`,
    method: 'get'
  })
}

// 模拟支付成功
export const simulatePaymentSuccess = (paymentId) => {
  return request({
    url: `/order/payment/callback/${paymentId}`,
    method: 'post'
  })
}

