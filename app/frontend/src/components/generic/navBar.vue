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
    <nav class="navbar navbar-expand-sm">
        <div class="container-fluid">
            <RouterLink class="navbar-brand" to="/home"><img :src="logo" width="30">Arbitaja</RouterLink>
            <button class="navbar-toggler" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasNavbar" aria-controls="offcanvasNavbar" aria-expanded="false" aria-label="Toggle navigation" >
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvasNavbar" aria-labelledby="offcanvasNavbarLabel">
                <div class="offcanvas-header">
                    <RouterLink class="navbar-brand" to="/home"><img :src="logo" width="30">Arbitaja</RouterLink>
                    <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
                </div>
                <div class="offcanvas-body">
                    <ul class="navbar-nav">
                        <li class="nav-item rounded m-1">
                        <RouterLink 
                        to="/home" 
                        :class="[
                            isLinkActive('/home') 
                                ? 'active-item' : '',
                            'nav-link rounded text-center',
                        ]" 
                        >Home</RouterLink>
                        </li>
                        <li class="nav-item dropdown rounded m-1">
                        <a class="nav-link dropdown-toggle text-center" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            Competitions
                        </a>
                        <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                            <li><RouterLink class="dropdown-item" to="/bogus1">Current Competition</RouterLink></li>
                            <li><RouterLink class="dropdown-item" to="/bogus2">Last Competition</RouterLink></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><RouterLink class="dropdown-item" to="/bogus3">Competition History</RouterLink></li>
                        </ul>
                        </li>
                        <li class="nav-item rounded m-1" v-if="isUserAdmin">
                            <RouterLink 
                                to="/admin"
                                :class="[
                                    isLinkActive('/admin') 
                                        ? 'active-item' : '',
                                    'nav-link rounded text-center',
                                ]" 
                            >Admin</RouterLink>
                        </li>
                    </ul>
                    <div class="row ms-auto">
                        <div class="col-2 col-sm-3 d-flex nav-item rounded" 
                            :class="[
                                isLinkActive('/userProfile') 
                                    ? 'active-item' : '',
                            ]">
                            <RouterLink v-if="isLoggedIn" class="github rounded nav-link ps-2 me-2 align-content-center" :to="'/userProfile'"><i class="bi bi-person-gear"></i></RouterLink>
                        </div>
                        <div class="col-7 col-sm-3 d-flex">
                            <a href="https://github.com/WS-EE/Arbitaja" class="github rounded nav-link ps-3 pe-3 align-content-center bi bi-github" target="_blank" rel="noopener noreferrer"></a>
                        </div>
                        <div class="col-3 col-sm-3 d-flex me-sm-1 ms-sm-1">
                            <a v-if="isLoggedIn" class="btn btn-outline-dark m-1" @click.prevent="userLogout">Logout</a>
                            <RouterLink  v-if="!isLoggedIn" class="btn btn-outline-dark m-1" to="/login">Login</RouterLink>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </nav>
</template>

<style scoped>

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
    scale: 1.65;
}


</style>