<script setup lang="ts">
import userProfile from '@/components/generic/userEdit/userProfile.vue';

import { onMounted, ref } from 'vue';
import { UserProfileResponse } from "@/services/api";
import { useUserStore } from '@/stores/userStore';
const store = useUserStore()



const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')

// User paramters ref
const userParameters = ref<UserProfileResponse>({} as UserProfileResponse)
const isLoading = ref(true)
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';


onMounted(async () => {
    // Try getting user data
    try {
        // Get user parameters from cookies
        userParameters.value = store.getUserProfile();
    } catch(error) {
        showAlert('<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i>Error!</h4><hr><p>Couldn\'t get user data! </p class=mb-0><p>Error:' + error + '</p>', 'danger', 4500);
    } finally {
        isLoading.value = false
    }
});

function showAlert(message: string, type: string, timeout: number = 3000){
  alertMessage.value = message
  alertType.value = type
  alertTimeout.value = timeout
}

// Update user cookies when profile is updated
const onUpdateUserProfile = async(userData: UserProfileResponse) => {
    store.setUserAuthorization(userData)
}
</script>

<template>
    <div v-if="isLoading" class="position-absolute top-50 start-50">
        <PulseLoader />
    </div>
    <userProfile v-else :user="userParameters" @userUpdate="onUpdateUserProfile"/>
</template>