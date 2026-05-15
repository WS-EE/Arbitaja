<script setup lang="ts">
import { computed } from 'vue'
import userProfile from '@/components/generic/userEdit/userProfile.vue'
import { UserProfileResponse } from '@/services/api'
import { useUserStore } from '@/stores/userStore'

const store = useUserStore()

const userParameters = computed<UserProfileResponse>(() => ({
  id: store.id,
  username: store.username,
  roles: store.roles ?? [],
  permissions: store.permissions ?? [],
  personal_data: store.personal_data,
}))

const onUpdateUserProfile = (userData: UserProfileResponse) => {
  store.setUserAuthorization(userData)
}
</script>

<template>
  <userProfile :user="userParameters" @userUpdate="onUpdateUserProfile" />
</template>
