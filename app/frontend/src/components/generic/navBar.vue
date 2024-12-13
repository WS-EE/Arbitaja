<script setup>
import { RouterLink, useRoute } from 'vue-router';
import logo from '@/assets/media/logo.svg'
import git_logo from '@/assets/media/git.png';

const isLinkActive = (routePath) => {
    const route = useRoute();
    return route.path === routePath;
}

// Get if user is logged in.
import { ref, onMounted } from 'vue';
import axios from 'axios';
const isLoggedIn = ref(false)
const userProfile = ref('')

onMounted(async () => {
    try {
        const response = await axios.get('http://localhost/api/v1/profile');
        if (response.data !== 'Successful login'){
            userProfile.value = response.data
            isLoggedIn.value = true
        }
    } catch(error) {
        console.log('Error:', error)
    };
});

</script>

<template>
    <nav class="navbar navbar-expand-lg">
        <div class="container-fluid">
            <RouterLink class="navbar-brand d-inline-block align-text-top" to="home"><img :src="logo" width="30">Arbitaja</RouterLink>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item rounded">
                <RouterLink 
                to="home" 
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
                <li class="nav-item rounded" v-if="isLoggedIn"><a class="nav-link disabled">Admin</a></li>
            </ul>
            <div class="d-flex me-5 p-1 github rounded">
                <img :src="git_logo" alt="" width="30px">
                <a href="https://github.com/WS-EE/Arbitaja" class="nav-link" target="_blank" rel="noopener noreferrer">GitHub</a>
            </div>
            <div class="d-flex" v-if="!isLoggedIn">
                <RouterLink class="btn btn-outline-dark" to="login">Login</RouterLink>
            </div>
            <div class="d-flex" v-if="isLoggedIn">
                <p class="me-2">{{ userProfile }}</p>
                <button class="btn btn-outline-dark" @click="logoutUser">Logout</button>
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

.disabled:hover {
    background-color: red !important;
}

.active-item {
    background-color: var(--button-light);
    color: var(--text-dark);
}

.github {
    color: brown;
    font-weight: 600;
}

.github:hover {
    background-color: var(--button-light);
}

</style>