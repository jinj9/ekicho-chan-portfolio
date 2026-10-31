import React from 'react';
import * as api from '../api/authApi';
import { SESSION_EXPIRED_EVENT } from '../api/apiClient';
import { PORTFOLIO_MODE } from '../portfolioMode';
type Actions={isLoading:boolean;sessionExpired:boolean;login:(e:string,p:string)=>Promise<void>;logout:()=>Promise<void>;refresh:()=>Promise<void>};
const UserContext=React.createContext<api.AuthUser|null>(null);
const ActionsContext=React.createContext<Actions|null>(null);
export const useAuthContext=()=>React.useContext(UserContext);
export const useAuthActions=()=>{const v=React.useContext(ActionsContext);if(!v)throw Error('AuthProvider is missing');return v;};
export function AuthProvider({children}:React.PropsWithChildren){
 const [user,setUser]=React.useState<api.AuthUser|null>(null);const [isLoading,setLoading]=React.useState(!PORTFOLIO_MODE);
 const [sessionExpired,setSessionExpired]=React.useState(false);
 React.useEffect(()=>{
  if(PORTFOLIO_MODE){setLoading(false);return;}
  api.currentUser().then(setUser).catch(()=>setUser(null)).finally(()=>setLoading(false));
 },[]);
 React.useEffect(()=>{
  const expire=()=>{setSessionExpired(true);setUser(null);};
  window.addEventListener(SESSION_EXPIRED_EVENT,expire);
  return()=>window.removeEventListener(SESSION_EXPIRED_EVENT,expire);
 },[]);
 const login=async(e:string,p:string)=>{setLoading(true);try{setUser(await api.login(e,p));setSessionExpired(false);}finally{setLoading(false);}};
 const logout=async()=>{setLoading(true);try{await api.logout();setUser(null);setSessionExpired(false);}finally{setLoading(false);}};
 const refresh=async()=>{setUser(await api.currentUser());};
 return <ActionsContext.Provider value={{isLoading,sessionExpired,login,logout,refresh}}><UserContext.Provider value={user}>{children}</UserContext.Provider></ActionsContext.Provider>;
}
