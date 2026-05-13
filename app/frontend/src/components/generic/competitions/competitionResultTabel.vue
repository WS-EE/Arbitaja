<script setup lang="ts">
import { computed, onMounted, ref, toRef } from 'vue'
import PulseLoader from 'vue-spinner/src/PulseLoader.vue'
import displayAlert from '@/components/generic/displayAlert.vue'
import { apiClient, CompetitorDashboardResponse } from '@/services/api'
import { useAutoRefresh } from '@/composables/useAutoRefresh'

// --- Props ---
const props = defineProps({
    competition_id: {
        type: Number,
        required: true,
        default: 0,
    },
    insertedResults: {
        type: Array as () => CompetitorDashboardResponse[] | undefined,
        default: undefined,
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

function showAlert(message: string, type: string, timeout: number = 3000) {
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
}

// --- Data ---
const results = ref<CompetitorDashboardResponse[]>([])
const isLoadingResults = ref(true)

const sortedCompetitors = computed(() =>
    [...(results.value ?? [])].sort((a, b) => (b.total_score ?? 0) - (a.total_score ?? 0))
)

// --- Fetch ---
async function fetchResults() {
    try {
        isLoadingResults.value = true
        const response = await apiClient.scoring.dashboard.history(props.competition_id)
        if (!response.success) {
            throw new Error(response.error.message || 'Unknown error')
        }
        results.value = response.data.competitors ?? []
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
        if (props.insertedResults && props.insertedResults.length > 0) {
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
