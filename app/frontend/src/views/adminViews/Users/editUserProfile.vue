<script setup>
import { onMounted, ref } from 'vue';

// Get user ID we are editing
import { useRoute } from 'vue-router';
const route = useRoute();
const userID = route.params.id

// import loading
const isLoading = ref(true)
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// Get the user we want to edit
import userProfile from '@/components/generic/userEdit/userProfile.vue';
import axios from 'axios';
const user = ref([])

onMounted(async() =>{
    try {
        const response = await axios('/user/profile/get', { params: { id: userID }})
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
    <userProfile v-else :user="user" :isAdmin=true />
</template>