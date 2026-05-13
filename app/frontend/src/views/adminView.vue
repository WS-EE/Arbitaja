<script setup>
import adminNavBar from '@/components/admin/adminNavBar.vue';
import { RouterView } from 'vue-router';

import { onMounted, ref } from 'vue';
import router from '@/router';
import {useUserStore} from "@/stores/userStore";
import { ensureAuthRehydrated } from '@/composables/useAuthRehydrate';

// Set isUser admin variable
const isUserAdmin = ref();

onMounted(async () => {
  try {
    await ensureAuthRehydrated({ force: true })
    isUserAdmin.value = useUserStore().hasPrivilege('ADMIN')

    if (!isUserAdmin.value) {
        await router.replace('/404');
    }

  } catch(error) {
    await router.replace('/404');
  }
});
</script>

<template>
    <adminNavBar />
    <RouterView />
</template>