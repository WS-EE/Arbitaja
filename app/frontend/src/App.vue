<script setup lang="ts">
import { RouterView } from 'vue-router'
import customFooter from '@/components/generic/customFooter.vue'
import { onMounted, ref } from 'vue'
import { apiClient } from '@/services/api'
import PulseSpinner from 'vue-spinner/src/PulseLoader.vue'

const backendLoading = ref(true)

onMounted(async () => {
  while (backendLoading.value) {
    const response = await apiClient.health()
    if (response.success) {
      backendLoading.value = false
    } else {
      await new Promise((resolve) => setTimeout(resolve, 2000))
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
