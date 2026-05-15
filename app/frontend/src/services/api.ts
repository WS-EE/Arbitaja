import { api } from '@/services/http';
import type { components } from './api-types';
import type { AxiosResponse } from 'axios';

export type ApiResponse<T> =
  | { success: true; data: T }
  | { success: false; error: components['schemas']['ErrorResponse'] };

export type PagedResponse<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
};

export type PageParams = {
  search?: string;
  page?: number;
  size?: number;
  sort?: string;
  status?: string;
};

const unwrap = <T>(promise: Promise<AxiosResponse<T>>): Promise<ApiResponse<T>> =>
  promise.then(response => ({ success: true, data: response.data } as const))
         .catch(error => ({ success: false, error: error.response?.data } as const));

export type UserProfileResponse = components['schemas']['UserProfileResponse'];
export type UpdateUserRequest = components['schemas']['UpdateUserRequest'];
export type ChangePasswordRequest = components['schemas']['ChangePasswordRequest'];

export type SignupRequest = components['schemas']['SignupRequest'];
export type SignupResponse = components['schemas']['SignupResponse'];

export type ApproveSignupRequest = components['schemas']["ApproveSignupRequest"]

export type SchoolResponse = components['schemas']['SchoolResponse'];
export type SchoolUpsertRequest = components['schemas']['SchoolUpsertRequest'];

export type CompetitionResponse = components['schemas']['CompetitionResponse'];
export type CompetitionUpsertRequest = components['schemas']['CompetitionUpsertRequest'];
export type CompetitionCompetitorsUpdateRequest = components['schemas']['OverwriteCompetitionCompetitorsRequest'];

export type CompetitorResponse = components['schemas']['CompetitorResponse'];
export type CompetitorUpsertRequest = components['schemas']['CompetitorUpsertRequest'];
export type PersonalDataResponse = components['schemas']['PersonalDataResponse'];

export type ScoringDashboardResponse = components['schemas']['ScoringDashboardResponse'];
export type CompetitorDashboardResponse = components['schemas']['CompetitorDashboardResponse'];
export type ScoringCriterionResponse = components['schemas']['ScoringCriterionResponse'];
export type ScoringCriterionUpsertRequest = components['schemas']['ScoringCriterionUpsertRequest'];

export type RoleRequest = components['schemas']['Role'];
export type CreateRoleRequest = components['schemas']['CreateRoleRequest'];
export type RoleResponse = components['schemas']['RoleResponse'];
export type OverWriteRolePermissionsRequest = components["schemas"]["AddPermissionToRoleRequest"]

export type PermissionResponse = components['schemas']["PermissionResponse"]

export type GeneralMessageResponse = components['schemas']['GeneralMessageResponse'];
export type ErrorResponse = components['schemas']['ErrorResponse']

const buildFormLoginPayload = ({ username, password, rememberMe }: { username: string, password: string, rememberMe: boolean }) => {
  const formData = new URLSearchParams();
  formData.append('username', username);
  formData.append('password', password);
  formData.append('rememberMe', rememberMe ? 'true' : 'false');
  return formData;
};

const toQueryString = (params: PageParams): string => {
  const parts: string[] = [];
  if (params.search) parts.push(`search=${encodeURIComponent(params.search)}`);
  if (params.page !== undefined) parts.push(`page=${params.page}`);
  if (params.size !== undefined) parts.push(`size=${params.size}`);
  if (params.sort) parts.push(`sort=${encodeURIComponent(params.sort)}`);
  if (params.status) parts.push(`status=${encodeURIComponent(params.status)}`);
  return parts.length ? `?${parts.join('&')}` : '';
};

