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
        default: '',
    },
})
const myModalId = props.modalId
const schoolName = ref("")

const emit = defineEmits(['addSchool'])

const createSchool = async(school) => {
    try {
        // Register the school
        await axios.post('/school/register', { "name": school })
        
        // show alert of success
        await showAlert('School <strong>' + school + '</strong> has been created.', 'success')
        
        // emit event to parent
        emit('addSchool');

        // empty out school on success 
        schoolName.value = '' 
    } catch(e) {
        showAlert('Couldn\'t create school. <br> Error: ' + e, 'danger')
    }
}

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
    <div :class="addButtonDivClass">
        <button
            type="button"
            class="btn btn-success"
            data-bs-toggle="modal"
            :data-bs-target="'#' + myModalId"
            
        >
            Add school
        </button>
    </div>
    <!-- Modal -->
    <div
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
                    <h5 class="modal-title" id="modalTitleId">
                        Add School
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
                        <div class="col-4">
                            School:
                        </div>
                        <div class="col">
                            <input type="text" class="rounded p-1 form-control" v-model="schoolName">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button @click.prevent="createSchool(schoolName)" type="button" class="btn btn-success" data-bs-dismiss="modal">Add</button>
                    <button type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</template>