<script setup>
import { ref } from 'vue';

import logo from '@/assets/media/logo.svg';
import {useRouter} from "vue-router";

const router = useRouter();
const username = ref("");
const password = ref("");

const userLogin = async () => {
  const response = await fetch('http://localhost/api/v1/login-user', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: new URLSearchParams({
      username: username.value,
      password: password.value,
    }),
    credentials: 'include',
  });
  if (response.ok) {
    console.log("Login successful");
    await router.replace("home")
  } else {
    console.log("Login failed");
  }
};
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
            <button class="btn btn-dark" type="submit">Login</button>
          </form>
        </div>
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

</style>
