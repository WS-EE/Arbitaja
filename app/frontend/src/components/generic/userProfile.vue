<script setup>
// import cookie handling
import { useCookies } from '@/assets/js/useCookies';
const $cookies = useCookies(); 

// import ref
import { onMounted, ref } from 'vue';

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



onMounted(async () => {
    // Try getting school data
    try {
        const response = await axios.get('schools')
        allSchools.value = response.data
    } catch(error) {
        displayAlert('Couldn\'t get data for all the schools. Error:' + error, 'danger', 9000)
    };

    // Try getting user data
    try {
        // Get user parameters from cookies
        const userParameters = $cookies.get('userParameters');

        // Map out cookie parameters
        userid.value = userParameters.id
        fullName.value = userParameters.personal_data.full_name
        email.value = userParameters.personal_data.email
        username.value = userParameters.username
        roles.value = userParameters.roles
        school.value = userParameters.school
    } catch(error) {
        displayAlert('<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i>Error!</h4><hr><p>Couldn\'t get user data! </p class=mb-0><p>Error:' + error + '</p>', 'danger', 4500);
    }
});

// Alert function
const showAlert = ref(false)
const alertMessage = ref('')
const alertType = ref('')

function displayAlert(message, type, timeout){
    // Set default type to primary(info)
    if (timeout === undefined){
        timeout = 3000;
    }
    if (type === undefined){
        type = 'primary';
    }

    // Set the alert type
    alertType.value = 'alert-' + type

    // Tell user that changes were discarded
    showAlert.value = true

    alertMessage.value = message
    // Fade out alert after 3000ms
    if (timeout !== 0) {
        setTimeout(() => {
            showAlert.value = false
        }, timeout);
    }
}

// Save and discard functions
const saveProfile = (async () =>{
    try {
        // Update data with PUT request
        const response = await axios.put('profile', {
            id: userid.value,
            username: username.value,
            personal_data: {
                full_name: fullName.value,
                email: email.value
            },
            school: {
                id: school.value.id
            }
        })

        // On positive response load new data.
        if (response.status === 200){

            // Set user data to cookie returned by response
            await $cookies.set('userParameters', response.data, 0);

            // Get user parameters from cookies
            const newUserParameters = await $cookies.get('userParameters');

            // Map out cookie parameters
            fullName.value = newUserParameters.personal_data.full_name
            email.value = newUserParameters.personal_data.email
            username.value = newUserParameters.username
            roles.value = newUserParameters.roles
            school.value = newUserParameters.school

            // Display a success message to user
            displayAlert('<h4 class=alert-heading>Success!</h4><hr><p class=mb-0>Changes have been saved.</p>', 'success')
        }
    } catch (error) {
        displayAlert('<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i><strong>Failed to save changes!</strong></h4><hr><p class=mb-0>Error: ' + error + '</p><p class=mb-0>For more information check console log.</p>', 'danger', 6000)
        console.log(error)
    }
});
function discardChanges(){
    try {
        // Get current values in cookies
        const prevParameters = $cookies.get('userParameters');

        // Set the old values
        fullName.value = prevParameters.personal_data.full_name
        email.value = prevParameters.personal_data.email
        username.value = prevParameters.username
        roles.value = prevParameters.roles
        school.value = prevParameters.school

        // Tell user that changes were discarded
        displayAlert('<i class="me-2 bi bi-trash"></i><strong>Changes were discarded</strong>', 'warning', 3000)
    } catch (error) {
        displayAlert('<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i>Error!</h4><hr><p class=mb-0>Error: ' + error + '</p>', 'danger')
    }
}

// Change school function.
function changeSchool(id, name){
    school.value = { id: id, name: name }
}

</script>

<template>
    <div class="container p-3 container-bottom">
        <!-- Alert when needed pressed -->
        <Transition name="alert">
            <div
                v-if="showAlert"
                class="alert"
                :class="alertType"
                role="alert"
            >
                <span v-html="alertMessage"></span>
            </div>
        </Transition>
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
                    <li v-for="role in roles" class="list-group-item disabled">{{ role.authority }}</li>
                </ul>          
            </div>
        </div>
    </div>
    <!-- Action Buttons -->
    <div class="container d-flex justify-content-end align-items-end pt-3 pb-3">
        <button @click.prevent="saveProfile" class="btn btn-success me-3">Save<i class="ms-1 bi bi-floppy"></i></button>
        <button @click="discardChanges" class="btn btn-outline-danger me-3">Discard<i class="ms-1 bi bi-trash"></i></button>
    </div>
</template>

<style scoped>
.alert-enter-active,
.alert-leave-active {
  transition: opacity 0.5s ease;
}

.alert-enter-from,
.alert-leave-to {
  opacity: 0;
}
</style>