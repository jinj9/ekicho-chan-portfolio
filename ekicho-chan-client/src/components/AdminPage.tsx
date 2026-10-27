import { Box, Select, FormControl, InputLabel, MenuItem } from '@mui/material';
import React from 'react';
import AppButton from './AppButton';
import ActionButtonRow from './ActionButtonRow';
import { PREFECTURE_LIST } from './Const';
import { AxiosResponse } from 'axios';
import apiClient from '../api/apiClient';
import Loading from './Loading';
import { DataGrid, GridCellParams, GridColDef } from '@mui/x-data-grid';
import StationMasterEditDialog from './StationMasterEditDialog';
import ImageEditDialog from './ImageEditDialog';
import ComStationSelect from './ComStationSelect';

/**
 * 管理者ページ
 */

type stationMaster = {
  id: number | undefined,
  stationCd: string,
  stationName: string,
  stationEnglishName: string,
  latitude: string,
  longitude: string,
  locationName: string,
  prefectureCd: string,
  appearanceRateCd: string,
  stationInformationText: string,
}

const initStationMasterList: stationMaster[] = [{
  id: undefined,
  stationCd: "",
  stationName: "",
  stationEnglishName: "",
  latitude: "",
  longitude: "",
  locationName: "",
  prefectureCd: "",
  appearanceRateCd: "",
  stationInformationText: "",
}];

type stationMasterImage = {
  id: number | undefined,
  stationCd: string,
  stationName: string,
  imageData: string,
  imageType: string,
  imageDataSP: string,
  imageTypeSP: string
}

const initStationMasterImage: stationMasterImage = {
  id: undefined,
  stationCd: "",
  stationName: "",
  imageData: "",
  imageType: "",
  imageDataSP: "",
  imageTypeSP: ""
};

