<script setup>
import logo from "@/assets/media/logo.svg";

// Get user state
//import { loggedInUsername } from "@/components/login/LoginPage.vue";
//const { authUser } = loggedInUsername();

</script>

<template>
    
    <div class="container p-3">
        <div
            class="alert alert-primary alert-dismissible fade show"
            role="alert"
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
    </div>
    <div class="container p-3">
    </div>
</template>