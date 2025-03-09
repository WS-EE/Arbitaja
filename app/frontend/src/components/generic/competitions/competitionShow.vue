<script setup>

import { onMounted, ref } from 'vue';
import axios from 'axios';
import { useRoute } from 'vue-router';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

import competitionChartResults from './competitionChartResults.vue';

const route = useRoute();


const competition = ref([]);
const isCompetition = ref(true)

// Get competition data
const getCompetition = async() => { 
    // Try getting user data
    try{
        // set competition id based on link id
        const competition_id = route.params.id
        
        // Try competition data
        const response = await axios.get('competition/get?id=' + competition_id)
        competition.value = response.data
    } catch(error) {
        // Throw console log error if fail
        showAlert('Couldn\'t get data for Users. <br> Error: ' + error, 'danger', 9000)
    }
}

onMounted(async () => {
    try {
        // Get competitions
        await getCompetition();

    } catch(error) {
        showAlert('Something went wrong. <br> Error:' + error, 'danger', 9000)
    } finally {
        isCompetition.value = false
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
            <!-- Show loader when data not ready -->
            <div v-if="isCompetition" class="text-center">
                <PulseLoader />
            </div>
            <!-- Show competition data -->
            <div v-else class="row" >
                <h1 class="text-center border border-2 rounded mt-2">{{ competition.name }}</h1>
                <!-- Show basic data about competitions -->
                <div class="col-md-2 col-sm-12">
                    <div class="border rounded">
                        <div class="p-2">
                            <p class="pt-2">ID: {{ competition.id }}</p>
                            <p>Organizer: {{ competition.organizer.full_name }}</p>
                            <p>Start Time: {{ competition.start_time }}</p>
                            <p>End Time: {{ competition.end_time }}</p>
                        </div>
                    </div>
                </div>
                <!-- Show data about competitions -->
                <div class="col-sm-12 col-md-10">
                    <h3 class="pt-2 rounded">Results:</h3>
                    <competitionChartResults />
                </div>
            </div>
        </div>
    </div>
</template>