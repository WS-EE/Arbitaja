import { createRouter, createWebHistory } from 'vue-router';
import login from '@/views/login.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login',
            name: 'login',
            component: login
        },
        {
            path: '/',
            redirect: { name: 'login' }
        }
    ]
});

export default router