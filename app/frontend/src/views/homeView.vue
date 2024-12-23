<script setup>
// Get if user is logged in.
import { onMounted,ref } from 'vue';

import { useCookies } from '@/assets/js/useCookies';
const $cookies = useCookies(); 

const isLoggedIn = ref(true)
const userParameters = ref('')
const displayUsername = ref('')

// Get cookies for user logged in status
onMounted(async () => {
    // get cookies
    try {
        isLoggedIn.value = await $cookies.get('isLoggedIn');
        userParameters.value = await $cookies.get('userParameters');
        displayUsername.value = userParameters.value.personal_data.full_name
    } catch(err) {
        console.log('User is unauthenticated')
        isLoggedIn.value = false
    }
})

</script>

<template>
    <!-- Alert message when conecting to site -->
    <div class="container p-3">
        <!-- When user is logged out -->
        <div v-if="!isLoggedIn">
            <h1>Welcome to Arbitaja</h1>
        </div>
        <!-- When user is logged in -->
        <div v-if="isLoggedIn">
            <h1>Welcome {{ displayUsername }}!</h1>
            <p>You have logged in!</p>
        </div>    
    </div>
    <div class="container p-3">
    </div>
</template>

<style scoped>
.v-enter-active,
.v-leave-active {
  transition: opacity 0.5s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}
</style>