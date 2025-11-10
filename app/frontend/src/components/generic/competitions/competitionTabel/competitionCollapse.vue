<script setup>
// Import stuff needed
import { ref, defineProps } from 'vue';
import axios from 'axios';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

const isShow = ref(false);
const isLoadingResults = ref(true);
const results = ref([]);

const props = defineProps({
    competitionId: {
        type: Number,
        required: true,
    },
    competitor: {
        type: Object,
        required: true,
    },
    index: {
        type: Number,
        required: true,
    },
})

const getCompetitorDetailedResults = async(competition_id, competitor_id) => {
    try {
        // Set Loading value to true
        isLoadingResults.value = true

        // Get the results
        const response = await axios.get(
            'dashboard/competition/criteria/competitor', 
            { 
                params: {
                    competition_id: ''+competition_id,
                    competitor_id: ''+competitor_id
                }
            }
        )

        // Set results
        results.value = response.data.criterias
        console.log(results.value)

    } catch (error) {
        showAlert('Couldn\'t get competitors for the user tabel. Error:' + error, 'warning')
    } finally {
        isLoadingResults.value = false
    }
}


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

const openCollapse = (competitor) => {
    isShow.value = !isShow.value
    getCompetitorDetailedResults(props.competitionId, competitor.id)
}

</script>

<template>
    <th scope="row">
        {{ index + 1 }}
    </th>
    <td>
        {{ competitor.name }}
        <div v-if="isShow" class="row">
            <div v-if="isLoadingResults">
                <PulseLoader />
            </div>
                <table v-else class="table table-striped mt-3">
                    <thead>
                    <th scope="col">Criteria Name</th>
                </thead>
                <tbody>
                    <tr v-for="result in results">
                        <td>{{ result.name }}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </td>
    <td>
        {{ competitor.total_score }}
        <div v-if="isShow" class="row">
            <div v-if="isLoadingResults">
                <PulseLoader />
            </div>
                <table v-else class="table table-striped mt-3">
                    <thead>
                    <th scope="col">Points</th>
                </thead>
                <tbody>
                    <tr v-for="result in results">
                        <td>{{ result.points }}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </td>
    <td>
        <button v-if="isShow" class="btn bi bi-caret-up-fill" @click="isShow = !isShow"></button>
        <button v-if="!isShow" class="btn bi bi-caret-down-fill" @click="openCollapse(competitor)"></button>
    </td>
    <!-- Alert when needed -->
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />
</template>