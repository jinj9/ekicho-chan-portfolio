import React from "react";
import {
  Box,
  Dialog,
  DialogContent,
} from '@mui/material';
import { FaMapLocationDot } from "react-icons/fa6";
import DownloadIcon from '@mui/icons-material/Download';
import { saveAs } from 'file-saver';
import AppButton from './AppButton';
import ActionButtonRow from './ActionButtonRow';

type Props = {
  isOpen: boolean;
  isDialogClose: (add: boolean) => void;
  isDesktop: boolean;
  stationData: stationMaster;
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
  gettingDate: string,
}

const WatchingImageDialog: React.FC<Props> = ({
  isOpen,
  isDialogClose,
  isDesktop,
  stationData
}) => {

  // const [isLoading, setIsLoading] = React.useState(true);
  const [dialogOpen, setDialogOpen] = React.useState(false);
  const [imageType, setImageType] = React.useState({
    className: "",
    rareName: "",
  });
  const [isRareDisplay, setIsRareDisplay] = React.useState(false);

  // トグルボタンのクリックイベント
  const handleClickToggle = () => {
    setIsRareDisplay(!isRareDisplay);
  }

  // ダイアログクローズ
  const handleClose = () => {
    setDialogOpen(false);
    isDialogClose(false);
  };

  // ダウンロードボタン押下イベント
  const handleClickDowload = () => {
    const imageData = getImage(stationData.imageData);
    const fileName = stationData.stationEnglishName + "_EKICHO-CHAN";
    saveAs(imageData, fileName);
  };

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

  return (
    <Dialog
      open={isOpen}
      onClose={(event, reason) => {
        if (reason === 'backdropClick') handleClose();
      }}
      maxWidth="sm"
      fullWidth={!isDesktop}
      fullScreen={!isDesktop}
      sx={
        isDesktop ? {} : { margin: 2, height: 'auto', maxHeight: 550 }
      }
      PaperProps={{
        style: {
          borderRadius: 5,
        },
      }}
    >
      <DialogContent >
        {/* <Box>
          <div className="toggle">
            <ToggleButton
              value={isRareDisplay}
              onClick={handleClickToggle}
            >
            </ToggleButton>
          </div>
        </Box> */}
        <Box className="image" sx={{ marginBottom: 1, padding: 0, border: 3 }}>
          <div className={imageType.className}>
            {/* {
              isRareDisplay ? (
                <div className="hexagon">
                  <div className={isDesktop ? "text_desktop" : "text_mobile"}>
                    {imageType.rareName}
                  </div>
                </div>) : (
                <></>
              )
            } */}
            <img src={getImage(stationData.imageData)} width={'100%'} />
            {/* {
              isRareDisplay ? (
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
              ) : (
                <></>
              )
            } */}
          </div>
        </Box>
        <Box sx={{ margin: 0, padding: 0, border: 3 }}>
          <div className="station_info_list">
            <ul style={{ paddingLeft: 22, margin: 5 }}>
              <li>{"駅名：" + stationData.stationName + "（" + stationData.stationEnglishName + "）"}</li>
              <li>
                {"所在地：" + stationData.locationName + " "}
                <a
                  href={
                    stationData.latitude && stationData.longitude ?
                      `https://www.google.com/maps/search/?api=1&query=${stationData.latitude},${stationData.longitude}` :
                      `https://www.google.com/maps/search/?api=1&query=${stationData.stationName}駅`
                  }
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <FaMapLocationDot />GoogleMap
                </a>
              </li>
              <li>{"GET日：" + stationData.gettingDate}</li>
              <li>{"駅情報メモ：" + (stationData.stationInformationText ? stationData.stationInformationText : "")}</li>
            </ul>
          </div>
        </Box>
      </DialogContent>
      <ActionButtonRow
        sx={{
          justifyContent: 'space-between',
          padding: 1,
          borderTop: 1,
          borderColor: '#C0C0C0'
        }}>
        <AppButton kind="secondary" onClick={handleClickDowload}>
          ダウンロード<DownloadIcon fontSize="small" />
        </AppButton>
        <AppButton kind="secondary"
          onClick={handleClose}
        >
          閉じる
        </AppButton>
      </ActionButtonRow>
    </Dialog >
  );
};

export default WatchingImageDialog;
