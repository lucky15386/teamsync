import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue'), meta: { noAuth: true } },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue'), meta: { noAuth: true } },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue'), meta: { title: '工作台' } },
      { path: 'projects', name: 'Projects', component: () => import('../views/Projects.vue'), meta: { title: '项目管理' } },
      { path: 'projects/:id', name: 'ProjectDetail', component: () => import('../views/ProjectDetail.vue'), meta: { title: '项目详情' } },
      { path: 'tasks', name: 'Tasks', component: () => import('../views/MyTasks.vue'), meta: { title: '我的任务' } },
      { path: 'documents', name: 'Documents', component: () => import('../views/Documents.vue'), meta: { title: '文档管理' } },
      { path: 'chat', name: 'Chat', component: () => import('../views/Chat.vue'), meta: { title: '聊天' } },
      { path: 'notifications', name: 'Notifications', component: () => import('../views/Notifications.vue'), meta: { title: '消息通知' } },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue'), meta: { title: '个人中心' } },
      { path: 'users', name: 'Users', component: () => import('../views/Users.vue'), meta: { title: '用户管理', admin: true } },
      { path: 'operation-logs', name: 'OperationLogs', component: () => import('../views/OperationLogs.vue'), meta: { title: '操作日志', admin: true } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (!to.meta.noAuth && !localStorage.getItem('token')) {
    next('/login')
    return
  }
  if (to.meta.admin) {
    const user = JSON.parse(localStorage.getItem('user') || 'null')
    if (user?.role !== 'ADMIN') {
      next('/dashboard')
      return
    }
  }
  next()
})

export default router
