<script setup>
import AddSchool from '@/components/admin/users/school/addSchool.vue';
import allSchools from '@/components/admin/users/school/allSchools.vue';

import axios from 'axios';
import { onMounted,ref } from 'vue';

import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
const isLoadingSchools = ref(true)
const schools = ref([]);

const onAddSchool = async() => {
    isLoadingSchools.value = true
    await getSchools();
    isLoadingSchools.value = false
}

// Get all schools
const getSchools = async() => {
    try {
        // Try getting school data
        const response = await axios.get('school/all/get')
        schools.value = response.data
    } catch(error) {
        showAlert('Couldn\'t get data for all the schools. <br> Error: ' + error, 'danger', 9000)
    };
}

onMounted(async () => {
    try {
        await getSchools();
    } catch(error) {
        showAlert('Something went wrong. <br> Error:' + error, 'danger', 9000)
    } finally {
        isLoadingSchools.value = false
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
    <div class="container">
        <!-- Alert when needed -->
        <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />
        <!-- Main content -->
        <div v-if="isLoadingSchools" class="position-absolute top-50 start-50">
            <PulseLoader />
        </div>
        <allSchools v-else :schools="schools" />
        <AddSchool modalId="addSchool" addButtonDivClass="z-0 pb-2 pt-2 sticky-bottom" @addSchool="onAddSchool()"/>
    </div>
</template>