import React from 'react';
import './App.css';
import { Route, Routes } from 'react-router-dom';
import LocationGetPage from './components/LocationGetPage';
import WatchingPage from './components/WatchingPage';
import GachaGetPage from './components/GachaGetPage';
import LoginPage from './components/LoginPage';
import Footer from './components/Footer';
import { useMediaQuery } from 'react-responsive';
import Header from './components/Header';
import HomePage from './components/HomePage';
import AdminPage from './components/AdminPage';
import { AuthProvider } from './context/AuthContext';
import { AdminRoute, ProtectedRoute, PublicOnlyRoute } from './components/AuthRoute';
import MyPage from './components/MyPage';
import StationSceneBackground from './components/StationSceneBackground';

function App() {

  // レスポンシブ対応のためのフラグ
  // 画面幅がminWidthの値以上のときはTRUEになる
  const isDesktop: boolean = useMediaQuery({ minWidth: 600 });

  // 画面タイトルを返却する
  const getMenuTitle = () => {
    // 現在URLを取得
    const path = location.pathname.split('/');
    // URLパスの末尾を取得
    const pathEnd = path.pop();

    switch (pathEnd) {
      case "login": return "えきちょーちゃんログイン！";
      case "": return "えきちょーちゃんガチャ！";
      case "location": return "位置情報でGETする！";
      case "watching": return "えきちょーちゃんの閲覧";
      case "mypage": return "マイページ";
      case "admin": return "えきちょーちゃん管理画面";
      default: return "えきちょーちゃん";
    }
  }

  return (
    <>
      <AuthProvider>
        <StationSceneBackground>
          <Header menuTitle={getMenuTitle()} isDesktop={isDesktop} />
          <Routes>
            <Route element={<PublicOnlyRoute />}>
              <Route path='/login' element={<LoginPage />} />
            </Route>
            <Route element={<ProtectedRoute />}>
              <Route path='/' element={<GachaGetPage />} />
              <Route path='/home' element={<HomePage />} />
              <Route path='/location' element={<LocationGetPage />} />
              <Route path='/watching' element={<WatchingPage />} />
              <Route path='/mypage' element={<MyPage />} />
              <Route element={<AdminRoute />}>
                <Route path='/admin' element={<AdminPage />} />
              </Route>
            </Route>
          </Routes>
          {isDesktop ? (<></>) : (<Footer />)}
        </StationSceneBackground>
      </AuthProvider>
    </>
  );
}

export default App;
