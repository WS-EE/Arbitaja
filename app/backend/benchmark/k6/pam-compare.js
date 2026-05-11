import http from 'k6/http';
import { check, sleep } from 'k6';

const mode     = (__ENV.MODE     || 'hex').toLowerCase();
const scenario = (__ENV.SCENARIO || 'signup').toLowerCase();
const baseUrl  = __ENV.BASE_URL  || 'http://localhost:8080';
const username =  'admin';
const password = __ENV.PASSWORD  || 'admin';
const userId   = __ENV.USER_ID   || '1';
const runId    = __ENV.RUN_ID    || '0';

const VALID_MODES     = ['legacy', 'hex'];
const VALID_SCENARIOS = ['signup', 'list-users', 'user-profile'];

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

// signup is unauthenticated (user registration); all other scenarios require a session.
export function setup() {
  if (scenario === 'signup') return { sessionId: '' };

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
  let res;
  const authHeaders = sessionId ? { Cookie: `JSESSIONID=${sessionId}` } : {};

  if (scenario === 'signup') {
    const unique = `${mode}-${runId}-${__VU}-${__ITER}`;
    const body = mode === 'hex'
      ? JSON.stringify({
          username: `user-${unique}`,
          password: 'Password123!',
          full_name: `User ${unique}`,
          email: `user-${unique}@example.com`,
          school_id: null,
        })
      : JSON.stringify({
          username: `user-${unique}`,
          salted_password: 'Password123!',
          personal_data: {
            id: 0,
            full_name: `User ${unique}`,
            email: `user-${unique}@example.com`,
            school: null,
            created_at: null,
          },
        });

    const endpoint = mode === 'hex' ? '/v2/signup' : '/v1/user/signup/create';
    res = http.post(`${baseUrl}${endpoint}`, body, {
      headers: { 'Content-Type': 'application/json' },
    });
    check(res, { 'signup status 2xx or 409': r => [200, 201, 409].includes(r.status) });

  } else if (scenario === 'list-users') {
    const url = mode === 'hex'
      ? `${baseUrl}/v2/user`
      : `${baseUrl}/v1/user/profile/all`;
    res = http.get(url, { headers: authHeaders });
    check(res, { 'list users status 2xx': r => r.status >= 200 && r.status < 300 });

  } else if (scenario === 'user-profile') {
    const url = mode === 'hex'
      ? `${baseUrl}/v2/user/${userId}`
      : `${baseUrl}/v1/user/profile/get?id=${userId}`;
    res = http.get(url, { headers: authHeaders });
    check(res, { 'user profile status 2xx': r => r.status >= 200 && r.status < 300 });
  }

  sleep(0.1);
}
