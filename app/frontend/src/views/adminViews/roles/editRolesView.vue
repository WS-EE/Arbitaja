<script setup lang="ts">
import { onMounted, ref } from "vue";
import { apiClient } from "@/services/api";
import type { PermissionResponse } from "@/services/api";
import { useRoute, useRouter } from "vue-router";
import displayAlert from '@/components/generic/displayAlert.vue';

const route = useRoute();
const router = useRouter();
const roleId = Number(route.params.id);

const roleName = ref('');
const allPermissions = ref<PermissionResponse[]>([]);
const selectedPermissionIds = ref<number[]>([]);

const alertTimeout = ref<number>(3000);
const alertMessage = ref<string>('');
const alertType = ref<string>('');
const alertTrigger = ref(0);

function showAlert(message: string, type: string, timeout: number = 3000) {
  alertMessage.value = message;
  alertType.value = type;
  alertTimeout.value = timeout;
  alertTrigger.value++;
}

const isSelected = (id?: number) => id !== undefined && selectedPermissionIds.value.includes(id);

const togglePermission = (id?: number) => {
  if (id === undefined) return;
  const idx = selectedPermissionIds.value.indexOf(id);
  if (idx >= 0) {
    selectedPermissionIds.value.splice(idx, 1);
  } else {
    selectedPermissionIds.value.push(id);
  }
};

onMounted(async () => {
  try {
    const allPermissionsResponse = await apiClient.permissions.list();
    if (!allPermissionsResponse.success) throw new Error(allPermissionsResponse.error.message || 'Unknown error');
    allPermissions.value = allPermissionsResponse.data;

    const roleResponse = await apiClient.roles.byId(roleId);
    if (!roleResponse.success) throw new Error(roleResponse.error.message || 'Unknown error');
    roleName.value = roleResponse.data.name ?? '';

    selectedPermissionIds.value = roleResponse.data.permissions
      .map(permKey => allPermissions.value.find(ap => ap.key === permKey)?.id)
      .filter((id): id is number => id !== undefined);
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

const savePermissions = async () => {
  try {
    const response = await apiClient.roles.overwriteRolePermissions(roleId, { permissionIds: selectedPermissionIds.value });
    if (!response.success) throw new Error(response.error.message || 'Unknown error');
    showAlert('Permissions updated successfully.', 'success', 3000);
  } catch (error) {
    showAlert(`Couldn't update permissions. Error: ${error}`, 'danger', 9000);
  }
};
</script>

<template>
  <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" :trigger="alertTrigger" />
  <div class="container mt-3">
    <h3>Edit Role</h3>

    <div class="mb-4 mt-3">
      <label class="form-label fw-semibold">Role Name</label>
      <div class="d-flex gap-2">
        <input v-model="roleName" type="text" class="form-control" placeholder="Role name" />
        <button class="btn btn-primary" @click.prevent="saveRoleName">Save Name</button>
      </div>
    </div>

    <div class="mb-4">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <label class="form-label fw-semibold mb-0">Permissions</label>
        <span class="text-muted small">{{ selectedPermissionIds.length }} / {{ allPermissions.length }} selected</span>
      </div>

      <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-2">
        <div v-for="permission in allPermissions" :key="permission.id" class="col">
          <div
            class="d-flex align-items-center gap-2 p-2 border rounded"
            :class="isSelected(permission.id) ? 'border-success bg-success-subtle' : 'border-secondary-subtle'"
            style="cursor: pointer"
            @click="togglePermission(permission.id)"
          >
            <input
              type="checkbox"
              class="form-check-input flex-shrink-0 mt-0"
              :checked="isSelected(permission.id)"
              @click.prevent
            />
            <div class="lh-sm" style="word-break: break-word">
              <span class="d-block fw-medium small">{{ permission.name }}</span>
              <span class="d-block text-muted" style="font-size: 0.7rem">{{ permission.key }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-3">
        <button class="btn btn-success" @click.prevent="savePermissions">Save Permissions</button>
      </div>
    </div>

    <div class="d-flex justify-content-end mt-3 mb-4">
      <button class="btn btn-outline-dark" @click="router.back()">Go Back</button>
    </div>
  </div>
</template>
