import http from 'k6/http';
import { check, sleep } from 'k6';

const mode     = (__ENV.MODE     || 'hex').toLowerCase();
const scenario = (__ENV.SCENARIO || 'dashboard-history').toLowerCase();
const baseUrl  = __ENV.BASE_URL  || 'http://localhost:8080';
const apiKey   = __ENV.API_KEY   || 'default_api_key';
const username =  'admin';
const password = __ENV.PASSWORD  || 'admin';

const competitionId = __ENV.COMPETITION_ID || '1';
const competitorId  = __ENV.COMPETITOR_ID  || '1';
const criteriaId    = __ENV.CRITERIA_ID    || '1';

const VALID_MODES     = ['legacy', 'hex'];
const VALID_SCENARIOS = ['dashboard-history', 'dashboard-criteria', 'dashboard-criteria-competitor', 'add-scoring'];

if (!VALID_MODES.includes(mode)) {
  throw new Error(`Unsupported MODE '${mode}'. Use: ${VALID_MODES.join(', ')}`);
}
if (!VALID_SCENARIOS.includes(scenario)) {
  throw new Error(`Unsupported SCENARIO '${scenario}'. Use: ${VALID_SCENARIOS.join(', ')}`);
}

export const options = {
  vus: Number(__ENV.VUS || 10),
  duration: __ENV.DURATION || '30s',
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<2000'],
  },
};

// Only hex criteria endpoints require a session cookie;
// dashboard-history (now public), all legacy endpoints, and add-scoring are unauthenticated.
export function setup() {
  if (mode !== 'hex' || scenario === 'add-scoring' || scenario === 'dashboard-history') {
    return { sessionId: '' };
  }

  const res = http.post(
    `${baseUrl}/login-user`,
    `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`,
    {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      redirects: 0,
    }
  );

  let sessionId = '';
  const setCookie = res.headers['Set-Cookie'] || '';
  const match = setCookie.match(/JSESSIONID=([^;]+)/);
  if (match) {
    sessionId = match[1];
  }

  return { sessionId };
}

export default function ({ sessionId }) {
  let res;

  if (scenario === 'dashboard-history') {
    const url = mode === 'hex'
      ? `${baseUrl}/v2/scoring/dashboard/competition/${competitionId}/history`
      : `${baseUrl}/v1/dashboard/competition/history?competition_id=${competitionId}`;

    const headers = mode === 'hex' && sessionId
      ? { Cookie: `JSESSIONID=${sessionId}` }
      : {};

    res = http.get(url, { headers });
    check(res, { 'dashboard history status 2xx': r => r.status >= 200 && r.status < 300 });

  } else if (scenario === 'dashboard-criteria') {
    const url = mode === 'hex'
      ? `${baseUrl}/v2/scoring/dashboard/competition/${competitionId}/criteria`
      : `${baseUrl}/v1/dashboard/competition/criteria?competition_id=${competitionId}`;

    const headers = mode === 'hex' && sessionId
      ? { Cookie: `JSESSIONID=${sessionId}` }
      : {};

    res = http.get(url, { headers });
    check(res, { 'dashboard criteria status 2xx': r => r.status >= 200 && r.status < 300 });

  } else if (scenario === 'dashboard-criteria-competitor') {
    const url = mode === 'hex'
      ? `${baseUrl}/v2/scoring/dashboard/competition/${competitionId}/criteria/competitor/${competitorId}`
      : `${baseUrl}/v1/dashboard/competition/criteria/competitor?competition_id=${competitionId}&competitor_id=${competitorId}`;

    const headers = mode === 'hex' && sessionId
      ? { Cookie: `JSESSIONID=${sessionId}` }
      : {};

    res = http.get(url, { headers });
    check(res, { 'dashboard criteria competitor status 2xx': r => r.status >= 200 && r.status < 300 });

  } else if (scenario === 'add-scoring') {
    if (mode === 'hex') {
      res = http.post(
        `${baseUrl}/v2/scoring/history`,
        JSON.stringify({
          competition_id: parseInt(competitionId),
          competitor_id:  parseInt(competitorId),
          criteria_id:    parseInt(criteriaId),
          points:         5.0,
        }),
        { headers: { 'Content-Type': 'application/json', 'X-API-KEY': apiKey } }
      );
    } else {
      const url = `${baseUrl}/v1/competition/criteria/history/add` +
        `?competition_id=${competitionId}&criteria_id=${criteriaId}&competitor_id=${competitorId}&points=5.0`;
      res = http.post(url, null, { headers: { 'X-API-KEY': apiKey } });
    }
    check(res, { 'add scoring status 2xx': r => r.status >= 200 && r.status < 300 });
  }

  sleep(0.1);
}