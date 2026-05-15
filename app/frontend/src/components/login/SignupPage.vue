<script setup lang="ts">
import { ref } from 'vue'
import logo from '@/assets/media/logo.svg'
import { RouterLink } from 'vue-router'
import { apiClient } from '@/services/api'
import displayAlert from '@/components/generic/displayAlert.vue'

const username = ref('')
const password = ref('')
const fullName = ref('')
const email = ref('')
const rePassword = ref('')

const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')

function showAlert(message: string, type: string, timeout = 3000) {
  alertMessage.value = message
  alertType.value = type
  alertTimeout.value = timeout
}

const userSignup = async () => {
  if (password.value !== rePassword.value) {
    showAlert(
      '<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i><strong>Failed to signup!</strong></h4><hr><p class=mb-0>Passwords don\'t match.</p>',
      'danger',
      6000
    )
    return
  }

  const response = await apiClient.users.signup({
    username: username.value,
    password: password.value,
    full_name: fullName.value,
    email: email.value,
  })

  if (response.success) {
    showAlert('<strong>Signup request submitted.</strong>', 'success')
    username.value = ''
    password.value = ''
    fullName.value = ''
    email.value = ''
    rePassword.value = ''
  } else {
    showAlert(
      `<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i><strong>Failed to signup!</strong></h4><hr><p class=mb-0>${response.error.message ?? 'Unknown error'}</p>`,
      'danger',
      6000
    )
  }
}
</script>

<template>
  <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />
  <div class="container text-center pt-5">
    <div class="row justify-content-center align-items-center align-self-center">
      <div class="col-lg-4 col-sm-7 col-11">
        <img alt="Vue logo" class="logo" :src="logo" width="125" height="125" />
        <h2 class="mb-4">Signup to use Arbitaja</h2>

        <form @submit.prevent="userSignup" class="text-start">
          <div class="row">
            <label for="fullName">Full Name:</label>
            <input type="text" class="form-control" v-model="fullName" placeholder="John Doe" required>
          </div>
          <div class="row">
            <label for="email">Email Address:</label>
            <input type="email" class="form-control" v-model="email" placeholder="user@example.com" required>
          </div>
          <div class="row">
            <label for="signupUsername">Username:</label>
            <input id="signupUsername" type="text" class="form-control" v-model="username" placeholder="Username" required>
          </div>
          <div class="row">
            <label for="signupPassword">Password:</label>
            <input id="signupPassword" type="password" class="form-control" v-model="password" placeholder="Password" required>
          </div>
          <div class="row">
            <label for="password">Re-type Password:</label>
            <input type="password" class="form-control" v-model="rePassword" placeholder="Re-type Password" required>
          </div>
          <div class="row text-center">
            <div class="col">
              <button class="btn btn-success m-1" type="submit">Send Signup Request</button>
              <RouterLink class="btn btn-outline-dark m-1" to="/home">Back Home</RouterLink>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>

input {
  margin-bottom: 1rem;
  padding: 1rem;
  border-radius: 1rem;
}

</style>
