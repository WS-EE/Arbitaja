<script setup lang="ts">
import { onMounted, ref } from "vue";
import { apiClient, PermissionResponse, RoleResponse } from "@/services/api";
import { useRoute } from "vue-router";
import Listbox from 'primevue/listbox';
import Button from 'primevue/button';


const route = useRoute();

const roleId = Number(route.params.id)

const allRoles = ref<RoleResponse[]>([]);
const allPermissions = ref<PermissionResponse[]>([]);
const rolePermissions = ref<PermissionResponse[]>([]);

const alertTimeout = ref<number>(3000);
const alertMessage = ref<string>('');
const alertType = ref<string>('');

onMounted(async () => {
  try {
    const allPermissionsResponse = await apiClient.permissions.list();
    if (!allPermissionsResponse.success) {
      throw new Error(allPermissionsResponse.error.message || 'Unknown error');
    }
    allPermissions.value = allPermissionsResponse.data;

    const rolePermissionsResponse = await apiClient.roles.byId(roleId);
    if (!rolePermissionsResponse.success) {
      throw new Error(rolePermissionsResponse.error.message || 'Unknown error');
    }
    rolePermissions.value = rolePermissionsResponse.data.permissions.map(p => allPermissions.value.find(ap => ap.key === p)!).filter(p => p !== undefined);


    const roleResponse = await apiClient.roles.list();
    if (!roleResponse.success) {
      throw new Error(roleResponse.error.message || 'Unknown error');
    }
    allRoles.value = roleResponse.data;
  } catch (error) {
    showAlert(`Couldn't get data. Error: ${error}`, 'danger', 9000);
  }
})


const overWriteRolePermissions = async (): Promise<void> => {
  try {
    const response = await apiClient.roles.overwriteRolePermissions(roleId,
        {
          permissionIds: rolePermissions.value.map(p => p.id)
        });
    if(!response.success){
      throw new Error(response.error.message || 'Unknown error');
    }
    showAlert('Permissions updated successfully.', 'success', 3000);
  } catch (error) {
    showAlert(`Couldn't update permissions. Error: ${error}`, 'danger', 9000);
  }
};

const showAlert = (message: string, type: string, timeout: number): void => {
  alertMessage.value = message;
  alertType.value = type;
  alertTimeout.value = timeout;
};

</script>

<template>
  <div class="container">
    <h2 class="text-2xl font-bold mb-4">Edit Permissions for Role {{ roleId }}</h2>
    <div class="mb-4">
      <span class="text-gray-700">Assigned Permissions:</span>
      <Listbox v-model="rolePermissions" :options="allPermissions" checkmark  multiple optionLabel="name" class="w-full md:w-56" />
      <Button label="Submit" @click.prevent="overWriteRolePermissions" />
    </div>
  </div>
</template>