<script setup>
import { RouterView } from 'vue-router';
import customFooter from '@/components/generic/customFooter.vue';
import axios from 'axios';
import { onMounted,ref } from 'vue';
const backendLoading = ref(true)

onMounted(async () => {
  let isLoading = true
  while (isLoading){
    const response = await axios.get('/health')
    if (response.status === 200){
      isLoading = false
    } else {
      await new Promise((resolve) => setTimeout(resolve, 2000));
    }
  }
  backendLoading.value = false
})
</script>

<template>
  <div v-if="backendLoading">Backend is loading up...</div>
  <div v-else id="page-container"> 
    <div id="content-wrap">
      <RouterView />
    </div>
    <customFooter />
  </div>
</template>