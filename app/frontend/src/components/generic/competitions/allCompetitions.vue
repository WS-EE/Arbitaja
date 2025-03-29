<script setup>

import { onMounted, ref, computed } from 'vue';
import axios from 'axios';
import { RouterLink, useRoute } from 'vue-router';
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// check the active link
const isAdmin = () => {
    const route = useRoute();
    return route.path === '/admin/competitions';
}

const competitions = ref([]);
const isLoading = ref(true)
const setCompetitionName = ref();
const setCompetitionId = ref();

// Sort competitions
const sortedCompetitions = computed(() => {
    return [...competitions.value].sort((a, b) => a.name.localeCompare(b.name));
});


// Get all competitions
const getAllCompetition = async() => {

    // Try getting all competitions
    try{
        // Try getting all competitions
        const response = await axios.get('competition/all/get')
        competitions.value = response.data

    } catch(error) {
        // Throw console log error if fail
        showAlert('Couldn\'t get data for competitions. <br> Error: ' + error, 'danger', 9000)
    }
}

// Competition delete functions
const setCompetitionToDelete = (id, name) => {
    setCompetitionName.value = name
    setCompetitionId.value = id
}

const unsetCompetitionToDelete = () => {
    setCompetitionName.value = null
    setCompetitionId.value = null
}

const deleteCompetition = async() => {
    // Delete the comp based on the variables we set earlier
    try{
        // Delete competition based on ID
        await axios.delete("/competition/delete", {
        params: { id: setCompetitionId.value }, // Send `id` as a query parameter
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
      });
        showAlert('competition ' + setCompetitionName.value + ' has been deleted', 'warning')
        
        // Unset to delete after deleting the competition
        unsetCompetitionToDelete();

        // Reload page to update the list of competitions
        isLoading.value = true;
        await getAllCompetition();
        isLoading.value = false

    } catch (e) {
        showAlert('Couldn\'t delete competition. <br> Error: ' + e, 'danger')
    }
}


// OnMount
onMounted(async () => {
    try {
        // Get competitions function
        await getAllCompetition();

    } catch(error) {
        showAlert('Something went wrong. <br> Error:' + error, 'danger', 9000)
    } finally {
        isLoading.value = false
    }
})

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

    <!-- Main content-->
    <div class="container">
        <div class="row">
            <div v-if="isLoading" class="text-center">
                    <PulseLoader />
                </div>
            <div v-else class="col-sm-12 col-md-6 col-lg-4 col-xl-3 mb-3 mb-sm-0" v-for="competition in sortedCompetitions">
                <div class="card mt-2 mb-2">
                    <div class="card-body">
                        <h3 class="card-title">{{ competition.name }}</h3>
                        <p class="card-text">
                            Organizer: {{ competition.organizer_id.full_name }}
                        </p>
                        <RouterLink v-if="isAdmin()" class="btn btn-success" :to="'/admin/competition/get/' + competition.id">View</RouterLink>
                        <RouterLink v-else class="btn btn-dark" :to="'/competition/' + competition.id">View</RouterLink>
                        <RouterLink class="btn btn-outline-dark ms-2" :to="'/admin/competition/edit/' + competition.id" v-if="isAdmin()">Edit</RouterLink>
                        <button @click.prevent="setCompetitionToDelete(competition.id, competition.name)" type="button" class="btn btn-danger ms-2" data-bs-toggle="modal" data-bs-target="#deleteCompetition">Delete</button>
                    </div>
                </div>
            </div>
        </div>
        <RouterLink class="btn btn-outline-success z-0 pb-2 pt-2 sticky-bottom" :to="'/admin/competition/new'" v-if="isAdmin()">Add Competition</RouterLink> 
    </div>
    <!-- Delete competition modal -->
    <div
        v-if="isAdmin()"
        class="modal fade"
        id="deleteCompetition"
        tabindex="-1"
        role="dialog"
        aria-labelledby="deleteCompetition"
        aria-hidden="true"
    >
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalTitleId">
                        Are you sure?
                    </h5>
                    <button
                        type="button"
                        class="btn-close"
                        data-bs-dismiss="modal"
                        aria-label="Close"
                    ></button>
                </div>
                <div class="modal-body">
                    <div class="container-fluid">You are about to delete <strong>{{ setCompetitionName }}</strong> with an <strong>ID of {{ setCompetitionId }}</strong></div>
                </div>
                <div class="modal-footer">
                    <button @click.prevent="deleteCompetition()" type="button" class="btn btn-danger" data-bs-dismiss="modal">Delete</button>
                    <button @click.prevent="unsetCompetitionToDelete()" type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</template>