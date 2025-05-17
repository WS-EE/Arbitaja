<script setup>
import userProfile from '@/components/generic/userEdit/userProfile.vue';

import { onMounted, ref } from 'vue';

// import cookie handling
import { useCookies } from '@/assets/js/useCookies';

const $cookies = useCookies();

// User paramters ref
const userParameters = ref([])
const isLoading = ref(true)
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

onMounted(async () => {
    // Try getting user data
    try {
        // Get user parameters from cookies
        userParameters.value = $cookies.get('userParameters');
    } catch(error) {
        showAlert('<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i>Error!</h4><hr><p>Couldn\'t get user data! </p class=mb-0><p>Error:' + error + '</p>', 'danger', 4500);
    } finally {
        isLoading.value = false
    }
});

// Update user cookies when profile is updated
const onUpdateUserProfile = async(userData) => {
    await $cookies.set('userParameters', userData, 0);
    userParameters.value = $cookies.get('userParameters');
}
</script>

<template>
    <div v-if="isLoading" class="position-absolute top-50 start-50">
        <PulseLoader />
    </div>
    <userProfile v-else :user="userParameters" @userUpdate="onUpdateUserProfile"/>
</template>