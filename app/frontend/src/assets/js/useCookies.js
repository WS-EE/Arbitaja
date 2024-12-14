import VueCookies from 'vue-cookies';

export function useCookies() {
  return {
    set: VueCookies.set,     // Set a cookie
    get: VueCookies.get,     // Get a cookie
    remove: VueCookies.remove, // Remove a cookie
    isKey: VueCookies.isKey, // Check if a cookie exists
    keys: VueCookies.keys,   // Get all cookie keys
  };
}