<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { ensureAuthRehydrated } from '@/composables/useAuthRehydrate';
import { useUserStore } from '@/stores/userStore';
import { apiClient } from '@/services/api'
import  displayAlert from '@/components/generic/displayAlert.vue';
import { UserProfileResponse } from '@/services/api';

const allUsers = ref<UserProfileResponse[]>([]);
const isLoadingUsers = ref(true)
const deleteUserId = ref<number | undefined>(undefined);
const deleteUserName = ref<string | undefined>(undefined);
const curUserId = ref<number | undefined>(undefined);

// Get if the user in list is the current user
const currentUser = (id?: number) => {
    return id === curUserId.value;
}

// Get all signup users
const getAllUsers = async() => {

    // Try getting user data
    try{
        // Try getting the Users
        const response = await apiClient.users.list()
        if (!response.success) {
            throw new Error(response.error.message || 'Unknown error');
        }
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

        // Get the current authenticated user from the auth store
        await ensureAuthRehydrated({ force: true })
        const userStore = useUserStore();
        curUserId.value = userStore.id === null ? undefined : userStore.id;
    } catch(error) {
        showAlert('Something went wrong. <br> Error:' + error, 'danger', 9000)
    } finally {
        isLoadingUsers.value = false
    }
})

// Set values if user is about to be deleted
const setUserToDelete = (id?: number, name?: string) => {
    deleteUserId.value = id;
    deleteUserName.value = name;
}

// Unset values if user cancled deletion
const unsetUserToDelete = () => {
    deleteUserId.value = undefined;
    deleteUserName.value = undefined;
}

// Delete user
const deleteUser = async(userID?: number, userName?: string) => {
    try {
        if(userID === undefined) {
            throw new Error('User ID is undefined')
        }
         await apiClient.users.delete(userID)
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

function showAlert(message: string, type: string, timeout: number = 3000){
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
                        <p class="m-0">{{ user.personal_data?.full_name }}</p>
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