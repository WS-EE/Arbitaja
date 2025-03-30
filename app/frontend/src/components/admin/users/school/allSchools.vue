<script setup>
const props = defineProps({
    schools: {
        type: Object,
    },
    addDelete: {
        type: Boolean,
        default: true,
    },
    limitItems: {
        type: Number,
        default: 0,
    },
})
import { onMounted, ref, watch } from 'vue';
import axios from 'axios';



// schools to loop over
const schools = ref();

// Display loading until onmount is doing stuff
const isLoadingSchools = ref(true)
import PulseLoader from 'vue-spinner/src/PulseLoader.vue';

// If we want to show delete button to user
const addDelete = ref(true)

// Add school limit
const loopedSchools = ref([]);

function changeLimit(schools, limit){
    // Limit schools
    if (limit === 0) {
        loopedSchools.value = schools
    } else (
        loopedSchools.value = schools.slice(0, limit)
    )
}

onMounted(async () => {
    try {
        // If prop schools is not defined try to get them ourselves
        if (props.schools === undefined){  
            const response = await axios.get('school/all/get')
            schools.value = response.data

        // else get the variables from props
        } else {
            schools.value = props.schools
        }

        // If parent component doesn't want to see delete button remove it
        if (props.addDelete === false) {
            addDelete.value = props.addDelete
        }

        // Limit schools
        changeLimit(schools.value, props.limitItems);
    } catch(error) {
        // Throw console log error if fail
        showAlert('Something went wrong while loading.', 'danger')
    } finally {
        // Show content when done loading
        isLoadingSchools.value = false
    }
})

// Variables for when deleting a school
const setSchoolId = ref();
const setSchoolName = ref();

// Set the school variables that the user wants to delete
function setSchoolToDelete(schoolId, schoolName) {
    setSchoolId.value = schoolId
    setSchoolName.value = schoolName
}

// if user closes the modal un commit the school variables
function unsetSchoolToDelete(){
    setSchoolId.value = ""
    setSchoolName.value = ""
}

// Delete the school based on the variables we set earlier
const deleteSchool = async() => {
    try{
        // Delete school based on ID
        await axios.delete("/school/register", {
        params: { id: setSchoolId.value }, // Send `id` as a query parameter
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
      });
        showAlert('School ' + setSchoolName + ' has been deleted', 'warning')
        
        // Unset to delete after deleting the school
        unsetSchoolToDelete();

        // Reload page to update the list of schools
        location.reload();
    } catch (e) {
        showAlert('Couldn\'t delete school. <br> Error: ' + e, 'danger')
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

// Watch for the limit
watch(
  () => props.limitItems,
  (newLimit) => {
    // Limit schools
    changeLimit(schools.value, newLimit);
  }
);

// See if schools change
watch(
  () => props.schools,
  (newSchools) => {
    // New list of schools
    schools.value = newSchools
    console.log(schools.value)
  }
);

</script>

<template>
    <!-- Alert when needed -->
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />
    <!-- main content -->
    <div v-if="!isLoadingSchools" class="mt-2">
        <div v-for="school in loopedSchools" :key="school.id" class="row border border-2 mt-2 justify-content-center text-center align-items-center">
            <div class="col-xl-2 d-xl-block d-none mt-3">
                <p>{{ school.id }}:</p>
            </div>
            <div class="col-lg-10 col-xl-8 mt-3">
                <p>{{ school.name }}</p>
            </div>
            <div v-if="addDelete" class="col">
                <button @click.prevent="setSchoolToDelete(school.id, school.name)" type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#deleteSchool">Delete</button>
            </div>
        </div>
    </div>
    <div v-else class="position-absolute top-50 start-50">
        <PulseLoader />
    </div>
    <!-- Delete school modal -->
    <div
        v-if="addDelete"
        class="modal fade"
        id="deleteSchool"
        tabindex="-1"
        role="dialog"
        aria-labelledby="deleteSchool"
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
                    <div class="container-fluid">You are about to delete <strong>{{ setSchoolName }}</strong> with an <strong>ID of {{ setSchoolId }}</strong></div>
                </div>
                <div class="modal-footer">
                    <button @click.prevent="deleteSchool()" type="button" class="btn btn-danger" data-bs-dismiss="modal">Delete</button>
                    <button @click.prevent="unsetSchoolToDelete()" type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</template>