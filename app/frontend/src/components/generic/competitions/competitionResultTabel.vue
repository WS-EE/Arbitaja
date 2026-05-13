<script setup>
import { computed, onMounted, ref, toRef } from 'vue'
import PulseLoader from 'vue-spinner/src/PulseLoader.vue'
import displayAlert from '@/components/generic/displayAlert.vue'
import { apiClient } from '@/services/api'
import { useAutoRefresh } from '@/composables/useAutoRefresh'

// --- Props ---
const props = defineProps({
    competition_id: {
        default: '',
    },
    insertedResults: {
        type: Object,
    },
    refreshInterval: {
        type: Number,
        default: 30000,
    },
    autoRefresh: {
        type: Boolean,
        default: false,
    },
})

// --- Alert ---
const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')

function showAlert(message, type, timeout = 3000) {
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
}

// --- Data ---
const results = ref([])
const isLoadingResults = ref(true)

const sortedCompetitors = computed(() =>
    [...results.value].sort((a, b) => b.total_score - a.total_score)
)

// --- Fetch ---
async function fetchResults() {
    try {
        isLoadingResults.value = true
        const response = await apiClient.scoring.dashboard.history(props.competition_id)
        results.value = response.competitors
    } catch (error) {
        showAlert(`Couldn't get competitors for the table. Error: ${error}`, 'warning')
    } finally {
        isLoadingResults.value = false
    }
}

// --- Auto-refresh ---
useAutoRefresh(fetchResults, toRef(props, 'refreshInterval'), toRef(props, 'autoRefresh'))

// --- Lifecycle ---
onMounted(async () => {
    try {
        if (props.insertedResults !== undefined) {
            results.value = props.insertedResults
        } else {
            await fetchResults()
        }
    } catch (error) {
        showAlert("Couldn't get results for the table", 'warning')
    } finally {
        isLoadingResults.value = false
    }
})
</script>

<template>
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />

    <div v-if="isLoadingResults" class="position-absolute top-50 start-50">
        <PulseLoader />
    </div>

    <table v-else class="table table-striped mt-3">
        <thead>
            <tr>
                <th scope="col">#</th>
                <th scope="col">Name</th>
                <th scope="col">Total Points</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="(competitor, index) in sortedCompetitors" :key="competitor.name">
                <th scope="row">{{ index + 1 }}</th>
                <td>{{ competitor.name }}</td>
                <td>{{ competitor.total_score }}</td>
            </tr>
        </tbody>
    </table>
</template>
