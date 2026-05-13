<script setup lang="ts">

import { onMounted, ref} from 'vue';
import { useRoute } from 'vue-router';
import { DateTime } from 'luxon';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import { apiClient, CompetitionResponse, CompetitorDashboardResponse } from '@/services/api'

import competitionChartResults from './competitionChartResults.vue';
import competitionResultTabel from './competitionResultTabel.vue';

const route = useRoute();

// Set default interval for auto update
const autoUpdateInterval = ref({
    interval: 30000,
    enabled: true
})

// Get current auto update interval
const getAutoIntervalNameByTimeout = (timeout: number) => {
  const displayInterval = setTimeIntervalForAutoUpdate.find(p => p.timeout === timeout)
  return displayInterval ? displayInterval.name : 'Not Set'
}

// Set new auto update interval
const changeAutoUpdateInterval = (timeout: number) => {
    autoUpdateInterval.value.interval = timeout
}

// Set Interval array
const setTimeIntervalForAutoUpdate = [
    { timeout: 5000, name: "5sec" },
    { timeout: 10000, name: "10sec" },
    { timeout: 15000, name: "15sec" },
    { timeout: 30000, name: "30sec" },
    { timeout: 60000, name: "60sec" }
]

const competition = ref<CompetitionResponse>({} as CompetitionResponse);
const results = ref<CompetitorDashboardResponse[]>([]);
const isCompetition = ref<boolean>(true)
const start_time = ref<string>('');
const end_time = ref<string>();

// Get competition data
const getCompetition = async() => { 
    // Try getting user data
    try{
        // set competition id based on link id
        const competition_id = Number(route.params.id)
        
        // Try competition data
        const response = await apiClient.competitions.details(competition_id)
        if (!response.success) {
            throw new Error(response.error.message || 'Unknown error');
        }
        competition.value = response.data

        // Format dates
        start_time.value = DateTime.fromISO(!competition.value.start_time ? new Date().toISOString() : competition.value.start_time, { zone: "utc" })
            .setZone(DateTime.local().zoneName)
            .toFormat("dd/MM/yyyy HH:mm");
        end_time.value = DateTime.fromISO(!competition.value.end_time ? new Date().toISOString() : competition.value.end_time, { zone: "utc" })
            .setZone(DateTime.local().zoneName)
            .toFormat("dd/MM/yyyy HH:mm");

    } catch(error) {
        // Throw console log error if fail
        showAlert('Couldn\'t get data for Users. <br> Error: ' + error, 'danger', 9000)
    }
}

const getResults = async() => {
    try {
        const competition_id = Number(route.params.id)
         // Try competition data
         const response = await apiClient.scoring.dashboard.history(competition_id)
         if (!response.success) {
             throw new Error(response.error.message || 'Unknown error');
         }
         results.value = response.data.competitors ?? [];
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

function showAlert(message: string, type: string, timeout: number = 3000){
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
}

</script>

<template>
    <!-- Alert when needed -->
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />

    <!-- Show loader when data not ready -->
    <div v-if="isCompetition" class="position-absolute top-50 start-50">
        <PulseLoader />
    </div>
    <!-- Main content-->
    <div v-else class="container">
        <!-- Show competition data -->
        <div class="row" >
            <h1 class="text-center border border-2 rounded mt-2">{{ competition.name }}</h1>
            
        </div> 
        <!-- Auto refresh -->
        <div class="row">
            
            
        </div>
        <!-- Show basic data about competitions -->
        <div class="row">
            <div class="col-md-2 col-sm-12">
                <div class="border rounded">
                    <div class="p-2">
                        <p class="pt-2">ID: {{ competition.id }}</p>
                        <p>Organizer: {{ competition.organizer?.full_name }}</p>
                        <p>Start Time: {{ start_time }}</p>
                        <p>End Time: {{ end_time }}</p>
                    </div>
                </div>
                <div class="pt-2">
                    <div class="form-check form-switch">
                        <input 
                            class="form-check-input" 
                            type="checkbox" 
                            role="switch" 
                            id="flexSwitchCheckDefault" 
                            v-model="autoUpdateInterval.enabled"
                        >
                        <label class="form-check-label" for="flexSwitchCheckDefault">Auto Refresh</label>
                    </div>
                    <div class="btn-group pt-2">
                        <button
                            class="btn btn-outline-dark dropdown-toggle"
                            type="button"
                            id="triggerId"
                            data-bs-toggle="dropdown"
                            aria-haspopup="true"
                            aria-expanded="false"
                        >
                            {{ getAutoIntervalNameByTimeout(autoUpdateInterval.interval) }}
                        </button>
                        <div class="dropdown-menu" aria-labelledby="triggerId">
                            <li
                                class="dropdown-item"
                                v-for="interval in setTimeIntervalForAutoUpdate"
                                @click="changeAutoUpdateInterval(interval.timeout)"
                            >
                                {{ interval.name }}
                            </li>
                        </div>
                    </div>
                    
                </div>
            </div>
            <!-- Show data about competitions -->
            <div class="col-sm-12 col-md-10">
                <h3 class="col pt-2 rounded">Results:</h3>
                <competitionChartResults 
                    :competition_id="competition.id"
                    :refreshInterval="autoUpdateInterval.interval"
                    :autoRefresh="autoUpdateInterval.enabled"
                />
            </div>
        </div>
        <div class="row">
            <competitionResultTabel 
                :competition_id="competition.id"
                :refreshInterval="autoUpdateInterval.interval"
                :autoRefresh="autoUpdateInterval.enabled"
            />
        </div>
    </div>
</template>