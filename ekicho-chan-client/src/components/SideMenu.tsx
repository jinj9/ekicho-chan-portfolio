import React from 'react';
import '../SideMenu.css';
import { AiOutlineCloseCircle } from "react-icons/ai";
import { useAuthContext, useAuthActions } from '../context/AuthContext';
import Loading from './Loading';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import { useNavigate } from 'react-router-dom';

type Props = {
  isSideMenuOpen: boolean;
  isSideMenuClose: (add: boolean) => void;
};

const SideMenu: React.FC<Props> = ({ isSideMenuOpen, isSideMenuClose }) => {

  const [isLoading, setIsLoading] = React.useState(false);

  // ログインユーザ情報
  const loggedUserInfo = useAuthContext();
  const authActions = useAuthActions();
  const navigate = useNavigate();

  // サイドメニュー閉じるイベント
  const handleClick = () => {
    isSideMenuClose(false);
  }

  // ログアウトボタン押下イベント
  const handleLogout = async () => {
    if (!confirm("ログアウトしますか？")) return;
    setIsLoading(true);
    try {
      await authActions.logout();
      isSideMenuClose(false);
      navigate('/login', { replace: true });
      alert("ログアウトしました。");
    } catch (error) {
      const message = error instanceof Error ? error.message : '';
      console.log(message);
      alert("ログアウトに失敗しました。" + message);
    } finally {
      setIsLoading(false);
    }
  }

  if (isLoading) {
    return (
      <div>
        <Loading />
      </div>
    );
  }

  return (
    <div id='nav_area' className={isSideMenuOpen ? 'open' : ''}>
      <nav>
        <div>
          <span className='close_btn' onClick={handleClick}>
            <AiOutlineCloseCircle size={20} />
          </span>
        </div>
        <div className='inner'>
          <ul className='menu_links'>
            {
              loggedUserInfo ? (
                <li className="profile_menu_item">
                  <a className="profile_card" href="/mypage" onClick={handleClick} title="マイページを開く">
                    <AccountCircleIcon className="profile_icon" aria-hidden="true" />
                    <span className="profile_details">
                      <span className="profile_name" title={loggedUserInfo.displayName}>{loggedUserInfo.displayName}</span>
                      {loggedUserInfo.role === 'ADMIN' && <span className="profile_role">管理者</span>}
                    </span>
                  </a>
                </li>
              ) : (
                <></>
              )
            }
            {!loggedUserInfo ? <li><a href="/login">ログイン</a></li> : <></>}
            <li><a href="/home">ホーム</a></li>
            {loggedUserInfo ? <li><a href="/mypage">マイページ</a></li> : <></>}
            <li><a href="/">ガチャでGETする</a></li>
            <li><a href="/location">位置情報でGETする</a></li>
            <li><a href="/watching">えきちょーちゃんを見る</a></li>
            {loggedUserInfo ? (
              <li><button type="button" className="side_menu_action" onClick={handleLogout}>ログアウト</button></li>
            ) : <></>}
            {loggedUserInfo?.role === 'ADMIN' ? <li><a href="/admin" style={{ backgroundColor: '#EEEEEE' }}>管理者</a></li> : <></>}
          </ul>
        </div>
      </nav>
      <div id='mask' onClick={handleClick}></div>
    </div>
  );
}

export default SideMenu;
