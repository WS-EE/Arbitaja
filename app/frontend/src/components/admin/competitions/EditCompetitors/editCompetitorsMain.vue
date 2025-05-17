<script setup>
// Import modules
import axios from 'axios';
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
import router from '@/router';

// Import components
import addCompetitor from '@/components/admin/competitions/EditCompetitors/addEditCompetitor.vue';
import competitorTable from './competitorTable.vue';
import addExistingCompetitor from './addExistingCompetitor.vue';

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
const isLoading = ref(true)
const competition = ref([]);
const competitors = ref([]);
const route = useRoute();
const isLoadingcompetitors = ref(true)

// Get id of the competition
const competition_id = route.params.id

// function for getting the competition
const getCompetition = async() => {
    try {
        // set loading to be true
        isLoading.value = true;

        // If prop schools is not defined try to get them ourselves
        const response = await axios.get('competition/get?id=' + competition_id);
        competition.value = response.data
        
    } catch(error) {
        // Throw console log error if fail
        showAlert('Something went wrong while loading. Error:' + error, 'danger')
    } finally {
        // Show content when done loading
        isLoading.value = false
    }
}

const getCompetitors = async() => {
    try {
        // Set competitor loading to true when starting function
        isLoadingcompetitors.value = true

        // Get competition id to get competitors
        const competition_id = route.params.id

        // Get and set competitors
        const response = await axios.get('competitor/get/all/in/competition?id=' + competition_id);
        competitors.value = response.data

    } catch(error) {
        // Throw console log error if fail
        showAlert('No competitors found!', 'warning')
    } finally {
        // Show content when done loading
        isLoadingcompetitors.value = false
    }
}

onMounted(async() => {
    getCompetition();
    getCompetitors();
})

const onAddCompetitor = () => {
    getCompetitors();
}

const onTableChanged = () => {
    getCompetitors();
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
                <addCompetitor
                    buttonName="Add Competitor"
                    modalId="addCompetitor"
                    :isLinked=false
                    :apiEndpoint="'/competitor/add?isLinked=false&competition_id=' + competition_id"
                    addButtonDivClass="btn btn-success col-lg-2 col-md-3 col-sm-5 ms-2 me-2 mt-1" 
                    @addItem="onAddCompetitor()"
                />
                <addCompetitor
                    buttonName="Add Linked Competitor"
                    modalId="addLinkedCompetitor"
                    :isLinked=true
                    :existingCompetitors="competitors"
                    :apiEndpoint="'/competitor/add?isLinked=true&competition_id=' + competition_id"
                    addButtonDivClass="btn btn-success col-lg-2 col-md-3 col-sm-5 ms-2 me-2 mt-1" 
                    @addItem="onAddCompetitor()"
                />
                <addExistingCompetitor
                    addButtonDivClass="btn btn-success col-lg-2 col-md-3 col-sm-5 ms-2 me-2 mt-1" 
                    :competitionId="competition_id"
                    :existingCompetitors="competitors"
                    @competitorAdd="onAddCompetitor()"
                />
            </div>
            <div v-if="isLoadingcompetitors" class="position-absolute top-50 start-50">
                <PulseLoader />
            </div>
            <!-- competitors show -->
            <div v-else>
                <competitorTable 
                    :competitors="competitors" 
                    :addActions="true" 
                    @tableChanged="onTableChanged()" 
                />
            </div>
        </div>
    </div>
    <!-- Action Buttons -->
    <div class="container d-flex justify-content-end align-items-end pt-3 pb-3">
        <button @click="router.back()" class="btn btn-outline-dark me-3">Go Back</button>
    </div>
</template>