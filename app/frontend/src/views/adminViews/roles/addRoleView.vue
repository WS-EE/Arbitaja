<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { apiClient, PermissionResponse, CreateRoleRequest } from '@/services/api';
import displayAlert from '@/components/generic/displayAlert.vue';
import Listbox from 'primevue/listbox';
import Button from 'primevue/button';

const router = useRouter();

const roleName = ref('');
const allPermissions = ref<PermissionResponse[]>([]);
const selectedPermissions = ref<PermissionResponse[]>([]);

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
        const response = await apiClient.permissions.list();
        if (!response.success) throw new Error(response.error.message || 'Unknown error');
        allPermissions.value = response.data;
    } catch (e) {
        showAlert('Could not load permissions. Error: ' + e, 'danger', 9000);
    }
});

const submit = async () => {
    try {
        if (!roleName.value.trim()) {
            showAlert('Role name is required.', 'danger');
            return;
        }
        const payload: CreateRoleRequest = {
            name: roleName.value,
            permissionIds: selectedPermissions.value.map(p => p.id),
        };
        const response = await apiClient.roles.create(payload as any);
        if (!response.success) throw new Error(response.error.message || 'Unknown error');
        showAlert('Role "' + roleName.value + '" created successfully.', 'success');
        setTimeout(() => router.push('/admin/users/role'), 1000);
    } catch (e) {
        showAlert('Could not create role. Error: ' + e, 'danger', 9000);
    }
};
</script>

<template>
    <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" :trigger="alertTrigger" />
    <div class="container mt-3">
        <h3>Add Role</h3>
        <form @submit.prevent="submit" class="mt-3">
            <div class="mb-3 row">
                <label class="col-sm-3 col-form-label">Role Name</label>
                <div class="col-sm-9">
                    <input v-model="roleName" type="text" class="form-control" required placeholder="Role name" />
                </div>
            </div>
            <div class="mb-3">
                <label class="form-label">Permissions</label>
                <Listbox
                    v-model="selectedPermissions"
                    :options="allPermissions"
                    checkmark
                    multiple
                    optionLabel="name"
                    class="w-full"
                />
            </div>
            <div class="d-flex justify-content-end gap-2 mb-4">
                <button type="button" class="btn btn-outline-dark" @click="router.back()">Cancel</button>
                <Button label="Create" type="submit" />
            </div>
        </form>
    </div>
</template>
