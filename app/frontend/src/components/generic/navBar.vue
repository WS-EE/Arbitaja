<script setup>
import { RouterLink, useRoute } from 'vue-router';
import logo from '@/assets/media/logo.svg'
import axios from 'axios';

// import cookie handler
import { useCookies } from '@/assets/js/useCookies';
const $cookies = useCookies(); 

// check the active link
const isLinkActive = (routePath) => {
    const route = useRoute();
    return route.path === routePath;
}

// Get if user is logged in.
import { ref, onMounted } from 'vue';
import router from '@/router';
const isLoggedIn = ref(false)
const userParameters = ref('')
const username = ref('')
const displayUsername = ref('')
const userRoles = ref([])
const isUserAdmin = ref();

// Get cookies for user logged in status
onMounted(async () => {
    try {
        // get cookies
        isLoggedIn.value = await $cookies.get('isLoggedIn');
        userParameters.value = await $cookies.get('userParameters');
        username.value = userParameters.value.username
        displayUsername.value = userParameters.value.username
        userRoles.value = userParameters.value.roles
        isUserAdmin.value = await checkUserAdmin();
    } catch(err) {
        console.log('User is unauthenticated')
    }
})

// User logout function
const userLogout = async () => {
    try {
        await axios.post('logout');
    } catch(error) {
        // We are expecting 401 response when loggin out.
        await $cookies.remove('userParameters');
        await $cookies.remove('isLoggedIn');
        await router.replace('/home');
        location.reload();
    }
}

// Is the admin button on navbar disabled
const checkUserAdmin = async () => {
    // Get user groups
    try {
        let isTrue = false
        const userGroups = userParameters.value.roles
        // loop over user groups
        for (var role of userGroups){
            // if we find admin groups in users groups return true
            if (role.authority === 'admin') {
                isTrue = true
            }
        }
        // else return false
        return isTrue
    } catch(error) {
        console.log('Error:' + error);
        return false
    }
}
</script>

<template>
    <nav class="navbar navbar-expand-lg">
        <div class="container-fluid">
            <RouterLink class="navbar-brand d-inline-block align-text-top" to="/home"><img :src="logo" width="30">Arbitaja</RouterLink>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item rounded">
                <RouterLink 
                to="/home" 
                :class="[
                    isLinkActive('/home') 
                        ? 'active-item' : '',
                    'nav-link rounded',
                ]" 
                >Home</RouterLink>
                </li>
                <li class="nav-item dropdown rounded">
                <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    Competitions
                </a>
                <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                    <li><RouterLink class="dropdown-item" to="/bogus1">Current Competition</RouterLink></li>
                    <li><RouterLink class="dropdown-item" to="/bogus2">Last Competition</RouterLink></li>
                    <li><hr class="dropdown-divider"></li>
                    <li><RouterLink class="dropdown-item" to="/bogus3">Competition History</RouterLink></li>
                </ul>
                </li>
                <li class="nav-item dropdown rounded" v-if="isLoggedIn">
                    <a class="nav-link dropdown-toggle" href="#" id="userProfile" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        User Profile
                    </a>
                    <ul class="dropdown-menu" aria-labelledby="userProfile">
                        <li><RouterLink class="dropdown-item" :to="'/userProfile/'+ username">{{ displayUsername }}</RouterLink></li>
                    </ul>
                </li>
                <li class="nav-item rounded" v-if="isUserAdmin">
                    <RouterLink 
                        to="/admin"
                        :class="[
                            isLinkActive('/admin') 
                                ? 'active-item' : '',
                            'nav-link rounded',
                        ]" 
                    >Admin</RouterLink>
                </li>
            </ul>
            <div class="d-flex me-5 p-1 github rounded">
                <i class="me-1 bi bi-github"></i>
                <a href="https://github.com/WS-EE/Arbitaja" class="nav-link" target="_blank" rel="noopener noreferrer">GitHub</a>
            </div>
            <div class="d-flex" v-if="!isLoggedIn">
                <RouterLink class="btn btn-outline-dark" to="/login">Login</RouterLink>
            </div>
            <div class="d-flex" v-if="isLoggedIn">
                <button class="btn btn-outline-dark" @click.prevent="userLogout">Logout</button>
            </div>
            </div>
        </div>
    </nav>
</template>

<style scoped>
.dropdown:hover .dropdown-menu {
    display: block;
    margin-top: 0; /* remove the gap so it doesn't close */
}

.big-box {
    background-color: var(--big-block-background);
}

.nav-item:hover {
    background-color: var(--button-dark) !important;
}

.active-item {
    background-color: var(--button-light);
    color: var(--text-dark);
}

.github {
    font-weight: 600;
    scale: 1.25;
}

.github:hover {
    background-color: var(--button-light);
}

</style>