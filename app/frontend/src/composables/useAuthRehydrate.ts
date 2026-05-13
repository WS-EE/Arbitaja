import { ref } from 'vue'
import { useUserStore } from '@/stores/userStore'
import { fetchCurrentUserAuthorization } from '@/features/auth/actions'

interface LoadAuthOptions {
  force?: boolean
}


const loaded = ref(false)
const loading = ref(false)
const error = ref<string | null>(null)

let rehydratePromise: Promise<void> | null = null

async function loadAuth(options: LoadAuthOptions = {}) {
  const force = options.force ?? false
  const userStore = useUserStore()

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
      const result =
          await fetchCurrentUserAuthorization()

        if (!result.success) {
            throw new Error(result.error.message ?? 'Failed to load authorization')
        }

      userStore.setUserAuthorization(result.data)
      error.value = null
    } catch (e) {
      userStore.setUserAuthorization({
        id: 0,
        username: undefined,
        roles: [],
        permissions: [],
        personal_data: undefined,
      })

      error.value =
          e instanceof Error ? e.message : 'Unknown error'
    } finally {
      loaded.value = true
      loading.value = false
      rehydratePromise = null
    }
  })()

  return rehydratePromise
}

export async function ensureAuthRehydrated(
    options: LoadAuthOptions = {}
) {
  await loadAuth(options)

  return {
    loaded: loaded.value,
    error: error.value,
  }
}

export function useAuthRehydrate() {
  return {
    loaded,
    loading,
    error,
    loadAuth,
  }
}
