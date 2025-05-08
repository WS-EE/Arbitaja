<script setup>
// define props
const props = defineProps({
    competitionId: {
        default: ''
    },
    insertedResults: {
        type: Object
    },
    refreshInterval: {
        type: Number,
        default: 30000
    },
    autoRefresh: {
        type: Boolean,
        default: false
    }
})
// Import vue modules
import { onMounted, onUnmounted, ref, watch, computed } from 'vue'

// Import axios
import axios from 'axios';

// Import pulseloader
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// Set result ref variable
const results = ref([]);
const isLoadingResults = ref(true)

// Get results
const getCompetitionResults = async(competitionId) => {
    try {
        // Set Loading value to true
        isLoadingResults.value = true

        // Get the results
        const response = await axios.get(
            'dashboard/competition/history', 
            { 
                params: {
                    competition_id: ''+competitionId 
                }
            }
        )

        // Set results
        results.value = response.data.competitors

    } catch (error) {
        showAlert('Couldn\'t get competitors for the user tabel. Error:' + error, 'warning')
    } finally {
        isLoadingResults.value = false
    }
};

// onMount get results
onMounted(async() => {
    try {
        // If results are not set by props, get them ourselves
        if (props.insertedResults === undefined) {
            // Get results via API
            await getCompetitionResults(props.competitionId)
        } else {
            // Set inserted results for the tabel
            results.value = props.insertedResults
        }

        // Start autoupdate
        if (props.autoRefresh) {
            startAutoRefresh()
        }
    } catch (error) {
        showAlert("Could't get results for the tabel", 'warning')
    } finally {
        isLoadingResults.value = false
    }
});

// Auto-refresh handling
let intervalHandle = null

watch(() => props.autoRefresh, (newVal) => {
    if (newVal) {
        startAutoRefresh()
    } else {
        stopAutoRefresh()
    }
})

watch(() => props.refreshInterval, () => {
    startAutoRefresh();
})

function startAutoRefresh() {
    stopAutoRefresh() // Clear any existing one

    intervalHandle = setInterval(() => {
        getCompetitionResults(props.competitionId)
    }, props.refreshInterval)
}

function stopAutoRefresh() {
    if (intervalHandle !== null) {
        clearInterval(intervalHandle)
        intervalHandle = null
    }
}

onUnmounted(() => {
    stopAutoRefresh() // Clean up interval on component destroy
})

// Sort competitors
const sortedComptitors = computed(() => {
    return [...results.value].sort((a, b) =>  b.total_score - a.total_score);
});

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
    <!-- show Loading when tabel not ready -->
    <div v-if="isLoadingResults" class="position-absolute top-50 start-50">
        <PulseLoader />
    </div>
    <!-- Table -->
    <table v-else class="table table-striped mt-3">
        <thead>
            <th scope="col">#</th>
            <th scope="col">Name</th>
            <th scope="col">Total Points</th>
        </thead>
        <tbody>
            <tr v-for="(competitor, index) in sortedComptitors">
                <th scope="row">{{ index + 1 }}</th>
                <td>{{ competitor.name }}</td>
                <td>{{ ''+competitor.total_score }}</td>
            </tr>
        </tbody>
    </table>
    
</template>