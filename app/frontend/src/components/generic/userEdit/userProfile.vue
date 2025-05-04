<script setup>
// Use props to get user profile
const props = defineProps({
    user: {
        type: Object,
        required: true
    },
    isAdmin: {
        type: Boolean,
        default: false,
    },
})

const emit = defineEmits(['userUpdate'])

// import ref and onmount
import { onMounted, ref, computed } from 'vue';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// Get school list
import axios from 'axios';
const allSchools = ref(''); 

// Set user parameters to empty
const userid = ref('')
const fullName = ref('')
const email = ref('')
const username = ref('')
const roles = ref('')
const school = ref('')
const successAlert = ref('')
const isLoading = ref(true)

const getSchools = async() => {
    try {
        const response = await axios.get('school/all/get')
        allSchools.value = response.data
    } catch(error) {
        showAlert('Couldn\'t get data for all the schools. Error:' + error, 'danger', 9000)
    }
}


onMounted(async () => {
    // Try getting school data
    await getSchools();

    // Try getting user data
    try {
        // Get user parameters from cookies
        const userParameters = props.user;

        // Map out cookie parameters
        userid.value = userParameters.id
        fullName.value = userParameters.personal_data.full_name
        email.value = userParameters.personal_data.email
        username.value = userParameters.username
        roles.value = userParameters.roles
        school.value = userParameters.personal_data.school
    } catch(error) {
        showAlert('<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i>Error!</h4><hr><p>Couldn\'t get user data! </p class=mb-0><p>Error:' + error + '</p>', 'danger', 4500);
    } finally {
        isLoading.value = false
    }
});

// Filter schools based on search
const searchSchools = ref('');

// filter schools based on name
const filteredSchools = computed(() => {
  const query = searchSchools.value.toLowerCase()
  return allSchools.value.filter(school =>
    school.name.toLowerCase().includes(query)
  )
})

// Save and discard functions
const saveProfile = (async () =>{
    try {
        // Update data with PUT request
        const response = await axios.put('user/profile/edit', {
            id: userid.value,
            username: username.value,
            personal_data: {
                full_name: fullName.value,
                email: email.value,
                school: {
                  id: school.value.id
                }
            },
        })

        // On positive response load new data.
        if (response.status === 200){

            // Set new parameters from reponse.data
            const newUserParameters = response.data

            // Map out parameters
            fullName.value = newUserParameters.personal_data.full_name
            email.value = newUserParameters.personal_data.email
            username.value = newUserParameters.username
            roles.value = newUserParameters.roles
            school.value = newUserParameters.personal_data.school

            // Display a success message to user
            showAlert('<h4 class=alert-heading>Success!</h4><hr><p class=mb-0>Changes have been saved.</p>', 'success')

            // emit pack the changes to the profile
            emit('userUpdate', newUserParameters)
        }
    } catch (error) {
        showAlert('<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i><strong>Failed to save changes!</strong></h4><hr><p class=mb-0>Error: ' + error + '</p><p class=mb-0>For more information check console log.</p>', 'danger', 6000)
    }
});
function discardChanges(){
    try {
        // Get current values in cookies
        const prevParameters = props.user;

        // Set the old values
        fullName.value = prevParameters.personal_data.full_name
        email.value = prevParameters.personal_data.email
        username.value = prevParameters.username
        roles.value = prevParameters.roles
        school.value = prevParameters.personal_data.school

        // Tell user that changes were discarded
        showAlert('<i class="me-2 bi bi-trash"></i><strong>Changes were discarded</strong>', 'warning', 3000)
    } catch (error) {
        showAlert('<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i>Error!</h4><hr><p class=mb-0>Error: ' + error + '</p>', 'danger')
    }
}

// Change school function.
function changeSchool(id, name){
    school.value = { id: id, name: name }
}

// Alert function
const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')

import displayAlert from '@/components/generic/displayAlert.vue';
import router from '@/router';

function showAlert(message, type, timeout){
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
}

// Import password reset
import changePassword from './changePassword.vue';

</script>

<template>
    <div v-if="isLoading" class="position-absolute top-50 start-50">
        <PulseLoader />
    </div>
    <div v-else>
        <!-- Alert when needed -->
        <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />
        <div class="container p-3 container-bottom">
            <h1 class="">User Profile</h1>
            <hr>

            <!-- Personal data start block -->
            <h5>Personal Data</h5>
            <div class="row justify-content-start pt-3">
                <div class="col">
                    <label for="fullName" class="form-label">Full Name</label>
                </div>
                <div class="col">
                    <input
                        type="text"
                        class="form-control"
                        name=""
                        id="fullName"
                        v-model="fullName"
                    />
                </div>
            </div>
            <div class="row justify-content-start pt-3">
                <div class="col">
                    <label for="email" class="form-label">Email</label>
                </div>
                <div class="col">
                    <input
                        type="email"
                        class="form-control"
                        v-model="email"
                        id="email"
                    />
                </div>
            </div>
            <!-- School logic -->
            <div class="row pt-3">
                <div class="col">
                    <label for="">School</label>
                </div>
                <div class="col">
                    <!-- Default dropright button -->
                    <div class="btn-group">
                        <button type="button" class="btn btn-outline-dark dropdown-toggle" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            {{ school.name }}
                        </button>
                        <ul class="dropdown-menu">
                            <!-- Search bar for schools -->
                            <div class="input-group rounded">
                                <input type="search" class="form-control rounded ms-1 me-1" 
                                    placeholder="Search" aria-label="Search" aria-describedby="search-addon"
                                    v-model="searchSchools"
                                />
                            </div>
                            <!-- Dropdown menu links -->
                            <li 
                                v-for="school in filteredSchools" 
                                @click="changeSchool(school.id, school.name)" 
                                class="dropdown-item"
                            >
                                {{ school.name }}
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            <!-- Password Reset -->
            <div class="row pt-3">
                <div class="col">
                    <p>Password</p>
                </div>
                <div class="col">
                    <changePassword :isAdmin="props.isAdmin" :userId="userid"/>
                </div>
            </div>

            <!-- System data start block -->
            <h5 class="pt-3">System Data</h5>
            <div class="row pt-3">
                <div class="col">
                    <label for="username" class="form-label">Logon Username</label>
                </div>
                <div class="col">
                    <input
                        type="text"
                        class="form-control"
                        v-model="username"
                        id="username"
                    />
                </div>
            </div>

            <!-- List roles given to the user -->
            <div class="row pt-3">
                <div class="col">
                    <label for="">System Roles</label>
                </div>
                <div class="col">
                    <!-- Horizontal under breakpoint -->
                    <ul class="list-group list-group-horizontal">
                        <li v-for="role in roles" class="list-group-item disabled">{{ role.name }}</li>
                    </ul>          
                </div>
            </div>
        </div>
        <!-- Action Buttons -->
        <div class="container d-flex justify-content-end align-items-end pt-3 pb-3">
            <button @click.prevent="saveProfile" class="btn btn-success me-3">Save<i class="ms-1 bi bi-floppy"></i></button>
            <button @click="discardChanges" class="btn btn-outline-danger me-3">Discard<i class="ms-1 bi bi-trash"></i></button>
            <button v-if="isAdmin" @click="router.back()" class="btn btn-outline-dark me-3">Go Back</button>
        </div>
    </div>
</template>
