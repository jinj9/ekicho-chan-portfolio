import React from "react";
import AppBar from "@mui/material/AppBar";
import Container from "@mui/material/Container";
import { Button, Grid, Toolbar } from "@mui/material";
import TrainOutlinedIcon from '@mui/icons-material/TrainOutlined';
import PlaceOutlinedIcon from '@mui/icons-material/PlaceOutlined';
import SearchOutlinedIcon from '@mui/icons-material/SearchOutlined';
import HomeOutlinedIcon from '@mui/icons-material/HomeOutlined';

const Footer: React.FC = () => {

  // 現在URLを取得
  const path = location.pathname.split('/');
  // URLパスの末尾を取得
  const pathEnd = path.pop();

  return (
    <>
      <AppBar
        component={"footer"}
        position="fixed"
        sx={{
          top: 'auto',
          bottom: 0,
          width: '100%',
          backgroundColor: '#EEFFFF',
          borderTop: 1,
          borderColor: "#C0C0C0",
          zIndex: 10
        }}
      >
        <Toolbar sx={{ padding: 0, width: 'auto' }}>
          <Container sx={{ padding: 0 }}>
            <Grid container sx={{ margin: 0, padding: 0, height: '56px' }}>
              <Grid
                item
                xs={3}
              >
                <Button
                  href="/home"
                  sx={{
                    height: '100%',
                    width: '100%',
                  }}
                >
                  <div>
                    <HomeOutlinedIcon
                      fontSize="medium"
                      sx={
                        pathEnd === "home" ? {
                          display: 'block',
                          color: '#AA0000',
                          margin: 'auto'
                        } : {
                          display: 'block',
                          color: '#666666',
                          margin: 'auto'
                        }
                      }
                    />
                    <div style={pathEnd === "home" ? {color: '#AA0000'} : { color: '#666666' }}>ホーム</div>
                  </div>
                </Button>
              </Grid>
              <Grid item xs={3}>
                <Button
                  href="/"
                  sx={{
                    height: '100%',
                    width: '100%',
                  }}
                >
                  <div>
                    <TrainOutlinedIcon
                      fontSize="medium"
                      sx={
                        pathEnd === "" ? {
                          display: 'block',
                          color: '#AA0000',
                          margin: 'auto'
                        } : {
                          display: 'block',
                          color: '#666666',
                          margin: 'auto'
                        }
                      }
                    />
                    <div style={pathEnd === "" ? {color: '#AA0000'} : { color: '#666666' }}>ガチャ</div>
                  </div>
                </Button>
              </Grid>
              <Grid item xs={3}>
                <Button
                  href="/location"
                  sx={{
                    height: '100%',
                    width: '100%',
                  }}
                >
                  <div>
                    <PlaceOutlinedIcon
                      fontSize="medium"
                      sx={
                        pathEnd === "location" ? {
                          display: 'block',
                          color: '#AA0000',
                          margin: 'auto'
                        } : {
                          display: 'block',
                          color: '#666666',
                          margin: 'auto'
                        }
                      }
                    />
                    <div style={pathEnd === "location" ? {color: '#AA0000'} : { color: '#666666' }}>位置情報</div>
                  </div>
                </Button>
              </Grid>
              <Grid item xs={3}>
                <Button
                  href="/watching"
                  sx={{
                    height: '100%',
                    width: '100%',
                  }}
                >
                  <div>
                    <SearchOutlinedIcon
                      fontSize="medium"
                      sx={
                        pathEnd === "watching" ? {
                          display: 'block',
                          color: '#AA0000',
                          margin: 'auto'
                        } : {
                          display: 'block',
                          color: '#666666',
                          margin: 'auto'
                        }
                      }
                    />
                    <div style={pathEnd === "watching" ? {color: '#AA0000'} : { color: '#666666' }}>閲覧</div>
                  </div>
                </Button>
              </Grid>
            </Grid>
          </Container>
        </Toolbar>
      </AppBar>
    </>
  );
};

export default Footer;