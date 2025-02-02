<script setup>
import { onMounted, ref } from 'vue';
import axios from 'axios';
import { RouterLink } from 'vue-router';

const allUsers = ref([]);
const isLoadingUsers = ref(true)
const deleteUserId = ref();
const deleteUserName = ref('');
const curUserId = ref();

// Get if the user in list is the current user
const currentUser = (id) => {
    // If user is current user return true to disable delete
    if (id === curUserId.value) {
        return true;
    } else {
    // else show delete
        return false;
    }
}

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

        // Get the current user
        const curUser = await axios.get('user/profile/get')
        curUserId.value = curUser.data.id
    } catch(error) {
        showAlert('Something went wrong. <br> Error:' + error, 'danger', 9000)
    } finally {
        isLoadingUsers.value = false
    }
})

// Set values if user is about to be deleted
const setUserToDelete = (id, name) => {
    deleteUserId.value = id;
    deleteUserName.value = name;
}

// Unset values if user cancled deletion
const unsetUserToDelete = () => {
    deleteUserId.value = '';
    deleteUserName.value = ''
}

// Delete user
const deleteUser = async(userID, userName) => {
    try {
        await axios.delete('/user/profile/delete', { params: {id: userID} })
        showAlert('User <strong>'+userName+'</strong> has been succesfully deleted.', 'success')
    } catch(error){
        // Throw error if fail
        showAlert('Couldn\'t delete user of id <strong>'+userID+'</strong>. <br>Failed to delete user <strong>'+userName+'</strong> <br> Error: ' + error, 'danger', 9000)
    } finally {
        isLoadingUsers.value = true
        await getAllUsers();
        isLoadingUsers.value = false
    }
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
                    <div class="col-lg-4 col-md-3 col-sm-3 text-lg-start text-sm-center">
                        <p class="m-0">User roles</p>
                    </div>
                    <div class="col-lg-4 col-md-4 col-sm-5 ms-lg-auto text-center text-lg-end pt-2 pt-md-0"></div>
                </div>
                <div v-for="user in allUsers" class="row border-2 border rounded m-2 p-1 p-md-2 p-lg-3 justify-content-center align-items-center text-center">
                    <div class="col-lg-2 col-md-3 col-sm-4">
                        <h5 class="m-0">{{ user.username }}</h5>
                    </div>
                    <div class="col-lg-2 col-md-3 col-sm-4">
                        <p class="m-0">{{ user.personal_data.full_name }}</p>
                    </div>
                    <div class="col-lg-4 col-md-3 col-sm-3">
                        <!-- Horizontal under breakpoint -->
                        <ul class="list-group list-group-horizontal justify-content-md-start justify-content-center">
                            <li v-for="role in user.roles" class="list-group-item disabled">{{ role.name }}</li>
                        </ul>
                    </div>
                    <div class="col-lg-4 col-md-4 col-sm-5 ms-lg-auto text-center text-lg-end pt-2 pt-md-0">
                        <RouterLink :to="'/admin/users/user_edit/' + user.id" class="btn btn-outline-success me-2">edit</RouterLink>
                        <button  
                            @click.prevent="setUserToDelete(user.id, user.username)" 
                            type="button" data-bs-toggle="modal" data-bs-target="#deleteUser" 
                            class="btn btn-danger"
                            :class="[ 
                                currentUser(user.id) 
                                ? 'disabled' : '',
                            ]"
                        >
                            Delete
                        </button>
                    </div>
                </div>
            </div>
            <div class="col-lg-2 d-none d-lg-block border-5 border rounded m-2 p-1 p-md-2 p-lg-3 justify-content-center align-items-center text-center">
                <h5>All Roles</h5>
            </div>
        </div>
    </div>
    <!-- Delete school modal -->
    <div
        class="modal fade"
        id="deleteUser"
        tabindex="-1"
        role="dialog"
        aria-labelledby="deleteUser"
        aria-hidden="true"
    >
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalTitleId">
                        Are you sure?
                    </h5>
                    <button
                        type="button"
                        class="btn-close"
                        data-bs-dismiss="modal"
                        aria-label="Close"
                    ></button>
                </div>
                <div class="modal-body">
                    <div class="container-fluid">You are about to delete <strong>{{ deleteUserName }}</strong> with an <strong>ID of {{ deleteUserId }}</strong></div>
                </div>
                <div class="modal-footer">
                    <button @click.prevent="deleteUser(deleteUserId, deleteUserName)" type="button" class="btn btn-danger" data-bs-dismiss="modal">Delete</button>
                    <button @click.prevent="unsetUserToDelete()" type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</template>