<script setup lang="ts">

// import ref and onmount
import { onMounted, ref, computed } from 'vue';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import { useUserStore } from "@/stores/userStore";
import {apiClient, UserProfileResponse, SchoolResponse, UpdateUserRequest, RoleResponse} from '@/services/api'

const store = useUserStore();

const userEditRequest = ref<UpdateUserRequest>({} as UpdateUserRequest)

// Use props to get user profile
const props = defineProps({
    user: {
        type: Object as () => UserProfileResponse,
        required: true
    }
})

const emit = defineEmits(['userUpdate'])

const user = ref<UserProfileResponse>(props.user)


// Get school list
const allSchools = ref<SchoolResponse[]>([]);
const allRoles = ref<RoleResponse[]>([]);
const selectedRoleIds = ref<number[]>([]);

// Set user parameters to empty
const isAdmin = computed(() => store.hasPrivilege('EDIT_USERS'))
const isLoading = ref(true)

const getSchools = async() => {
    try {
        const response = await apiClient.schools.list({ size: 500 });
        if(!response.success){
          throw new Error(response.error.message || 'Unknown error')
        }
        allSchools.value = response.data.content;
    } catch(error) {
        showAlert('Couldn\'t get data for all the schools. Error:' + error, 'danger', 9000)
    }
}


const getRoles = async () => {
    try {
        const response = await apiClient.roles.list({ size: 500 });
        if (!response.success) throw new Error(response.error.message || 'Unknown error');
        allRoles.value = response.data.content;
        selectedRoleIds.value = (props.user.roles ?? []).map(r => r.id).filter((id): id is number => id !== undefined);
    } catch (error) {
        showAlert('Couldn\'t load roles. Error: ' + error, 'danger', 9000);
    }
};

const saveRoles = async () => {
    try {
        const response = await apiClient.users.overwriteUserRoles(props.user.id, selectedRoleIds.value);
        if (!response.success) throw new Error(response.error.message || 'Unknown error');
        showAlert('Roles updated successfully.', 'success');
    } catch (error) {
        showAlert('Couldn\'t update roles. Error: ' + error, 'danger', 9000);
    }
};

onMounted(async () => {
  // Try getting school data
  await getSchools();
  if (isAdmin.value) await getRoles();
  isLoading.value = false;
});

// Filter schools based on search
const searchSchools = ref('');

// filter schools based on name
const filteredSchools = computed(() => {
  const query = searchSchools.value.toLowerCase()
  return allSchools.value.filter(school =>
    school.name?.toLowerCase().includes(query)
  )
})

// Save and discard functions
const saveProfile = (async () =>{
    try {
      const response = await apiClient.users.update(props.user?.id, getEditRequestFromProfile());

      if(!response.success){
        throw new Error(response.error.message || 'Unknown error')
      }

      user.value = response.data

      // Display a success message to user
      showAlert('<h4 class=alert-heading>Success!</h4><hr><p class=mb-0>Changes have been saved.</p>', 'success')

    } catch (error) {
        showAlert('<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i><strong>Failed to save changes!</strong></h4><hr><p class=mb-0>Error: ' + error + '</p><p class=mb-0>For more information check console log.</p>', 'danger', 6000)
    }
});
function discardChanges(){
    try {


        // Tell user that changes were discarded
        showAlert('<i class="me-2 bi bi-trash"></i><strong>Changes were discarded</strong>', 'warning', 3000)
    } catch (error) {
        showAlert('<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i>Error!</h4><hr><p class=mb-0>Error: ' + error + '</p>', 'danger')
    }
}

// Change school function.
function changeSchool(id: number, name?: string){
  if(!user.value.personal_data) {
    throw new Error('User personal data is undefined')
  }
    user.value.personal_data.school = { id: id, name: name }
}

// Alert function
const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')
const alertTrigger = ref(0)

import displayAlert from '@/components/generic/displayAlert.vue';
import router from '@/router';

function showAlert(message: string, type: string, timeout: number = 3000){
  alertMessage.value = message
  alertType.value = type
  alertTimeout.value = timeout
  alertTrigger.value++
}

function getEditRequestFromProfile() {
  userEditRequest.value = {
    full_name: user.value.personal_data?.full_name || '',
    email: user.value.personal_data?.email || '',
    school_id: user.value.personal_data?.school?.id || undefined,
    username: user.value.username,
  }
  return userEditRequest.value
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
        <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" :trigger="alertTrigger" />
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
                        v-model="user.personal_data!.full_name"
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
                        v-model="user.personal_data!.email"
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
                            {{ user.personal_data!.school?.name || '' }}
                        </button>
                        <ul class="dropdown-menu">
                            <!-- Search bar for schools -->
                            <li class="px-2 py-1">
                                <div class="input-group rounded">
                                    <input type="search" class="form-control rounded ms-1 me-1"
                                        placeholder="Search" aria-label="Search" aria-describedby="search-addon"
                                        v-model="searchSchools"
                                    />
                                </div>
                            </li>
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
                    <changePassword :isAdmin="isAdmin" :userId="user.id"/>
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
                        v-model="user.username"
                        id="username"
                    />
                </div>
            </div>

            <!-- List roles given to the user -->
            <div class="row pt-3">
                <div class="col">
                    <label>System Roles</label>
                </div>
                <div class="col">
                    <div v-if="isAdmin">
                        <div class="d-flex flex-wrap gap-2 mb-2">
                            <div
                                v-for="role in allRoles"
                                :key="role.id"
                                class="form-check"
                            >
                                <input
                                    class="form-check-input"
                                    type="checkbox"
                                    :id="'role-' + role.id"
                                    :value="role.id"
                                    v-model="selectedRoleIds"
                                />
                                <label class="form-check-label" :for="'role-' + role.id">{{ role.name }}</label>
                            </div>
                        </div>
                        <button @click.prevent="saveRoles" class="btn btn-sm btn-outline-primary">Save Roles</button>
                    </div>
                    <ul v-else class="list-group list-group-horizontal">
                        <li v-for="role in user.roles" class="list-group-item disabled">{{ role.name }}</li>
                    </ul>
                </div>
            </div>
        </div>
        <!-- Action Buttons -->
        <div class="container d-flex justify-content-end align-items-end pt-3 pb-4 mb-3">
            <button @click.prevent="saveProfile" class="btn btn-success me-3">Save<i class="ms-1 bi bi-floppy"></i></button>
            <button @click="discardChanges" class="btn btn-outline-danger me-3">Discard<i class="ms-1 bi bi-trash"></i></button>
            <button v-if="isAdmin" @click="router.back()" class="btn btn-outline-dark me-3">Go Back</button>
        </div>
    </div>
</template>
