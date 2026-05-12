<script setup>
// Define props
const props = defineProps({
    competitionId: {
        type: Number,
        required: true
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

// Auto refresh BLOCK START
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
    if (props.autoRefresh) {
        startAutoRefresh();
    }
})

function startAutoRefresh() {
    stopAutoRefresh() // Clear any existing one

    intervalHandle = setInterval(() => {
        getResultsByCompetitionId(props.competitionId)
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
//
// Auto refresh BLOCK END
//

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


// Import required modules
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import axios from 'axios'
import { Line } from 'vue-chartjs'
import { Chart as ChartJS, registerables } from 'chart.js'
import 'chartjs-adapter-date-fns'
import { endpoints } from '@/services/endpoints'

// Import pulse loader
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// Set empty variable
const isLoadingResults = ref(true)
const results = ref([])
const chartPalette = [
    '#0d6efd',
    '#198754',
    '#fd7e14',
    '#dc3545',
    '#6f42c1',
    '#20c997',
    '#0dcaf0',
]

const chartData = computed(() => ({
    datasets: results.value.map((competitor, index) => ({
        label: competitor.name,
        data: [...(competitor.results ?? [])]
            .sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp))
            .map((result) => ({
                x: new Date(result.timestamp),
                y: result.point_amount,
            })),
        borderColor: chartPalette[index % chartPalette.length],
        backgroundColor: 'transparent',
        pointRadius: 2,
        tension: 0.15,
        parsing: false,
    })),
}))

const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    animation: false,
    interaction: {
        mode: 'index',
        intersect: false,
    },
    plugins: {
        legend: {
            position: 'bottom',
        },
    },
    scales: {
        x: {
            type: 'time',
            time: {
                unit: 'minute',
            },
            title: {
                display: true,
                text: 'Timestamp',
            },
        },
        y: {
            beginAtZero: true,
            title: {
                display: true,
                text: 'Points',
            },
        },
    },
}

ChartJS.register(...registerables)

// Get results
const getResultsByCompetitionId = async (id) => {
    try {
        // Set Loading value to true
        isLoadingResults.value = true

        // Get the results
        const response = await axios.get(
            endpoints.scoring.history.dashboard(id)
        )

        // Set results
        results.value = response.data.competitors

    } catch (error) {
        showAlert('Couldn\'t get competitors for the chart. Error:' + error, 'warning')
    } finally {
        isLoadingResults.value = false
    }
}

// Get results and create chart on mount
onMounted(async () => {
    await getResultsByCompetitionId(props.competitionId)
    if (props.autoRefresh) {
        startAutoRefresh()
    }
})
</script>

<template>
    <!-- Display alert -->
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />

    <!-- Pulseloader when changed -->
    <div v-if="isLoadingResults" class="position-absolute top-50 start-50">
        <PulseLoader />
    </div>
    <!-- Show chart -->
    <div v-show="!isLoadingResults" class="position-relative" style="min-height: 420px;">
        <Line :data="chartData" :options="chartOptions" />
    </div>
</template>