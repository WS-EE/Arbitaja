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
    startAutoRefresh();
})

function startAutoRefresh() {
    stopAutoRefresh() // Clear any existing one

    intervalHandle = setInterval(() => {
        console.log('Update interval: '+ props.refreshInterval)
        getResultsByCompetitionId(props.competitionId)
        showAlert('Refreshed results on chart.')
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
     // Clean up chart
    if (chartInstance) {
        chartInstance.destroy()
        chartInstance = null
    }
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


// Import required moduls
import { onMounted, onUnmounted, ref, watch, nextTick } from 'vue'
import axios from 'axios';
import { Chart, registerables } from 'chart.js'
import 'chartjs-adapter-date-fns'

// Import pulse loader
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// Set empty variable
const isLoadingResults = ref(true)
const results = ref([])

// Get results
const getResultsByCompetitionId = async (id) => {
    try {
        // Set Loading value to true
        isLoadingResults.value = true

        // Get the results
        const response = await axios.get(
            'dashboard/competition/history',
            {
                params: {
                    competition_id: '' + id
                }
            }
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
    nextTick(() => createChart())
})


// Create chart (vibe code, I have no idea what is going on here)
// VIBE CODE BLOCK START
Chart.register(...registerables)
const chartCanvas = ref(null)
let chartInstance = null
function getRandomColor() {
    return `hsl(${Math.random() * 360}, 100%, 50%)`
}
function createChart() {
  if (!chartCanvas.value) return

  const ctx = chartCanvas.value.getContext('2d')

  // Destroy old chart if it exists
  if (chartInstance) {
    chartInstance.destroy()
    chartInstance = null
  }

  const datasets = results.value.map((competitor) => {
    const data = competitor.results
      .sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp))
      .map((result) => ({
        x: new Date(result.timestamp),
        y: result.point_amount
      }))

    return {
      label: competitor.name,
      data,
      borderColor: getRandomColor(),
      fill: false,
      tension: 0
    }
  })

  chartInstance = new Chart(ctx, {
    type: 'line',
    data: { datasets },
    options: {
      responsive: true,
      scales: {
        x: {
          type: 'time',
          time: { unit: 'minute' },
          title: { display: true, text: 'Timestamp' }
        },
        y: {
          title: { display: true, text: 'Points' }
        }
      }
    }
  })
}

watch(results, () => {
  // Only create chart when canvas is present
  nextTick(() => {
    createChart()
  })
})
// END OF VIBE CODE BLOCK
</script>

<template>
    <!-- Display alert -->
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />

    <!-- Pulseloader when changed -->
    <div v-if="isLoadingResults" class="position-absolute top-50 start-50">
        <PulseLoader />
    </div>
    <!-- Show canvas -->
    <canvas v-show="!isLoadingResults" ref="chartCanvas"></canvas>
</template>