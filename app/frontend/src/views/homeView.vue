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
        displayUsername.value = userParameters.value.username
    } catch(err) {
        console.log('User is unauthenticated')
        isLoggedIn.value = false
    }
})

</script>

<template>
    <div class="container p-3">
        <div v-if="!isLoggedIn">
            <div
                class="alert alert-primary alert-dismissible fade show"
                role="alert">
                <button
                    type="button"
                    class="btn-close"
                    data-bs-dismiss="alert"
                    aria-label="Close"
                ></button>
                <strong>You are not authenticated!</strong> More content visable when logged in.
            </div>
            <h1>Welcome to Arbitaja</h1>
        </div>
        <div v-if="isLoggedIn">
            <div
            class="alert alert-success alert-dismissible fade show"
            role="alert">
            <button
                type="button"
                class="btn-close"
                data-bs-dismiss="alert"
                aria-label="Close"
            ></button>
            <strong>Success.</strong> Your are now authenticated and ready to use arbitaja.
        </div>
            
            <h1>Welcome {{ displayUsername }}!</h1>
            <p>You have logged in!</p>
        </div>
    </div>
    <div class="container p-3">
    </div>
</template>