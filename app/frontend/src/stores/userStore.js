import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    id: null,
    username: null,
    roles: [],        // flat string[] e.g. ['USER', 'ADMIN']
    permissions: [],   // flat string[] e.g. ['USER_VIEW', 'USER_CREATE']
    personal_data: {
      first_name: null,
      last_name: null,
      email: null,
      school: null,
    },
  }),

  actions: {
    setUserAuthorization(data) {
      this.id = data.id
      this.username = data.username
      this.roles = data.roles
      this.permissions = data.permissions
      this.personal_data = data.personal_data
    },
  },

  getters: {
    hasPrivilege: (state) => (privilege) => {
      return state.permissions.includes(privilege)
    },
  },
})