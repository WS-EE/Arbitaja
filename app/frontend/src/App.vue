<script setup>
import { RouterView } from 'vue-router';
import customFooter from '@/components/generic/customFooter.vue';
import axios from 'axios';
import { onMounted,ref } from 'vue';
const backendLoading = ref(true)

import PulseSpinner from 'vue-spinner/src/PulseLoader.vue';

onMounted(async () => {
  while (backendLoading.value){
    try {
      const response = await axios.get('/health')
      if (response.status === 200){
        backendLoading.value = false
      }

    } catch(e) {
      await new Promise((resolve) => setTimeout(resolve, 2000));
    }
  }
})
</script>

<template>
  <div id="page-container" v-if="backendLoading" class="text-center justify-content-center align-items-center align-content-center">    
    <h5 class="pb-3">Loading backend</h5>
    <PulseSpinner />
  </div>
  <div v-else id="page-container"> 
    <div id="content-wrap">
      <RouterView />
    </div>
    <customFooter />
  </div>
</template>