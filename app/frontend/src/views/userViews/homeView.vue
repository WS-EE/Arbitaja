<script setup lang="ts">
// Get if user is logged in.
import { useUserStore} from "@/stores/userStore";

const store = useUserStore();

// Import dashboard for competitions
import allcompetition from '@/components/generic/competitions/allCompetitions.vue';

</script>

<template>
    <!-- Alert message when conecting to site -->
    <div class="container p-3">
        <!-- When user is logged out -->
        <div v-if="!store.isAuthenticated">
            <h1>Welcome to Arbitaja</h1>
        </div>
        <!-- When user is logged in -->
        <div v-if="store.isAuthenticated">
            <h1>Welcome Arbitaja!</h1>
            <p>Hello {{ store.username }}!</p>
            <p>You have logged in!</p>
        </div>
    </div>
    <allcompetition />
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