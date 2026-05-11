import http from 'k6/http';
import { check, sleep } from 'k6';

const mode     = (__ENV.MODE     || 'hex').toLowerCase();
const scenario = (__ENV.SCENARIO || 'list-competitions').toLowerCase();
const baseUrl  = __ENV.BASE_URL  || 'http://localhost:8080';
const username = 'admin';
const password = __ENV.PASSWORD  || 'admin';
const competitionId = __ENV.COMPETITION_ID || '1';

const VALID_MODES     = ['legacy', 'hex'];
const VALID_SCENARIOS = ['list-competitions', 'competitors-in-competition'];

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

export function setup() {
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
  if (match) sessionId = match[1];
  return { sessionId };
}

export default function ({ sessionId }) {
  const headers = sessionId ? { Cookie: `JSESSIONID=${sessionId}` } : {};
  let res;

  if (scenario === 'list-competitions') {
    const url = mode === 'hex'
      ? `${baseUrl}/v2/competition`
      : `${baseUrl}/v1/competition/all/get`;
    res = http.get(url, { headers });
    check(res, { 'list competitions status 2xx': r => r.status >= 200 && r.status < 300 });

  } else if (scenario === 'competitors-in-competition') {
    const url = mode === 'hex'
      ? `${baseUrl}/v2/competitor/competition/${competitionId}`
      : `${baseUrl}/v1/competitor/get/all/in/competition?id=${competitionId}`;
    res = http.get(url, { headers });
    check(res, { 'competitors in competition status 2xx': r => r.status >= 200 && r.status < 300 });
  }

  sleep(0.1);
}
