<script setup>
import { RouterView } from 'vue-router';
import customFooter from '@/components/generic/customFooter.vue';
</script>

<template>
  <div id="page-container">
    <div id="content-wrap">
      <RouterView />
    </div>
    <customFooter />
  </div>
</template>