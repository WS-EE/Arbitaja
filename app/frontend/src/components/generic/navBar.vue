<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import logo from '@/assets/media/logo.svg'
import router from '@/router'
import { useUserStore } from '@/stores/userStore'
import { apiClient } from '@/services/api'
import { COPYRIGHT_HEADER } from '@/config'

const userStore = useUserStore()
const route = useRoute()

const isLinkActive = (routePath: string) => route.path === routePath

const isLoggedIn = computed(() => !!userStore.username && userStore.username !== 'anonymousUser')
const isUserAdmin = computed(() => userStore.hasPrivilege('ADMIN'))

const userLogout = async () => {
    try {
        const response = await apiClient.auth.logout()
        if (!response.success) {
            throw new Error(response.error.message || 'Unknown error')
        }
        userStore.clearUserAuthorization()
        await router.replace('/home')
        location.reload()
    } catch {
        location.reload()
    }
}
</script>

<template>
    <!-- License Modal before navbar -->
    <!-- If this is below navbar then it breaks on mobile-->
    <div class="modal fade" id="LicenseBackdrop" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticLicenseBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="staticBackdropLabel">MIT License</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>MIT License</p>

                    <p>{{ COPYRIGHT_HEADER }}</p>

                    <p>
                        Permission is hereby granted, free of charge, to any person obtaining a copy
                        of this software and associated documentation files (the "Software"), to deal
                        in the Software without restriction, including without limitation the rights
                        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
                        copies of the Software, and to permit persons to whom the Software is
                        furnished to do so, subject to the following conditions:
                    </p>

                    <p>
                        The above copyright notice and this permission notice shall be included in all
                        copies or substantial portions of the Software.
                    </p>

                    <p>
                        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
                        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
                        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
                        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
                        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
                        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
                        SOFTWARE.
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    <!-- Modal content when redirecting to github -->
    <div class="modal fade" id="github-modal" tabindex="-1" aria-labelledby="github-redirect-label" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">You are about to be redirected!</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>You are going to be redirected to the source code of this project.</p>
                    <p>Which is held on Github<i class="bi bi-github"></i></p>
                    <p><strong>Yes. The entire source code.</strong> No ifs or buts.</p>
                </div>
                <div class="modal-footer">
                    <a href="https://github.com/WS-EE/Arbitaja" class="btn btn-outline-success" target="_blank" rel="noopener noreferrer">To source code</a>
                    <button type="button" class="btn btn-dark" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
    <nav class="navbar navbar-expand-md">
        <div class="container-fluid">
            <RouterLink class="navbar-brand" to="/home"><img :src="logo" alt="Arbitaja logo" width="30">Arbitaja</RouterLink>
            <button class="navbar-toggler" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasNavbar" aria-controls="offcanvasNavbar" aria-expanded="false" aria-label="Toggle navigation" >
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvasNavbar" aria-labelledby="offcanvasNavbarLabel">
                <div class="offcanvas-header">
                    <RouterLink class="navbar-brand" to="/home"><img :src="logo" alt="Arbitaja logo" width="30">Arbitaja</RouterLink>
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
                    <div class="row ms-auto flex-row-reverse">
                        <div class="col-4 col-sm-4 d-flex justify-content-center align-items-center m-1"
                            :class="[
                                (isLoggedIn) ? 'col-4 col-sm-4' : 'col-6 col-sm-6'
                            ]"
                        >
                            <a v-if="isLoggedIn" class="btn btn-outline-dark" @click.prevent="userLogout">Logout</a>
                            <RouterLink  v-if="!isLoggedIn" class="btn btn-outline-dark align-content-center" to="/login">Login</RouterLink>
                        </div>
                        <div class="col-2 col-sm-2 d-flex justify-content-center nav-item rounded align-items-center m-1">
                            <button type="button" class="btn github align-content-center nav-link rounded" data-bs-toggle="modal" data-bs-target="#LicenseBackdrop">
                                <i class="bi bi-pen"></i>
                            </button>
                        </div>
                        <div class="col-2 col-sm-2 d-flex justify-content-center nav-item rounded align-items-center m-1">
                            <button type="button" class="btn rounded nav-link align-content-center github" data-bs-toggle="modal" data-bs-target="#github-modal">
                                <i class="bi bi-github"></i>
                            </button>
                        </div>
                        <div class="col-2 col-sm-2 d-flex rounded justify-content-center align-items-center m-1"
                            v-if="isLoggedIn"
                            :class="[
                                (isLoggedIn) ? 'nav-item' : '',
                                isLinkActive('/userProfile')
                                ? 'active-item' : '',
                            ]"
                        >
                            <RouterLink class="github rounded nav-link align-content-center" to="/userProfile"><i class="bi bi-person-gear"></i></RouterLink>
                        </div>
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

.github {
    font-weight: 600;
    scale: 1.65;
}

</style>
