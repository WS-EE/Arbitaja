import { apiClient } from '@/services/api'

export async function fetchCurrentUserAuthorization() {
  return apiClient.auth.currentUser()
}