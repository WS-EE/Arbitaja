<script setup>
import adminNavBar from '@/components/admin/adminNavBar.vue';
import { RouterView } from 'vue-router';

import { onMounted, ref } from 'vue';
import axios from 'axios';

// import cookie handler
import { useCookies } from '@/assets/js/useCookies';
import router from '@/router';
const $cookies = useCookies(); 

// Set isUser admin variable
const isUserAdmin = ref();
const userParameters = ref();

onMounted(async () => {
  try {
    const response = await axios.get('user/profile/get')
    if(response.status === 200 && response.data.username !== 'anonymousUser'){
      // Set user to be logged in
      await $cookies.set('isLoggedIn', true, 0);
      await $cookies.set('userParameters', response.data, 0);
    }
    userParameters.value = await $cookies.get('userParameters') 
    isUserAdmin.value = await checkUserAdmin();

    if (!isUserAdmin.value) {
        await router.replace('/login');
    }

  } catch(error) {
    console.log('Error:', error)
    await router.replace('/login');
  };
});

// is the user admin
const checkUserAdmin = async () => {
    // Get user groups
    try {
        let isTrue = false
        const userGroups = userParameters.value.roles
        // loop over user groups
        for (const role of userGroups){
            // if we find admin groups in users groups return true
            if (role.name === 'admin') {
                isTrue = true
            }
        }
        // else return false
        return isTrue
    } catch(error) {
        console.log('Error:' + error);
        return false
    }
}
</script>

<template>
    <adminNavBar />
    <RouterView />
</template>