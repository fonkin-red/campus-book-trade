import request from '@/utils/request'

// ===== 用户相关 =====
export const login = (data) => request.post('/user/login', data)
export const register = (data) => request.post('/user/register', data)
export const getUserInfo = () => request.get('/user/info')
export const updateUser = (data) => request.put('/user/info', data)

// ===== 图书相关 =====
export const getBookList = (params) => request.get('/book/list', { params })
export const getBookDetail = (id) => request.get(`/book/${id}`)
export const searchBooks = (keyword) => request.get('/book/search', { params: { keyword } })
export const publishBook = (data) => request.post('/book', data)
export const updateBook = (id, data) => request.put(`/book/${id}`, data)
export const deleteBook = (id) => request.delete(`/book/${id}`)

// ===== 分类相关 =====
export const getCategories = () => request.get('/category/list')

// ===== 购物车相关 =====
export const getCart = () => request.get('/cart')
export const addToCart = (data) => request.post('/cart', data)
export const updateCartItem = (id, data) => request.put(`/cart/${id}`, data)
export const removeCartItem = (id) => request.delete(`/cart/${id}`)

// ===== 订单相关 =====
export const getOrders = (params) => request.get('/order/list', { params })
export const createOrder = (data) => request.post('/order', data)
export const payOrder = (id) => request.put(`/order/${id}/pay`)
export const confirmOrder = (id) => request.put(`/order/${id}/confirm`)
export const cancelOrder = (id) => request.put(`/order/${id}/cancel`)

// ===== 收藏相关 =====
export const getFavorites = () => request.get('/favorite')
export const addFavorite = (bookId) => request.post(`/favorite/${bookId}`)
export const removeFavorite = (bookId) => request.delete(`/favorite/${bookId}`)
export const checkFavorite = (bookId) => request.get(`/favorite/check/${bookId}`)
// ===== 公告相关 =====
export const getAnnouncements = () => request.get('/admin/announcements')
export const publishAnnouncement = (data) => request.post('/admin/announcement', data)
export const updateAnnouncement = (id, data) => request.put(`/admin/announcement/${id}`, data)
export const deleteAnnouncement = (id) => request.delete(`/admin/announcement/${id}`)
export const getPublicAnnouncements = () => request.get('/announcement/list')

// ===== 文件上传 =====
export const uploadFile = (file, onProgress) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload', formData, {
    onUploadProgress: onProgress ? (e) => {
      const pct = e.total ? Math.round((e.loaded / e.total) * 100) : 0
      onProgress(pct)
    } : undefined
  })
}

export const deleteFile = (filename) => request.delete('/file', { params: { filename } })

// ===== 管理员相关 =====
export const getAdminUsers = (params) => request.get('/admin/users', { params })
export const updateUserStatus = (id, status) => request.put(`/admin/users/${id}/status`, { status })
export const addCategory = (data) => request.post('/admin/category', data)
export const updateCategory = (id, data) => request.put(`/admin/category/${id}`, data)
export const deleteCategory = (id) => request.delete(`/admin/category/${id}`)
export const getAdminOrders = (params) => request.get('/admin/orders', { params })
export const shipOrder = (id) => request.put(`/order/${id}/ship`)
// 公告相关已在上面声明，这里删除重复的即可