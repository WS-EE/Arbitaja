<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { apiClient } from '@/services/api';
import { usePagedList } from '@/composables/usePagedList';
import type { SchoolResponse } from '@/services/api';
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

const { items: schools, search, page, pageSize, totalElements, totalPages, isLoading, error,
        sortField, sortDir, load, onSearchInput, goToPage, setSort } =
    usePagedList<SchoolResponse>((params) => apiClient.schools.list(params), 'id,asc');

// Add school modal state
const newSchoolName = ref('');

const createSchool = async () => {
    try {
        if (!newSchoolName.value.trim()) return;
        const response = await apiClient.schools.create({ name: newSchoolName.value.trim() });
        if (!response.success) throw new Error(response.error.message || 'Unknown error');
        showAlert('School "' + newSchoolName.value + '" created.', 'success');
        newSchoolName.value = '';
        await load();
    } catch (e) {
        showAlert('Could not create school. Error: ' + e, 'danger', 9000);
    }
};

// Edit school modal state
const editSchoolId = ref<number | undefined>();
const editSchoolName = ref('');

const setSchoolToEdit = (id?: number, name?: string) => {
    editSchoolId.value = id;
    editSchoolName.value = name ?? '';
};

const saveSchoolEdit = async () => {
    try {
        if (editSchoolId.value === undefined) throw new Error('No school selected');
        if (!editSchoolName.value.trim()) return;
        const response = await apiClient.schools.update(editSchoolId.value, { name: editSchoolName.value.trim() });
        if (!response.success) throw new Error(response.error.message || 'Unknown error');
        showAlert('School updated.', 'success');
        editSchoolId.value = undefined;
        editSchoolName.value = '';
        await load();
    } catch (e) {
        showAlert('Could not update school. Error: ' + e, 'danger', 9000);
    }
};

// Delete school modal state
const deleteSchoolId = ref<number | undefined>();
const deleteSchoolName = ref<string | undefined>();

const setSchoolToDelete = (id?: number, name?: string) => {
    deleteSchoolId.value = id;
    deleteSchoolName.value = name;
};

const deleteSchool = async () => {
    try {
        if (deleteSchoolId.value === undefined) throw new Error('No school selected');
        const response = await apiClient.schools.remove(deleteSchoolId.value);
        if (!response.success) throw new Error(response.error.message || 'Unknown error');
        showAlert('School "' + deleteSchoolName.value + '" deleted.', 'success');
        await load();
    } catch (e) {
        showAlert('Could not delete school. Error: ' + e, 'danger', 9000);
    } finally {
        deleteSchoolId.value = undefined;
        deleteSchoolName.value = undefined;
    }
};

onMounted(() => load());
</script>

<template>
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" :trigger="alertTrigger" />
    <div class="container">
        <div class="row mb-3 mt-2">
            <div class="col">
                <h3>Schools</h3>
            </div>
            <div class="col-auto">
                <button
                    type="button"
                    class="btn btn-success"
                    data-bs-toggle="modal"
                    data-bs-target="#addSchoolModal"
                >Add School</button>
            </div>
        </div>

        <div class="mb-3">
            <input
                v-model="search"
                @input="onSearchInput"
                type="text"
                class="form-control"
                placeholder="Search schools..."
            />
        </div>

        <div v-if="error" class="alert alert-danger">{{ error }}</div>
        <div v-if="isLoading" class="text-center p-4">Loading...</div>
        <div v-else>
            <div class="row border-5 border rounded m-2 p-2 justify-content-center align-items-center text-center fw-bold user-select-none">
                <div class="col-2" style="cursor:pointer" @click="setSort('id')">
                    ID <span v-if="sortField === 'id'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </div>
                <div class="col-6" style="cursor:pointer" @click="setSort('name')">
                    Name <span v-if="sortField === 'name'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </div>
                <div class="col-4"></div>
            </div>
            <div
                v-for="school in schools"
                :key="school.id"
                class="row border-2 border rounded m-2 p-2 justify-content-center align-items-center text-center"
            >
                <div class="col-2">{{ school.id }}</div>
                <div class="col-6">{{ school.name }}</div>
                <div class="col-4 d-flex justify-content-end gap-1">
                    <button
                        @click.prevent="setSchoolToEdit(school.id, school.name)"
                        type="button"
                        data-bs-toggle="modal"
                        data-bs-target="#editSchoolModal"
                        class="btn btn-outline-success btn-sm"
                    >Edit</button>
                    <button
                        @click.prevent="setSchoolToDelete(school.id, school.name)"
                        type="button"
                        data-bs-toggle="modal"
                        data-bs-target="#deleteSchoolModal"
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

    <!-- Add School Modal -->
    <div class="modal fade" id="addSchoolModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Add School</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <label class="form-label">School Name</label>
                    <input type="text" class="form-control" v-model="newSchoolName" placeholder="Enter school name" />
                </div>
                <div class="modal-footer">
                    <button @click.prevent="createSchool()" type="button" class="btn btn-success" data-bs-dismiss="modal">Add</button>
                    <button type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Edit School Modal -->
    <div class="modal fade" id="editSchoolModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Edit School</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <label class="form-label">School Name</label>
                    <input type="text" class="form-control" v-model="editSchoolName" />
                </div>
                <div class="modal-footer">
                    <button @click.prevent="saveSchoolEdit()" type="button" class="btn btn-success" data-bs-dismiss="modal">Save</button>
                    <button type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Delete School Modal -->
    <div class="modal fade" id="deleteSchoolModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Delete School?</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    You are about to permanently delete <strong>{{ deleteSchoolName }}</strong> (ID: {{ deleteSchoolId }}).
                </div>
                <div class="modal-footer">
                    <button @click.prevent="deleteSchool()" type="button" class="btn btn-danger" data-bs-dismiss="modal">Delete</button>
                    <button type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>
</template>
