<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { apiClient, ScoringCriterionUpsertRequest } from '@/services/api';
import displayAlert from '@/components/generic/displayAlert.vue';

const route = useRoute();
const router = useRouter();
const criteriaId = Number(route.params.id);

const form = ref<ScoringCriterionUpsertRequest>({
    name: '',
    description: '',
    total_points: 0,
});

const isLoading = ref(true);
const alertTimeout = ref(3000);
const alertMessage = ref('');
const alertType = ref('');
const alertTrigger = ref(0);

function showAlert(message: string, type: string, timeout = 3000) {
    alertMessage.value = message;
    alertType.value = type;
    alertTimeout.value = timeout;
    alertTrigger.value++;
}

onMounted(async () => {
    try {
        const response = await apiClient.scoring.criteria.byId(criteriaId);
        if (!response.success) throw new Error(response.error.message || 'Unknown error');
        const found = response.data;
        form.value = {
            name: found.name ?? '',
            description: found.description ?? '',
            total_points: found.total_points ?? 0,
        };
    } catch (e) {
        showAlert('Could not load criteria. Error: ' + e, 'danger', 9000);
    } finally {
        isLoading.value = false;
    }
});

const submit = async () => {
    try {
        const response = await apiClient.scoring.criteria.update(criteriaId, form.value);
        if (!response.success) throw new Error(response.error.message || 'Unknown error');
        showAlert('Criteria updated successfully.', 'success');
        setTimeout(() => router.push('/admin/criteria'), 1000);
    } catch (e) {
        showAlert('Could not update criteria. Error: ' + e, 'danger', 9000);
    }
};
</script>

<template>
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" :trigger="alertTrigger" />
    <div class="container mt-3">
        <h3>Edit Scoring Criteria</h3>
        <div v-if="isLoading" class="text-center p-4">Loading...</div>
        <form v-else @submit.prevent="submit" class="mt-3">
            <div class="mb-3 row">
                <label class="col-sm-3 col-form-label">ID</label>
                <div class="col-sm-9">
                    <input :value="criteriaId" type="number" class="form-control" disabled />
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-3 col-form-label">Name</label>
                <div class="col-sm-9">
                    <input v-model="form.name" type="text" class="form-control" required />
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-3 col-form-label">Max Points</label>
                <div class="col-sm-9">
                    <input v-model="form.total_points" type="number" step="0.5" class="form-control" required />
                </div>
            </div>
            <div class="mb-3 row">
                <label class="col-sm-3 col-form-label">Description</label>
                <div class="col-sm-9">
                    <textarea v-model="form.description" class="form-control" rows="3"></textarea>
                </div>
            </div>
            <div class="d-flex justify-content-end gap-2 mb-4">
                <button type="button" class="btn btn-outline-dark" @click="router.back()">Cancel</button>
                <button type="submit" class="btn btn-success">Save</button>
            </div>
        </form>
    </div>
</template>
