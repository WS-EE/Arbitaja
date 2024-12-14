<script setup>
import { RouterView } from 'vue-router';
import navBar from '@/components/generic/navBar.vue';
import { onMounted } from 'vue';
import axios from 'axios';

// import cookie handler
import { useCookies } from '@/assets/js/useCookies';
const $cookies = useCookies(); 

onMounted(async () => {
  try {
    const response = await axios.get('http://localhost/api/v1/profile')
    if(response.status === 200 && response.data.username !== 'anonymousUser'){
      // Set user to be logged in
      await $cookies.set('isLoggedIn', true, 0);
      await $cookies.set('userParameters', response.data, 0);
    }
  } catch(error) {
    console.log('Error:', error)
  };
});
</script>

<template>
  <navBar />
  <RouterView />
</template>