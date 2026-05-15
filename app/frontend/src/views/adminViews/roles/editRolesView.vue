<script setup lang="ts">
import { onMounted, ref } from "vue";
import { apiClient, PermissionResponse, CreateRoleRequest } from "@/services/api";
import { useRoute, useRouter } from "vue-router";
import Listbox from 'primevue/listbox';
import Button from 'primevue/button';
import displayAlert from '@/components/generic/displayAlert.vue';

const route = useRoute();
const router = useRouter();
const roleId = Number(route.params.id);

const roleName = ref('');
const allPermissions = ref<PermissionResponse[]>([]);
const rolePermissions = ref<PermissionResponse[]>([]);

const alertTimeout = ref<number>(3000);
const alertMessage = ref<string>('');
const alertType = ref<string>('');

function showAlert(message: string, type: string, timeout: number = 3000) {
  alertMessage.value = message;
  alertType.value = type;
  alertTimeout.value = timeout;
}

onMounted(async () => {
  try {
    const allPermissionsResponse = await apiClient.permissions.list();
    if (!allPermissionsResponse.success) throw new Error(allPermissionsResponse.error.message || 'Unknown error');
    allPermissions.value = allPermissionsResponse.data;

    const roleResponse = await apiClient.roles.byId(roleId);
    if (!roleResponse.success) throw new Error(roleResponse.error.message || 'Unknown error');
    roleName.value = roleResponse.data.name ?? '';
    rolePermissions.value = roleResponse.data.permissions
      .map(p => allPermissions.value.find(ap => ap.key === p)!)
      .filter(p => p !== undefined);
  } catch (error) {
    showAlert(`Couldn't get data. Error: ${error}`, 'danger', 9000);
  }
});

const saveRoleName = async () => {
  try {
    const response = await apiClient.roles.update(roleId, { name: roleName.value, id: roleId });
    if (!response.success) throw new Error(response.error.message || 'Unknown error');
    showAlert('Role name updated.', 'success', 3000);
  } catch (error) {
    showAlert(`Couldn't update role name. Error: ${error}`, 'danger', 9000);
  }
};

const overWriteRolePermissions = async () => {
  try {
    const payload: CreateRoleRequest = { permissionIds: rolePermissions.value.map(p => p.id) };
    const response = await apiClient.roles.overwriteRolePermissions(roleId, { permissionIds: payload.permissionIds });
    if (!response.success) throw new Error(response.error.message || 'Unknown error');
    showAlert('Permissions updated successfully.', 'success', 3000);
  } catch (error) {
    showAlert(`Couldn't update permissions. Error: ${error}`, 'danger', 9000);
  }
};
</script>

<template>
  <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />
  <div class="container mt-3">
    <h3>Edit Role</h3>

    <div class="mb-4 mt-3">
      <label class="form-label fw-semibold">Role Name</label>
      <div class="d-flex gap-2">
        <input v-model="roleName" type="text" class="form-control" placeholder="Role name" />
        <Button label="Save Name" @click.prevent="saveRoleName" />
      </div>
    </div>

    <div class="mb-4">
      <label class="form-label fw-semibold">Permissions</label>
      <Listbox v-model="rolePermissions" :options="allPermissions" checkmark multiple optionLabel="name" class="w-full md:w-56" />
      <div class="mt-2">
        <Button label="Save Permissions" @click.prevent="overWriteRolePermissions" />
      </div>
    </div>

    <div class="d-flex justify-content-end">
      <button class="btn btn-outline-dark" @click="router.back()">Go Back</button>
    </div>
  </div>
</template>
