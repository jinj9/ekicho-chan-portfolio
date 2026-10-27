import { Alert, Box, TextField } from '@mui/material';
import React from 'react';
import '../App.css';
import { useMediaQuery } from 'react-responsive';
import LockOpenIcon from '@mui/icons-material/LockOpen';
import UserRegistDialog from './UserRegsitDialog';
import { useLocation, useNavigate } from 'react-router-dom';
import Loading from './Loading';
import { useAuthActions } from '../context/AuthContext';
import PasswordResetDialog from './PasswordResetDialog';
import PasswordTextField from './PasswordTextField';
import AppButton from './AppButton';
import ActionButtonRow from './ActionButtonRow';

/**
 * ログインページ
 */

const LoginPage = () => {

  const [isLoading, setIsLoading] = React.useState(false);
  // ユーザ登録情報
  const initLoginUserInfo = {
    email: "",
    password: ""
  }
  const [loginUserInfo, setLoginUserInfo] = React.useState(initLoginUserInfo);

  const navigate = useNavigate();
  const location = useLocation();
  const authActions = useAuthActions();
  const routeState = location.state as { from?: string; message?: string } | null;

  // 新規登録ダイアログのオープンフラグ
  const [isDialogOpen, setIsDialogOpen] = React.useState(false);
  const [isPasswordResetOpen, setIsPasswordResetOpen] = React.useState(false);

  // レスポンシブ対応のためのフラグ
  // 画面幅がminWidthの値以上のときはTRUEになる
  const isDesktop: boolean = useMediaQuery({ minWidth: 600 });

  // ログインボタンのクリックイベント
  const handleClickLogin = async () => {
    if (validator()) return;
    setIsLoading(true);
    try {
      await authActions.login(loginUserInfo.email, loginUserInfo.password);
      alert("ログインしました！");
      navigate(routeState?.from || "/home", { replace: true });
    } catch (error) {
      console.log(error);
      alert(error instanceof Error ? error.message : "ユーザ認証エラーが発生しました。");
    } finally {
      setIsLoading(false);
    }
  }

  // バリデーションチェック
  const validator = () => {
    const errorMessage: string[] = Array<string>();
    // メールアドレスチェック
    if (!loginUserInfo.email) {
      errorMessage.push("メールアドレスを入力してください。");
    }
    // パスワードチェック
    if (!loginUserInfo.password) {
      errorMessage.push("パスワードを入力してください。");
    }

    // エラーメッセージ出力
    if (errorMessage.length) {
      alert(errorMessage.join('\n'));
    }
    return errorMessage.length ? true : false;
  };

  // 新規登録ボタンのクリックイベント
  const handleClickRegist = () => {
    setIsDialogOpen(true);
  }

  // テキスト入力イベント
  const handleChangeText = (event: React.ChangeEvent<HTMLInputElement>) => {
    setLoginUserInfo({
      ...loginUserInfo,
      [event.target.name]: event.currentTarget.value
    });
  }

  if (isLoading) {
    return (
      <div>
        <Loading />
      </div>
    );
  }

  return (
    <>
      <div className={'browser_height'}>
        <Box sx={{
          display: 'block',
          position: 'static',
          textAlign: 'center',
          marginTop: '40px',
          marginLeft: 'auto',
          marginRight: 'auto',
          minHeight: 340,
          height: 'auto',
          paddingBottom: 2,
          width: '25%',
          minWidth: 280,
          maxWidth: 400,
          backgroundColor: '#ffffff',
          border: 1,
          borderRadius: 2,
          borderWidth: 2,
          borderColor: '#CC9933'
        }}>
          {routeState?.message && (
            <Alert severity="info" sx={{ borderRadius: 0 }}>{routeState.message}</Alert>
          )}
          <Box sx={{ marginTop: 3, marginBottom: 2 }}>
            <LockOpenIcon fontSize='large' />
          </Box>
          <Box>
            <TextField
              name='email'
              label='メールアドレス'
              type='email'
              size='small'
              color='primary'
              required={true}
              value={loginUserInfo.email}
              inputProps={{ maxLength: 320 }}
              sx={{
                margin: 1,
                minWidth: '80%'
              }}
              onChange={handleChangeText}
            />
          </Box>
          <Box>
            <PasswordTextField
              name='password'
              label='パスワード'
              size='small'
              required
              inputProps={{ maxLength: 128 }}
              value={loginUserInfo.password}
              sx={{
                margin: 1,
                minWidth: '80%'
              }}
              onChange={handleChangeText}
            />
          </Box>
          <Box sx={{ mt: 2 }}>
            <ActionButtonRow sx={{ justifyContent: 'center' }}>
              <AppButton kind="primary"
                onClick={handleClickLogin}
              >
                ログイン
              </AppButton>
              <AppButton kind="secondary"
                onClick={handleClickRegist}
              >
                新規登録
              </AppButton>
            </ActionButtonRow>
            <AppButton kind="text" size="small" onClick={() => setIsPasswordResetOpen(true)} sx={{ mt: 2, display: 'inline-block' }}>
              パスワードを忘れた方
            </AppButton>
          </Box>
        </Box>
      </div>
      <UserRegistDialog
        isOpen={isDialogOpen}
        isDialogClose={setIsDialogOpen}
        isDesktop={isDesktop}
      />
      <PasswordResetDialog
        open={isPasswordResetOpen}
        onClose={() => setIsPasswordResetOpen(false)}
      />
    </>
  );
}

export default LoginPage;
