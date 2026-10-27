import apiClient from './apiClient';

export type AccountProfile = {
  accountId: string;
  email: string;
  displayName: string;
  createdAt: string;
};

export async function getAccountProfile() {
  const response = await apiClient.get<AccountProfile>('/api/v1/web/account');
  return response.data;
}

export async function changeDisplayName(displayName: string) {
  const response = await apiClient.put<AccountProfile>('/api/v1/web/account/display-name', { displayName });
  return response.data;
}

export async function changePassword(currentPassword: string, newPassword: string) {
  await apiClient.post('/api/v1/web/account/password/change', { currentPassword, newPassword });
}

export async function deleteAccount(currentPassword: string) {
  await apiClient.post('/api/v1/web/account/delete', { currentPassword });
}
