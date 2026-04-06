import axios from 'axios'

export async function fetchCurrentUserAuthorization() {
  const response = await axios.get('/v2/user/auth', {
    withCredentials: true,  // sends the SESSION cookie
  })
  return response.data      // axios unwraps .data automatically
}