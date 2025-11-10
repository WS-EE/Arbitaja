<script setup>
// Import stuff needed
import { ref, defineProps } from 'vue';
import axios from 'axios';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// Emit an error to parent alert
const emit = defineEmits(['showAlert'])

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

const getCompetitorDetailedResults = async(competition_id, competitor) => {
    try {
        // Set Loading value to true
        isLoadingResults.value = true

        // Get the results
        const response = await axios.get(
            'dashboard/competition/criteria/competitor', 
            { 
                params: {
                    competition_id: ''+competition_id,
                    competitor_id: ''+competitor.id
                }
            }
        )

        // Set results
        results.value = response.data.criterias
        console.log(results.value)

    } catch (error) {
        showAlert('Couldn\'t get criteria restults for the competitor: "'+ competitor.name +'". Error:' + error, 'warning')
    } finally {
        isLoadingResults.value = false
    }
}


// Alert function
function showAlert(message, type, timeout){
    emit('showAlert', message, type, timeout)
}

const openCollapse = (competitor) => {
    isShow.value = !isShow.value
    getCompetitorDetailedResults(props.competitionId, competitor)
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
    
</template>