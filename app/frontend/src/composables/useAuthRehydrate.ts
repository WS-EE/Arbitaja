import { ref } from 'vue'
import { useUserStore } from '@/stores/userStore'
import { fetchCurrentUserAuthorization } from '@/features/auth/actions'
import type { UserProfileResponse, ErrorResponse } from '@/services/api'

interface LoadAuthOptions {
  force?: boolean
}

type AuthResponse = UserProfileResponse | ErrorResponse;

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
      const result: AuthResponse =
          await fetchCurrentUserAuthorization()

      if (!isAuthSuccess(result)) {
        useUserStore().setUserAuthorization({
          id: null,
          username: null,
          roles: [],
          permissions: [],
          personal_data: null,
        });

        error.value = result?.message ?? 'Failed to load authorization'
        return
      }

      userStore.setUserAuthorization(result)
      error.value = null
    } catch (e) {
      userStore.setUserAuthorization({
        id: null,
        username: null,
        roles: [],
        permissions: [],
        personal_data: null,
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

function isAuthSuccess(
    value: AuthResponse
): value is UserProfileResponse {
  return typeof value === 'object' && value !== null && 'username' in value;
}
