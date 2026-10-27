import React from 'react';
import { Alert, Box, Card, CardContent, Dialog, DialogActions, DialogContent, DialogTitle, Divider, TextField, Typography } from '@mui/material';
import { changeDisplayName, changePassword, deleteAccount, getAccountProfile, AccountProfile } from '../api/accountApi';
import axios from 'axios';
import { useAuthActions } from '../context/AuthContext';
import Loading from './Loading';
import PasswordTextField from './PasswordTextField';
import AppButton from './AppButton';
import ActionButtonRow from './ActionButtonRow';

const MyPage = () => {
  const auth = useAuthActions();
  const [profile, setProfile] = React.useState<AccountProfile | null>(null);
  const [displayName, setDisplayName] = React.useState('');
  const [isLoading, setIsLoading] = React.useState(true);
  const [passwordOpen, setPasswordOpen] = React.useState(false);
  const [currentPassword, setCurrentPassword] = React.useState('');
  const [newPassword, setNewPassword] = React.useState('');
  const [passwordConfirm, setPasswordConfirm] = React.useState('');
  const [deleteOpen, setDeleteOpen] = React.useState(false);
  const [deletePassword, setDeletePassword] = React.useState('');
  const [deleteConfirmation, setDeleteConfirmation] = React.useState('');

  React.useEffect(() => {
    getAccountProfile().then(value => { setProfile(value); setDisplayName(value.displayName); })
      .catch(() => alert('アカウント情報を取得できませんでした。')).finally(() => setIsLoading(false));
  }, []);

  const saveDisplayName = async () => {
    const length = Array.from(displayName.trim()).length;
    if (length < 1 || length > 30) { alert('ユーザー名は1～30文字で入力してください。'); return; }
    setIsLoading(true);
    try {
      const updated = await changeDisplayName(displayName.trim());
      setProfile(updated); setDisplayName(updated.displayName);
      await auth.refresh();
      alert('ユーザー名を変更しました。');
    } catch (error) {
      alert(error instanceof Error ? error.message : 'ユーザー名を変更できませんでした。');
    } finally { setIsLoading(false); }
  };

  const submitPassword = async () => {
    const errors: string[] = [];
    if (!currentPassword) errors.push('現在のパスワードを入力してください。');
    if (!/^[!-~]{15,128}$/.test(newPassword)) errors.push('新しいパスワードは半角英数字・記号のみで15～128文字で入力してください。');
    if (newPassword !== passwordConfirm) errors.push('確認用パスワードが一致しません。');
    if (errors.length) { alert(errors.join('\n')); return; }
    setIsLoading(true);
    try {
      await changePassword(currentPassword, newPassword);
      setPasswordOpen(false);
      alert('パスワードを変更しました。新しいパスワードで再ログインしてください。');
      await auth.logout();
    } catch (error) {
      const credentialError = axios.isAxiosError(error) && error.response?.status === 401;
      alert(credentialError ? '現在のパスワードが正しくありません。' : 'パスワードを変更できませんでした。');
    } finally { setIsLoading(false); }
  };

  const submitDeletion = async () => {
    if (!deletePassword) { alert('現在のパスワードを入力してください。'); return; }
    if (deleteConfirmation !== '削除') { alert('確認欄に「削除」と入力してください。'); return; }
    setIsLoading(true);
    try {
      await deleteAccount(deletePassword);
      alert('アカウントを削除しました。');
      window.location.replace('/login');
    } catch (error) {
      const credentialError = axios.isAxiosError(error) && error.response?.status === 401;
      alert(credentialError ? '現在のパスワードが正しくありません。' : 'アカウントを削除できませんでした。');
      setIsLoading(false);
    }
  };

  if (isLoading) return <Loading />;

  return (
    <Box className="browser_height" sx={{ p: 2 }}>
      <Card sx={{ maxWidth: 520, mx: 'auto', mt: 2, border: 2, borderColor: '#CC9933' }}>
        <CardContent>
          <Typography variant="h5" textAlign="center" mb={2}>マイページ</Typography>
          <Alert severity="info" sx={{ mb: 2 }}>登録情報の確認と変更ができます。</Alert>
          <Typography color="text.secondary">メールアドレス</Typography>
          <Typography sx={{ mb: 2, overflowWrap: 'anywhere' }}>{profile?.email}</Typography>
          <Typography color="text.secondary">アカウント作成日時</Typography>
          <Typography sx={{ mb: 2 }}>{profile?.createdAt ? new Date(profile.createdAt).toLocaleString('ja-JP') : '-'}</Typography>
          <Divider sx={{ my: 2 }} />
          <TextField fullWidth label="ユーザー名" value={displayName} inputProps={{ maxLength: 30 }} onChange={e => setDisplayName(e.target.value)} />
          <AppButton kind="primary" fullWidth sx={{ mt: 2 }} onClick={saveDisplayName}>ユーザー名を変更</AppButton>
          <AppButton kind="secondary" fullWidth sx={{ mt: 2 }} onClick={() => { setCurrentPassword(''); setNewPassword(''); setPasswordConfirm(''); setPasswordOpen(true); }}>パスワードを変更</AppButton>
          <Divider sx={{ my: 3 }} />
          <Alert severity="warning" sx={{ mb: 2 }}>
            アカウントを削除すると、登録情報と取得した駅の記録は元に戻せません。
          </Alert>
          <AppButton kind="danger" fullWidth onClick={() => { setDeletePassword(''); setDeleteConfirmation(''); setDeleteOpen(true); }}>
            アカウントを削除
          </AppButton>
        </CardContent>
      </Card>
      <Dialog open={passwordOpen} fullWidth maxWidth="xs" onClose={() => setPasswordOpen(false)}>
        <DialogTitle>パスワード変更</DialogTitle>
        <DialogContent>
          <PasswordTextField fullWidth required margin="normal" label="現在のパスワード" value={currentPassword} inputProps={{ maxLength: 128 }} onChange={e => setCurrentPassword(e.target.value)} />
          <PasswordTextField fullWidth required margin="normal" label="新しいパスワード" value={newPassword} helperText="半角英数字・記号のみで15～128文字で入力してください" inputProps={{ maxLength: 128 }} onChange={e => setNewPassword(e.target.value)} />
          <PasswordTextField fullWidth required margin="normal" label="新しいパスワード（確認）" value={passwordConfirm} inputProps={{ maxLength: 128 }} onChange={e => setPasswordConfirm(e.target.value)} />
        </DialogContent>
        <DialogActions sx={{ p: 1.5 }}><ActionButtonRow><AppButton kind="secondary" onClick={() => setPasswordOpen(false)}>キャンセル</AppButton><AppButton kind="primary" onClick={submitPassword}>変更する</AppButton></ActionButtonRow></DialogActions>
      </Dialog>
      <Dialog open={deleteOpen} fullWidth maxWidth="xs" onClose={() => setDeleteOpen(false)}>
        <DialogTitle>アカウントの削除</DialogTitle>
        <DialogContent>
          <Alert severity="error" sx={{ mt: 1, mb: 1 }}>
            この操作は取り消せません。登録情報と取得した駅の記録がすべて削除されます。
          </Alert>
          <PasswordTextField fullWidth required margin="normal" label="現在のパスワード" value={deletePassword} inputProps={{ maxLength: 128 }} onChange={e => setDeletePassword(e.target.value)} />
          <TextField fullWidth required margin="normal" label="確認のため「削除」と入力" value={deleteConfirmation} inputProps={{ maxLength: 2 }} onChange={e => setDeleteConfirmation(e.target.value)} />
        </DialogContent>
        <DialogActions sx={{ p: 1.5 }}>
          <ActionButtonRow>
            <AppButton kind="secondary" onClick={() => setDeleteOpen(false)}>キャンセル</AppButton>
            <AppButton kind="danger" disabled={!deletePassword || deleteConfirmation !== '削除'} onClick={submitDeletion}>削除する</AppButton>
          </ActionButtonRow>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default MyPage;
