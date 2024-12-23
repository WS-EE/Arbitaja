import { createRouter, createWebHistory } from 'vue-router';
import loginView from '@/views/loginView.vue';
import notFoundView from '@/views/notFoundView.vue';
import homeView from '@/views/homeView.vue';
import userView from '@/views/userView.vue';
import userProfileView from '@/views/userProfileView.vue';
import adminView from '@/views/adminView.vue';

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
            name: 'user',
            component: userView,
            redirect: 'home',
            children:[
                {
                    path: '/home',
                    name: 'home',
                    component: homeView 
                },
                {
                    path: '/userProfile',
                    name: 'userProfile',
                    component: userProfileView
                },
                {
                    path: '/admin/',
                    name: 'admin',
                    component: adminView
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