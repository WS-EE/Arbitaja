import { createRouter, createWebHistory } from 'vue-router';
import loginView from '@/views/loginView.vue';
import notFoundView from '@/views/notFoundView.vue';
import homeView from '@/views/userViews/homeView.vue';
import userView from '@/views/userView.vue';
import userProfileView from '@/views/userViews/userProfileView.vue';
import adminView from '@/views/adminView.vue';
import LoginPage from '@/components/login/LoginPage.vue';
import SignupPage from '@/components/login/SignupPage.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login',
            name: 'login',
            component: loginView,
            redirect: '/login/1',
            children: [
                {
                    path: '/login/1',
                    name: 'loginMenu',
                    component: LoginPage
                },
                {
                    path: '/login/2',
                    name: 'signupMenu',
                    component: SignupPage
                },
            ]
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
            ]
        },
        {
            path: '/admin/',
            name: 'admin',
            component: adminView,
            children: [
                {
                    path: 'competition',
                    name: 'adminCompetiton',
                    component: homeView 
                },
                {
                    path: 'schools',
                    name: 'adminSchools',
                    component: userProfileView
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