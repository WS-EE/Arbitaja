<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { DateTime } from 'luxon';
import { apiClient } from '@/services/api';
import { usePagedList } from '@/composables/usePagedList';
import type { CompetitionResponse } from '@/services/api';
import displayAlert from '@/components/generic/displayAlert.vue';
import PaginationControls from '@/components/generic/PaginationControls.vue';

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

const statusFilter = ref('');
const deleteId = ref<number | undefined>();
const deleteName = ref<string | undefined>();

const { items: competitions, search, page, pageSize, totalElements, totalPages, isLoading, error,
        sortField, sortDir, load, onSearchInput, goToPage, setSort } =
    usePagedList<CompetitionResponse>(
        (params) => apiClient.competitions.list({ ...params, status: statusFilter.value || undefined }),
        'id,asc'
    );

const onStatusChange = () => {
    page.value = 0;
    load();
};

const setToDelete = (id?: number, name?: string) => {
    deleteId.value = id;
    deleteName.value = name;
};

const deleteCompetition = async () => {
    try {
        if (deleteId.value === undefined) throw new Error('No competition selected');
        const response = await apiClient.competitions.remove(deleteId.value);
        if (!response.success) throw new Error(response.error.message || 'Unknown error');
        showAlert('Competition "' + deleteName.value + '" deleted.', 'success');
        await load();
    } catch (e) {
        showAlert('Could not delete competition. Error: ' + e, 'danger', 9000);
    } finally {
        deleteId.value = undefined;
        deleteName.value = undefined;
    }
};

const formatDate = (iso?: string) => {
    if (!iso) return '—';
    return DateTime.fromISO(iso).toFormat('dd MMM yyyy HH:mm');
};

onMounted(() => load());
</script>

<template>
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" :trigger="alertTrigger" />
    <div class="container">
        <div class="row mb-3 mt-2">
            <div class="col">
                <h3>Competitions</h3>
            </div>
            <div class="col-auto">
                <RouterLink to="/admin/competition/new" class="btn btn-success">Add Competition</RouterLink>
            </div>
        </div>

        <div class="row g-2 mb-3">
            <div class="col-md-6">
                <input
                    v-model="search"
                    @input="onSearchInput"
                    type="text"
                    class="form-control"
                    placeholder="Search competitions..."
                />
            </div>
            <div class="col-md-3">
                <select v-model="statusFilter" @change="onStatusChange" class="form-select">
                    <option value="">All statuses</option>
                    <option value="UPCOMING">Upcoming</option>
                    <option value="ONGOING">Ongoing</option>
                    <option value="FINISHED">Finished</option>
                </select>
            </div>
        </div>

        <div v-if="error" class="alert alert-danger">{{ error }}</div>
        <div v-if="isLoading" class="text-center p-4">Loading...</div>
        <div v-else>
            <div class="row border-5 border rounded m-2 p-2 justify-content-center align-items-center text-center fw-bold user-select-none">
                <div class="col-1" style="cursor:pointer" @click="setSort('id')">
                    ID <span v-if="sortField === 'id'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </div>
                <div class="col-3" style="cursor:pointer" @click="setSort('name')">
                    Name
                    <span v-if="sortField === 'name'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </div>
                <div class="col-3 d-none d-md-block" style="cursor:pointer" @click="setSort('startTime')">
                    Start Time
                    <span v-if="sortField === 'startTime'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </div>
                <div class="col-2 d-none d-md-block" style="cursor:pointer" @click="setSort('endTime')">
                    End Time
                    <span v-if="sortField === 'endTime'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </div>
                <div class="col-3"></div>
            </div>

            <div
                v-for="competition in competitions"
                :key="competition.id"
                class="row border-2 border rounded m-2 p-2 justify-content-center align-items-center text-center"
            >
                <div class="col-1">{{ competition.id }}</div>
                <div class="col-3">{{ competition.name }}</div>
                <div class="col-3 d-none d-md-block small">{{ formatDate(competition.start_time) }}</div>
                <div class="col-2 d-none d-md-block small">{{ formatDate(competition.end_time) }}</div>
                <div class="col-3 d-flex justify-content-end gap-1">
                    <RouterLink :to="'/admin/competition/get/' + competition.id" class="btn btn-outline-dark btn-sm">View</RouterLink>
                    <RouterLink :to="'/admin/competition/edit/' + competition.id" class="btn btn-outline-success btn-sm">Edit</RouterLink>
                    <button
                        @click.prevent="setToDelete(competition.id, competition.name)"
                        type="button"
                        data-bs-toggle="modal"
                        data-bs-target="#deleteCompetitionModal"
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

    <div class="modal fade" id="deleteCompetitionModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Delete Competition?</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    You are about to permanently delete <strong>{{ deleteName }}</strong> (ID: {{ deleteId }}).
                </div>
                <div class="modal-footer">
                    <button @click.prevent="deleteCompetition()" type="button" class="btn btn-danger" data-bs-dismiss="modal">Delete</button>
                    <button type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>
</template>