const AdminPage = () => {

  const initSearchRequestParam = {
    prefectureCd: "",
    stationCd: "",
  }

  // 検索処理実行時のリクエストパラメータ
  const [searchRequestParam, setSearchRequestParam] = React.useState(initSearchRequestParam);

  const [isLoading, setIsLoading] = React.useState(false);
  // 取得した駅データリスト
  const [stationDataList, setStationDataList] = React.useState<stationMaster[]>(initStationMasterList);
  const [rows, setRows] = React.useState<any[]>([]);

  // 行クリック時の駅データ
  const [selectedData, setSelectedData] = React.useState<{
    id: number | undefined,
    stationCd: string,
    stationName: string,
    stationEnglishName: string,
    latitude: string,
    longitude: string,
    locationName: string,
    prefectureCd: string,
    appearanceRateCd: string,
    stationInformationText: string,
  }>({
    id: undefined,
    stationCd: "",
    stationName: "",
    stationEnglishName: "",
    latitude: "",
    longitude: "",
    locationName: "",
    prefectureCd: "",
    appearanceRateCd: "",
    stationInformationText: "",
  });

  // 画像編集ボタン押下時の駅データ
  const [selectedImageData, setSelectedImageData] = React.useState<stationMasterImage>(initStationMasterImage);

  // ダイアログ表示フラグ
  const [isDialogOpen, setIsDialogOpen] = React.useState(false);
  const [isNew, setIsNew] = React.useState(false);
  const [isImageDialogOpen, setIsImageDialogOpen] = React.useState(false);

  // 検索ボタン押下イベント
  const handleClickSearch = () => {
    const getStationDataList = async () => {
      setIsLoading(true);
      try {
        const response: AxiosResponse<stationMaster[]> = await apiClient.post(
          '/api/v1/web/admin/stations/search', {
          prefectureCd: searchRequestParam.prefectureCd,
          stationCd: searchRequestParam.stationCd,
        });
        // 検索結果が０件の場合はアラート表示する
        if (!response.data.length) {
          alert("検索結果がありませんでした。");
        } else {
          setStationDataList(response.data);
          setRows(response.data);
        }
      } catch (e) {
        alert("サーバ通信エラーです。");
      } finally {
        setIsLoading(false);
      }
    };
    getStationDataList();
  }

  // リセットボタン押下イベント
  const handleClickReset = () => {
    setSearchRequestParam(initSearchRequestParam);
    setRows([]);
    console.log(searchRequestParam);
  }

  // 新規登録ボタン押下イベント
  const handleClickRegist = () => {
    setIsNew(true);
    setSelectedData({
      id: undefined,
      stationCd: "",
      stationName: "",
      stationEnglishName: "",
      latitude: "",
      longitude: "",
      locationName: "",
      prefectureCd: "",
      appearanceRateCd: "",
      stationInformationText: "",
    });
    setIsDialogOpen(true);
  }

  // グリッドの行クリックイベント
  const handleCellClick = (params: GridCellParams) => {
    if (params.field != 'imageData') {
      setIsNew(false);
      getStationData(params.row.id).then(() => {
        setIsDialogOpen(true);
      });
    }
  }

  // 画像編集ボタン押下イベント
  const handleClickImageEdit = (params: GridCellParams) => {
    getStationImageData(params.row.id).then(() => {
      setIsImageDialogOpen(true);
    });
  }

  // 駅情報を取得する
  const getStationData = async (id: number) => {
    setIsLoading(true);
    try {
      const response: AxiosResponse<stationMaster> = await apiClient.get(
        `/api/v1/web/admin/stations/${id}`
      );
      setSelectedData(response.data);
    } catch (e: any) {
      if (e.response !== undefined) {
        alert(e.response.data.message);
      } else {
        alert("サーバ通信エラーです。");
      }
    } finally {
      setIsLoading(false);
    }
  };

  // 駅の画像データを取得する
  const getStationImageData = async (id: number) => {
    setIsLoading(true);
    try {
      const response: AxiosResponse<stationMasterImage> = await apiClient.get(
        `/api/v1/web/admin/stations/${id}/image`
      );
      setSelectedImageData(response.data);
    } catch (e: any) {
      if (e.response !== undefined) {
        alert(e.response.data.message);
      } else {
        alert("サーバ通信エラーです。");
      }
    } finally {
      setIsLoading(false);
    }
  };

  // グリッド定義
  const columns: GridColDef[] = [
    { field: 'stationCd', headerName: '駅コード', width: 80 },
    { field: 'stationName', headerName: '駅名', width: 200 },
    { field: 'stationEnglishName', headerName: '駅名（英名）', width: 200 },
    { field: 'latitude', headerName: '緯度', width: 120 },
    { field: 'longitude', headerName: '経度', width: 120 },
    { field: 'locationName', headerName: '都道府県', width: 100 },
    { field: 'appearanceRateCd', headerName: '出現率コード', width: 100 },
    { field: 'stationInformationText', headerName: '駅情報テキスト', width: 320 },
    {
      field: 'imageData', headerName: '画像', width: 120, align: 'center',
      renderCell: (params) => (
        <Box>
          <AppButton kind="secondary"
            id='image_edit_btn'
            sx={{ margin: 1 }}
            onClick={() => handleClickImageEdit(params)}
          >
            画像編集
          </AppButton>
        </Box>)
    },
  ];

  // グリッドのスタイル
  const grdiStyle = {
    // 列ヘッダに背景色を指定
    '.MuiDataGrid-columnHeaders': {
      backgroundColor: '#000033',
      color: '#fff',
    }
  }

  return (
    <>
      <Box sx={{
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
            marginRight: '20px'
          }}
        >
          <InputLabel id="prefecture_label">都道府県</InputLabel>
          <Select
            labelId="prefecture_label"
            id="prefecture_list"
            value={searchRequestParam.prefectureCd}
            label="都道府県"
            onChange={(event, value) => {
              setSearchRequestParam({
                ...searchRequestParam,
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
          prefectureCd={searchRequestParam.prefectureCd}
          value={searchRequestParam.stationCd}
          onChange={(event: any, value: { stationCd: string; } | null) => {
            setSearchRequestParam({
              ...searchRequestParam,
              stationCd: value === null ? "" : value.stationCd,
            });
          }}
          isRequired={false}
        />
      </Box>
      <Box
        sx={{
          display: 'block',
          textAlign: 'center',
          padding: 1,
          marginTop: '20px',
          marginLeft: 'auto',
          marginRight: 'auto',
          border: 1,
          borderColor: '#C0C0C0',
          borderRadius: '3px',
          width: '390px',
          maxWidth: 'calc(100% - 32px)',
          overflowX: 'auto',
        }}
      >
        <ActionButtonRow sx={{ justifyContent: 'center', flexWrap: 'nowrap' }}><AppButton kind="primary"
          id='search_btn'
          onClick={handleClickSearch}
        >
          検索
        </AppButton>
        <AppButton kind="secondary"
          id='reset_btn'
          onClick={handleClickReset}
        >
          リセット
        </AppButton>
        <AppButton kind="primary"
          id='regist_btn'
          onClick={handleClickRegist}
        >
          新規登録
        </AppButton></ActionButtonRow>
      </Box>
      <Box
        sx={{
          display: 'block',
          textAlign: 'center',
          padding: 1,
          marginTop: '20px',
          marginBottom: '20px',
          marginLeft: 'auto',
          marginRight: 'auto',
          border: 1,
          borderColor: '#C0C0C0',
          borderRadius: '3px',
          width: '1400px',
          height: '600px'
        }}
      >
        <DataGrid
          columns={columns}
          rows={rows}
          sx={grdiStyle}
          onCellClick={(param: GridCellParams) => {
            handleCellClick(param);
          }}
        />
      </Box>
      {
        isLoading ? (<div><Loading /></div>) : (<></>)
      }
      <StationMasterEditDialog
        isOpen={isDialogOpen}
        isDialogClose={setIsDialogOpen}
        selectedData={selectedData}
        isNew={isNew}
      />
      <ImageEditDialog
        isOpen={isImageDialogOpen}
        isDialogClose={setIsImageDialogOpen}
        selectedData={selectedImageData}
      />
    </>
  );
}

export default AdminPage;
