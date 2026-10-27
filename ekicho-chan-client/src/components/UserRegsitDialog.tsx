import React from 'react';
import { Alert, Box, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from '@mui/material';
import { confirmEmail, registerAccount, resendVerificationEmail } from '../api/authApi';
import Loading from './Loading';
import PasswordTextField from './PasswordTextField';
import AppButton from './AppButton';
import ActionButtonRow from './ActionButtonRow';

type Props = { isOpen: boolean; isDialogClose: (open: boolean) => void; isDesktop: boolean };

const UserRegistDialog: React.FC<Props> = ({ isOpen, isDialogClose }) => {
  const [phase, setPhase] = React.useState<'register' | 'verify'>('register');
  const [isLoading, setIsLoading] = React.useState(false);
  const [email, setEmail] = React.useState('');
  const [displayName, setDisplayName] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [passwordConfirm, setPasswordConfirm] = React.useState('');
  const [token, setToken] = React.useState('');

  const reset = () => {
    setPhase('register'); setEmail(''); setDisplayName(''); setPassword(''); setPasswordConfirm(''); setToken('');
  };

  const handleRegister = async () => {
    const errors: string[] = [];
    const nameLength = Array.from(displayName.trim()).length;
    if (nameLength < 1 || nameLength > 30) errors.push('ユーザー名は1～30文字で入力してください。');
    if (!email.trim()) errors.push('メールアドレスを入力してください。');
    if (!/^[!-~]{15,128}$/.test(password)) errors.push('パスワードは半角英数字・記号のみで15～128文字で入力してください。');
    if (password !== passwordConfirm) errors.push('確認用パスワードが一致しません。');
    if (errors.length) { alert(errors.join('\n')); return; }
    setIsLoading(true);
    try {
      await registerAccount(email.trim(), displayName.trim(), password);
      setPhase('verify');
    } catch (error) {
      alert(error instanceof Error ? error.message : 'ユーザー登録に失敗しました。');
    } finally { setIsLoading(false); }
  };

  const handleVerify = async () => {
    if (!token.trim()) { alert('確認トークンを入力してください。'); return; }
    setIsLoading(true);
    try {
      await confirmEmail(token.trim());
      alert('メールアドレスの確認が完了しました。ログインしてください。');
      isDialogClose(false);
    } catch (error) {
      alert(error instanceof Error ? error.message : 'メールアドレスの確認に失敗しました。');
    } finally { setIsLoading(false); }
  };

  const handleResend = async () => {
    setIsLoading(true);
    try {
      await resendVerificationEmail(email.trim());
      alert('確認メールの再送を受け付けました。メールが届かない場合は、入力内容と迷惑メールフォルダーをご確認ください。再送は60秒後から行えます。');
    } catch (error) {
      alert(error instanceof Error ? error.message : '確認メールの再送に失敗しました。');
    } finally { setIsLoading(false); }
  };

  return (
    <Dialog open={isOpen} fullWidth maxWidth="xs" onTransitionEnter={reset}>
      <DialogTitle sx={{ textAlign: 'center', borderBottom: 1 }}>
        {phase === 'register' ? 'ユーザー登録' : 'メールアドレス確認'}
      </DialogTitle>
      <DialogContent>
        {isLoading ? <Loading /> : phase === 'register' ? (
          <Box sx={{ pt: 1 }}>
            <TextField fullWidth required margin="normal" label="ユーザー名" value={displayName} inputProps={{ maxLength: 30 }} onChange={e => setDisplayName(e.target.value)} />
            <TextField fullWidth required margin="normal" label="メールアドレス" type="email" value={email} inputProps={{ maxLength: 320 }} onChange={e => setEmail(e.target.value)} />
            <PasswordTextField fullWidth required margin="normal" label="パスワード" value={password} helperText="半角英数字・記号のみで15～128文字で入力してください" inputProps={{ maxLength: 128 }} onChange={e => setPassword(e.target.value)} />
            <PasswordTextField fullWidth required margin="normal" label="パスワード（確認）" value={passwordConfirm} inputProps={{ maxLength: 128 }} onChange={e => setPasswordConfirm(e.target.value)} />
          </Box>
        ) : (
          <Box sx={{ pt: 2 }}>
            <Alert severity="info" sx={{ mb: 2 }}>確認メールを送信しました。メールに記載された確認トークンを入力してください。</Alert>
            <TextField fullWidth required label="確認トークン" value={token} onChange={e => setToken(e.target.value)} />
            {process.env.NODE_ENV !== 'production' && (
              <Box sx={{ mt: 1, color: 'text.secondary', fontSize: 13 }}>ローカル環境ではMailpit（http://localhost:8025）でメールを確認できます。</Box>
            )}
          </Box>
        )}
      </DialogContent>
      <DialogActions sx={{ borderTop: 1, p: 1.5 }}><ActionButtonRow>
        {phase === 'verify' && <AppButton kind="secondary" onClick={() => setPhase('register')}>戻る</AppButton>}
        {phase === 'verify' && <AppButton kind="secondary" onClick={handleResend} disabled={isLoading}>確認メールを再送</AppButton>}
        <AppButton kind="secondary" onClick={() => isDialogClose(false)}>閉じる</AppButton>
        <AppButton kind="primary" onClick={phase === 'register' ? handleRegister : handleVerify} disabled={isLoading}>
          {phase === 'register' ? '登録' : '確認する'}
        </AppButton>
      </ActionButtonRow></DialogActions>
    </Dialog>
  );
};

export default UserRegistDialog;
