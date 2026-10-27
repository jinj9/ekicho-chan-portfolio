import React from 'react';
import { Alert, Box, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from '@mui/material';
import { confirmPasswordReset, requestPasswordReset } from '../api/authApi';
import Loading from './Loading';
import PasswordTextField from './PasswordTextField';
import AppButton from './AppButton';
import ActionButtonRow from './ActionButtonRow';

type Props = { open: boolean; onClose: () => void };

const PasswordResetDialog: React.FC<Props> = ({ open, onClose }) => {
  const [phase, setPhase] = React.useState<'request' | 'confirm'>('request');
  const [email, setEmail] = React.useState('');
  const [token, setToken] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [passwordConfirm, setPasswordConfirm] = React.useState('');
  const [isLoading, setIsLoading] = React.useState(false);

  const reset = () => {
    setPhase('request'); setEmail(''); setToken(''); setPassword(''); setPasswordConfirm('');
  };

  const handleRequest = async () => {
    if (!email.trim()) { alert('メールアドレスを入力してください。'); return; }
    setIsLoading(true);
    try {
      await requestPasswordReset(email.trim());
      setPhase('confirm');
    } catch (error) {
      alert(error instanceof Error ? error.message : '再設定メールの送信に失敗しました。');
    } finally { setIsLoading(false); }
  };

  const handleConfirm = async () => {
    const errors: string[] = [];
    if (!token.trim()) errors.push('再設定トークンを入力してください。');
    if (!/^[!-~]{15,128}$/.test(password)) errors.push('新しいパスワードは半角英数字・記号のみで15～128文字で入力してください。');
    if (password !== passwordConfirm) errors.push('確認用パスワードが一致しません。');
    if (errors.length) { alert(errors.join('\n')); return; }
    setIsLoading(true);
    try {
      await confirmPasswordReset(token.trim(), password);
      alert('パスワードを変更しました。新しいパスワードでログインしてください。');
      onClose();
    } catch (error) {
      alert(error instanceof Error ? error.message : 'パスワードの再設定に失敗しました。');
    } finally { setIsLoading(false); }
  };

  return (
    <Dialog open={open} fullWidth maxWidth="xs" onTransitionEnter={reset}>
      <DialogTitle sx={{ textAlign: 'center', borderBottom: 1 }}>パスワード再設定</DialogTitle>
      <DialogContent>
        {isLoading ? <Loading /> : phase === 'request' ? (
          <Box sx={{ pt: 2 }}>
            <Alert severity="info" sx={{ mb: 2 }}>登録済みのメールアドレスを入力してください。</Alert>
            <TextField fullWidth required label="メールアドレス" type="email" value={email} inputProps={{ maxLength: 320 }} onChange={e => setEmail(e.target.value)} />
          </Box>
        ) : (
          <Box sx={{ pt: 2 }}>
            <Alert severity="info" sx={{ mb: 2 }}>メールに記載された再設定トークンと、新しいパスワードを入力してください。</Alert>
            <TextField fullWidth required margin="normal" label="再設定トークン" value={token} onChange={e => setToken(e.target.value)} />
            <PasswordTextField fullWidth required margin="normal" label="新しいパスワード" value={password} helperText="半角英数字・記号のみで15～128文字で入力してください" inputProps={{ maxLength: 128 }} onChange={e => setPassword(e.target.value)} />
            <PasswordTextField fullWidth required margin="normal" label="新しいパスワード（確認）" value={passwordConfirm} inputProps={{ maxLength: 128 }} onChange={e => setPasswordConfirm(e.target.value)} />
          </Box>
        )}
      </DialogContent>
      <DialogActions sx={{ borderTop: 1, p: 1.5 }}><ActionButtonRow>
        {phase === 'confirm' && <AppButton kind="secondary" onClick={() => setPhase('request')}>戻る</AppButton>}
        <AppButton kind="secondary" onClick={onClose}>閉じる</AppButton>
        <AppButton kind="primary" onClick={phase === 'request' ? handleRequest : handleConfirm} disabled={isLoading}>
          {phase === 'request' ? 'メールを送信' : '変更する'}
        </AppButton>
      </ActionButtonRow></DialogActions>
    </Dialog>
  );
};

export default PasswordResetDialog;
