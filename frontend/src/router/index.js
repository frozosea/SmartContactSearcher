import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AboutView from '../views/AboutView.vue'
import LoginPage from '@/pages/LoginPage.vue'
import RegisterPage from '@/pages/RegisterPage.vue'
import NotFound from '@/pages/NotFound.vue'
import MainPage from '@/pages/MainPage.vue'
import AddContactPage from '@/pages/AddContactPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
    { path: '/', name: 'Home', component: MainPage },
    { path: '/login', name: 'Login', component: LoginPage },
    { path: '/register', name: 'Register', component: RegisterPage },
    {path: '/sign-up', name: 'Sign up', component: RegisterPage},
    {
      path: '/:pathMatch(.*)*', // This will match any route that doesn't exist
      name: 'NotFound',
      component: NotFound,  // Redirects to NotFoundPage.vue
    },
    {path: '/add-contact', name: 'Create contact', component: AddContactPage}
  ],
})

export default router
