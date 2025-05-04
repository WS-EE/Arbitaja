<script setup>
import { ref } from 'vue';

import logo from '@/assets/media/logo.svg';
import { RouterLink } from "vue-router";
import axios from 'axios';


const username = ref("");
const password = ref("");
const fullName = ref("");
const email = ref("");
const rePassword = ref("");


const userSignup = async () => {
  try{

    // check if passwords match
    if (password.value === rePassword.value){
      // Try to signup user
      const response = await axios.post('user/signup/create', {
        username: username.value,
        salted_password: password.value,
        personal_data: {
          full_name: fullName.value,
          email: email.value
        }
      })

      // If user is created
      if (response.status === 200){
        // show user is created
        showAlert('<strong>' + response.data.message + '</strong>', 'success')
        // empty fields after success
        username.value = ''
        password.value = ''
        fullName.value = ''
        email.value = ''
        rePassword.value = ''
      }

    // if passwords don't match show alert
    } else {
      showAlert(
        '<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i><strong>Failed to signup!</strong></h4><hr><p class=mb-0>Error: Passwords didn\'t  match.</p>',
        'danger',
        6000
      )
    }

  // If error in request made
  } catch(error) {
    showAlert(
      '<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i><strong>Failed to signup!</strong></h4><hr><p class=mb-0>Error: ' + 
      error + 
      '</p><p class=mb-0><strong>' + error.response.data.error + '</strong></p>',
      'danger',
      6000
    )
  }
}

// Alert function
const alertTimeout = ref(3000)
const alertMessage = ref('')
const alertType = ref('')

import displayAlert from '@/components/generic/displayAlert.vue';

function showAlert(message, type, timeout){
    alertMessage.value = message
    alertType.value = type
    alertTimeout.value = timeout
}
</script>

<template>
  <!-- Alert when needed -->
  <displayAlert :message="alertMessage" :type="alertType" :timeout="alertTimeout" />
  <!-- Start Main body -->
  <div class="container text-center pt-5">
    <div class="row justify-content-center align-items-center align-self-center">
      <div class="col-lg-4 col-sm-7 col-11">
        <!-- Logo and header -->
        <img alt="Vue logo" class="logo" :src="logo" width="125" height="125" />
        <h2 class="mb-4">Signup to use Arbitaja</h2>

        <!-- Form -->
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
            <input id=signupUsername type="text" class="form-control" v-model="username" placeholder="Username" required>
          </div>
          <div class="row">
            <label for="signupPassword">Password:</label>
            <input id=signupPassword type="password" class="form-control" v-model="password" placeholder="Password" required>
          </div>
          <div class="row">
            <label for="password">Re-type Password:</label>
            <input type="password" class="form-control" v-model="rePassword" placeholder="Re-type Password" required>
          </div>
          <!-- Buttons -->
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
