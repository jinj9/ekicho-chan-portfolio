import React from 'react';
import { Alert, Box, Grid } from '@mui/material';
import { useAuthContext } from '../context/AuthContext';
import { PORTFOLIO_MODE } from '../portfolioMode';

/** 
 * ホーム画面
 */

const HomePage = () => {

  // ログインユーザ情報
  const loggedUserInfo = useAuthContext();

  return (
    <>
      <Box
        className={'browser_height'}
        sx={{
          display: 'block',
          textAlign: 'center'
        }}>
        {PORTFOLIO_MODE && (
          <Alert severity="info" sx={{ maxWidth: 600, mx: 'auto', mb: 2, textAlign: 'left' }}>
            公開デモではユーザー登録を行いません。獲得データはこのブラウザにのみ保存され、
            ブラウザのデータを削除すると失われます。
          </Alert>
        )}
        {
          loggedUserInfo ? (
            <h1>{`ようこそ！${loggedUserInfo.displayName}さん！`}</h1>
          ) : (
            <h1>{"ようこそ！ゲストさん！"}</h1>
          )
        }
        <Grid
          container
          sx={{
            display: 'block',
            textAlign: 'center',
            margin: 'auto',
            width: '25%',
            minWidth: '280px',
            border: 2,
            borderRadius: '3px'
          }}
        >
          <Grid item xs={12} gridRow={1} sx={{ margin: 0, border: 1 }}>
            ★管理人メモ★
          </Grid>
          <Grid item xs={12} sx={{ border: 1, height: '300px' }}>
            <Box sx={{ padding: '5px' }}>
              <p style={{ margin: 0, textAlign: 'start' }}>
                こんにちは。管理人です。
              </p>
              <p style={{ margin: 0, textAlign: 'start' }}>
                現在はガチャでのGETと位置情報GETで遊べます。
              </p>
              <p style={{ margin: 0, textAlign: 'start' }}>
                ガチャではサンプルとして４０種類くらいのえきちょーちゃんが出現します。
              </p>
              <p style={{ margin: 0, textAlign: 'start' }}>
                最も出現率が低いSSRは0.1%の確率で出現します。（月曜日は0.5%になります。）
              </p>
              <p style={{ margin: 0, textAlign: 'start' }}>
                GETしたえきちょーちゃんは閲覧ページで確認できます。
              </p>
              <p style={{ margin: 0, textAlign: 'start', color: 'red' }}>
                ※GETするボタン実行時と閲覧ページの検索実行時は、データ通信が発生しますのでご注意ください。
              </p>
            </Box>
          </Grid>
        </Grid>
      </Box>
    </>
  );
}

export default HomePage;
