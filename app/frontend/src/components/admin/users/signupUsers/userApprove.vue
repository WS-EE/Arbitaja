<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { apiClient, SignupResponse, SchoolResponse } from '@/services/api'
import PulseLoader from 'vue-spinner/src/PulseLoader.vue'
import displayAlert from '@/components/generic/displayAlert.vue'
import approveModal from './userApproveModal.vue'
import addSchool from '../school/addSchool.vue'
import allSchools from '../school/allSchools.vue'

const signupUsers = ref<SignupResponse[]>([])
const schools = ref<SchoolResponse[]>([])
const isLoadingUsers = ref(true)

const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')

function showAlert(message: string, type: string, timeout = 3000) {
  alertMessage.value = message
  alertType.value = type
  alertTimeout.value = timeout
}

const getSignupUsers = async () => {
  try {
    const response = await apiClient.users.signupList()
    if (!response.success) {
      throw new Error(response.error.message || 'Unknown error')
    }
    signupUsers.value = response.data
  } catch (error) {
    showAlert('Couldn\'t get data for signup Users. <br> Error: ' + error, 'danger', 9000)
  }
}

const getSchools = async () => {
  try {
    const response = await apiClient.schools.list()
    if (!response.success) {
      throw new Error(response.error.message || 'Unknown error')
    }
    schools.value = response.data
  } catch (error) {
    showAlert('Couldn\'t get data for all the schools. <br> Error: ' + error, 'danger', 9000)
  }
}

onMounted(async () => {
  try {
    await getSignupUsers()
    await getSchools()
  } catch (error) {
    showAlert('Something went wrong. <br> Error:' + error, 'danger', 9000)
  } finally {
    isLoadingUsers.value = false
  }
})

const limitSchools = ref(7)
const showMoreSchools = ref(true)

function moreSchools() {
  limitSchools.value = 0
  showMoreSchools.value = false
}

function lessSchools() {
  limitSchools.value = 7
  showMoreSchools.value = true
}

const onApproveSignupUser = async () => {
  isLoadingUsers.value = true
  await getSignupUsers()
  isLoadingUsers.value = false
}

const onAddSchool = async () => {
  await getSchools()
}
</script>

<template>
  <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />
  <div class="container">
    <div class="row">
      <div class="col-lg-9 col-sm-12">
        <div class="row border-5 border rounded m-2 p-1 p-md-2 p-lg-3 justify-content-center align-items-center text-center">
          <div class="col-lg-2 col-md-3 col-sm-4">
            <h5 class="m-0">Username</h5>
          </div>
          <div class="col-lg-3 col-md-2 col-sm-3">
            <p class="m-0">Full Name</p>
          </div>
          <div class="col-lg-3 col-md-4 col-sm-5 ms-auto text-center text-lg-end">
            <addSchool modalId="lg-Modal" @addSchool="onAddSchool" />
          </div>
        </div>
        <div v-if="isLoadingUsers" class="position-fixed top-50 start-50">
          <PulseLoader />
        </div>
        <approveModal v-else :users="signupUsers" :schools="schools" @approveSignupUser="onApproveSignupUser" />
      </div>
      <div class="col-lg-2 d-none d-lg-block border-5 border rounded m-2 p-1 p-md-2 p-lg-3 justify-content-center align-items-center text-center">
        <div class="row">
          <div class="col">
            <h4>Schools</h4>
          </div>
        </div>
        <div class="row">
          <div v-if="isLoadingUsers" class="position-static">
            <PulseLoader />
          </div>
          <allSchools v-else :schools="schools" :addDelete="false" :limitItems="limitSchools" />
        </div>
        <div class="row text-center">
          <div class="col">
            <p v-if="showMoreSchools" class="btn btn-dark mt-2" @click.prevent="moreSchools()">More schools<i class="ms-1 bi bi-arrow-down"></i></p>
            <p v-else class="btn btn-dark mt-2" @click.prevent="lessSchools()">Less schools<i class="ms-1 bi bi-arrow-up"></i></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
