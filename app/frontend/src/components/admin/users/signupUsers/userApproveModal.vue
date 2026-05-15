<script setup lang="ts">
import { ref, computed } from 'vue'
import { DateTime } from 'luxon'
import { apiClient, SignupResponse, SignupRequest, SchoolResponse } from '@/services/api'
import displayAlert from '@/components/generic/displayAlert.vue'

const props = defineProps<{
  users: SignupResponse[]
  schools: SchoolResponse[]
}>()

const emit = defineEmits(['approveSignupUser'])

const signupUsers = ref<SignupResponse[]>(props.users)
const allSchools = computed(() => props.schools)

const commitedUserData = ref<SignupResponse>({} as SignupResponse)
const commitSet = ref(false)

const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')
const alertTrigger = ref(0)

function showAlert(message: string, type: string, timeout = 3000) {
  alertMessage.value = message
  alertType.value = type
  alertTimeout.value = timeout
  alertTrigger.value++
}

const selectedSchoolName = computed(() => {
  const match = allSchools.value.find(s => s.id === commitedUserData.value.schoolId)
  return match?.name ?? 'Select school'
})

function setCommitedUserData(user: SignupResponse) {
  commitSet.value = true
  commitedUserData.value = user
}

function deleteCommitedUserData() {
  commitSet.value = false
  commitedUserData.value = {} as SignupResponse
}

function changeSchool(id?: number) {
  if (commitedUserData.value) {
    commitedUserData.value.schoolId = id
  }
}

const formatDate = (iso?: string) => {
  if (!iso) return '—'
  return DateTime.fromISO(iso).toFormat('dd MMM yyyy HH:mm')
}

const buildSignupPayload = (): SignupRequest => {
  if (!commitedUserData.value) {
    throw new Error('No user data to build payload')
  }
  return {
    username: commitedUserData.value.username,
    email: commitedUserData.value.email,
    full_name: commitedUserData.value.fullName,
    school_id: commitedUserData.value.schoolId,
  }
}

const approveUser = async () => {
  try {
    if (commitedUserData.value.userId === undefined) {
      throw new Error('User ID is undefined')
    }
    await apiClient.users.approveSignup(commitedUserData.value.userId, buildSignupPayload())
    showAlert('User ' + commitedUserData.value.username + ' has been approved.', 'success')
    emit('approveSignupUser')
  } catch (error) {
    showAlert('Couldn\'t approve user. <br> Error: ' + error, 'danger')
  }
}

const deleteUser = async () => {
  try {
    if (commitedUserData.value.userId === undefined) {
      throw new Error('User ID is undefined')
    }
    await apiClient.users.declineSignup(commitedUserData.value.userId)
    showAlert('User ' + commitedUserData.value.username + ' has been deleted.', 'success')
    emit('approveSignupUser')
  } catch (error) {
    showAlert('Couldn\'t delete user. <br> Error: ' + error, 'danger')
  }
}
</script>

<template>
  <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" :trigger="alertTrigger" />
  <div v-for="user in signupUsers" :key="user.userId" class="row border rounded m-2 p-1 p-md-2 text-center justify-content-center align-items-center">
    <div class="col-lg-3 col-sm-4">
      <strong>{{ user.username }}</strong>
    </div>
    <div class="col-lg-3 col-sm-3">
      {{ user.fullName }}
    </div>
    <div class="col-lg-4 col-sm-5 text-end">
      <button @click.prevent="setCommitedUserData(user)" type="button" class="me-2 btn btn-success btn-sm" data-bs-toggle="modal" data-bs-target="#ApproveModal">Accept</button>
      <button @click.prevent="setCommitedUserData(user)" type="button" data-bs-toggle="modal" data-bs-target="#DeleteModal" class="btn btn-danger btn-sm">Decline</button>
    </div>
  </div>
  <!-- Modal for accept -->
  <div class="modal fade" id="ApproveModal" tabindex="-1" aria-labelledby="ApproveModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div v-if="commitSet" class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title fs-5" id="exampleModalLabel">Are you sure?</h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="row">
            <h5>ID: {{ commitedUserData.userId }}</h5>
          </div>
          <div class="row">
            <div class="col-4">Username:</div>
            <div class="col">
              <input type="text" class="rounded p-1 form-control" v-model="commitedUserData.username">
            </div>
          </div>
          <div class="row mt-2">
            <div class="col-4">E-Mail:</div>
            <div class="col">
              <input type="text" class="rounded p-1 form-control" v-model="commitedUserData.email">
            </div>
          </div>
          <div class="row mt-2">
            <div class="col-4">Full name:</div>
            <div class="col">
              <input type="text" class="rounded p-1 form-control" v-model="commitedUserData.fullName">
            </div>
          </div>
          <div class="row mt-2">
            <div class="col-4">School:</div>
            <div class="col">
              <div class="dropdown">
                <button type="button" class="btn btn-outline-dark dropdown-toggle" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                  {{ selectedSchoolName }}
                </button>
                <ul class="dropdown-menu">
                  <li v-for="school in allSchools" :key="school.id">
                    <button class="dropdown-item" type="button" @click="changeSchool(school.id)">
                      {{ school.name }}
                    </button>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" @click.prevent="approveUser()" class="btn btn-success" data-bs-dismiss="modal">Approve</button>
          <button @click.prevent="deleteCommitedUserData()" type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>
  <!-- Modal for delete -->
  <div class="modal fade" id="DeleteModal" tabindex="-1" aria-labelledby="DeleteModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div v-if="commitSet" class="modal-content">
        <div class="modal-header">
          <h1 class="modal-title fs-5" id="exampleModalLabel">Are you sure?</h1>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="row">
            <h5>ID: {{ commitedUserData.userId }}</h5>
          </div>
          <div class="row">
            <div class="col-4">Username:</div>
            <div class="col">
              <p class="rounded form-control">{{ commitedUserData.username }}</p>
            </div>
          </div>
          <div class="row mt-2">
            <div class="col-4">E-Mail:</div>
            <div class="col">
              <p class="rounded form-control">{{ commitedUserData.email }}</p>
            </div>
          </div>
          <div class="row mt-2">
            <div class="col-4">Full name:</div>
            <div class="col">
              <p class="rounded form-control">{{ commitedUserData.fullName }}</p>
            </div>
          </div>
          <div class="row mt-2">
            <div class="col-4">School:</div>
            <div class="col">
              <p class="rounded form-control">{{ selectedSchoolName }}</p>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" @click.prevent="deleteUser()" class="btn btn-danger" data-bs-dismiss="modal">Delete</button>
          <button @click.prevent="deleteCommitedUserData()" type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>
