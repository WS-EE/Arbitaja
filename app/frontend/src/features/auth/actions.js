import axios from 'axios'

export async function fetchCurrentUserAuthorization() {
  const response = await axios.get('/v2/user/auth', {
    withCredentials: true,  // sends the SESSION cookie
  })
  console.log('Fetched user authorization:', response.data)
  return response.data      // axios unwraps .data automatically
}