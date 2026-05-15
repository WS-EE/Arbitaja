<script setup lang="ts">
import { ref } from 'vue';
import { apiClient } from '@/services/api';

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
    competition_id: {
        type: Number,
        required: true
    },
})

const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')
const alertTrigger = ref(0)

import displayAlert from '@/components/generic/displayAlert.vue';

function showAlert(message: string, type: string, timeout: number = 3000) {
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
    alertTrigger.value++
}

const emit = defineEmits(['removeCriteria'])

const removeCriteria = async(criteriaId: number, criteriaName?: string) => {
    try {
        const response = await apiClient.scoring.criteria.removeFromCompetition(criteriaId, props.competition_id)
        if (!response.success) {
            throw new Error(response.error.message || 'Unknown error')
        }
        emit('removeCriteria')
        showAlert('Criteria named ' + criteriaName + ' has been removed from competition.', 'success')
    } catch (error) {
        showAlert('Criteria ' + criteriaName + ' couldn\'t be removed. Error: ' + error, 'danger', 9000)
    }
}

</script>

<template>
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" :trigger="alertTrigger" />
    <button
        type="button"
        :class="props.addButtonDivClass"
        data-bs-toggle="modal"
        :data-bs-target="'#' + props.modalId"
    >
    </button>
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
                        Remove from competition: <b>{{ props.criteriaName }}</b>
                    </h5>
                    <button
                        type="button"
                        class="btn-close"
                        data-bs-dismiss="modal"
                        aria-label="Close"
                    ></button>
                </div>
                <div class="modal-body">
                    <div class="row mt-2 mb-2">
                        <p>You are about to remove <b>{{ props.criteriaName }}</b> (ID: <b>{{ props.criteriaId }}</b>) from this competition. The criteria itself will not be deleted.</p>
                    </div>
                </div>
                <div class="modal-footer">
                    <button @click.prevent="removeCriteria(props.criteriaId, props.criteriaName)" type="button" class="btn btn-danger" data-bs-dismiss="modal">Remove</button>
                    <button type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</template>
