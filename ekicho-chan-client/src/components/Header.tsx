import React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';
import { ThemeProvider, createTheme } from '@mui/material';
import SideMenu from './SideMenu';
import '../SideMenu.css';
import { useAuthContext, useAuthActions } from '../context/AuthContext';
import Loading from './Loading';

const theme = createTheme({
  palette: {
    primary: {
      main: "#CC9933"
    },
    secondary: {
      main: "#000033"

    },
  },
});

type Props = {
  isDesktop: boolean;
  menuTitle: string;
};

const Header: React.FC<Props> = ({
  isDesktop,
  menuTitle
}) => {

  const [isLoading, setIsLoading] = React.useState(false);

   // ログインユーザ情報
  const loggedUserInfo = useAuthContext();
  const authActions = useAuthActions();

  const [isSideMenu, setISideMenu] = React.useState(false);

  const handleClickMenu = () => {
    setISideMenu(!isSideMenu);
  };

  // ログアウトイベント
  const handleLogout = async () => {
    if (!confirm("ログアウトしますか？")) return;
    setIsLoading(true);
    try {
      await authActions.logout();
      alert("ログアウトしました。");
    } catch (error: any) {
      console.log(error.message);
      alert("ログアウトに失敗しました。" + error.message);
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
    <>
      <SideMenu isSideMenuOpen={isSideMenu} isSideMenuClose={handleClickMenu} />
      <Box sx={{ flexGrow: 1 }} >
        <ThemeProvider theme={theme}>
          <AppBar
            position="static"
            color="secondary"
            sx={{ borderBottom: 3, borderBottomColor: "#AA0000" }}>
            <Toolbar sx={{ padding: 0 }}>
              {
                isDesktop ? (
                  <div className='menu_icon'>
                    <IconButton
                      // size="small"
                      edge="start"
                      color="inherit"
                      aria-label="menu"
                      sx={{ mt: 1, mb: 1, mr: 1 }}
                      onClick={handleClickMenu}
                    >
                      <MenuIcon />
                    </IconButton>
                  </div>
                ) : (
                  <></>
                )
              }
              <Typography
                variant="h6"
                component="div"
                color="primary"
                sx={{
                  flexGrow: 1,
                  textAlign: 'center',
                  fontWeight: 'bold',
                  fontFamily: "游ゴシック",
                  fontSize: isDesktop ? "30px" : "22px",
                }}
              >
                {menuTitle}
              </Typography>
              {
                isDesktop ? (
                  loggedUserInfo ? (
                    <Button
                      color="inherit"
                      onClick={handleLogout}
                    >
                      ログアウト
                    </Button>
                  ) : (
                    <Button
                      color="inherit"
                      href="/login"
                    >
                      ログイン
                    </Button>
                  )
                ) : (
                  <div className='menu_icon'>
                    <IconButton
                      // size="small"
                      edge="start"
                      color="inherit"
                      aria-label="menu"
                      sx={{ mt: 1, mb: 1, mr: 1 }}
                      onClick={handleClickMenu}
                    >
                      <MenuIcon />
                    </IconButton>
                  </div>
                )
              }
            </Toolbar>
          </AppBar>
        </ThemeProvider>
      </Box>
    </>
  );
};

export default Header;
