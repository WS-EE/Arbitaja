<script setup lang="ts">
import { onMounted, ref } from 'vue';

// Get user ID we are editing
import { useRoute } from 'vue-router';
const route = useRoute();
const userID = Number(route.params.id)

// import loading
const isLoading = ref(true)
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// Get the user we want to edit
import userProfile from '@/components/generic/userEdit/userProfile.vue';
import { apiClient, UserProfileResponse } from '@/services/api'

const user = ref<UserProfileResponse>({} as UserProfileResponse)

onMounted(async() =>{
    try {
        const response = await apiClient.users.details(userID)
        if(!response.success){
            throw new Error(response.error.message || 'Unknown error')
        }
        user.value = response.data
    } catch(e) {

    } finally {
        isLoading.value = false 
    }
})
</script>

<template>
    <!-- Main content-->
    <div v-if="isLoading" class="position-absolute top-50 start-50">
        <PulseLoader />
    </div>
    <userProfile v-else :user="user" />
</template>