export const apiClient = {
  health: (): Promise<ApiResponse<GeneralMessageResponse>> => unwrap(api.get('/health')),
  auth: {
    currentUser: (): Promise<ApiResponse<UserProfileResponse>> => unwrap(api.get('/v2/user/auth')),
    login: ({ username, password, rememberMe }: { username: string, password: string, rememberMe: boolean }): Promise<ApiResponse<UserProfileResponse>> => {
      const formData = buildFormLoginPayload({ username, password, rememberMe });
      return unwrap(api.post('/login-user', formData, {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      }));
    },
    logout: (): Promise<ApiResponse<GeneralMessageResponse>> => unwrap(api.post('/logout')),
  },
  users: {
    list: (params: PageParams = {}): Promise<ApiResponse<PagedResponse<UserProfileResponse>>> => unwrap(api.get(`/v2/user${toQueryString(params)}`)),
    details: (id: number): Promise<ApiResponse<UserProfileResponse>> => unwrap(api.get(`/v2/user/${id}`)),
    update: (id: number, payload: UpdateUserRequest): Promise<ApiResponse<UserProfileResponse>> => unwrap(api.put(`/v2/user/${id}`, payload)),
    delete: (id: number): Promise<ApiResponse<GeneralMessageResponse>> => unwrap(api.delete(`/v2/user/${id}`)),
    changePassword: (id: number, payload: ChangePasswordRequest): Promise<ApiResponse<GeneralMessageResponse>> => unwrap(api.put(`/v2/user/change-password/${id}`, payload)),
    overwriteUserRoles: (id: number, roleIds: number[]): Promise<ApiResponse<UserProfileResponse>> => unwrap(api.put(`/v2/user/${id}/roles`, { roleIds })),
    adminCreate: (payload: SignupRequest): Promise<ApiResponse<UserProfileResponse>> => unwrap(api.post('/v2/user/admin-create', payload)),
    signup: (payload: SignupRequest): Promise<ApiResponse<SignupResponse>> => unwrap(api.post('/v2/signup', payload)),
    signupList: (params: PageParams = {}): Promise<ApiResponse<PagedResponse<SignupResponse>>> => unwrap(api.get(`/v2/signup/signup${toQueryString(params)}`)),
    approveSignup: (id: number, payload: ApproveSignupRequest): Promise<ApiResponse<GeneralMessageResponse>> => unwrap(api.post(`/v2/signup/signup/${id}/approve`, payload)),
    declineSignup: (id: number): Promise<ApiResponse<GeneralMessageResponse>> => unwrap(api.delete(`/v2/signup/signup/${id}`)),
  },
  schools: {
    list: (params: PageParams = {}): Promise<ApiResponse<PagedResponse<SchoolResponse>>> => unwrap(api.get(`/v2/schools${toQueryString(params)}`)),
    create: (payload: SchoolUpsertRequest): Promise<ApiResponse<SchoolResponse>> => unwrap(api.post('/v2/schools', payload)),
    update: (id: number, payload: SchoolUpsertRequest): Promise<ApiResponse<SchoolResponse>> => unwrap(api.put(`/v2/schools/${id}`, payload)),
    remove: (id: number): Promise<ApiResponse<GeneralMessageResponse>> => unwrap(api.delete(`/v2/schools/${id}`)),
  },
  competitions: {
    list: (params: PageParams = {}): Promise<ApiResponse<PagedResponse<CompetitionResponse>>> => unwrap(api.get(`/v2/competition${toQueryString(params)}`)),
    details: (id: number): Promise<ApiResponse<CompetitionResponse>> => unwrap(api.get(`/v2/competition/${id}`)),
    create: (payload: CompetitionUpsertRequest): Promise<ApiResponse<CompetitionResponse>> => unwrap(api.post('/v2/competition', payload)),
    update: (id: number, payload: CompetitionUpsertRequest): Promise<ApiResponse<CompetitionResponse>> => unwrap(api.put(`/v2/competition/${id}`, payload)),
    remove: (id: number): Promise<ApiResponse<GeneralMessageResponse>> => unwrap(api.delete(`/v2/competition/${id}`)),
    addCompetitor: (competitionId: number, competitorId: number): Promise<ApiResponse<GeneralMessageResponse>> => unwrap(api.post(`/v2/competition/${competitionId}/competitors/${competitorId}`)),
    removeCompetitor: (competitionId: number, competitorId: number): Promise<ApiResponse<GeneralMessageResponse>> => unwrap(api.delete(`/v2/competition/${competitionId}/competitors/${competitorId}`)),
    overwriteCompetitors: (competitionId: number, payload: CompetitionCompetitorsUpdateRequest): Promise<ApiResponse<GeneralMessageResponse>> => unwrap(api.put(`/v2/competition/${competitionId}/competitors`, payload)),
  },
  competitors: {
    list: (params: PageParams = {}): Promise<ApiResponse<PagedResponse<CompetitorResponse>>> => unwrap(api.get(`/v2/competitor${toQueryString(params)}`)),
    details: (id: number): Promise<ApiResponse<CompetitorResponse>> => unwrap(api.get(`/v2/competitor/${id}`)),
    byCompetition: (competitionId: number): Promise<ApiResponse<CompetitorResponse[]>> => unwrap(api.get(`/v2/competitor/competition/${competitionId}`)),
    create: (payload: CompetitorUpsertRequest): Promise<ApiResponse<CompetitorResponse>> => unwrap(api.post('/v2/competitor', payload)),
    update: (id: number, payload: CompetitorUpsertRequest): Promise<ApiResponse<CompetitorResponse>> => unwrap(api.put(`/v2/competitor/${id}`, payload)),
  },
  scoring: {
    dashboard: {
      history: (competitionId: number): Promise<ApiResponse<ScoringDashboardResponse>> => unwrap(api.get(`/v2/scoring/dashboard/competition/${competitionId}/history`)),
      criteria: (competitionId: number): Promise<ApiResponse<ScoringCriterionResponse[]>> => unwrap(api.get(`/v2/scoring/dashboard/competition/${competitionId}/criteria`)),
      criteriaForCompetitor: (competitionId: number, competitorId: number): Promise<ApiResponse<ScoringCriterionResponse[]>> =>
        unwrap(api.get(`/v2/scoring/dashboard/competition/${competitionId}/criteria/competitor/${competitorId}`)),
    },
    criteria: {
      list: (params: PageParams = {}): Promise<ApiResponse<PagedResponse<ScoringCriterionResponse>>> => unwrap(api.get(`/v2/scoring/criteria${toQueryString(params)}`)),
      byId: (id: number): Promise<ApiResponse<ScoringCriterionResponse>> => unwrap(api.get(`/v2/scoring/criteria/${id}`)),
      byCompetition: (competitionId: number): Promise<ApiResponse<ScoringCriterionResponse[]>> => unwrap(api.get(`/v2/scoring/criteria/by-competition/${competitionId}`)),
      create: (payload: ScoringCriterionUpsertRequest): Promise<ApiResponse<ScoringCriterionResponse>> => unwrap(api.post('/v2/scoring/criteria', payload)),
      update: (id: number, payload: ScoringCriterionUpsertRequest): Promise<ApiResponse<ScoringCriterionResponse>> => unwrap(api.put(`/v2/scoring/criteria/${id}`, payload)),
      remove: (id: number): Promise<ApiResponse<GeneralMessageResponse>> => unwrap(api.delete(`/v2/scoring/criteria/${id}`)),
      linkToCompetition: (criterionId: number, competitionId: number): Promise<ApiResponse<GeneralMessageResponse>> => unwrap(api.post(`/v2/scoring/criteria/${criterionId}/competitions/${competitionId}`)),
      removeFromCompetition: (criterionId: number, competitionId: number): Promise<ApiResponse<GeneralMessageResponse>> => unwrap(api.delete(`/v2/scoring/criteria/${criterionId}/competitions/${competitionId}`)),
    },
  },
  roles: {
    list: (params: PageParams = {}): Promise<ApiResponse<PagedResponse<RoleResponse>>> => unwrap(api.get(`/v2/roles${toQueryString(params)}`)),
    byId: (id: number): Promise<ApiResponse<RoleResponse>> => unwrap(api.get(`/v2/roles/${id}`)),
    create: (payload: RoleRequest): Promise<ApiResponse<RoleResponse>> => unwrap(api.post('/v2/roles/create', payload)),
    update: (id: number, payload: RoleRequest): Promise<ApiResponse<RoleResponse>> => unwrap(api.put(`/v2/roles/${id}`, payload)),
    delete: (id: number): Promise<ApiResponse<GeneralMessageResponse>> => unwrap(api.delete(`/v2/roles/${id}`)),
    overwriteRolePermissions: (roleId: number, payload: OverWriteRolePermissionsRequest): Promise<ApiResponse<RoleResponse>> => unwrap(api.put(`/v2/roles/${roleId}/permissions`, payload)),
  },
  permissions: {
    list: (): Promise<ApiResponse<PermissionResponse[]>> => unwrap(api.get('/v2/permissions')),
  }
};
