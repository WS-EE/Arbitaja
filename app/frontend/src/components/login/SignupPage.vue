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
    if (password.value === rePassword.value){
      const response = await axios.post('user/signup/create', {
        username: username.value,
        salted_password: password.value,
        personal_data: {
          full_name: fullName.value,
          email: email.value
        }
      })
      if (response.status === 200){
        displayAlert('<strong>' + response.data.message + '</strong>', 'success')
      }
    } else {
      displayAlert(
      '<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i><strong>Failed to signup!</strong></h4><hr><p class=mb-0>Error: Passwords didn\'t  match.</p>',
      'danger',
      6000
    )
    }
  } catch(error) {
    displayAlert(
      '<h4 class=alert-heading><i class="me-2 bi bi-exclamation-triangle"></i><strong>Failed to signup!</strong></h4><hr><p class=mb-0>Error: ' + 
      error + 
      '</p><p class=mb-0><strong>' + error.response.data.error + '</strong></p>',
      'danger',
      6000
    )
    console.log(error)
  }
}

// Alert function
const showAlert = ref(false)
const alertMessage = ref('')
const alertType = ref('')

function displayAlert(message, type, timeout){
    // Set default type to primary(info)
    if (timeout === undefined){
        timeout = 3000;
    }
    if (type === undefined){
        type = 'primary';
    }

    // Set the alert type
    alertType.value = 'alert-' + type

    // Tell user that changes were discarded
    showAlert.value = true

    alertMessage.value = message
    // Fade out alert after 3000ms
    if (timeout !== 0) {
        setTimeout(() => {
            showAlert.value = false
        }, timeout);
    }
}
</script>

<template>
  <!-- Alert when needed -->
  <div class="container text-center fixed-top">
    <div class="row justify-content-center align-items-center align-self-center">
      <Transition class="m-2 col-10 col-md-8 col-lg-6" name="alert">
        <div
            v-if="showAlert"
            class="alert alert-sizes align-self-center"
            :class="alertType"
            role="alert"
        >
          <span v-html="alertMessage"></span>
        </div>
      </Transition>
    </div>
  </div>
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
              <RouterLink class="btn btn-outline-dark m-1" to="/login">Back to login</RouterLink>
              <RouterLink class="btn btn-outline-danger m-1" to="/home">Back Home</RouterLink>
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
