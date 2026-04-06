import { ref } from 'vue'
import { useUserStore } from '@/stores/userStore'
import { fetchCurrentUserAuthorization } from '@/features/auth/actions'

const loaded = ref(false)
const loading = ref(false)
const error = ref(null)
let rehydratePromise = null

async function loadAuth(options = {}) {
  const { force = false } = options

  if (force) {
    loaded.value = false
    error.value = null
    rehydratePromise = null
  }

  if (loaded.value) {
    return
  }
  if (rehydratePromise) {
    return rehydratePromise
  }

  loading.value = true
  rehydratePromise = (async () => {
    try {
      const result = await fetchCurrentUserAuthorization()

      if (!result?.username) {
        useUserStore().setUserAuthorization({
          id: null,
          username: null,
          roles: [],
          permissions: [],
          personal_data: null,
        })
        error.value = result?.message ?? 'Failed to load authorization'
        return
      }

      useUserStore().setUserAuthorization(result)
      error.value = null
    } catch (e) {
      useUserStore().setUserAuthorization({
        id: null,
        username: null,
        roles: [],
        permissions: [],
        personal_data: null,
      })
      error.value = e
    } finally {
      loaded.value = true
      loading.value = false
    }
  })()

  return rehydratePromise
}

export async function ensureAuthRehydrated(options = {}) {
  await loadAuth(options)
  return { loaded: loaded.value, error: error.value }
}

export function useAuthRehydrate() {
  return { loaded, loading, error, loadAuth }
}