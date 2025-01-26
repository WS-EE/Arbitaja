<script setup>
import { onMounted, ref } from 'vue';
import axios from 'axios';

// Import components
import approveModal from './userApproveModal.vue';
import addSchool from '../school/addSchool.vue';
import allSchools from '../school/allSchools.vue';

const singupUsers = ref([]);
const schools = ref('');
const isLoadingUsers = ref(true)

// Get all signup users
const getSignupUsers = async() => {
    try{
        // Try getting the Users
        const response = await axios.get('user/signup/get')
        singupUsers.value = response.data.signup_users
    } catch(error) {
        // Throw console log error if fail
        showAlert('Couldn\'t get data for signup Users. <br> Error: ' + error, 'danger', 9000)
    }
}

// Get all schools
const getSchools = async() => {
    try {
        // Try getting school data
        const response = await axios.get('school/all/get')
        schools.value = response.data
    } catch(error) {
        showAlert('Couldn\'t get data for all the schools. <br> Error: ' + error, 'danger', 9000)
    };
}

onMounted(async () => {
    try {
        // 
        await getSignupUsers();
        await getSchools();
    } catch(error) {
        showAlert('Something went wrong. <br> Error:' + error, 'danger', 9000)
    } finally {
        isLoadingUsers.value = false
    }
})

// limit school count
const limitSchools = ref(7);
const showMoreSchools = ref(true)
function moreSchools(){
    limitSchools.value = 0
    showMoreSchools.value = false
}
function lessSchools(){
    limitSchools.value = 7
    showMoreSchools.value = true
}

// Alert function
const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')

import displayAlert from '@/components/generic/displayAlert.vue';

function showAlert(message, type, timeout){
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
}

// when signup user is approve get all the signup users again.
const onApproveSignupUser = async() => {
    await getSignupUsers();
}

// When school is added
const onAddSchool = async() => {
    await getSchools();
}
</script>

<template>
    <!-- Alert when needed -->
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />
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
                    <div class="col-lg-3 col-md-4 col-sm-5 ms-auto text-center text-lg-end">                        
                        <addSchool modalId="lg-Modal" @addSchool="onAddSchool" />
                    </div>
                </div>
                <p v-if="isLoadingUsers">Loading...</p>
                <approveModal v-else :users="singupUsers" :schools="schools" @approveSignupUser="onApproveSignupUser" />
            </div>
            <div class="col-lg-2 d-none d-lg-block border-5 border rounded m-2 p-1 p-md-2 p-lg-3 justify-content-center align-items-center text-center">
                <div class="row">
                    <div class="col">
                        <h4>Schools</h4>
                    </div>
                </div>
                <div class="row">
                    <p v-if="isLoadingUsers">Loading...</p>
                    <allSchools v-else :schools="schools" :addDelete="false" :limitItems="limitSchools" />
                </div>
                <div class="row text-center">
                    <div class="col">
                        <p v-if="showMoreSchools" class="btn btn-dark mt-2" @click.prevent="moreSchools()">More schools<i class="ms-1 bi bi-arrow-down"></i></p>
                        <p v-else class="btn btn-dark mt-2" @click.prevent="lessSchools()">Less schools<i class="ms-1 bi bi-arrow-up"></i></p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>