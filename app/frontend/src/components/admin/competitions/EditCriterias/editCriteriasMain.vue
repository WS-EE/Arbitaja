<script setup>
// Import modules
import axios from 'axios';
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import router from '@/router';

// Import components
import addEditCriteria from '@/components/admin/competitions/EditCriterias/addEditCriteria.vue'
import removeCriteria from '@/components/admin/competitions/EditCriterias/removeCriteria.vue';
import CriteriaTabel from './CriteriaTabel.vue';

// Import displayalert
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

// Main Content
const emptyCriteria = {
            name: '',
            description: '',
            totalPoints: '',
        }
const isLoading = ref(true)
const competition = ref([]);
const criterias = ref([]);
const route = useRoute();
const isLoadingCriterias = ref(true)

// Get id of the competition
const competition_id = route.params.id

// function for getting the competition
const getCompetitionById = async(id) => {
    try {
        // set loading to be true
        isLoading.value = true;

        // If prop schools is not defined try to get them ourselves
        const response = await axios.get('competition/get?id=' + id);
        competition.value = response.data
        
    } catch(error) {
        // Throw console log error if fail
        showAlert('Something went wrong while loading. Error:' + error, 'danger')
    } finally {
        // Show content when done loading
        isLoading.value = false
    }
}


// function for getting criterias based on competition
const getCriteriasByCompetition = async(competitionId) => {
    try {
        // Set loading true
        isLoadingCriterias.value = true

        // Get criterias based on competition id
        const response = await axios.get(
            'scoring/criteria/by/competition',
            { 
                params: { 
                    competition_id: competitionId 
                } 
            }
        )

        // Set criterias based on the response
        criterias.value = response.data

        //TEMP REMOVE
        console.log(response)
        
    } catch (error) {
        showAlert('Something went wrong while loading criterias.', 'danger')
    } finally {
        isLoadingCriterias.value = false
    }
}

onMounted(async() => {
    getCompetitionById(competition_id);
    getCriteriasByCompetition(competition_id);
})

const onAddCriteria = () => {
    getCriteriasByCompetition(competition_id);
}

const onTableChanged = () => {
    getCriteriasByCompetition(competition_id);
}

</script>

<template>
    <div class="containter">
        <!-- Alert when needed -->
        <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout"/>
        <div v-if="isLoading" class="position-absolute top-50 start-50">
            <PulseLoader />
        </div>
        <div v-else class="container">
            <!-- Header of the html -->
            <h1>Competition: {{ competition.name }}</h1>
            <p>Organizer: <b>{{ competition.organizer_id.full_name }}</b></p>
            
            <!-- Buttons for action of adding -->
            <div class="row">
                <addEditCriteria
                    buttonName="Add Criteria"propCriteria
                    modalId="addCriteria"
                    :isAdd="true"
                    :competitionId="competition_id"
                    addButtonDivClass="btn btn-success col-lg-2 col-md-3 col-sm-5 ms-2 mt-1" 
                    @addEditCriteria="onAddCriteria()"
                />
            </div>
            <div v-if="isLoadingCriterias" class="position-absolute top-50 start-50">
                <PulseLoader />
            </div>
            <!-- Criteria show -->
            <div v-else>
                <CriteriaTabel :criterias="criterias" :competitionId="competition_id" :addActions="true" @tableChanged="onTableChanged()"/>
            </div>
        </div>
    </div>
    <!-- Action Buttons -->
    <div class="container d-flex justify-content-end align-items-end pt-3 pb-3">
        <button @click="router.back()" class="btn btn-outline-dark me-3">Go Back</button>
    </div>
</template>