<script setup>

import { onMounted, ref } from 'vue';
import axios from 'axios';
import { RouterLink, useRoute } from 'vue-router';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// check the active link
const isAdmin = () => {
    const route = useRoute();
    return route.path === '/admin/competitions';
}

const competitions = ref([]);
const isCompetitions = ref(true)

// Get all competitions
const getAllCompetition = async() => {

    // Try getting all competitions
    try{
        // Try getting all competitions
        const response = await axios.get('competition/all/get')
        competitions.value = response.data
        console.log(competitions.value)
    } catch(error) {
        // Throw console log error if fail
        showAlert('Couldn\'t get data for competitions. <br> Error: ' + error, 'danger', 9000)
    }
}

onMounted(async () => {
    try {
        // Get competitions function
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
            <div v-if="isCompetitions" class="text-center">
                    <PulseLoader />
                </div>
            <div v-else class="col-4 p-2">
                <div class="card" v-for="competition in competitions">
                    <div class="card-body">
                        <h3 class="card-title">{{ competition.name }}</h3>
                        <p class="card-text">
                            Organizer: {{ competition.organizer.full_name }}
                        </p>
                        <RouterLink v-if="isAdmin()" class="btn btn-dark me-2" :to="'/admin/competition/get/' + competition.id">View</RouterLink>
                        <RouterLink v-else class="btn btn-dark me-2" :to="'/competition/' + competition.id">View</RouterLink>
                        <RouterLink class="btn btn-outline-success" :to="'/admin/competition/edit/' + competition.id" v-if="isAdmin()">Edit</RouterLink>
                    </div>
                </div>
            </div>
        </div>
        <RouterLink class="btn btn-outline-success z-0 pb-2 pt-2 sticky-bottom" :to="'/admin/competition/new'" v-if="isAdmin()">Add Competition</RouterLink>
    </div>
</template>