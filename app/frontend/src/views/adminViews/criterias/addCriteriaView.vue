<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { apiClient, ScoringCriterionUpsertRequest } from '@/services/api';
import displayAlert from '@/components/generic/displayAlert.vue';

const router = useRouter();

const form = ref<ScoringCriterionUpsertRequest>({
    name: '',
    description: '',
    total_points: 0,
});

const alertTimeout = ref(3000);
const alertMessage = ref('');
const alertType = ref('');

function showAlert(message: string, type: string, timeout = 3000) {
    alertMessage.value = message;
    alertType.value = type;
    alertTimeout.value = timeout;
}

const submit = async () => {
    try {
        const response = await apiClient.scoring.criteria.create(form.value);
        if (!response.success) throw new Error(response.error.message || 'Unknown error');
        showAlert('Criteria "' + form.value.name + '" created successfully.', 'success');
        setTimeout(() => router.push('/admin/criteria'), 1000);
    } catch (e) {
        showAlert('Could not create criteria. Error: ' + e, 'danger', 9000);
    }
};
</script>

<template>
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />
    <div class="container mt-3">
        <h3>Add Scoring Criteria</h3>
        <form @submit.prevent="submit" class="mt-3">
            <div class="mb-3 row">
                <label class="col-sm-3 col-form-label">Name</label>
                <div class="col-sm-9">
                    <input v-model="form.name" type="text" class="form-control" required placeholder="Criteria name" />
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-3 col-form-label">Max Points</label>
                <div class="col-sm-9">
                    <input v-model="form.total_points" type="number" step="0.5" class="form-control" required placeholder="10" />
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-3 col-form-label">Description</label>
                <div class="col-sm-9">
                    <textarea v-model="form.description" class="form-control" rows="3" placeholder="Optional description"></textarea>
                </div>
            </div>
            <div class="d-flex justify-content-end gap-2">
                <button type="button" class="btn btn-outline-dark" @click="router.back()">Cancel</button>
                <button type="submit" class="btn btn-success">Create</button>
            </div>
        </form>
    </div>
</template>
