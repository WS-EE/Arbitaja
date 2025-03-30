<script setup>

import { onMounted, ref } from 'vue';
import axios from 'axios';
import { useRoute } from 'vue-router';
import { DateTime } from 'luxon';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

import competitionChartResults from './competitionChartResults.vue';

const route = useRoute();


const competition = ref([]);
const results = ref([]);
const isCompetition = ref(true)
const start_time = ref();
const end_time = ref();

// Get competition data
const getCompetition = async() => { 
    // Try getting user data
    try{
        // set competition id based on link id
        const competition_id = route.params.id
        
        // Try competition data
        const response = await axios.get('competition/get?id=' + competition_id)
        competition.value = response.data

        // Format dates
        start_time.value = DateTime.fromISO(competition.value.start_time, { zone: "utc" })
            .setZone(DateTime.local().zoneName)
            .toFormat("dd/MM/yyyy HH:mm");
        end_time.value = DateTime.fromISO(competition.value.end_time, { zone: "utc" })
            .setZone(DateTime.local().zoneName)
            .toFormat("dd/MM/yyyy HH:mm");

    } catch(error) {
        // Throw console log error if fail
        showAlert('Couldn\'t get data for Users. <br> Error: ' + error, 'danger', 9000)
    }
}

const getResults = async() => {
    try {
        const competition_id = route.params.id

        const response = await axios.get('dashboard/competition/history?competition_id=' + competition_id)
        results.value = response.data.competitors
    } catch(error) {
        // Throw console log error if fail
        showAlert('Couldn\'t get data for Users. <br> Error: ' + error, 'danger', 9000)
    }
}

onMounted(async () => {
    try {
        // Get competitions
        await getCompetition();
        await getResults()

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
            <div v-if="isCompetition" class="position-absolute top-50 start-50">
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
                            <p>Organizer: {{ competition.organizer_id.full_name }}</p>
                            <p>Start Time: {{ start_time }}</p>
                            <p>End Time: {{ end_time }}</p>
                        </div>
                    </div>
                </div>
                <!-- Show data about competitions -->
                <div class="col-sm-12 col-md-10">
                    <h3 class="pt-2 rounded">Results:</h3>
                    <competitionChartResults :results="results"/>
                </div>
            </div>
        </div>
    </div>
</template>