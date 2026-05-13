<script setup lang="ts">

import { CompetitorResponse } from '@/services/api';

// Define props
const props = defineProps({
    competitors: {
        type: Array as () => Array<CompetitorResponse>,
        required: true
    },
    competition_id: {
        type: Number,
        default: 0
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
import addEditCompetitor from './addEditCompetitor.vue';

// On event handler
const onRemoveCompetitor = () => {
    // When competitor remove tell table changed
    emit('tableChanged');
}
const onEditCompetitor = () => {
    // When competitor remove tell table changed
    emit('tableChanged');
}

</script>

<template>
    <table class="table table-striped mt-3">
        <thead>
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Alias</th>
                <th scope="col">Full Name</th>
                <th scope="col" class="d-none d-lg-table-cell">E-Mail</th>
                <th scope="col" class="d-none d-md-table-cell">School</th>
                <th v-if="addActions" scope="col">Actions</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="competitor in competitors" :key="competitor.id">
                <th scope="row">{{ competitor.id }}</th>
                <td>{{ competitor.alias }}</td>
                <td>{{ competitor.personal_data?.full_name }}</td>
                <td class="d-none d-lg-table-cell">{{ competitor.personal_data?.email }}</td>
                <td class="d-none d-md-table-cell">{{ competitor.personal_data?.school?.name }}</td>
                <td v-if="addActions">
                    <addEditCompetitor
                        addButtonDivClass="btn btn-dark bi bi-pencil me-1"
                        buttonName=""
                        :modalId="'editCompetitor' + competitor.id"
                        :modalHeader="'Edit Competitor \'' + competitor.personal_data?.full_name + '\''"
                        :isEdit="true"
                        :competitor="competitor"
                        @editCompetitor="onEditCompetitor()"
                    />
                    <removeCompetitor 
                        addButtonDivClass="btn btn-danger bi bi-trash"
                        modalId="DeleteModal"
                        :competition_id="props.competition_id"
                        :competitorId="competitor.id"
                        :competitorName="competitor.personal_data?.full_name"
                        @removeCompetitor="onRemoveCompetitor()"
                    />
                </td>
            </tr>
        </tbody>
    </table>
</template>