<script setup>
// Set props
const props = defineProps({ 
    criterias: {
        type: Object,
        required: true
    },
    competitionId: {
        type: String,
        required: true
    },
    addActions: {
        type: Boolean,
        default: false
    }
})
// Import components
import addEditCriteria from '@/components/admin/competitions/EditCriterias/addEditCriteria.vue'
import removeCriteria from '@/components/admin/competitions/EditCriterias/removeCriteria.vue';

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
            <th scope="col">ID</th>
            <th scope="col">Name</th>
            <th scope="col">Max Points</th>
            <th scope="col" class="d-none d-lg-table-cell">Description</th>
            <th scope="col" v-if="addActions">Actions</th>
        </thead>
        <tbody>
            <tr v-for="criteria in criterias">
                <th scope="row">{{ criteria.id }}</th>
                <td>{{ criteria.name }}</td>
                <td>{{ criteria.totalPoints }}</td>
                <td class="d-none d-lg-table-cell">{{ criteria.description }}</td>
                <td v-if="addActions">
                    <addEditCriteria
                        buttonName=""
                        :modalId="'editCriteria' + criteria.id"
                        :propCriteria="criteria"
                        :modalHeader="'Edit criteria ' + criteria.name"
                        :useButtonNameAsModalHeader="false"
                        :isAdd="false"
                        :competitionId="competitionId"
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