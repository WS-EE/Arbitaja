<script setup>
// Alert function
const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')

import displayAlert from '@/components/generic/displayAlert.vue';

function showAlert(message, type, timeout) {
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
}

// Import pulseloader
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// Import axios
import axios from 'axios';

// Import vue modules
import { ref, onMounted, computed } from 'vue';

// Get props
const props = defineProps({
    addButtonDivClass: {
        type: String,
        default: 'btn btn-success'
    },
    competitionId: {
        type: String,
        required: true
    },
    existingCompetitors: {
        type: Object,
        default: []
    }
})

// Set empty parameters
const allCompetitors = ref([]);
const isLoadingCompetirors = ref(true)
const competitorToAdd = ref({})

// Set competitor to add empty
function setCompetitorToAddEmpty() {
    competitorToAdd.value = { 
        id: '',
        alias: '',
        personal_data: {
            full_name: 'Not set',
            school: {
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
        const response = await axios.get('competitor/get/all')

        // Set competitors
        allCompetitors.value = response.data

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
const addCompetitorToCompetition = async (competitionId, competitorId) => {
    try {

        // Create object for the axios to post
        const apiBody = {
            competition: { id: competitionId },
            competitor: { id: competitorId }
        }

        // Add exstiting competitor to competition
        await axios.post('competitor/add/to/competition', apiBody)

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
const setCompetitorToAdd = (competitor) => {
    competitorToAdd.value = competitor
}

// Filter out competitor that already exist
const unLinkedCompetitors = computed(() => {
    const existingUserIds = new Set(props.existingCompetitors.map(user => user.id))
    return allCompetitors.value.filter(
        user => !existingUserIds.has(user.id)
    )
})

// filter users based on name
const searchCompetitor = ref('');
const filteredCompetitors = computed(() => {
  const query = searchCompetitor.value.toLowerCase()
  return unLinkedCompetitors.value.filter(user =>
    user.personal_data.full_name.toLowerCase().includes(query)
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
                        <div class="col">{{ competitorToAdd.personal_data.school.name }}</div>
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
                                    {{ competitorToAdd.personal_data.full_name }}
                                </button>

                                <ul class="dropdown-menu">
                                    <div class="input-group rounded">
                                        <input type="search" class="form-control rounded me-1 ms-1" 
                                            placeholder="Search" aria-label="Search" aria-describedby="search-addon" 
                                            v-model="searchCompetitor"
                                        />
                                    </div>
                                    <!-- Dropdown menu links -->
                                    <li v-for="competitor in filteredCompetitors" @click="setCompetitorToAdd(competitor)"
                                        class="dropdown-item">
                                        ID: {{ competitor.id }}|Full Name: {{ competitor.personal_data.full_name }}
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button
                            @click.prevent="addCompetitorToCompetition(props.competitionId, competitorToAdd.id)"
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