<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { ensureAuthRehydrated } from '@/composables/useAuthRehydrate';
import { useUserStore } from '@/stores/userStore';
import { apiClient } from '@/services/api';
import { usePagedList } from '@/composables/usePagedList';
import type { UserProfileResponse } from '@/services/api';
import displayAlert from '@/components/generic/displayAlert.vue';
import PaginationControls from '@/components/generic/PaginationControls.vue';

const curUserId = ref<number | undefined>(undefined);
const deleteUserId = ref<number | undefined>(undefined);
const deleteUserName = ref<string | undefined>(undefined);

const alertTimeout = ref(3000);
const alertMessage = ref('');
const alertType = ref('');

function showAlert(message: string, type: string, timeout = 3000) {
    alertMessage.value = message;
    alertType.value = type;
    alertTimeout.value = timeout;
}

const { items: users, search, page, pageSize, totalElements, totalPages, isLoading, error,
        sortField, sortDir, load, onSearchInput, goToPage, setSort } =
    usePagedList<UserProfileResponse>((params) => apiClient.users.list(params), 'username,asc');

const currentUser = (id?: number) => id === curUserId.value;

const setUserToDelete = (id?: number, name?: string) => {
    deleteUserId.value = id;
    deleteUserName.value = name;
};

const unsetUserToDelete = () => {
    deleteUserId.value = undefined;
    deleteUserName.value = undefined;
};

const deleteUser = async (userID?: number, userName?: string) => {
    try {
        if (userID === undefined) throw new Error('User ID is undefined');
        await apiClient.users.delete(userID);
        showAlert('User <strong>' + userName + '</strong> has been successfully deleted.', 'success');
        await load();
    } catch (err) {
        showAlert('Couldn\'t delete user. Error: ' + err, 'danger', 9000);
    }
};

onMounted(async () => {
    try {
        await load();
        await ensureAuthRehydrated({ force: true });
        const userStore = useUserStore();
        curUserId.value = userStore.id === null ? undefined : userStore.id;
    } catch (err) {
        showAlert('Something went wrong. Error: ' + err, 'danger', 9000);
    }
});
</script>

<template>
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />

    <div class="container">
        <div class="row mb-2 mt-2">
            <div class="col">
                <h3>Users</h3>
            </div>
            <div class="col-auto">
                <RouterLink to="/admin/users/user_new" class="btn btn-success">Create User</RouterLink>
            </div>
        </div>

        <div class="mb-3">
            <input
                v-model="search"
                @input="onSearchInput"
                type="text"
                class="form-control"
                placeholder="Search by username or name..."
            />
        </div>

        <div v-if="error" class="alert alert-danger">{{ error }}</div>
        <div v-if="isLoading" class="text-center p-4">Loading...</div>
        <div v-else>
            <div class="row border-5 border rounded m-2 p-1 p-md-2 justify-content-center align-items-center text-center fw-bold user-select-none">
                <div class="col-lg-3 col-md-3 col-sm-4" style="cursor:pointer" @click="setSort('username')">
                    Username <span v-if="sortField === 'username'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </div>
                <div class="col-lg-3 col-md-3 col-sm-4" style="cursor:pointer" @click="setSort('personalData.fullName')">
                    Full Name <span v-if="sortField === 'personalData.fullName'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
                </div>
                <div class="col-lg-3 col-md-3 col-sm-3">Roles</div>
                <div class="col-lg-3 col-md-3 col-sm-1"></div>
            </div>

            <div
                v-for="user in users"
                :key="user.id"
                class="row border-2 border rounded m-2 p-1 p-md-2 justify-content-center align-items-center text-center"
            >
                <div class="col-lg-3 col-md-3 col-sm-4">
                    <strong>{{ user.username }}</strong>
                </div>
                <div class="col-lg-3 col-md-3 col-sm-4">{{ user.personal_data?.full_name }}</div>
                <div class="col-lg-3 col-md-3 col-sm-3">
                    <span v-for="role in user.roles" :key="role.id" class="badge bg-secondary me-1">{{ role.name }}</span>
                </div>
                <div class="col-lg-3 col-md-3 col-sm-1 text-end">
                    <RouterLink :to="'/admin/users/user_edit/' + user.id" class="btn btn-outline-success btn-sm me-1">Edit</RouterLink>
                    <button
                        @click.prevent="setUserToDelete(user.id, user.username)"
                        type="button"
                        data-bs-toggle="modal"
                        data-bs-target="#deleteUserModal"
                        class="btn btn-danger btn-sm"
                        :class="{ disabled: currentUser(user.id) }"
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

    <div class="modal fade" id="deleteUserModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Are you sure?</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    You are about to delete <strong>{{ deleteUserName }}</strong> (ID: {{ deleteUserId }}).
                </div>
                <div class="modal-footer">
                    <button @click.prevent="deleteUser(deleteUserId, deleteUserName)" type="button" class="btn btn-danger" data-bs-dismiss="modal">Delete</button>
                    <button @click.prevent="unsetUserToDelete()" type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>
</template>
