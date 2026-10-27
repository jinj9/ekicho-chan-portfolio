import { API_PATH } from '../components/Const';

export type AuthUser = { accountId: string; uid: string; email: string; displayName: string; role: 'USER' | 'ADMIN' };
type AuthUserResponse = Omit<AuthUser, 'uid'>;

const csrfToken = () => document.cookie.split('; ').find(v => v.startsWith('XSRF-TOKEN='))?.split('=').slice(1).join('=');
const toUser = (value: AuthUserResponse): AuthUser => ({ ...value, uid: value.accountId });

const responseError = async (response: Response, fallback: string) => {
  try {
    const body: { message?: string } = await response.json();
    return body.message || fallback;
  } catch {
    return fallback;
  }
};

const prepareCsrf = async () => {
  const response = await fetch(`${API_PATH}/api/v1/web/auth/csrf`, { credentials: 'include' });
  if (!response.ok) throw new Error('サーバーとの接続準備に失敗しました。');
};

const postPublic = async (path: string, body: object) => {
  const response = await fetch(`${API_PATH}${path}`, {
    method: 'POST', credentials: 'include', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body),
  });
  if (!response.ok) throw new Error(await responseError(response, '処理に失敗しました。'));
};

export async function currentUser(): Promise<AuthUser | null> {
  const response = await fetch(`${API_PATH}/api/v1/web/auth/me`, { credentials: 'include' });
  if (response.status === 401) return null;
  if (!response.ok) throw new Error('ログイン状態を確認できませんでした。');
  return toUser(await response.json());
}

export async function login(email: string, password: string) {
  await prepareCsrf();
  const response = await fetch(`${API_PATH}/api/v1/web/auth/login`, {
    method: 'POST', credentials: 'include',
    headers: { 'Content-Type': 'application/json', 'X-XSRF-TOKEN': decodeURIComponent(csrfToken() || '') },
    body: JSON.stringify({ email, password }),
  });
  if (response.status === 401) throw new Error('メールアドレスまたはパスワードを確認してください。');
  if (!response.ok) throw new Error(await responseError(response, 'ログインに失敗しました。'));
  return toUser(await response.json());
}

export async function logout() {
  await prepareCsrf();
  const response = await fetch(`${API_PATH}/api/v1/web/auth/logout`, {
    method: 'POST', credentials: 'include', headers: { 'X-XSRF-TOKEN': decodeURIComponent(csrfToken() || '') },
  });
  if (!response.ok && response.status !== 401) throw new Error('ログアウトに失敗しました。');
}

export const registerAccount = (email: string, displayName: string, password: string) =>
  postPublic('/api/v1/auth/register', { email, displayName, password });

export const confirmEmail = (token: string) =>
  postPublic('/api/v1/auth/email-verification/confirm', { token });

export const resendVerificationEmail = (email: string) =>
  postPublic('/api/v1/auth/email-verification/resend', { email });

export const requestPasswordReset = (email: string) =>
  postPublic('/api/v1/auth/password-reset/request', { email });

export const confirmPasswordReset = (token: string, newPassword: string) =>
  postPublic('/api/v1/auth/password-reset/confirm', { token, newPassword });
