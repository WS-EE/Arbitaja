<script setup>
import { RouterView } from 'vue-router';
import navBar from '@/components/generic/navBar.vue';
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