import axios from 'axios'

const stripTrailingSlashes = (value = '') => value.replace(/\/+$/, '')
const stripLeadingSlashes = (value = '') => value.replace(/^\/+/, '')

const buildBaseUrl = (baseUrl = '', apiEndpoint = '') => {
  const normalizedBase = stripTrailingSlashes(baseUrl)
  const normalizedEndpoint = stripLeadingSlashes(apiEndpoint)

  if (!normalizedBase && !normalizedEndpoint) {
    return ''
  }

  if (!normalizedBase) {
    return `/${normalizedEndpoint}`
  }

  if (!normalizedEndpoint) {
    return normalizedBase
  }

  return `${normalizedBase}/${normalizedEndpoint}`
}

export const API_BASE_URL = buildBaseUrl(
  import.meta.env.VITE_APP_BASE_URL,
  import.meta.env.VITE_APP_API_ENDPOINT
)

export const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
  headers: {
    post: { 'Content-Type': 'application/json' },
    put: { 'Content-Type': 'application/json' },
  },
})


