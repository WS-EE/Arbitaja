<script setup>

// Define props
const props = defineProps({
    competitors: {
        type: Object,
        required: true
    },
    addActions: {
        type: Boolean,
        default: false
    }
})

// Define emits
const emit = defineEmits(['tableChanged'])

// Import components
import removeCompetitor from './removeCompetitor.vue';

// On event handler
const onRemoveCompetitor = () => {
    // When competitor remove tell table changed
    emit('tableChanged');
}

</script>

<template>
    <table class="table table-striped mt-3">
        <thead>
            <th scope="col">ID</th>
            <th scope="col">Alias</th>
            <th scope="col">Full Name</th>
            <th scope="col" class="d-none d-lg-table-cell">E-Mail</th>
            <th scope="col" class="d-none d-md-table-cell">School</th>
            <th v-if="addActions" scope="col">Actions</th>
        </thead>
        <tbody>
            <tr v-for="competitor in competitors">
                <th scope="row">{{ competitor.id }}</th>
                <td>{{ competitor.alias }}</td>
                <td>{{ competitor.personal_data.full_name }}</td>
                <td class="d-none d-lg-table-cell">{{ competitor.personal_data.email }}</td>
                <td class="d-none d-md-table-cell">{{ competitor.personal_data.school.name }}</td>
                <td v-if="addActions">
                    <removeCompetitor 
                        addButtonDivClass="btn btn-danger bi bi-trash"
                        modalId="DeleteModal"
                        :competitorId="competitor.id"
                        :competitorName="competitor.personal_data.full_name"
                        @removeCompetitor="onRemoveCompetitor()"
                    />
                </td>
            </tr>
        </tbody>
    </table>
</template>