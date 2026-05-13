<script setup lang="ts">

import { onMounted, ref, computed } from 'vue';
import { ensureAuthRehydrated } from '@/composables/useAuthRehydrate';
import { useUserStore } from '@/stores/userStore';
import { apiClient, RoleResponse } from '@/services/api';
import displayAlert from '@/components/generic/displayAlert.vue';

const allRoles = ref<RoleResponse[]>([]);
const isLoadingRoles = ref<boolean>(true);
const curUserId = ref<number | undefined>();
const deleteRoleId = ref<number | undefined>();

// Alert state
const alertTimeout = ref<number>(3000);
const alertMessage = ref<string>('');
const alertType = ref<string>('');

// Computed property for the role to be deleted
const roleToDelete = computed<RoleResponse>(() => {
  return allRoles.value.find(role => role.id === deleteRoleId.value) || { id: deleteRoleId.value || 0, name: 'Unknown Role', permissions: [] };
});

const deleteRoleName = computed<string>(() => {
  return roleToDelete.value?.name || 'Unknown Role';
});

const getAllRoles = async (): Promise<void> => {
  try {
    const response = await apiClient.roles.list();
    if(!response.success) {
      throw new Error(response.error.message || 'Unknown error');
    }
    allRoles.value = response.data;
  } catch(error) {
    showAlert(`Couldn't get data for Roles. <br> Error: ${error}`, 'danger', 9000);
  }
};

const deleteRole = async (roleId?: number, roleName?: string): Promise<void> => {
  try {
    if(roleId === undefined) {
      throw new Error('Role ID is undefined');
    }
    await apiClient.roles.delete(roleId);
    await getAllRoles();
    showAlert(`Role "${roleName}" deleted successfully.`, 'success', 3000);
  } catch(error) {
    showAlert(`Couldn't delete role. <br> Error: ${error}`, 'danger', 9000);
  }
};

const unsetRoleToDelete = (): void => {
  deleteRoleId.value = undefined;
};

const setRoleToDelete = (id?: number): void => {
  deleteRoleId.value = id;
};

const currentUser = (id?: number): boolean => {
  return id === curUserId.value;
};

const showAlert = (message: string, type: string, timeout: number): void => {
  alertMessage.value = message;
  alertType.value = type;
  alertTimeout.value = timeout;
};

onMounted(async () => {
  try {
    await getAllRoles();

    await ensureAuthRehydrated({ force: true });
    currentUser(useUserStore().id);
  } catch(error) {
    showAlert(`Something went wrong. <br> Error: ${error}`, 'danger', 9000);
  } finally {
    isLoadingRoles.value = false;
  }
});
</script>



<template>
  <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />

  <!-- Main content-->
  <div class="container">
    <div class="row">
      <div class="col-lg-9 col-sm-12">
        <!-- Tabel Header -->
        <div class="row border-5 border rounded m-2 p-1 p-md-2 p-lg-3 justify-content-center align-items-center text-center">
          <div class="col-lg-2 col-md-3 col-sm-4">
            <h5 class="m-0">Role Id</h5>
          </div>
          <div class="col-lg-2 col-md-3 col-sm-4">
            <p class="m-0">Role Name</p>
          </div>
          <div class="col-lg-4 col-md-3 col-sm-3 text-lg-start text-sm-center">
            <p class="m-0">Permission Count</p>
          </div>
          <div class="col-lg-4 col-md-4 col-sm-5 ms-lg-auto text-center text-lg-end pt-2 pt-md-0"></div>
        </div>
        <div v-for="role in allRoles" class="row border-2 border rounded m-2 p-1 p-md-2 p-lg-3 justify-content-center align-items-center text-center">
          <div class="col-lg-2 col-md-3 col-sm-4">
            <h5 class="m-0">{{ role.id }}</h5>
          </div>
          <div class="col-lg-2 col-md-3 col-sm-4">
            <p class="m-0">{{ role.name }}</p>
          </div>
          <div class="col-lg-4 col-md-3 col-sm-3">
            <p class="m-0">{{ !role.permissions ? 0 : role.permissions.length }}</p>
          </div>
          <div class="col-lg-4 col-md-4 col-sm-5 ms-lg-auto text-center text-lg-end pt-2 pt-md-0">
            <button
                @click.prevent="setRoleToDelete(role.id)"
                type="button" data-bs-toggle="modal" data-bs-target="#deleteUser"
                class="btn btn-danger"
                :class="[currentUser(role.id)? 'disabled' : '',]">
              Delete
            </button>
          </div>
        </div>
      </div>
      <div class="col-lg-2 d-none d-lg-block border-5 border rounded m-2 p-1 p-md-2 p-lg-3 justify-content-center align-items-center text-center">
        <h5>All Roles</h5>
      </div>
    </div>
  </div>
  <!-- Delete role modal -->
  <div
      class="modal fade"
      id="deleteUser"
      tabindex="-1"
      role="dialog"
      aria-labelledby="deleteRole"
      aria-hidden="true"
  >
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="modalTitleId">
            Are you sure?
          </h5>
          <button
              type="button"
              class="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
          ></button>
        </div>
        <div class="modal-body">
          <div class="container-fluid">You are about to delete <strong>{{ deleteRoleName }}</strong> with an <strong>ID of {{ deleteRoleId }}</strong></div>
        </div>
        <div class="modal-footer">
          <button @click.prevent="deleteRole(deleteRoleId, deleteRoleName)" type="button" class="btn btn-danger" data-bs-dismiss="modal">Delete</button>
          <button @click.prevent="unsetRoleToDelete()" type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>