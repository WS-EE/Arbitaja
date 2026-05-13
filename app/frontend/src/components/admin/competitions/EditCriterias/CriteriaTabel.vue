<script setup lang="ts">

import { ScoringCriterionResponse } from '@/services/api';
// Import components
import addEditCriteria from '@/components/admin/competitions/EditCriterias/addEditCriteria.vue'
import removeCriteria from '@/components/admin/competitions/EditCriterias/removeCriteria.vue';


// Set props
const props = defineProps({ 
    criterias: {
        type: Array as () => Array<ScoringCriterionResponse>,
        required: true
    },
    competition_id: {
        type: Number,
        required: true
    },
    addActions: {
        type: Boolean,
        default: false
    }
})

// Define emits
const emit = defineEmits<{
    tableChanged: []
}>();

// Send event when changed removed
const onEditCriteria = () => {
    // emit that table changed
    emit('tableChanged');
}

// Send event when criteria removed
const onRemoveCriteria = () => {
    // emit that table changed
    emit('tableChanged');
}

</script>

<template>
    <!-- Table -->
    <table class="table table-striped mt-3">
        <thead>
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Name</th>
                <th scope="col">Max Points</th>
                <th scope="col" class="d-none d-lg-table-cell">Description</th>
                <th scope="col" v-if="addActions">Actions</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="criteria in criterias" :key="criteria.id">
                <th scope="row">{{ criteria.id }}</th>
                <td>{{ criteria.name }}</td>
                <td>{{ criteria.total_points }}</td>
                <td class="d-none d-lg-table-cell">{{ criteria.description }}</td>
                <td v-if="addActions">
                    <addEditCriteria
                        buttonName=""
                        :modalId="'editCriteria' + criteria.id"
                        :criteria="criteria"
                        :modalHeader="'Edit criteria ' + criteria.name"
                        :useButtonNameAsModalHeader="false"
                        :isAdd="false"
                        :competition_id="competition_id"
                        addButtonDivClass="btn btn-dark me-1 bi bi-pencil"
                        @addEditCriteria="onEditCriteria()"
                    />
                    <removeCriteria
                        addButtonDivClass="btn btn-danger bi bi-trash"
                        modalId="DeleteModal"
                        :criteriaId="criteria.id"
                        :criteriaName="criteria.name"
                        @removeCriteria="onRemoveCriteria()"
                    />
                </td>
            </tr>
        </tbody>
    </table>
</template>