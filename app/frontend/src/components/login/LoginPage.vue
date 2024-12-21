<script setup>
import { ref } from 'vue';

import logo from '@/assets/media/logo.svg';
import {useRouter} from "vue-router";
import axios from 'axios';

// import cookie handler
import { useCookies } from '@/assets/js/useCookies';
const $cookies = useCookies(); 

const router = useRouter();
const username = ref("");
const password = ref("");
const getLogonError = ref('');
const rememberMe = ref(false);

const userLogin = () => {
  getLogonError.value = false
  const params = new URLSearchParams();
  params.append('username', username.value)
  params.append('rememberMe', rememberMe.value)
  params.append('password', password.value)
  axios.post('login-user', params)
      .then(function (response) {
        if(response.status === 200){
          // Set user to be logged in
          $cookies.set('isLoggedIn', true, 0);
          $cookies.set('userParameters', response.data, 0);
          router.replace('home');
        }
      })
      .catch(function (error) {
        // Log error to console
        console.log(error);

        // Show logon error
        getLogonError.value = true

        // Fade out logon error
        setTimeout(() => {
          getLogonError.value = false
        }, 3000);
      });
}
</script>

<template>
  <div class="container">
    <div class="login-block">
      <div class="row d-flex justify-content-center">
        <img alt="Vue logo" class="logo" :src="logo" width="125" height="125" />
        <h1 class="d-flex justify-content-center">Arbitaja</h1>
        
        <div class="form-group form-width text-center">
          <form @submit.prevent="userLogin">
            <input type="text" class="form-control" v-model="username" placeholder="Username" required>
            <input type="password" class="form-control" v-model="password" placeholder="Password" required>
            <Transition>
              <div
                v-if="getLogonError"
                class="alert alert-danger"
                role="alert"
              >
                <strong>Logon failure</strong>
              </div>
            </Transition>
            <div class="checkbox-div">
              <input class="me-2" type="checkbox" id="rememberMe" v-model="rememberMe">
              <label for="rememberMe">Remember me</label>
            </div>
            <button class="btn btn-dark" type="submit">Login</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.v-enter-active,
.v-leave-active {
  transition: opacity 0.5s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}

input {
  margin-bottom: 1rem;
  padding: 1rem;
  border-radius: 1rem;
}

form {
  align-items: center;

}

.form-width {
  max-width: 300px;
}

.login-block {
  padding-top: 20vh;
  padding-bottom: 20vh;
}

.login-menu {
  align-items: center;
  flex-direction: column;
  display: flex;
}

.checkbox-div {
    display: flex;
    align-content: center;
    justify-content: center;
}

input[type="checkbox"] {
    appearance: checkbox;
    -webkit-appearance: none;
    display: flex;
    align-content: center;
    justify-content: center;
    padding: 0.2rem;
    border: 0.25rem solid var(--text-dark);
    border-radius: 0.5rem;
}

input[type="checkbox"]::before {
    content: "";
    width: 0.8rem;
    height: 0.8rem;
    clip-path: polygon(0 0, 50% 0, 100% 0, 100% 100%, 50% 100%, 0 100%);
    transform: scale(0);
    transition-duration: 100ms;
    background-color: var(--text-dark);
}

input[type="checkbox"]:checked::before {
    transform: scale(2);
    clip-path: polygon(28% 38%, 41% 53%, 75% 24%, 86% 38%, 40% 78%, 15% 50%);
    transition-duration: 100ms;
}

</style>
