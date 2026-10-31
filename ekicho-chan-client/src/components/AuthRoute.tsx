import React from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuthActions, useAuthContext } from '../context/AuthContext';
import Loading from './Loading';
import { PORTFOLIO_MODE } from '../portfolioMode';

export const ProtectedRoute = () => {
  const user = useAuthContext();
  const { isLoading, sessionExpired } = useAuthActions();
  const location = useLocation();

  if (PORTFOLIO_MODE) return <Outlet />;
  if (isLoading) return <Loading />;
  if (!user) {
    const message = sessionExpired ? 'ログインの有効期限が切れました。もう一度ログインしてください。' : 'ログインが必要です。';
    return <Navigate to="/login" replace state={{ from: location.pathname, message }} />;
  }
  return <Outlet />;
};

export const PublicOnlyRoute = () => {
  const user = useAuthContext();
  const { isLoading } = useAuthActions();
  if (PORTFOLIO_MODE) return <Navigate to="/home" replace />;
  if (isLoading) return <Loading />;
  return user ? <Navigate to="/home" replace /> : <Outlet />;
};

export const AdminRoute = () => {
  const user = useAuthContext();
  if (PORTFOLIO_MODE) return <Navigate to="/home" replace />;
  return user?.role === 'ADMIN' ? <Outlet /> : <Navigate to="/home" replace />;
};
