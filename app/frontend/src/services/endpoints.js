export const endpoints = {
  auth: {
    login: '/login-user',
    logout: '/logout',
    currentUser: '/v2/user/auth',
  },
  users: {
    list: '/v2/user',
    details: (id) => `/v2/user/${id}`,
    profileAll: '/v2/user',
    deleteProfile: (id) => `/v2/user/${id}`,
    signupCreate: '/v2/signup',
    signupApprove: (id) => `/v2/signup/signup/${id}/approve`,
    signupDecline: (id) => `/v2/signup/signup/${id}`,
    signupList: '/v2/signup/signup',
    changePassword: (id) => `/v2/user/change-password/${id}`,
  },
  schools: {
    list: '/v2/schools',
    details: (id) => `/v2/schools/${id}`,
    create: '/v2/schools',
    update: (id) => `/v2/schools/${id}`,
    remove: (id) => `/v2/schools/${id}`,
  },
  competitions: {
    list: '/v2/competition',
    details: (id) => `/v2/competition/${id}`,
    byName: (name) => `/v2/competition/by-name?name=${name}`,
    create: '/v2/competition',
    update: (id) => `/v2/competition/${id}`,
    remove: (id) => `/v2/competition/${id}`,
    addCompetitor: (competitionId, competitorId) => `/v2/competition/${competitionId}/competitors/${competitorId}`,
    removeCompetitor: (competitionId, competitorId) => `/v2/competition/${competitionId}/competitors/${competitorId}`,
    overwriteCompetitors: (competitionId) => `/v2/competition/${competitionId}/competitors`,
  },
  competitors: {
    list: '/v2/competitor',
    details: (id) => `/v2/competitor/${id}`,
    detailsInCompetition: (competitionId) => `/v2/competitor/competition/${competitionId}`,
    create: '/v2/competitor',
    update: (id) => `/v2/competitor/${id}`,
  },
  scoring: {
    history: {
      dashboard: (competitionId) => `/v2/scoring/dashboard/competition/${competitionId}/history`,
      criteria: (competitionId) => `/v2/scoring/dashboard/competition/${competitionId}/criteria`,
    },
    criteria: {
      list: '/v2/scoring/criteria',
      details: (id) => `/v2/scoring/criteria/${id}`,
      create: '/v2/scoring/criteria',
      update: (id) => `/v2/scoring/criteria/${id}`,
      byCompetition: (competitionId) => `/v2/scoring/criteria/by-competition/${competitionId}`,
      addToCompetition: (criterionId, competitionId) => `/v2/scoring/criteria/${criterionId}/competitions/${competitionId}`,
      remove: (id) => `/v2/scoring/criteria/${id}`,
    },
  },
}




