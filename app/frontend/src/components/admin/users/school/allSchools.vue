<script setup>
const props = defineProps({
    schools: {
        type: Object,
    },
    addDelete: {
        type: Boolean,
        default: true,
    },
})
import { onMounted, ref } from 'vue';
import axios from 'axios';

// schools to loop over
const schools = ref();

// Display loading until onmount is doing stuff
const isLoadingSchools = ref(true)

// If we want to show delete button to user
const addDelete = ref(true)

onMounted(async () => {
    try {
        // If prop schools is not defined try to get them ourselves
        if (props.schools === undefined){  
            const response2 = await axios.get('school/all/get')
            schools.value = response2.data

        // else get the variables from props
        } else {
            schools.value = props.schools
        }

        // If parent component doesn't want to see delete button remove it
        if (props.addDelete === false) {
            addDelete.value = props.addDelete
        }
    } catch(error) {
        // Throw console log error if fail
        console.log(error)
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
const deleteSchool = async(school) => {
    // Delete school based on ID
    await axios.delete("/school/register", { id: setSchoolId });
    
    // Unset to delete after deleting the school
    unsetSchoolToDelete();

    // Reload page to update the list of schools
    location.reload();
}

</script>

<template>
    <div v-if="!isLoadingSchools" class="mt-2">
        <div v-for="school in schools" class="row border border-2 mt-2 justify-content-center text-center align-items-center">
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
    <div v-else>
        Loading...
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