import React from 'react';
import AppButton from './AppButton';
import Loading from './Loading';
import ImageDialog from './ImageDialog';
import { useMediaQuery } from 'react-responsive';
import { Box, FormControl, Grid, InputLabel, MenuItem, Select } from '@mui/material';
import { AxiosResponse } from 'axios';
import { PREFECTURE_LIST } from './Const';
import ComStationSelect from './ComStationSelect';
import apiClient from '../api/apiClient';

type locationInfo = {
  latitude: number | null;
  longitude: number | null;
}

type stationMaster = {
  id: number | undefined,
  stationName: string,
  stationEnglishName: string,
  latitude: number | undefined,
  longitude: number | undefined,
  locationName: string,
  imageData: string,
  appearanceRateCd: string,
  stationInformationText: string,
  isObtained: boolean,
}

const initStationMaster = {
  id: undefined,
  stationName: "",
  stationEnglishName: "",
  latitude: undefined,
  longitude: undefined,
  locationName: "",
  imageData: "",
  appearanceRateCd: "",
  stationInformationText: "",
  isObtained: false,
};

/**
 * 位置情報でえきちょーちゃんをGETするページ
 * */
const LocationGetPage = () => {

  const [isDialogOpen, setIsDialogOpen] = React.useState(false);
  const [isLoading, setIsLoading] = React.useState(false);
  // レスポンシブ対応のためのフラグ
  // 画面幅がminWidthの値以上のときはTRUEになる
  const isDesktop: boolean = useMediaQuery({ minWidth: 600 })

  // 駅データを管理する
  const [stationData, setStationData] = React.useState<stationMaster>(initStationMaster);

  // リクエストパラメータの初期値
  const initLocationGetParam = {
    // userId: GUEST_USER_ID,
    prefectureCd: "",
    stationCd: "",
  };

  const [locationGetParam, setLocationGetParam] = React.useState(initLocationGetParam);

  // GETするボタン押下イベント
  const handleClickBtn = async () => {
    // 駅選択のバリデーションチェック
    if (locationGetParam.stationCd === "") {
      alert("駅を選択してください。");
      return;
    }
    setIsLoading(true);

    const getStationData = async (locationInfo: locationInfo) => {
      try {
        const response: AxiosResponse<stationMaster> = await apiClient.post(
          '/api/v1/web/stations/location-get', {
          stationCd: locationGetParam.stationCd,
          latitude: locationInfo.latitude,
          longitude: locationInfo.longitude,
        });
        setStationData(response.data);
        setIsDialogOpen(true);
        setTimeout(
          () => {
            setIsLoading(false);
          },
          500
        );
      } catch (e: any) {
        if (e.response.data.message !== undefined) {
          alert(e.response.data.message);
        } else if (e.response !== undefined && e.response.data.status === 400) {
          alert(e.response.data.status + ":リクエストエラーです。");
        } else {
          alert("サーバ通信エラーです。");
        }
        setIsLoading(false);
      }
    };
    // 駅情報を取得（テスト用）
    // getStationData(getLocationInfo());

    // TODO:駅情報を取得（本番用）
    getLocationInfo()
      .then(
        (locationInfo: locationInfo) => {
          getStationData(locationInfo);
        }
      )
      .catch(
        () => {
          setIsLoading(false);
        }
      );
  }

  // 現在地情報を取得する（テスト用）
  // const getLocationInfo = (): locationInfo => {
  //   return {
  //     latitude: TEST_LATITUDE,
  //     longitude: TEST_LONGITUDE
  //   }
  // };

  // 現在位置情報を取得する
  // TODO： 実際の位置情報を取得するのが、個人情報保護の観点からNGかもしれないのでコメントアウトする
  const getLocationInfo = () => {
    return new Promise<locationInfo>((resolve, reject) => {
      // 現在位置情報取得のオプション
      const options = {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0,
      };
      // 現在位置情報を取得
      navigator.geolocation.getCurrentPosition(success, error, options);

      // 取得成功した場合
      function success(position: { coords: locationInfo }) {
        // console.log("緯度：", position.coords.latitude);
        // console.log("経度：", position.coords.longitude);
        const locationInfo: locationInfo = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
        };
        resolve(locationInfo);
      }

      // 取得失敗した場合
      function error(error: { code: number; }) {
        switch (error.code) {
          case 1: //PERMISSION_DENIED
            alert("位置情報の利用が許可されていません。");
            break;
          case 2: //POSITION_UNAVAILABLE
            alert("現在位置が取得できませんでした。");
            break;
          case 3: //TIMEOUT
            alert("タイムアウトになりました。");
            break;
          default:
            alert("その他のエラー(エラーコード:" + error.code + ")");
            break;
        }
        reject(error);
      }
    });
  }

  // 位置情報取得が可能か確認する
  const checkLocationGet = (): void => {
    // Geolocation APIに対応している
    if (navigator.geolocation) {
      alert("この端末では位置情報が取得できます。");
      // Geolocation APIに対応していない
    } else {
      alert("この端末では位置情報が取得できません。");
    }
  }

  return (
    <>
      <div className={'browser_height'}>
        <Box
          sx={{
            display: 'block',
            textAlign: 'center',
            margin: '20px',
            marginLeft: 'auto',
            marginRight: 'auto',
          }}>
          <FormControl
            variant="standard"
            sx={{
              width: '150px',
              margin: 1
            }}
          >
            <InputLabel id="prefecture_label">都道府県</InputLabel>
            <Select
              labelId="prefecture_label"
              id="prefecture_list"
              value={locationGetParam.prefectureCd}
              label="都道府県"
              onChange={(event, value) => {
                setLocationGetParam({
                  ...locationGetParam,
                  prefectureCd: event.target.value as string,
                });
              }}
            >
              <MenuItem value=''>{"選択しない"}</MenuItem>
              {
                PREFECTURE_LIST.map((value, key) => (
                  <MenuItem key={key} value={value.prefectureCd}>{value.label}</MenuItem>
                ))
              }
            </Select>
          </FormControl>
          <ComStationSelect
            prefectureCd={locationGetParam.prefectureCd}
            value={locationGetParam.stationCd}
            onChange={(event: any, value: { stationCd: string; } | null) => {
              setLocationGetParam({
                ...locationGetParam,
                stationCd: value === null ? "" : value.stationCd,
              });
            }}
            isRequired={true}
          />
          <AppButton kind="secondary"
            onClick={checkLocationGet}
            sx={{
              display: 'inline-flex',
              margin: 1,
              paddingTop: '7px',
              paddingBottom: '7px',
              paddingLeft: '3px',
              paddingRight: '3px',
            }}
          >
            端末確認
          </AppButton>
        </Box>
        <Box sx={{
          display: 'block',
          textAlign: 'center',
          margin: 2
        }}>
          <div>
            <button
              className="location_get_btn"
              onClick={handleClickBtn}
            >
              GETする
            </button>
          </div>
        </Box>
        <Box>
          <Grid
            container
            sx={{
              display: 'block',
              textAlign: 'center',
              margin: 'auto',
              marginTop: '20px',
              marginBottom: '70px',
              width: '25%',
              minWidth: '280px',
              border: 2,
              borderRadius: '3px'
            }}
          >
            <Grid item xs={12} gridRow={1} sx={{ margin: 0, border: 1 }}>
              ★位置情報GETの使い方★
            </Grid>
            <Grid item xs={12} sx={{ border: 1, height: '200px' }}>
              <Box sx={{ padding: '5px' }}>
                <p style={{ margin: 0, textAlign: 'start' }}>
                  まず駅を選択します。
                </p>
                <p style={{ margin: 0, textAlign: 'start' }}>
                  次に「GETする」ボタンをクリックしたときに、現在位置と選択した駅の距離が約100m以内の場合は、えきちょーちゃんをGETできます。
                </p>
                {/* <p style={{ margin: 0, textAlign: 'start', color: 'red' }}>
                ※現在はお試しとして、博多駅付近にいると仮定した位置情報を使用してGETします。
              </p>
              <p style={{ margin: 0, textAlign: 'start', color: 'red' }}>
                実際の位置情報を使用してGETするわけではありません。
              </p> */}
              </Box>
            </Grid>
          </Grid>
        </Box>
        {isLoading ? (
          <div>
            <Loading />
          </div>
        ) : (
          <div>
            <ImageDialog
              isOpen={isDialogOpen}
              isDialogClose={setIsDialogOpen}
              isDesktop={isDesktop}
              isSpecial={true}
              propsStationData={stationData}
            />
          </div>
        )}
      </div>
    </>
  );
}

export default LocationGetPage;
