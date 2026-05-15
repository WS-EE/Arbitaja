<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { apiClient } from '@/services/api';
import { usePagedList } from '@/composables/usePagedList';
import type { ScoringCriterionResponse } from '@/services/api';
import displayAlert from '@/components/generic/displayAlert.vue';
import PaginationControls from '@/components/generic/PaginationControls.vue';

const deleteId = ref<number | undefined>();
const deleteName = ref<string | undefined>();

const alertTimeout = ref(3000);
const alertMessage = ref('');
const alertType = ref('');

function showAlert(message: string, type: string, timeout = 3000) {
    alertMessage.value = message;
    alertType.value = type;
    alertTimeout.value = timeout;
}

const { items: criterias, search, page, pageSize, totalElements, totalPages, isLoading, error,
        sortField, sortDir, load, onSearchInput, goToPage, setSort } =
    usePagedList<ScoringCriterionResponse>((params) => apiClient.scoring.criteria.list(params), 'id,asc');

const setToDelete = (id?: number, name?: string) => {
    deleteId.value = id;
    deleteName.value = name;
};

const deleteCriteria = async () => {
    try {
        if (deleteId.value === undefined) throw new Error('No criteria selected');
        const response = await apiClient.scoring.criteria.remove(deleteId.value);
        if (!response.success) throw new Error(response.error.message || 'Unknown error');
        showAlert('Criteria "' + deleteName.value + '" deleted.', 'success');
        await load();
    } catch (e) {
        showAlert('Could not delete criteria. Error: ' + e, 'danger', 9000);
    } finally {
        deleteId.value = undefined;
        deleteName.value = undefined;
    }
};

onMounted(() => load());
</script>

<template>
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />
    <div class="container">
        <div class="row mb-3 mt-2">
            <div class="col">
                <h3>Scoring Criteria</h3>
            </div>
            <div class="col-auto">
                <RouterLink to="/admin/criteria/new" class="btn btn-success">Add Criteria</RouterLink>
            </div>
        </div>

        <div class="mb-3">
            <input
                v-model="search"
                @input="onSearchInput"
                type="text"
                class="form-control"
                placeholder="Search criteria..."
            />
        </div>

        <div v-if="error" class="alert alert-danger">{{ error }}</div>
        <div v-if="isLoading" class="text-center p-4">Loading...</div>
        <div v-else>
            <div class="row border-5 border rounded m-2 p-2 justify-content-center align-items-center text-center fw-bold user-select-none">
                <div class="col-1" style="cursor:pointer" @click="setSort('id')">
                    ID <span v-if="sortField === 'id'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </div>
                <div class="col-3" style="cursor:pointer" @click="setSort('name')">
                    Name <span v-if="sortField === 'name'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </div>
                <div class="col-2" style="cursor:pointer" @click="setSort('totalPoints')">
                    Max Points <span v-if="sortField === 'totalPoints'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </div>
                <div class="col-4 d-none d-md-block">Description</div>
                <div class="col-2"></div>
            </div>
            <div
                v-for="criteria in criterias"
                :key="criteria.id"
                class="row border-2 border rounded m-2 p-2 justify-content-center align-items-center text-center"
            >
                <div class="col-1">{{ criteria.id }}</div>
                <div class="col-3">{{ criteria.name }}</div>
                <div class="col-2">{{ criteria.total_points }}</div>
                <div class="col-4 d-none d-md-block text-truncate">{{ criteria.description }}</div>
                <div class="col-2 d-flex justify-content-end gap-1">
                    <RouterLink :to="'/admin/criteria/edit/' + criteria.id" class="btn btn-outline-success btn-sm">Edit</RouterLink>
                    <button
                        @click.prevent="setToDelete(criteria.id, criteria.name)"
                        type="button"
                        data-bs-toggle="modal"
                        data-bs-target="#deleteCriteriaModal"
                        class="btn btn-danger btn-sm"
                    >Delete</button>
                </div>
            </div>

            <PaginationControls
                :page="page"
                :total-pages="totalPages"
                :total-elements="totalElements"
                :page-size="pageSize"
                @go-to-page="goToPage"
                @update:page-size="pageSize = $event"
            />
        </div>
    </div>

    <div class="modal fade" id="deleteCriteriaModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Delete criteria?</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    You are about to permanently delete <strong>{{ deleteName }}</strong> (ID: {{ deleteId }}).
                    This will remove it from all competitions it is linked to.
                </div>
                <div class="modal-footer">
                    <button @click.prevent="deleteCriteria()" type="button" class="btn btn-danger" data-bs-dismiss="modal">Delete</button>
                    <button type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>
</template>
