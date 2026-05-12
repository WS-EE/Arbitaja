import axios from 'axios'
import { endpoints } from '@/services/endpoints'

export async function fetchCurrentUserAuthorization() {
  const response = await axios.get(endpoints.auth.currentUser, {
    withCredentials: true,  // sends the SESSION cookie
  })
  return response.data      // axios unwraps .data automatically
}