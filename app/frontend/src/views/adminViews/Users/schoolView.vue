<script setup lang="ts">
import AddSchool from '@/components/admin/users/school/addSchool.vue';
import allSchools from '@/components/admin/users/school/allSchools.vue';

import { onMounted,ref } from 'vue';
import { apiClient, SchoolResponse } from '@/services/api'

import PulseLoader from 'vue-spinner/src/PulseLoader.vue';
const isLoadingSchools = ref(true)
const schools = ref<SchoolResponse[]>([]);

const onAddSchool = async() => {
    isLoadingSchools.value = true
    await getSchools();
    isLoadingSchools.value = false
}

// Get all schools
const getSchools = async() => {
    try {
        const response = await apiClient.schools.list()
        if (!response.success) {
            throw new Error(response.error.message || 'Unknown error');
        }
        schools.value = response.data
    } catch(error) {
        showAlert('Couldn\'t get data for all the schools. <br> Error: ' + error, 'danger', 9000)
    }
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

function showAlert(message: string, type: string, timeout: number = 3000){
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