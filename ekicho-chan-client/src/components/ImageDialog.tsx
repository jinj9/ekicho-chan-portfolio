import '../App.css';
import { useEffect } from "react";
import React from "react";
import {
  Box,
  Button,
  Dialog,
  DialogContent,
  DialogTitle,
  Grid,
  IconButton,
  Tooltip
} from '@mui/material';
import { BsStar, BsStarFill } from 'react-icons/bs';
import { AxiosResponse } from "axios";
import Loading from "./Loading";
import { FaCircleInfo } from "react-icons/fa6";
import apiClient from '../api/apiClient';
import AppButton from './AppButton';

type Props = {
  isOpen: boolean;
  isDialogClose: (add: boolean) => void;
  isDesktop: boolean;
  isSpecial: boolean;
  propsStationData: stationMaster;
};

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

const ImageDialog: React.FC<Props> = ({
  isOpen,
  isDialogClose,
  isDesktop,
  isSpecial,
  propsStationData
}) => {

  const [isLoading, setIsLoading] = React.useState(false);
  const [imageType, setImageType] = React.useState({
    className: "",
    rareName: "",
  });
  // 駅データを管理する
  const [stationData, setStationData] = React.useState<stationMaster>(initStationMaster);

  // ダイアログクローズ
  const handleClose = () => {
    isDialogClose(false);
  };

  // ダイアログ初期表示時にpropsで渡された駅データをセットする
  React.useEffect(() => {
    setStationData(propsStationData);
  }, [propsStationData]);

  // もう一度GETするボタン押下時の処理
  const reGetButton = () => {
    setIsLoading(true);
    const getStationData = async () => {
      try {
        const response: AxiosResponse<stationMaster> = await apiClient.get('/api/v1/web/stations/gacha');
        setStationData(response.data);
        setTimeout(
          () => {
            setIsLoading(false);
          },
          500
        );
      } catch (e) {
        alert("サーバ通信エラーです。");
        setIsLoading(false);
        isDialogClose(false);
      }
    };
    getStationData();
  }

  // ガチャ実行時にRareImageを設定する
  React.useEffect(() => {
    getRareImage(stationData.appearanceRateCd);
    // setImageURL(getImage(stationData.imageData));
  }, [stationData]);

  // 画像データの変換
  const convertToBlob = (value: string, mimeType: string) => {
    const buffer = new Uint8Array(value.length);
    for (let i = 0; i < value.length; i++) {
      buffer[i] = value.charCodeAt(i);
    }
    return new Blob([buffer], { type: mimeType });
  }

  // 画像データを変換して返す
  const getImage = (imageData: string) => {
    const baseImageData = atob(imageData);
    const imageDataBlob = convertToBlob(baseImageData, 'image/png');
    const url = URL.createObjectURL(imageDataBlob);
    return url;
  };

  // レア度に応じて画像種別をセットする
  const getRareImage = (appearanceRateCd: string) => {
    switch (appearanceRateCd) {
      case "1": setImageType({ className: "image-n", rareName: "N" });
        break;
      case "2": setImageType({ className: "image-hn", rareName: "HN" });
        break;
      case "3": setImageType({ className: "image-r", rareName: "R" });
        break;
      case "4": setImageType({ className: "image-sr", rareName: "SR" });
        break;
      case "5": setImageType({ className: "image-ssr", rareName: "SSR" });
        break;
      default: return;
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
    <Dialog
      open={isOpen}
      maxWidth="sm"
      fullWidth={!isDesktop}
      fullScreen={!isDesktop}
      sx={
        isDesktop ? {} : { margin: 2, maxHeight: 520 }
      }
      PaperProps={{
        style: {
          borderRadius: 5,
        },
      }}
    >
      <DialogTitle sx={{ margin: 0, padding: '3px', borderBottom: 1, borderColor: '#C0C0C0' }}>
        <Box sx={{
          display: 'flex',
          justifyContent: 'center',
          width: 120,
          margin: 'auto',
        }}>
          {
            stationData.isObtained ? (
              <div className="second_get">
                GET済み
              </div>
            ) : (
              <div className="first_get">
                初GET！
              </div>
            )
          }
        </Box>
      </DialogTitle>
      <DialogContent sx={{ marginTop: 1, paddingLeft: 2, paddingRight: 2 }}>
        <Box className="image" width={'100%'}>
          <div className={imageType.className}>
            <div className="hexagon">
              <div className={isDesktop ? "text_desktop" : "text_mobile"}>
                {imageType.rareName}
              </div>
            </div>
            <img src={getImage(stationData.imageData)} width={'100%'} />
            <p>
              <BsStarFill size={isDesktop ? 40 : 25} color="ffd700" />
              {Number(stationData.appearanceRateCd) >= 2 ? (<BsStarFill size={isDesktop ? 40 : 25} color="ffd700" />) : (
                <BsStar size={isDesktop ? 40 : 25} color="ffd700" />)}
              {Number(stationData.appearanceRateCd) >= 3 ? (<BsStarFill size={isDesktop ? 40 : 25} color="ffd700" />) : (
                <BsStar size={isDesktop ? 40 : 25} color="ffd700" />)}
              {Number(stationData.appearanceRateCd) >= 4 ? (<BsStarFill size={isDesktop ? 40 : 25} color="ffd700" />) : (
                <BsStar size={isDesktop ? 40 : 25} color="ffd700" />)}
              {Number(stationData.appearanceRateCd) >= 5 ? (<BsStarFill size={isDesktop ? 40 : 25} color="ffd700" />) : (
                <BsStar size={isDesktop ? 40 : 25} color="ffd700" />)}
            </p>
          </div>
        </Box>
        <div className="station_name">
          <h2 className={isDesktop ? "desktop" : "mobile"}>
            {
              isSpecial ? stationData.stationName + "駅 スペシャルえきちょーちゃん！" : stationData.stationName + "駅 えきちょーちゃん！"
            }
          </h2>
          <Tooltip
            title={stationData.stationInformationText}
            placement="bottom"
            arrow={true}
            color="primary"
          >
            <IconButton sx={{ padding: 1 }}>
              <FaCircleInfo size={isDesktop ? 25 : 20} />
            </IconButton>
          </Tooltip>
        </div>
      </DialogContent>
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'space-between',
          padding: 1,
          borderTop: 1,
          borderColor: '#C0C0C0'
        }}>
        <Button
          size="small"
          variant="contained"
          sx={{
            backgroundColor: "#CC9933",
            border: 2,
            borderStyle: "solid",
            borderColor: "#AA0000",
          }}
          onClick={reGetButton}
          disabled={isSpecial}
        >
          もう一度GETする
        </Button>
        <AppButton kind="secondary"
          onClick={handleClose}
        >
          閉じる
        </AppButton>
      </Box>
    </Dialog >
  );
};

export default ImageDialog;
