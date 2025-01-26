<script setup>
import { onMounted, ref } from 'vue';
import axios from 'axios';
import { RouterLink } from 'vue-router';

const allUsers = ref([]);
const isLoadingUsers = ref(true)

// Get all signup users
const getAllUsers = async() => {

    // Try getting user data
    try{
        // Try getting the Users
        const response = await axios.get('user/profile/all')
        allUsers.value = response.data
    } catch(error) {
        // Throw console log error if fail
        showAlert('Couldn\'t get data for Users. <br> Error: ' + error, 'danger', 9000)
    }
}

onMounted(async () => {
    try {
        // Get Users with a function
        await getAllUsers();
    } catch(error) {
        showAlert('Something went wrong. <br> Error:' + error, 'danger', 9000)
    } finally {
        isLoadingUsers.value = false
    }
})

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
                    <div class="col-lg-2 col-md-3 col-sm-4">
                        <p class="m-0">Full Name</p>
                    </div>
                    <div class="col-lg-8 col-md-6 col-sm-4 text-md-start text-sm-center">
                        <p class="m-0">User roles</p>
                    </div>
                </div>
                <div v-for="user in allUsers" class="row border-2 border rounded m-2 p-1 p-md-2 p-lg-3 justify-content-center align-items-center text-center">
                    <div class="col-lg-2 col-md-3 col-sm-4">
                        <h5 class="m-0">{{ user.username }}</h5>
                    </div>
                    <div class="col-lg-2 col-md-3 col-sm-4">
                        <p class="m-0">{{ user.personal_data.full_name }}</p>
                    </div>
                    <div class="col-lg-6 col-md-3 col-sm-3">
                        <!-- Horizontal under breakpoint -->
                        <ul class="list-group list-group-horizontal justify-content-md-start justify-content-center">
                            <li v-for="role in user.roles" class="list-group-item disabled">{{ role.name }}</li>
                        </ul>
                    </div>
                    <div class="col">
                        <RouterLink :to="'/admin/users/user_edit/' + user.id" class="btn btn-outline-success">edit</RouterLink>
                    </div>
                </div>
            </div>
            <div class="col-lg-2 d-none d-lg-block border-5 border rounded m-2 p-1 p-md-2 p-lg-3 justify-content-center align-items-center text-center">
                <h5>All Roles</h5>
            </div>
        </div>
    </div>
</template>