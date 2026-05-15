<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { apiClient, SchoolResponse } from '@/services/api'
import type { SignupResponse } from '@/services/api'
import { usePagedList } from '@/composables/usePagedList'
import displayAlert from '@/components/generic/displayAlert.vue'
import PaginationControls from '@/components/generic/PaginationControls.vue'
import approveModal from './userApproveModal.vue'

const schools = ref<SchoolResponse[]>([])

const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')

function showAlert(message: string, type: string, timeout = 3000) {
  alertMessage.value = message
  alertType.value = type
  alertTimeout.value = timeout
}

const { items: signupUsers, search, page, pageSize, totalElements, totalPages, isLoading, error,
        sortField, sortDir, load, onSearchInput, goToPage, setSort } =
    usePagedList<SignupResponse>((params) => apiClient.users.signupList(params), 'username,asc')

const getSchools = async () => {
  try {
    const response = await apiClient.schools.list({ size: 500 })
    if (!response.success) throw new Error(response.error.message || 'Unknown error')
    schools.value = response.data.content
  } catch (error) {
    showAlert('Couldn\'t get schools. Error: ' + error, 'danger', 9000)
  }
}

const onApproveSignupUser = async () => {
  await load()
}

onMounted(async () => {
  try {
    await load()
    await getSchools()
  } catch (err) {
    showAlert('Something went wrong. Error: ' + err, 'danger', 9000)
  }
})
</script>

<template>
  <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />
  <div class="container">
    <div class="row mb-3 mt-2">
      <div class="col">
        <h3>Signup Requests</h3>
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

    <div class="row border-5 border rounded m-2 p-1 p-md-2 justify-content-center align-items-center text-center fw-bold user-select-none">
      <div class="col-lg-3 col-sm-4" style="cursor:pointer" @click="setSort('username')">
        Username <span v-if="sortField === 'username'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
      </div>
      <div class="col-lg-3 col-sm-3" style="cursor:pointer" @click="setSort('personalData.fullName')">
        Full Name <span v-if="sortField === 'personalData.fullName'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
      </div>
      <div class="col-lg-2 d-none d-lg-block" style="cursor:pointer" @click="setSort('createdAt')">
        Submitted <span v-if="sortField === 'createdAt'">{{ sortDir === 'asc' ? '↑' : '↓' }}</span>
      </div>
      <div class="col-lg-4 col-sm-5 text-end">Actions</div>
    </div>

    <div v-if="isLoading" class="text-center p-4">Loading...</div>
    <approveModal
      v-else
      :users="signupUsers"
      :schools="schools"
      @approveSignupUser="onApproveSignupUser"
    />

    <PaginationControls
      :page="page"
      :total-pages="totalPages"
      :total-elements="totalElements"
      :page-size="pageSize"
      @go-to-page="goToPage"
      @update:page-size="pageSize = $event"
    />
  </div>
</template>
