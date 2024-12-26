<script setup>
// active link handler
import { RouterLink, useRoute } from 'vue-router';

// check the active link
const isLinkActive = (routePath) => {
    const route = useRoute();
    return route.path === routePath;
}

// import logo
import logo from '@/assets/media/logo.svg'

</script>

<template>
    <nav class="navbar navbar-expand-md">
        <div class="container-fluid">
            <RouterLink class="navbar-brand" to="/admin"><img :src="logo" width="30">Arbitaja Admin</RouterLink>
            <button class="navbar-toggler" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasNavbar" aria-controls="offcanvasNavbar" aria-expanded="false" aria-label="Toggle navigation" >
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvasNavbar" aria-labelledby="offcanvasNavbarLabel">
                <div class="offcanvas-header">
                    <RouterLink class="navbar-brand" to="/home"><img :src="logo" width="30">Arbitaja Admin</RouterLink>
                    <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
                </div>
                <div class="offcanvas-body">
                    <ul class="navbar-nav">
                        <li class="nav-item rounded m-1">
                            <RouterLink 
                            to="/admin/competition" 
                            :class="[
                                isLinkActive('/admin/competition') 
                                    ? 'active-item' : '',
                                'nav-link rounded text-center',
                            ]" 
                            >Competitions</RouterLink>
                        </li>
                        <li class="nav-item rounded m-1">
                            <RouterLink 
                            to="/admin/schools" 
                            :class="[
                                isLinkActive('/admin/schools') 
                                    ? 'active-item' : '',
                                'nav-link rounded text-center',
                            ]" 
                            >Schools</RouterLink>
                        </li>
                        <li class="nav-item rounded m-1 dropdown">
                            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                Users
                            </a>
                            <ul class="dropdown-menu">
                                <li><RouterLink class="dropdown-item" to="/admin/users/user">Users</RouterLink></li>
                                <li><RouterLink class="dropdown-item" to="/admin/users/role">Roles</RouterLink></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><RouterLink class="dropdown-item" to="/admin/users/singup">Signup Approve</RouterLink></li>
                            </ul>
                        </li>
                    </ul>
                    <div class="ms-auto flex-row-reverse justify-content-center align-items-center text-center">
                        <RouterLink class="btn btn-outline-dark align-content-center m-1" to="/home">Back to User View</RouterLink>
                    </div>
                </div>
            </div>
        </div>
    </nav>
</template>

<style scoped>
.nav-item:hover {
    background-color: var(--button-dark) !important;
}

.active-item {
    background-color: var(--button-light);
    color: var(--text-dark);
}
</style>