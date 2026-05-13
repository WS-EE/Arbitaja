import { spawnSync } from 'node:child_process';

const normalizeEnv = (value) => {
  if (value == null) {
    return '';
  }

  const trimmed = String(value).trim();
  return trimmed === '' ? '' : trimmed;
};

const baseUrl = normalizeEnv(process.env.VITE_APP_BASE_URL) || 'http://localhost:8080';
const apiDocsEndpoint = normalizeEnv(process.env.API_DOCS_ENDPOINT) || '/api-docs';

const normalizeBase = baseUrl.replace(/\/+$/, '');
const normalizeEndpoint = apiDocsEndpoint.replace(/^\/+/, '');
const apiDocsUrl = normalizeEndpoint ? `${normalizeBase}/${normalizeEndpoint}` : normalizeBase;

const outputFile = 'src/services/api-types.d.ts';

const result = spawnSync(
  'npx',
  ['openapi-typescript', apiDocsUrl, '-o', outputFile],
  { stdio: 'inherit', shell: true }
);

process.exit(result.status ?? 1);
