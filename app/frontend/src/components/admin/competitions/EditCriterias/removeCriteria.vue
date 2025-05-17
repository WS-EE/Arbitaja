<script setup>
import { ref } from 'vue';
import axios from 'axios';
const props = defineProps({
    modalId: {
        type: String,
        default: "modelId"
    },
    addButtonDivClass: {
        type: String,
        default: 'btn btn-success',
    },
    criteriaId: {
        type: Number,
        required: true
    },
    criteriaName: {
        type: String,
    },
})

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

// Set event to emit
const emit = defineEmits(['removeCriteria'])

const removeCompetitor = async(criteriaId, criteriaName) => {
    try {
        // Delete competitor
        await axios.delete
        (
            'scoring/criteria/delete',
            { params: { scoring_id: criteriaId } }
        )

        // emit event
        emit('removeCriteria')

        // Show alert
        showAlert('Criteria named ' + criteriaName + ' has been removed. ID: ' + criteriaId, 'success')
    } catch (error) {
        // On error show error
        showAlert('Criteria name ' + criteriaName + ' couldn\'t be removed. Error: ' + error + '<br>' + error.response.data.error , 'danger', 9000)
    }
}

</script>

<template>
    <!-- Alert when needed -->
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />
    <!-- Button trigger modal -->
    <button
        type="button"
        :class="props.addButtonDivClass"
        data-bs-toggle="modal"
        :data-bs-target="'#' + props.modalId"
    >
    </button>
    <!-- Modal -->
    <div 
        class="modal fade"
        :id="props.modalId"
        tabindex="-1"
        role="dialog"
        aria-labelledby="modalTitleId"
        aria-hidden="true"
    >
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalTitleId">
                        Delete: <b>{{ props.criteriaName }}</b>
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
                    <div class="row mt-2 mb-2">
                        <div class="">
                            <p>You are about delete the competitor <b>{{ props.criteriaName }}</b> with the ID of <b>{{ props.criteriaId }}</b></p>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button @click.prevent="removeCompetitor(props.criteriaId, criteriaName)" type="button" class="btn btn-danger" data-bs-dismiss="modal">Delete</button>
                    <button type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</template>