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

// Import apiClient
import { apiClient, CompetitorResponse } from '@/services/api'

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
        required: true
    },
    existingCompetitors: {
        type: Array as () => Array<CompetitorResponse>,
        default: () => []
    }
})

// Set empty parameters
const allCompetitors = ref<CompetitorResponse[]>([]);
const isLoadingCompetirors = ref(true)
const competitorToAdd = ref<CompetitorResponse>({} as CompetitorResponse);

// Set competitor to add empty
function setCompetitorToAddEmpty() {
    competitorToAdd.value = { 
        id: 0,
        alias: '',
        personal_data: {
            id: 0,
            full_name: 'Not set',
            school: {
                id: 0,
                name: ''
            }
        }

    }
}


// Function to get all the competitors
const getAllCompetitors = async () => {
    try {
        // Set loading true when getting competitors
        isLoadingCompetirors.value = true

        // Get competitors
        const response = await apiClient.competitors.list({ size: 500 })
        if (!response.success) {
            throw new Error(response.error.message || 'Unknown error');
        }
        allCompetitors.value = response.data.content

    } catch (error) {
        showAlert('Couldn\'t get all the competitors for adding existing competitors. Error: ' + error, 'danger')
    } finally {
        // Set loading false when competitors loaded
        isLoadingCompetirors.value = false
    }
}

// Create emit to send pack when competitor added
const emit = defineEmits(['competitorAdd'])

// Function to add competitor to competition
const addCompetitorToCompetition = async (competitionId: number, competitorId: number) => {
    try {

         // Add exstiting competitor to competition
        await apiClient.competitions.addCompetitor(competitionId, competitorId)

        // alert success
        showAlert('Success on adding competitor to competition.', 'success')

        // Emit that competitor was added
        emit('competitorAdd')

    } catch (error) {
        // Show error alert on fail
        showAlert('Error adding competitor to competiton. Error' + error, 'danger')
    } finally {
        setCompetitorToAddEmpty()
    }
}

// Function to set competitor to add to competition
const setCompetitorToAdd = (competitor: CompetitorResponse) => {
    competitorToAdd.value = competitor
}

// Filter out competitor that already exist
const unLinkedCompetitors = computed(() => {
    const existingCompetitorIds = new Set(
        props.existingCompetitors
            .map(competitor => competitor?.id)
            .filter(id => id != null)
    )
    return allCompetitors.value.filter(competitor => {
        const competitorId = competitor?.id
        return competitorId != null && !existingCompetitorIds.has(competitorId)
    })
})

// filter users based on name
const searchCompetitor = ref('');
const filteredCompetitors = computed(() => {
  const query = searchCompetitor.value.toLowerCase()
  return unLinkedCompetitors.value.filter(user =>
    (user?.personal_data?.full_name ?? '').toLowerCase().includes(query)
  )
})

onMounted(async() => {
    setCompetitorToAddEmpty()
    await getAllCompetitors()
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
        data-bs-target="#addExistingCompetitor"
    >
        Add Existing Competitor
    </button>

    <!-- Modal -->
    <div class="modal fade" id="addExistingCompetitor" tabindex="-1" role="dialog" aria-labelledby="modalTitleId"
        aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div v-if="isLoadingCompetirors" class="position-absolute top-50 start-50">
                <PulseLoader />
            </div>
            <div v-else class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalTitleId">
                        Add Existing Competitor
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
                            <strong>{{ competitorToAdd.id }}</strong>
                        </div>
                    </div>
                    <div class="row mt-2 mb-2">
                        <div class="col-4">
                            Alias:
                        </div>
                        <div class="col">{{ competitorToAdd.alias }}</div>
                    </div>
                    <div class="row mt-2 mb-2">
                        <div class="col-4">
                            School:
                        </div>
                        <div class="col">{{ competitorToAdd.personal_data?.school?.name }}</div>
                    </div>
                    <div class="row mt-2 mb-2">
                        <div class="col-4">
                            Full Name:
                        </div>
                        <div class="col">
                            <!-- Default dropright button -->
                            <div class="btn-group">
                                <button type="button" class="btn btn-outline-dark dropdown-toggle"
                                    data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                    {{ competitorToAdd.personal_data?.full_name }}
                                </button>

                                <ul class="dropdown-menu">
                                    <li class="px-2 py-1">
                                        <div class="input-group rounded">
                                            <input type="search" class="form-control rounded me-1 ms-1"
                                                placeholder="Search" aria-label="Search" aria-describedby="search-addon"
                                                v-model="searchCompetitor"
                                            />
                                        </div>
                                    </li>
                                    <!-- Dropdown menu links -->
                                    <li v-for="competitor in filteredCompetitors" :key="competitor.id" @click="setCompetitorToAdd(competitor)"
                                        class="dropdown-item">
                                        ID: {{ competitor.id }}|Full Name: {{ competitor.personal_data?.full_name }}
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button
                            @click.prevent="addCompetitorToCompetition(props.competition_id, competitorToAdd.id)"
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