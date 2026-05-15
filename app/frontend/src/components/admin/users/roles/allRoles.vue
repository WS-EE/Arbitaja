<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { apiClient } from '@/services/api';
import { usePagedList } from '@/composables/usePagedList';
import type { RoleResponse } from '@/services/api';
import displayAlert from '@/components/generic/displayAlert.vue';
import PaginationControls from '@/components/generic/PaginationControls.vue';

const deleteRoleId = ref<number | undefined>();
const deleteRoleName = ref<string | undefined>();

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

const { items: roles, search, page, pageSize, totalElements, totalPages, isLoading, error,
        sortField, sortDir, load, onSearchInput, goToPage, setSort } =
    usePagedList<RoleResponse>((params) => apiClient.roles.list(params), 'id,asc');

const setRoleToDelete = (id?: number, name?: string) => {
    deleteRoleId.value = id;
    deleteRoleName.value = name;
};

const deleteRole = async () => {
    try {
        if (deleteRoleId.value === undefined) throw new Error('Role ID is undefined');
        await apiClient.roles.delete(deleteRoleId.value);
        showAlert(`Role "${deleteRoleName.value}" deleted successfully.`, 'success');
        await load();
    } catch (err) {
        showAlert('Couldn\'t delete role. Error: ' + err, 'danger', 9000);
    } finally {
        deleteRoleId.value = undefined;
        deleteRoleName.value = undefined;
    }
};

onMounted(async () => {
    try {
        await load();
    } catch (err) {
        showAlert('Something went wrong. Error: ' + err, 'danger', 9000);
    }
});
</script>

<template>
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" :trigger="alertTrigger" />

    <div class="container">
        <div class="row mb-2 mt-2">
            <div class="col">
                <h3>Roles</h3>
            </div>
            <div class="col-auto">
                <RouterLink to="/admin/users/role_new" class="btn btn-success">Add Role</RouterLink>
            </div>
        </div>

        <div class="mb-3">
            <input
                v-model="search"
                @input="onSearchInput"
                type="text"
                class="form-control"
                placeholder="Search roles..."
            />
        </div>

        <div v-if="error" class="alert alert-danger">{{ error }}</div>
        <div v-if="isLoading" class="text-center p-4">Loading...</div>
        <div v-else>
            <div class="row border-5 border rounded m-2 p-2 justify-content-center align-items-center text-center fw-bold user-select-none">
                <div class="col-2" style="cursor:pointer" @click="setSort('id')">
                    ID <span v-if="sortField === 'id'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </div>
                <div class="col-4" style="cursor:pointer" @click="setSort('name')">
                    Name <span v-if="sortField === 'name'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </div>
                <div class="col-3">Permissions</div>
                <div class="col-3"></div>
            </div>

            <div
                v-for="role in roles"
                :key="role.id"
                class="row border-2 border rounded m-2 p-2 justify-content-center align-items-center text-center"
            >
                <div class="col-2">{{ role.id }}</div>
                <div class="col-4">{{ role.name }}</div>
                <div class="col-3">{{ role.permissions?.length ?? 0 }}</div>
                <div class="col-3 text-end">
                    <RouterLink :to="'/admin/users/role_edit/' + role.id" class="btn btn-outline-success btn-sm me-1">Edit</RouterLink>
                    <button
                        @click.prevent="setRoleToDelete(role.id, role.name)"
                        type="button"
                        data-bs-toggle="modal"
                        data-bs-target="#deleteRoleModal"
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

    <div class="modal fade" id="deleteRoleModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Are you sure?</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    You are about to delete <strong>{{ deleteRoleName }}</strong> (ID: {{ deleteRoleId }}).
                </div>
                <div class="modal-footer">
                    <button @click.prevent="deleteRole()" type="button" class="btn btn-danger" data-bs-dismiss="modal">Delete</button>
                    <button type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>
</template>
