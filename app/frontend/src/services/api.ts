import { api } from './http';
import type { components } from './api-types';

const unwrap = (promise) => promise.then((response) => response.data);

export type UserProfileResponse = components['schemas']['UserProfileResponse'];
export type UpdateUserRequest = components['schemas']['UpdateUserRequest'];
export type ChangePasswordRequest = components['schemas']['ChangePasswordRequest'];
export type SignupRequest = components['schemas']['SignupRequest'];
export type SignupResponse = components['schemas']['SignupResponse'];
export type SchoolResponse = components['schemas']['SchoolResponse'];
export type SchoolUpsertRequest = components['schemas']['SchoolUpsertRequest'];
export type CompetitionResponse = components['schemas']['CompetitionResponse'];
export type CompetitionUpsertRequest = components['schemas']['CompetitionUpsertRequest'];
export type CompetitorResponse = components['schemas']['CompetitorResponse'];
export type CompetitorUpsertRequest = components['schemas']['CompetitorUpsertRequest'];
export type ScoringDashboardResponse = components['schemas']['ScoringDashboardResponse'];
export type ScoringCriterionResponse = components['schemas']['ScoringCriterionResponse'];
export type ScoringCriterionUpsertRequest = components['schemas']['ScoringCriterionUpsertRequest'];
export type GeneralMessageResponse = components['schemas']['GeneralMessageResponse'];
export type ErrorResponse = components['schemas']['ErrorResponse']

const buildFormLoginPayload = ({ username, password, rememberMe }) => {
  const formData = new URLSearchParams();
  formData.append('username', username);
  formData.append('password', password);
  formData.append('rememberMe', rememberMe ? 'true' : 'false');
  return formData;
};

export const apiClient = {
  health: () => unwrap(api.get('/health')),
  auth: {
    currentUser: () => unwrap(api.get('/v2/user/auth')),
    login: ({ username, password, rememberMe }) => {
      const formData = buildFormLoginPayload({ username, password, rememberMe });
      return api.post('/login-user', formData, {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      });
    },
    logout: () => api.post('/logout'),
  },
  users: {
    list: () => unwrap(api.get('/v2/user')),
    details: (id: number) => unwrap(api.get(`/v2/user/${id}`)),
    update: (id: number, payload) => unwrap(api.put(`/v2/user/${id}`, payload)),
    delete: (id: number) => unwrap(api.delete(`/v2/user/${id}`)),
    changePassword: (id: number, payload) => unwrap(api.put(`/v2/user/change-password/${id}`, payload)),
    signup: (payload) => unwrap(api.post('/v2/signup', payload)),
    signupList: () => unwrap(api.get('/v2/signup/signup')),
    approveSignup: (id: number, payload) => unwrap(api.post(`/v2/signup/signup/${id}/approve`, payload)),
    declineSignup: (id: number) => unwrap(api.delete(`/v2/signup/signup/${id}`)),
  },
  schools: {
    list: () => unwrap(api.get('/v2/schools')),
    create: (payload) => unwrap(api.post('/v2/schools', payload)),
    update: (id: number, payload) => unwrap(api.put(`/v2/schools/${id}`, payload)),
    remove: (id: number) => unwrap(api.delete(`/v2/schools/${id}`)),
  },
  competitions: {
    list: () => unwrap(api.get('/v2/competition')),
    details: (id: number) => unwrap(api.get(`/v2/competition/${id}`)),
    create: (payload) => unwrap(api.post('/v2/competition', payload)),
    update: (id: number, payload: CompetitionUpsertRequest) => unwrap(api.put(`/v2/competition/${id}`, payload)),
    remove: (id: number) => unwrap(api.delete(`/v2/competition/${id}`)),
    addCompetitor: (competitionId, competitorId) => unwrap(api.post(`/v2/competition/${competitionId}/competitors/${competitorId}`)),
    removeCompetitor: (competitionId, competitorId) => unwrap(api.delete(`/v2/competition/${competitionId}/competitors/${competitorId}`)),
    overwriteCompetitors: (competitionId, payload) => unwrap(api.put(`/v2/competition/${competitionId}/competitors`, payload)),
  },
  competitors: {
    list: () => unwrap(api.get('/v2/competitor')),
    details: (id: number) => unwrap(api.get(`/v2/competitor/${id}`)),
    byCompetition: (competitionId) => unwrap(api.get(`/v2/competitor/competition/${competitionId}`)),
    create: (payload) => unwrap(api.post('/v2/competitor', payload)),
    update: (id: number, payload) => unwrap(api.put(`/v2/competitor/${id}`, payload)),
  },
  scoring: {
    dashboard: {
      history: (competitionId) => unwrap(api.get(`/v2/scoring/dashboard/competition/${competitionId}/history`)),
      criteria: (competitionId) => unwrap(api.get(`/v2/scoring/dashboard/competition/${competitionId}/criteria`)),
      criteriaForCompetitor: (competitionId, competitorId) =>
        unwrap(api.get(`/v2/scoring/dashboard/competition/${competitionId}/criteria/competitor/${competitorId}`)),
    },
    criteria: {
      list: () => unwrap(api.get('/v2/scoring/criteria')),
      byCompetition: (competitionId: number) => unwrap(api.get(`/v2/scoring/criteria/by-competition/${competitionId}`)),
      create: (payload) => unwrap(api.post('/v2/scoring/criteria', payload)),
      update: (id: number, payload) => unwrap(api.put(`/v2/scoring/criteria/${id}`, payload)),
      remove: (id: number) => unwrap(api.delete(`/v2/scoring/criteria/${id}`)),
      linkToCompetition: (criterionId, competitionId) => unwrap(api.post(`/v2/scoring/criteria/${criterionId}/competitions/${competitionId}`)),
    },
  },
  roles: {
    list: () => unwrap(api.get('/v2/roles')),
    delete: (id: number) => unwrap(api.delete(`/v2/roles/${id}`)),
  }
};
