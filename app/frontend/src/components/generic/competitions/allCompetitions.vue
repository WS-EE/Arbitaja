<script setup>
// Use props to get user profile
const props = defineProps({
    isAdmin: {
        type: Boolean,
        default: false,
    },
})

import { onMounted, ref } from 'vue';
import axios from 'axios';
import { RouterLink } from 'vue-router';

const competitions = ref([]);
const isCompetitions = ref(true)

// Get all signup users
const getAllCompetition = async() => {

    // Try getting user data
    try{
        // Try getting the Users
        const response = await axios.get('competition/all/get')
        competitions.value = response.data
        console.log(competitions.value)
    } catch(error) {
        // Throw console log error if fail
        showAlert('Couldn\'t get data for Users. <br> Error: ' + error, 'danger', 9000)
    }
}

onMounted(async () => {
    try {
        // Get Users with a function
        await getAllCompetition();

    } catch(error) {
        showAlert('Something went wrong. <br> Error:' + error, 'danger', 9000)
    } finally {
        isCompetitions.value = false
    }
})

// Alert function
const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')

import displayAlert from '@/components/generic/displayAlert.vue';

function showAlert(message, type, timeout){
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
}

</script>

<template>
    <!-- Alert when needed -->
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />

    <!-- Main content-->
    <div class="container">
        <div class="row">
            <div v-if="isLoadingUsers" class="text-center">
                    <PulseLoader />
                </div>
            <div v-else class="col-4 p-2">
                <div class="card" v-for="competition in competitions">
                    <div class="card-body">
                        <h3 class="card-title">{{ competition.name }}</h3>
                        <p class="card-text">
                            Organizer: {{ competition.organizer.full_name }}
                        </p>
                        <RouterLink class="btn btn-dark me-2" :to="'/competition/get?id=' + competition.id">View</RouterLink>
                        <RouterLink class="btn btn-outline-success" :to="'/admin/competition/edit?id=' + competition.id" v-if="isAdmin">Edit</RouterLink>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>