import '../App.css';
import React from "react";
import ImageDialog from "./ImageDialog";
import { useMediaQuery } from 'react-responsive';
import Loading from "./Loading";
import { Box } from '@mui/material';
import apiClient from '../api/apiClient';
import { PORTFOLIO_MODE } from '../portfolioMode';
import { drawPortfolioStation } from '../api/portfolioApi';
import { recordPortfolioObtained } from '../portfolio/storage';

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
 * えきちょーちゃんガチャのページ
 *  */
const GachaGetPage = () => {
  const [isDialogOpen, setIsDialogOpen] = React.useState(false);
  const [isLoading, setIsLoading] = React.useState(false);
  // 駅データを管理する
  const [stationData, setStationData] = React.useState<stationMaster>(initStationMaster);

  // レスポンシブ対応のためのフラグ
  // 画面幅がminWidthの値以上のときはTRUEになる
  const isDesktop: boolean = useMediaQuery({ minWidth: 600 })

  // GETするボタン押下イベント
  const handleClickBtn = () => {
    setIsLoading(true);
    const getStationData = async () => {
      try {
        const data = PORTFOLIO_MODE
          ? await drawPortfolioStation()
          : (await apiClient.get<stationMaster>('/api/v1/web/stations/gacha')).data;
        if (PORTFOLIO_MODE && data.id !== undefined) {
          data.isObtained = recordPortfolioObtained(data.id, 'GACHA');
        }
        setStationData(data);
        setIsDialogOpen(true);
        setTimeout(
          () => {
            setIsLoading(false);
          },
          500
        );
      } catch (e) {
        alert("サーバ通信エラーです。");
        setIsLoading(false);
      }
    };
    getStationData();
  }

  return (
    <>
      <Box className={'browser_height'} sx={{textAlign: 'center'}}>
        <div className="content">
          <button
            className="get_btn"
            onClick={handleClickBtn}
            disabled={isLoading}
          >
            GETする
          </button>
        </div>
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
              isSpecial={false}
              propsStationData={stationData}
            />
          </div>
        )}
      </Box>
    </>
  );
};

export default GachaGetPage;
