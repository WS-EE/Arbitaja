<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
const props = defineProps({
    modalId: {
        type: String,
        default: "modelId"
    },
    buttonName: {
        type: String,
        default: 'Button',
    },
    useButtonNameAsModalHeader: {
        type: Boolean,
        default: true
    },
    modalHeader: {
        type: String,
    },
    addButtonDivClass: {
        type: String,
        default: 'btn btn-success',
    },
    isAdd: {
        type: Boolean,
        required: true
    },
    competitionId: {
        type: String,
        required: true
    },
    propCriteria: {
        type: Object,
        default: {
            name: '',
            description: '',
            totalPoints: '',
        }
    }
})
// Make the criteria from props
const modalCriteria = ref();

const isLoading = ref(true)

// Set emits
const emit = defineEmits(['addEditCriteria'])

// Create either linked competitor or a "dummy" competitor
const addEditCriteria = async(addEditCriteria, competitionId) => {
    try {
        // Set CompetitionId for the object
        modalCriteria.value.competitionId = competitionId
        
        // Make the api call
        if (props.isAdd) {
            // create and object for the new criteria
            const newCriteria = {
                name: addEditCriteria.name,
                description: addEditCriteria.description,
                totalPoints: addEditCriteria.totalPoints,
                competitionId: competitionId
            }
        
            // add the new criteria
            await axios.post('scoring/criteria/add', newCriteria)

            // show alert of success
            await showAlert(props.buttonName + ' <strong>' + addEditCriteria.name + '</strong> was a success.', 'success')

        } else {
            const updateCriteria = {
                id: addEditCriteria.id,
                name: addEditCriteria.name,
                description: addEditCriteria.description,
                totalPoints: addEditCriteria.totalPoints
            }
            // Register the item with personal data
            await axios.put('scoring/criteria/update', updateCriteria)

            // Edit alert success
            await showAlert('Editing criteria <strong>' + addEditCriteria.name + '</strong> was a success.', 'success')
        }

        emit('addEditCriteria');
    } catch(e) {
        showAlert('Couldn\'t ' + props.buttonName + '. <br> Error: ' + e + '<br>' + e.response.data.error, 'danger', 9000)
        console.log(e)
    }
}

onMounted(() => {
    try {
        modalCriteria.value = props.propCriteria
    } catch(error){
        showAlert('Failed to get criteria info.', 'danger')
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
    <!-- Button trigger modal -->
    <button
        v-if="!isLoading"
        type="button"
        :class="addButtonDivClass"
        data-bs-toggle="modal"
        :data-bs-target="'#' + modalId"
        
    >
        {{ props.buttonName }}
    </button>
    <!-- Modal -->
    <div
        v-if="!isLoading"
        class="modal fade"
        :id="modalId"
        tabindex="-1"
        role="dialog"
        aria-labelledby="modalTitleId"
        aria-hidden="true"
    >
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 v-if="props.useButtonNameAsModalHeader" class="modal-title" id="modalTitleId">
                        {{ props.buttonName }}
                    </h5>
                    <h5 v-else class="modal-title" id="modalTitleId">
                        {{ props.modalHeader }}
                    </h5>
                    <button
                        type="button"
                        class="btn-close"
                        data-bs-dismiss="modal"
                        aria-label="Close"
                    ></button>
                </div>
                <div class="modal-body">
                <!-- Normal block -->
                    <div v-if="!isAdd" class="row mt-2 mb-2">
                        <div class="col-4">
                            <label for="criteriaId" class="form-label">ID</label>
                        </div>
                        <div class="col">
                            <input id="criteriaId" type="number" class="rounded p-1 form-control" v-model="modalCriteria.id" disabled>
                        </div>
                    </div>
                    <div class="row mt-2 mb-2">
                        <div class="col-4">
                            <label for="fullName" class="form-label">Name</label>
                        </div>
                        <div class="col">
                            <input 
                                type="text"
                                placeholder="Criteria name"
                                id="criteriaName"
                                class="rounded p-1 form-control" v-model="modalCriteria.name">
                        </div>
                    </div>
                    <div class="row mt-2 mb-2">
                        <div class="col-4">
                            <label for="fullName" class="form-label">Max Points</label>
                        </div>
                        <div class="col">
                            <input 
                                type="number"
                                placeholder="1.5"
                                id="criteriaName"
                                class="rounded p-1 form-control" v-model="modalCriteria.totalPoints">
                        </div>
                    </div>
                    <div class="row mt-2 mb-2">
                        <div class="col-4">
                            <label for="fullName" class="form-label">Description</label>
                        </div>
                        <div class="col">
                            <input 
                                type="texarea"
                                placeholder="This is an awesome criteria"
                                id="criteriaName"
                                class="rounded p-1 form-control" v-model="modalCriteria.description">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button v-if="isAdd" @click.prevent="addEditCriteria(modalCriteria, props.competitionId)" type="button" class="btn btn-success" data-bs-dismiss="modal">Add</button>
                    <button v-else @click.prevent="addEditCriteria(modalCriteria, props.competitionId)" type="button" class="btn btn-success" data-bs-dismiss="modal">Edit</button>
                    <button type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</template>


