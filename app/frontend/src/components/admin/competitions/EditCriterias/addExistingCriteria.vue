<script setup lang="ts">
// Alert function
const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')

import displayAlert from '@/components/generic/displayAlert.vue';

function showAlert(message: string, type: string, timeout: number = 3000) {
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
}

// Import pulseloader
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// Import axios
import { apiClient, ScoringCriterionResponse } from '@/services/api'

// Import vue modules
import { ref, onMounted, computed } from 'vue';

// Get props
const props = defineProps({
    addButtonDivClass: {
        type: String,
        default: 'btn btn-success'
    },
    competition_id: {
        type: Number,
        default: 0
    },
    existingCriterias: {
        type: Array as () => Array<ScoringCriterionResponse>,
        default: () => []
    }
})

// Set empty parameters
const allCriterias = ref<ScoringCriterionResponse[]>([]);
const isLoadingCompetirors = ref(true)
const CriteriaToAdd = ref<ScoringCriterionResponse>({} as ScoringCriterionResponse);

// Set Criteria to add empty
function setCriteriaToAddEmpty() {
    CriteriaToAdd.value = { 
        id: 0,
        total_points: undefined,
        name: undefined,
        description: undefined,
    }
}


// Function to get all the Criterias
const getAllCriterias = async () => {
    try {
        // Set loading true when getting Criterias
        isLoadingCompetirors.value = true

        // Get Criterias
        const response = await apiClient.scoring.criteria.list()
        if (!response.success) {
            throw new Error(response.error.message || 'Unknown error');
        }
        allCriterias.value = response.data

    } catch (error) {
        showAlert('Couldn\'t get all the Criterias for adding existing Criterias. Error: ' + error, 'danger')
    } finally {
        // Set loading false when Criterias loaded
        isLoadingCompetirors.value = false
    }
}

// Create emit to send pack when Criteria added
const emit = defineEmits(['CriteriaAdd'])

// Function to add Criteria to competition
const addCriteriaToCompetition = async (competitionId?: number, CriteriaId?: number) => {
    try {

        if(!competitionId || !CriteriaId) {
             throw new Error('No competition id or criteria id provided for adding criteria to competition')
        }
         // Add exstiting Criteria to competition
        const response = await apiClient.scoring.criteria.linkToCompetition(CriteriaId, competitionId)
        if (!response.success) {
            throw new Error(response.error.message || 'Unknown error');
        }

        // alert success
        showAlert('Success on adding Criteria to competition.', 'success')

        // Emit that Criteria was added
        emit('CriteriaAdd')

    } catch (error) {
        // Show error alert on fail
        showAlert('Error adding Criteria to competiton. Error' + error, 'danger')
    } finally {
        setCriteriaToAddEmpty()
    }
}

// Function to set Criteria to add to competition
const setCriteriaToAdd = (Criteria: ScoringCriterionResponse) => {
    CriteriaToAdd.value = Criteria
}

// Filter out Criteria that already exist
const unLinkedCriterias = computed(() => {
    const existingcriteriaIds = new Set(props.existingCriterias.map(criteria => criteria.id))
    return allCriterias.value.filter(
        criteria => !existingcriteriaIds.has(criteria.id)
    )
})

// filter criterias based on name
const searchCriteria = ref('');
const filteredCriterias = computed(() => {
  const query = searchCriteria.value.toLowerCase()
  return unLinkedCriterias.value.filter(criteria =>
    !criteria.name ? false : criteria.name.toLowerCase().includes(query)
  )
})

onMounted(async() => {
    setCriteriaToAddEmpty()
    await getAllCriterias()
})

</script>

<template>
    <!-- Alert when needed -->
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />

    <!-- Button trigger modal -->
    <button 
        type="button" 
        :class="addButtonDivClass" 
        data-bs-toggle="modal"
        data-bs-target="#addExistingCriteria"
    >
        Add Existing Criteria
    </button>

    <!-- Modal -->
    <div class="modal fade" id="addExistingCriteria" tabindex="-1" role="dialog" aria-labelledby="modalTitleId"
        aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div v-if="isLoadingCompetirors" class="position-absolute top-50 start-50">
                <PulseLoader />
            </div>
            <div v-else class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalTitleId">
                        Add Existing Criteria
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <!-- Normal block -->
                    <div class="row mt-2 mb-2">
                        <div class="col-4">
                            ID:
                        </div>
                        <div class="col">
                            <strong>{{ CriteriaToAdd.id }}</strong>
                        </div>
                    </div>
                    <div class="row mt-2 mb-2">
                        <div class="col-4">
                            Total Points:
                        </div>
                        <div class="col">
                            <strong>{{ CriteriaToAdd.total_points }}</strong>
                        </div>
                    </div>
                    <div class="row mt-2 mb-2">
                        <div class="col-4">
                            Name:
                        </div>
                        <div class="col">
                            <!-- Default dropright button -->
                            <div class="btn-group">
                                <button type="button" class="btn btn-outline-dark dropdown-toggle"
                                    data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                    {{ CriteriaToAdd.name }}
                                </button>

                                <ul class="dropdown-menu">
                                    <li class="px-2 py-1">
                                        <div class="input-group rounded">
                                            <input type="search" class="form-control rounded me-1 ms-1"
                                                placeholder="Search" aria-label="Search" aria-describedby="search-addon"
                                                v-model="searchCriteria"
                                            />
                                        </div>
                                    </li>
                                    <!-- Dropdown menu links -->
                                    <li v-for="Criteria in filteredCriterias" @click="setCriteriaToAdd(Criteria)"
                                        class="dropdown-item">
                                        ID: {{ Criteria.id }}|Name: {{ Criteria.name }}
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button
                            @click.prevent="addCriteriaToCompetition(props.competition_id, CriteriaToAdd.id)"
                            type="button" class="btn btn-success" data-bs-dismiss="modal"
                        >
                            Add
                        </button>
                        <button type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>