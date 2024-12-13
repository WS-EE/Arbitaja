import { createRouter, createWebHistory } from 'vue-router';
import loginView from '@/views/loginView.vue';
import notFoundView from '@/views/notFoundView.vue';
import homeView from '@/views/homeView.vue';
import baseView from '@/views/baseView.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login',
            name: 'login',
            component: loginView
        },
        {
            path: '/',
            name: 'base',
            component: baseView,
            children:[
                {
                    path: '/home',
                    name: 'home',
                    component: homeView 
                },
            ]
        },
        {
            path: '/:catchAll(.*)',
            name: 'not-found',
            component: notFoundView
        }
    ]
});

export default router