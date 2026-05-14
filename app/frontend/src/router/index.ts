import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import { ensureAuthRehydrated } from '@/composables/useAuthRehydrate';
import { API_BASE_URL } from '@/services/http'

import loginView from '@/views/loginView.vue';
import notFoundView from '@/views/notFoundView.vue';

import homeView from '@/views/userViews/homeView.vue';
import userView from '@/views/userView.vue';
import userProfileView from '@/views/userViews/userProfileView.vue';

import adminView from '@/views/adminView.vue';

import LoginPage from '@/components/login/LoginPage.vue';
import SignupPage from '@/components/login/SignupPage.vue';

import singupApproveView from '@/views/adminViews/Users/singupApproveView.vue';
import schoolView from '@/views/adminViews/Users/schoolView.vue';
import AdminUsersView from '@/views/adminViews/Users/adminUsersView.vue';
import editUserProfile from '@/views/adminViews/Users/editUserProfile.vue';

import allCompetitions from '@/views/competitionViews/allCompetitions.vue';
import competitionShow from '@/views/competitionViews/competitionShow.vue';

import addCompetitionView from '@/views/adminViews/competitions/addCompetitionView.vue';
import editCompetitionView from '@/views/adminViews/competitions/editCompetitionView.vue';
import editCompetitionCompetitorView from '@/views/adminViews/competitions/editCompetitionCompetitorView.vue';
import editCompetitionCriteriasView from '@/views/adminViews/competitions/editCompetitionCriteriasView.vue';

import adminRolesView from '@/views/adminViews/roles/adminRolesView.vue';
import editRolesView from '@/views/adminViews/roles/editRolesView.vue';

declare module 'vue-router' {
    interface RouteMeta {
        requiresAuth?: boolean
        requiresPrivilege?: string
    }
}

const router = createRouter({
    history: createWebHistory(API_BASE_URL),
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
                {
                    path: '/competition/:id',
                    name: 'userCompetitionShow',
                    component: competitionShow
                },
            ]
        },
        {
            path: '/admin/',
            name: 'admin',
            redirect: '/admin/competitions',
            component: adminView,
            meta: { requiresPrivilege: 'ADMIN' },
            children: [
                {
                    path: 'competitions/',
                    name: 'adminCompetiton',
                    component: allCompetitions 
                },
                {
                    path: 'competition/',
                    name: 'adminShowCompetiton',
                    children: [
                        {
                            path: 'get/:id',
                            name: 'adminCompetitionShow',
                            component: competitionShow
                        },
                        {
                            path: 'edit/:id',
                            name: 'editCompetition',
                            component: editCompetitionView
                        },
                        {  
                            path: 'edit/competitors/:id',
                            name: 'editCompetitionCompetitors',
                            component: editCompetitionCompetitorView
                        },
                        {  
                            path: 'edit/criterias/:id',
                            name: 'editCompetitionCriterias',
                            component: editCompetitionCriteriasView
                        },
                        {
                            path: 'new',
                            name: 'addCompetition',
                            component: addCompetitionView
                        },
                    ]
                },
                {
                    path: 'schools',
                    name: 'adminSchools',
                    component: schoolView
                },
                {
                    path: 'users/',
                    name: 'adminUsers',
                    redirect: 'signup',
                    children: [
                        {
                            path: 'user',
                            name: 'adminUsersUser',
                            component: AdminUsersView,
                        },
                        {
                            path: 'user_edit/:id',
                            name: 'adminUsersUserEdit',
                            component: editUserProfile
                        },
                        {
                            path: 'role',
                            name: 'adminUsersRole',
                            component: adminRolesView
                        },
                        {
                            path: 'role_edit/:id',
                            name: 'adminUsersRoleEdit',
                            component: editRolesView
                        },
                        {
                            path: 'singup',
                            name: 'adminUsersSingup',
                            component: singupApproveView,
                        },
                    ]
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

router.beforeEach(async (to) => {
    const store = useUserStore()

    const requiresAuth =
        Boolean(to.meta.requiresAuth) ||
        Boolean(to.meta.requiresPrivilege)

    if (requiresAuth) {
        await ensureAuthRehydrated({
            force: !store.id,
        })
    }

    if (requiresAuth && !store.id) {
        return {
            path: '/login',
            query: {
                redirect: to.fullPath,
            },
        }
    }

    if (
        to.meta.requiresPrivilege &&
        !store.hasPrivilege(to.meta.requiresPrivilege)
    ) {
        return {
            path: '/',
            query: { forbidden: '1' },
        }
    }

    return true
})

export default router