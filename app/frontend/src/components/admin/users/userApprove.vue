<script setup>
import { onMounted, ref } from 'vue';
import axios from 'axios';

const singupUsers = ref([]);
const schools = ref('');
const isLoadingUsers = ref(true)

onMounted(async () => {
    try {
        // Try getting the Users
        const response = await axios.get('user/signup/get')
        singupUsers.value = response.data.signup_users
        // Try getting school data
        const response2 = await axios.get('school/all/get')
        schools.value = response2.data
    } catch(error) {
        // Throw console log error if fail
        console.log(error)
    } finally {
        isLoadingUsers.value = false
    }
    
    try {
        
    } catch(error) {
        //displayAlert('Couldn\'t get data for all the schools. Error:' + error, 'danger', 9000)
    };
})


import approveModal from './userApproveModal.vue';
import addSchool from './school/addSchool.vue';
import allSchools from './school/allSchools.vue';
</script>

<template>
    <!-- Main content-->
    <div class="container">
        <div class="row">
            <div class="col-lg-9 col-sm-12">
                <!-- Tabel Header -->
                <div class="row border-5 border rounded m-2 p-1 p-md-2 p-lg-3 justify-content-center align-items-center text-center">
                    <div class="col-lg-2 col-md-3 col-sm-4">
                        <h5 class="m-0">Username</h5>
                    </div>
                    <div class="col-lg-3 col-md-2 col-sm-3">
                        <p class="m-0">Full Name</p>
                    </div>
                    <div class="col-lg-2 col-md-4 col-sm-5 ms-auto text-center">
                            <addSchool :modalId="'lg-Modal'" />
                    </div>
                </div>
                <p v-if="isLoadingUsers">Loading...</p>
                <approveModal v-else :users="singupUsers" :schools="schools" />
            </div>
            <div class="col-lg-2 d-none d-lg-block border-5 border rounded m-2 p-1 p-md-2 p-lg-3 justify-content-center align-items-center text-center">
                <div class="row">
                    <div class="col">
                        <h4>Schools</h4>
                    </div>
                </div>
                <div class="row">
                    <p v-if="isLoadingUsers">Loading...</p>
                    <allSchools v-else :schools="schools" :addDelete="false" />
                </div>
            </div>
        </div>
    </div>
</template>