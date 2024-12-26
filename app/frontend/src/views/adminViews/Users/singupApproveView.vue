<script setup>
import { onMounted, ref } from 'vue';
import axios from 'axios';

const singupUsers = ref([]);
const allSchools = ref('');


onMounted(async () => {
    try {
      const response = await axios.get('user/signup/get')
      singupUsers.value = response.data.signup_users
      console.log(singupUsers.value)
    } catch(error) {
        console.log(error)
    }
    // Try getting school data
    try {
        const response = await axios.get('school/all/get')
        allSchools.value = response.data
    } catch(error) {
        //displayAlert('Couldn\'t get data for all the schools. Error:' + error, 'danger', 9000)
    };
})

// Data when commiting user
const commitedUserData = ref([]);
const commitSet = ref(false)

function setCommitedUserData(user) {
    commitSet.value = true;
    commitedUserData.value = user; 
    if (commitedUserData.value.personal_data.school === null){
        commitedUserData.value.personal_data.school = { id: null, name: 'Not Set' }
    }
}

function deleteCommitedUserData(){
    commitSet.value = false;
    commitedUserData.value = [];
}

// Change school function.
function changeSchool(id, name){
    commitedUserData.value.personal_data.school = { id: id, name: name }
}

// approve user
function approveUser() {
    try {
        axios.post('user/signup/approve', commitedUserData.value)
    } catch(error){
        console.log(error)
    }
}

</script>

<template>
    <!-- Main content-->
    <div class="container">
        <!-- Tabel Header -->
        <div class="row border-5 border rounded m-2 p-1 p-md-2 p-lg-3 justify-content-center align-items-center text-center">
            <div class="col-lg-2 col-md-3 col-sm-4">
                <h5 class="m-0">Username</h5>
            </div>
            <div class="col-lg-3 col-md-2 col-sm-3">
                <p class="m-0">Full Name</p>
            </div>
            <div class="col-lg-2 col-md-4 col-sm-5 ms-auto">
            </div>
        </div>

        <!-- Tabel Content -->
        <div v-for="user in singupUsers" class="row border rounded m-2 p-1 p-md-2 p-lg-3 text-center justify-content-center align-items-center text-center">
            <div class="col-lg-2 col-md-3 col-sm-4">
                <h5 class="m-0">{{ user.username }}</h5>
            </div>
            <div class="col-lg-3 col-md-2 col-sm-3">
                <p class="m-0">{{ user.personal_data.full_name }}</p>
            </div>
            <div class="col-lg-3 col-md-4 col-sm-5 ms-auto">
                <button @click.prevent="setCommitedUserData(user)" type="button" class="me-2 btn btn-success" data-bs-toggle="modal" data-bs-target="#ApproveModal">Accept</button>
                <button class="btn btn-danger">Delete</button>
            </div>
        </div>
    </div>
    <!-- Modal for accept -->
    <div class="modal fade" id="ApproveModal" tabindex="-1" aria-labelledby="ApproveModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div v-if="commitSet" class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">Are you sure?</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <h5>ID: {{  commitedUserData.id }}</h5>
                        <p class="small">Create at: {{ commitedUserData.createdAt }}</p>
                    </div>
                    <div class="row">
                        <div class="col-4">
                            Username:
                        </div>
                        <div class="col">
                            <input type="text" class="rounded p-1 form-control" v-model="commitedUserData.username">
                        </div>
                    </div>
                    <div class="row mt-2">
                        <div class="col-4">
                            E-Mail:
                        </div>
                        <div class="col">
                            <input type="text" class="rounded p-1 form-control" v-model="commitedUserData.personal_data.email">
                        </div>
                    </div>
                    <div class="row mt-2">
                        <div class="col-4">
                            Full name:
                        </div>
                        <div class="col">
                            <input type="text" class="rounded p-1 form-control" v-model="commitedUserData.personal_data.full_name">
                        </div>
                    </div>
                    <div class="row mt-2">
                        <!-- School dropright button -->
                        <div class="btn-group">
                            <div class="col-4">
                                School:
                            </div>
                            <div class="col">
                                <button type="button" class="btn btn-outline-dark dropdown-toggle ms-2" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                    {{ commitedUserData.personal_data.school.name }}
                                </button>
                                <ul class="dropdown-menu">
                                    <!-- Dropdown menu links -->
                                    <li 
                                        v-for="allSchool in allSchools" 
                                        @click="changeSchool(allSchool.id, allSchool.name)" 
                                        class="dropdown-item"
                                    >
                                        {{ allSchool.name }}
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button @click.prevent="deleteCommitedUserData()" type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Close</button>
                    <button type="button" @click.prevent="approveUser()" class="btn btn-dark">Save changes</button>
                </div>
            </div>
        </div>
    </div>
</template>