import axios from 'axios';
import { API_PATH } from '../components/Const';
import { PORTFOLIO_MODE } from '../portfolioMode';

export const SESSION_EXPIRED_EVENT = 'ekicho-auth-session-expired';

const client = axios.create({
  baseURL: API_PATH,
  withCredentials: true,
});

const csrfToken = () => document.cookie
  .split('; ')
  .find(value => value.startsWith('XSRF-TOKEN='))
  ?.split('=')
  .slice(1)
  .join('=');

client.interceptors.request.use(async config => {
  const method = config.method?.toUpperCase() || 'GET';
  const portfolioRequest = PORTFOLIO_MODE && config.url?.startsWith('/api/v1/portfolio/');
  if (!portfolioRequest && !['GET', 'HEAD', 'OPTIONS'].includes(method)) {
    await fetch(`${API_PATH}/api/v1/web/auth/csrf`, { credentials: 'include' });
    config.headers.set('X-XSRF-TOKEN', decodeURIComponent(csrfToken() || ''));
  }
  return config;
});

client.interceptors.response.use(
  response => response,
  error => {
    const isCredentialError = error.response?.data?.message === 'LOGIN_FAILED';
    if (error.response?.status === 401 && !isCredentialError) {
      window.dispatchEvent(new Event(SESSION_EXPIRED_EVENT));
    }
    return Promise.reject(error);
  },
);

export default client;
