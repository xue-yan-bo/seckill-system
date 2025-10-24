<template>
  <div class="product">
    <h1 class="page-title">商品列表</h1>
    
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索商品名称或描述"
        @keyup.enter="handleSearch"
        clearable
        style="max-width: 400px"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch" />
        </template>
      </el-input>
    </div>
    
    <div class="product-grid">
      <el-card v-for="item in productList" :key="item.id" class="product-card" shadow="hover">
        <div class="product-image">
          <el-image :src="item.image || 'https://via.placeholder.com/250'" fit="cover" />
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ item.name }}</h3>
          <p class="product-desc">{{ item.description }}</p>
          <div class="product-footer">
            <span class="price">¥{{ item.price }}</span>
            <div class="stats">
              <span>库存: {{ item.stock }}</span>
              <span>销量: {{ item.sales }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>
    
    <el-pagination
      v-if="total > 0"
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadProductList"
      @current-change="loadProductList"
      class="pagination"
    />
    
    <el-empty v-if="productList.length === 0" description="暂无商品" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getProductList } from '@/api/product'

const searchKeyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const productList = ref([])

const handleSearch = () => {
  pageNum.value = 1
  loadProductList()
}

const loadProductList = async () => {
  try {
    const res = await getProductList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value
    })
    
    if (res.code === 200) {
      productList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取商品列表失败', error)
  }
}

onMounted(() => {
  loadProductList()
})
</script>

<style scoped>
.product {
  padding: 20px;
}

.page-title {
  margin-bottom: 30px;
  font-size: 32px;
  color: #333;
}

.search-bar {
  margin-bottom: 30px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.product-card {
  transition: transform 0.3s;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  height: 250px;
  margin-bottom: 15px;
  border-radius: 4px;
  overflow: hidden;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.product-name {
  font-size: 16px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 14px;
  color: #666;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 20px;
  color: #ff4d4f;
  font-weight: bold;
}

.stats {
  display: flex;
  flex-direction: column;
  gap: 5px;
  font-size: 12px;
  color: #999;
}

.pagination {
  justify-content: center;
}
</style>

