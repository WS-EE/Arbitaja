import { defineStore } from 'pinia'
import {UserProfileResponse} from "@/services/api";

export const useUserStore = defineStore('user', {
  state: (): UserProfileResponse => ({
    id: 0,
    username: undefined,
    roles: [],
    permissions: [],
    personal_data: undefined,
  }),

  actions: {
    setUserAuthorization(data: UserProfileResponse): void {
        this.id = data.id
        this.username = data.username
        this.roles = data.roles
        this.permissions = data.permissions
        this.personal_data = data.personal_data
    },
  },

  getters: {
    hasPrivilege: (state: UserProfileResponse) => (privilege: string): boolean => {
      if (!state.permissions) {
        return false;
      }
      return state.permissions.some(auth => auth === privilege);
    },
  },
})