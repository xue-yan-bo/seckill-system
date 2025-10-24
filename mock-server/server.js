import express from 'express';
import cors from 'cors';
import QRCode from 'qrcode';

const app = express();
app.use(cors());
app.use(express.json());

// Mock数据
let users = [
  { id: 1, username: 'admin', password: 'e10adc3949ba59abbe56e057f20f883e', nickname: '管理员', phone: '13800138000', email: 'admin@seckill.com', status: 0, createTime: new Date() }
];

let products = [
  { id: 1, name: 'iPhone 15 Pro', description: '苹果最新旗舰手机，搭载A17 Pro芯片，钛金属边框', image: 'https://picsum.photos/300/300?random=1', price: 7999.00, stock: 1000, sales: 0, status: 1 },
  { id: 2, name: '小米14', description: '骁龙8 Gen3处理器，徕卡光学镜头，120W快充', image: 'https://picsum.photos/300/300?random=2', price: 3999.00, stock: 2000, sales: 0, status: 1 },
  { id: 3, name: '华为Mate 60 Pro', description: '麒麟9000S芯片，卫星通信，鸿蒙4.0系统', image: 'https://picsum.photos/300/300?random=3', price: 6999.00, stock: 1500, sales: 0, status: 1 },
  { id: 4, name: 'MacBook Pro 16', description: 'M3 Max芯片，16GB内存，512GB SSD', image: 'https://picsum.photos/300/300?random=4', price: 19999.00, stock: 500, sales: 0, status: 1 },
  { id: 5, name: 'AirPods Pro 2', description: '主动降噪无线耳机，USB-C充电', image: 'https://picsum.photos/300/300?random=5', price: 1899.00, stock: 3000, sales: 0, status: 1 }
];

let seckillActivities = [
  { id: 1, productId: 1, productName: 'iPhone 15 Pro', productImage: 'https://picsum.photos/300/300?random=1', seckillPrice: 6999.00, originalPrice: 7999.00, seckillStock: 95, status: 1, startTime: '2024-12-01T10:00:00', endTime: '2024-12-31T23:59:59' },
  { id: 2, productId: 2, productName: '小米14', productImage: 'https://picsum.photos/300/300?random=2', seckillPrice: 2999.00, originalPrice: 3999.00, seckillStock: 180, status: 1, startTime: '2024-12-01T10:00:00', endTime: '2024-12-31T23:59:59' },
  { id: 3, productId: 3, productName: '华为Mate 60 Pro', productImage: 'https://picsum.photos/300/300?random=3', seckillPrice: 5999.00, originalPrice: 6999.00, seckillStock: 140, status: 1, startTime: '2024-12-01T10:00:00', endTime: '2024-12-31T23:59:59' }
];

let orders = [];
let currentToken = null;
let paymentRecords = {}; // 存储支付记录 { paymentId: { orderId, status, qrcode } }

// 用户登录
app.post('/api/user/login', (req, res) => {
  const { username, password } = req.body;
  const user = users.find(u => u.username === username);
  
  if (!user) {
    return res.json({ code: 500, message: '用户不存在' });
  }
  
  if (user.password !== password && password !== '123456') {
    return res.json({ code: 500, message: '密码错误' });
  }
  
  const token = 'mock_token_' + Date.now();
  currentToken = token;
  
  res.json({
    code: 200,
    message: '登录成功',
    data: { token }
  });
});

// 用户注册
app.post('/api/user/register', (req, res) => {
  const { username, password, nickname, phone, email } = req.body;
  
  if (users.find(u => u.username === username)) {
    return res.json({ code: 500, message: '用户名已存在' });
  }
  
  const newUser = {
    id: users.length + 1,
    username,
    password: 'e10adc3949ba59abbe56e057f20f883e',
    nickname: nickname || username,
    phone,
    email,
    status: 0,
    createTime: new Date()
  };
  
  users.push(newUser);
  res.json({ code: 200, message: '注册成功' });
});

// 获取用户信息
app.get('/api/user/info', (req, res) => {
  res.json({
    code: 200,
    data: {
      id: 1,
      username: 'admin',
      nickname: '管理员',
      phone: '13800138000',
      email: 'admin@seckill.com',
      status: 0,
      createTime: new Date()
    }
  });
});

// 商品列表
app.get('/api/product/list', (req, res) => {
  const { pageNum = 1, pageSize = 10, keyword = '' } = req.query;
  
  let filteredProducts = products;
  if (keyword) {
    filteredProducts = products.filter(p => 
      p.name.includes(keyword) || p.description.includes(keyword)
    );
  }
  
  const start = (pageNum - 1) * pageSize;
  const end = start + parseInt(pageSize);
  const records = filteredProducts.slice(start, end);
  
  res.json({
    code: 200,
    data: {
      records,
      total: filteredProducts.length,
      current: parseInt(pageNum),
      size: parseInt(pageSize)
    }
  });
});

// 商品详情
app.get('/api/product/:id', (req, res) => {
  const product = products.find(p => p.id == req.params.id);
  res.json({
    code: 200,
    data: product
  });
});

// 秒杀活动列表
app.get('/api/seckill/list', (req, res) => {
  res.json({
    code: 200,
    data: seckillActivities
  });
});

// 秒杀活动详情
app.get('/api/seckill/:id', (req, res) => {
  const activity = seckillActivities.find(a => a.id == req.params.id);
  res.json({
    code: 200,
    data: activity
  });
});

