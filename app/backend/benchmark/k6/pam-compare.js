import http from 'k6/http';
import { check, sleep } from 'k6';

const mode = (__ENV.MODE || 'legacy').toLowerCase();
const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';

const endpointByMode = {
  legacy: '/v1/user/signup/create',
  hex: '/v2/signup',
};

const expectedStatusByMode = {
  legacy: [200, 409],
  hex: [201, 409],
};

if (!endpointByMode[mode]) {
  throw new Error(`Unsupported MODE '${mode}'. Use MODE=legacy or MODE=hex.`);
}

export const options = {
  vus: Number(__ENV.VUS || 10),
  duration: __ENV.DURATION || '30s',
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<1000'],
  },
};

function payload(iteration) {
  const unique = `${mode}-${__VU}-${iteration}`;

  if (mode === 'legacy') {
    return JSON.stringify({
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
  }

  return JSON.stringify({
    username: `user-${unique}`,
    password: 'Password123!',
    full_name: `User ${unique}`,
    email: `user-${unique}@example.com`,
    school_id: null,
  });
}

export default function () {
  const response = http.post(
    `${baseUrl}${endpointByMode[mode]}`,
    payload(__ITER),
    { headers: { 'Content-Type': 'application/json' } }
  );

  check(response, {
    'status is expected': (r) => expectedStatusByMode[mode].includes(r.status),
  });

  sleep(0.2);
}

