import { isAuthenticated } from '@/mutations/auth'
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/pages/HomePage.vue'),
  },
  // Property routes
  {
    path: '/properties',
    name: 'properties',
    component: () => import('@/pages/property/PropertiesPage.vue'),
  },
  {
    path: '/properties/me',
    name: 'user-properties',
    component: () => import('@/pages/property/UserPropertiesPage.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/properties/new',
    name: 'new-property',
    component: () => import('@/pages/property/NewPropertyPage.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/properties/:slug',
    name: 'property-details',
    component: () => import('@/pages/property/PropertyDetailsPage.vue'),
  },
  // Auth routes
  {
    path: '/login',
    name: 'login',
    component: () => import('@/pages/auth/LoginPage.vue'),
    meta: { requiresGuest: true },
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/pages/auth/RegisterPage.vue'),
    meta: { requiresGuest: true },
  },
  // User routes
  {
    path: '/verify-email',
    name: 'verify-email',
    component: () => import('@/pages/user/VerifyEmailPage.vue'),
    meta: { requiresGuest: true },
  },
  {
    path: '/settings',
    name: 'settings',
    component: () => import('@/pages/user/SettingsPage.vue'),
    meta: { requiresAuth: true },
  },
  // Other routes
  {
    path: '/terms-of-service',
    name: 'tos',
    component: () => import('@/pages/other/TosPage.vue'),
  },
  {
    path: '/privacy-policy',
    name: 'privacy-policy',
    component: () => import('@/pages/other/PrivacyPolicyPage.vue'),
  },
  // Catch 404 errors
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/pages/ErrorPage.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes,
})

router.beforeEach(async (to, from, next) => {
  const authenticated = isAuthenticated()

  if (to.meta.requiresAuth && !authenticated) {
    next({ name: 'login' })
  } else if (to.meta.requiresGuest && authenticated) {
    next({ name: 'home' })
  } else {
    next()
  }
})

export default router