// 参与秒杀
app.post('/api/seckill/:id', (req, res) => {
  const activityId = parseInt(req.params.id);
  const activity = seckillActivities.find(a => a.id === activityId);
  
  if (!activity) {
    return res.json({ code: 500, message: '秒杀活动不存在' });
  }
  
  if (activity.seckillStock <= 0) {
    return res.json({ code: 500, message: '商品已售罄' });
  }
  
  // 扣减库存
  activity.seckillStock--;
  
  // 创建订单
  const order = {
    id: orders.length + 1,
    orderNo: 'SK' + Date.now(),
    userId: 1,
    productId: activity.productId,
    activityId: activity.id,
    productName: activity.productName,
    productImage: 'https://picsum.photos/60/60?random=' + activity.productId,
    quantity: 1,
    price: activity.seckillPrice,
    totalAmount: activity.seckillPrice,
    status: 0,
    createTime: new Date()
  };
  
  orders.push(order);
  
  res.json({
    code: 200,
    message: '秒杀成功，订单创建中...'
  });
});

// 检查用户是否已参与秒杀
app.get('/api/seckill/check/:id', (req, res) => {
  const activityId = parseInt(req.params.id);
  const participated = orders.some(o => o.activityId === activityId);
  
  res.json({
    code: 200,
    data: participated
  });
});

// 订单列表
app.get('/api/order/list', (req, res) => {
  const { pageNum = 1, pageSize = 10 } = req.query;
  
  const start = (pageNum - 1) * pageSize;
  const end = start + parseInt(pageSize);
  const records = orders.slice(start, end);
  
  res.json({
    code: 200,
    data: {
      records,
      total: orders.length,
      current: parseInt(pageNum),
      size: parseInt(pageSize)
    }
  });
});

// 订单详情
app.get('/api/order/:id', (req, res) => {
  const order = orders.find(o => o.id == req.params.id);
  res.json({
    code: 200,
    data: order
  });
});

// 生成支付二维码
app.post('/api/order/qrcode/:id', async (req, res) => {
  const orderId = req.params.id;
  const order = orders.find(o => o.id == orderId);
  
  if (!order) {
    return res.json({ code: 500, message: '订单不存在' });
  }
  
  if (order.status !== 0) {
    return res.json({ code: 500, message: '订单状态不正确' });
  }
  
  // 生成支付ID
  const paymentId = 'PAY' + Date.now();
  
  // 这里可以放您的微信或支付宝收款链接，或者您的个人信息
  const paymentUrl = `您的微信/支付宝收款码链接或信息-订单号:${order.orderNo}-金额:${order.totalAmount}元`;
  
  try {
    // 生成二维码
    const qrcodeDataUrl = await QRCode.toDataURL(paymentUrl, {
      width: 300,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#FFFFFF'
      }
    });
    
    // 保存支付记录
    paymentRecords[paymentId] = {
      orderId: orderId,
      orderNo: order.orderNo,
      amount: order.totalAmount,
      status: 'pending', // pending: 待支付, success: 支付成功
      createTime: new Date()
    };
    
    res.json({
      code: 200,
      data: {
        paymentId,
        qrcode: qrcodeDataUrl,
        amount: order.totalAmount,
        orderNo: order.orderNo
      }
    });
  } catch (error) {
    console.error('生成二维码失败', error);
    res.json({ code: 500, message: '生成二维码失败' });
  }
});

// 查询支付状态（前端轮询此接口）
app.get('/api/order/payment/status/:paymentId', (req, res) => {
  const paymentId = req.params.paymentId;
  const payment = paymentRecords[paymentId];
  
  if (!payment) {
    return res.json({ code: 500, message: '支付记录不存在' });
  }
  
  res.json({
    code: 200,
    data: {
      status: payment.status,
      orderId: payment.orderId
    }
  });
});

// 模拟支付成功回调（实际应用中这是支付平台的回调）
app.post('/api/order/payment/callback/:paymentId', (req, res) => {
  const paymentId = req.params.paymentId;
  const payment = paymentRecords[paymentId];
  
  if (!payment) {
    return res.json({ code: 500, message: '支付记录不存在' });
  }
  
  // 更新支付状态
  payment.status = 'success';
  payment.payTime = new Date();
  
  // 更新订单状态
  const order = orders.find(o => o.id == payment.orderId);
  if (order) {
    order.status = 1;
    order.payTime = new Date();
  }
  
  res.json({
    code: 200,
    message: '支付成功'
  });
});

// 支付订单（保留原接口，用于直接支付）
app.post('/api/order/pay/:id', (req, res) => {
  const order = orders.find(o => o.id == req.params.id);
  if (order) {
    order.status = 1;
    order.payTime = new Date();
  }
  res.json({
    code: 200,
    message: '支付成功'
  });
});

// 取消订单
app.post('/api/order/cancel/:id', (req, res) => {
  const order = orders.find(o => o.id == req.params.id);
  if (order) {
    order.status = 2;
    
    // 恢复库存
    const activity = seckillActivities.find(a => a.id === order.activityId);
    if (activity) {
      activity.seckillStock++;
    }
  }
  res.json({
    code: 200,
    message: '订单已取消'
  });
});

const PORT = 8083;
app.listen(PORT, () => {
  console.log('========================================');
  console.log(`Mock API服务器启动成功！`);
  console.log(`地址: http://localhost:${PORT}`);
  console.log('========================================');
});

