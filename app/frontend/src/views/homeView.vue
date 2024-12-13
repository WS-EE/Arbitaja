<script setup>
import { onMounted } from 'vue';
import { ref } from 'vue';
import axios from 'axios';
const isLoggedIn = ref(false)
const userProfile = ref('')

onMounted(async () => {
    try {
        const response = await axios.get('http://localhost/api/v1/profile');
        if (response.data !== 'Successful login'){
            userProfile.value = response.data
            isLoggedIn.value = true
        }
    } catch(error) {
        console.log('Error:', error)
    };
});

</script>

<template>
    <div class="container p-3">
        <div
            class="alert alert-primary alert-dismissible fade show"
            role="alert"
            v-if="!isLoggedIn"
        >
            <button
                type="button"
                class="btn-close"
                data-bs-dismiss="alert"
                aria-label="Close"
            ></button>
            <strong>Try logging in!</strong> More content visable when logged in.
        </div>
        <h1>Welcome to Arbitaja</h1>
        <p v-if="isLoggedIn">{{ userProfile }}</p>
    </div>
    <div class="container p-3">
    </div>
</template>