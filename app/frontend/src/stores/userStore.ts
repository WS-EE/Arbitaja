import { defineStore } from 'pinia'
import { components } from "@/services/api-types";

import {UserProfileResponse} from "@/services/api";

interface UserState {
  id: number | null;
  username: string | null;
  roles: components["schemas"]["Role"][];
  permissions: components["schemas"]["SimpleGrantedAuthority"][];
  personal_data: components["schemas"]["PersonalDataResponse"] | null;
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    id: null,
    username: null,
    roles: [],
    permissions: [],
    personal_data: null,
  }),

  actions: {
    setUserAuthorization(data: UserProfileResponse): void {
      this.id = data.id ?? null;
      this.username = data.username ?? null;
      this.roles = data.roles ?? [];
      this.permissions = data.permissions ?? [];
      this.personal_data = data.personal_data ?? null;
    },
  },

  getters: {
    hasPrivilege: (state: UserState) => (privilege: string): boolean => {
      return state.permissions.some(auth => auth === privilege);
    },
  },
})