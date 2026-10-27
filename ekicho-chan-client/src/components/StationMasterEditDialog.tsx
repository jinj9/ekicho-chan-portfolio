import React from "react";
import AppButton from './AppButton';
import ActionButtonRow from './ActionButtonRow';
import {
  Box,
  Dialog,
  DialogContent,
  DialogTitle,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Select,
  TextField,
} from '@mui/material';
import { PREFECTURE_LIST } from "./Const";
import { AxiosResponse } from "axios";
import apiClient from '../api/apiClient';
import Loading from './Loading';

/** 
 * 駅データ登録・編集ダイアログ
 */

type Props = {
  isOpen: boolean;
  isDialogClose: (add: boolean) => void;
  isNew: boolean;
  selectedData: stationMaster;
};

type stationMaster = {
  id: number | undefined,
  stationCd: string,
  stationName: string,
  stationEnglishName: string,
  latitude: string,
  longitude: string,
  prefectureCd: string,
  appearanceRateCd: string,
  stationInformationText: string,
}

const initStationMaster: stationMaster = {
  id: undefined,
  stationCd: "",
  stationName: "",
  stationEnglishName: "",
  latitude: "",
  longitude: "",
  prefectureCd: "",
  appearanceRateCd: "",
  stationInformationText: "",
};

const StationMasterEditDialog: React.FC<Props> = ({
  isOpen,
  isDialogClose,
  selectedData,
  isNew
}) => {
  const [isLoading, setIsLoading] = React.useState(false);
  const [stationData, setStationData] = React.useState<stationMaster>(initStationMaster);

  // ダイアログ初期化イベント(Enter->Entered)
  const handleDialogEnter = () => {
    setStationData(initStationMaster);
  };

  const handleDialogEntered = () => {
    console.log("Enterd.selectedData:", selectedData);
    setStationData({
      ...selectedData,
      stationName: selectedData.stationName,
      stationEnglishName: selectedData.stationEnglishName,
      latitude: selectedData.latitude !== null ? String(selectedData.latitude) : "",
      longitude: selectedData.longitude !== null ? String(selectedData.longitude) : "",
      prefectureCd: selectedData.prefectureCd,
      appearanceRateCd: selectedData.appearanceRateCd,
      stationInformationText: selectedData.stationInformationText
    });
  };

  // テキストの変更イベント
  const handleChangeText = (
    event: React.ChangeEvent<{ name?: string; value: unknown }>
  ) => {
    setStationData({
      ...stationData,
      [event.target.name as string]: event.target.value as string,
    });
  };

  // 更新ボタンのクリックイベント
  const handleClickRegist = () => {
    console.log("stationData", stationData);
    if (validator()) return;
    if (!confirm(isNew ? "登録します。よろしいですか？" : "更新します。よろしいですか？")) return;
    if (isNew) {
      registStationData();
    } else {
      updateStationData();
    }
  }

  // 登録処理のAPI
  const registStationData = async () => {
    setIsLoading(true);
    try {
      const response: AxiosResponse<number> = await apiClient.post(
        '/api/v1/web/admin/stations', {
        stationCd: stationData.stationCd,
        stationName: stationData.stationName,
        stationEnglishName: stationData.stationEnglishName,
        latitude: stationData.latitude,
        longitude: stationData.longitude,
        prefectureCd: stationData.prefectureCd,
        appearanceRateCd: stationData.appearanceRateCd,
        stationInformationText: stationData.stationInformationText
      });
      // 登録成功
      if (response.data) {
        alert("登録しました。");
      }
      isDialogClose(false);
    } catch (e: any) {
      if (e.response !== undefined) {
        alert(e.response.data.message);
      } else {
        alert("サーバ通信エラーです。");
      }
    } finally {
      setIsLoading(false);
    }
  }

  // 更新処理のAPI
  const updateStationData = async () => {
    setIsLoading(true);
    try {
      const response: AxiosResponse<number> = await apiClient.put(
        `/api/v1/web/admin/stations/${stationData.id}`, {
        stationCd: stationData.stationCd,
        stationName: stationData.stationName,
        stationEnglishName: stationData.stationEnglishName,
        latitude: stationData.latitude,
        longitude: stationData.longitude,
        prefectureCd: stationData.prefectureCd,
        appearanceRateCd: stationData.appearanceRateCd,
        stationInformationText: stationData.stationInformationText
      });
      // 更新成功
      if (response.data) {
        alert("更新しました。");
      }
      isDialogClose(false);
    } catch (e: any) {
      alert(e.response.data);
    } finally {
      setIsLoading(false);
    }
  }

  // バリデーションチェック
  const validator = () => {
    const errorMessage: string[] = Array<string>();
    // 駅コードチェック
    if (!stationData.stationCd) {
      errorMessage.push("駅コードを入力してください。");
    }
    // 駅名チェック
    if (!stationData.stationName) {
      errorMessage.push("駅名を入力してください。");
    }
    // 駅名（英名）チェック
    if (!stationData.stationEnglishName) {
      errorMessage.push("駅名（英名）を入力してください。");
    }
    // 緯度チェック
    // 整数３桁まで、小数点以下７桁まで
    const latitude = Number(stationData.latitude);
    if (stationData.latitude && (!Number.isFinite(latitude) || latitude < -90 || latitude > 90 || !stationData.latitude.match(/^-?\d+(\.\d{1,7})?$/))) {
      errorMessage.push("緯度は-90～90、小数点以下7桁以内で入力してください。");
    }
    // 経度チェック
    // 整数３桁まで、小数点以下７桁まで
    const longitude = Number(stationData.longitude);
    if (stationData.longitude && (!Number.isFinite(longitude) || longitude < -180 || longitude > 180 || !stationData.longitude.match(/^-?\d+(\.\d{1,7})?$/))) {
      errorMessage.push("経度は-180～180、小数点以下7桁以内で入力してください。");
    }
    // 都道府県チェック
    if (!stationData.prefectureCd) {
      errorMessage.push("都道府県を選択してください。");
    }
    // 出現率チェック
    if (!stationData.appearanceRateCd) {
      errorMessage.push("出現率コードを選択してください。");
    }
    // エラーメッセージ出力
    if (errorMessage.length) {
      alert(errorMessage.join('\n'));
    }
    return errorMessage.length ? true : false;
  };

  // 閉じるボタン押下イベント
  const handleClose = () => {
    if (confirm("編集中のデータは保存されません。よろしいですか？")) {
      isDialogClose(false);
    }
  };

  if (isLoading) {
    return (
      <div>
        <Loading />
      </div>
    );
  }

  return (
    <>
      <Dialog
        open={isOpen}
        maxWidth="sm"
        PaperProps={{
          style: {
            borderRadius: 5,
          },
        }}
        TransitionProps={{
          onEntered: handleDialogEntered,
          onEnter: handleDialogEnter,
        }}
      >
        <DialogTitle sx={{ padding: 1, borderBottom: 1 }}>
          <Box sx={{ display: 'block', textAlign: 'center' }}>
            {isNew ? "駅データ登録" : "駅データ編集"}
          </Box>
        </DialogTitle>
        <DialogContent>
          <Box sx={{
            display: 'block',
            textAlign: 'center',
            paddingTop: '20px',
            height: 'auto',
            backgroundColor: '#ffffff',
          }}>
            <Grid container>
              <Grid item xs={12}>
                <Box sx={{ margin: 1 }}>
                  <TextField
                    name='stationCd'
                    label='駅コード'
                    type='text'
                    size='small'
                    fullWidth
                    color='primary'
                    required
                    disabled={!isNew}
                    value={stationData.stationCd}
                    inputProps={{ maxLength: 8 }}
                    onChange={handleChangeText}
                    sx={{
                      margin: 0,
                    }}
                  />
                </Box>
              </Grid>
              <Grid item xs={6}>
                <Box sx={{ margin: 1 }}>
                  <TextField
                    name='stationName'
                    label='駅名'
                    type='text'
                    size='small'
                    fullWidth
                    color='primary'
                    required
                    value={stationData.stationName}
                    inputProps={{ maxLength: 100 }}
                    onChange={handleChangeText}
                    sx={{
                      margin: 0,
                    }}
                  />
                </Box>
              </Grid>
              <Grid item xs={6}>
                <Box sx={{ margin: 1 }}>
                  <TextField
                    name='stationEnglishName'
                    label='駅名（英名）'
                    type='text'
                    size='small'
                    fullWidth
                    required
                    value={stationData.stationEnglishName}
                    inputProps={{ maxLength: 100 }}
                    onChange={handleChangeText}
                    sx={{
                      margin: 0,
                    }}
                  />
                </Box>
              </Grid>
              <Grid item xs={6}>
                <Box sx={{ margin: 1 }}>
                  <TextField
                    name='latitude'
                    label='緯度'
                    type='text'
                    size='small'
                    fullWidth
                    value={stationData.latitude}
                    inputProps={{ maxLength: 16 }}
                    onChange={handleChangeText}
                    sx={{
                      margin: 0,
                    }}
                  />
                </Box>
              </Grid>
              <Grid item xs={6}>
                <Box sx={{ margin: 1 }}>
                  <TextField
                    name='longitude'
                    label='経度'
                    type='text'
                    size='small'
                    fullWidth
                    value={stationData.longitude}
                    inputProps={{ maxLength: 16 }}
                    onChange={handleChangeText}
                    sx={{
                      margin: 0,
                    }}
                  />
                </Box>
              </Grid>
              <Grid item xs={6}>
                <Box sx={{ margin: 1 }}>
                  <FormControl
                    variant="standard"
                    fullWidth
                  >
                    <InputLabel id="prefecture_label">都道府県 *</InputLabel>
                    <Select
                      labelId="prefecture_label"
                      id="prefecture_list"
                      value={stationData.prefectureCd ?? ""}
                      onChange={(event, value) => {
                        setStationData({
                          ...stationData,
                          prefectureCd: event.target.value as string,
                        });
                      }}
                    >
                      {
                        PREFECTURE_LIST.map((value, key) => (
                          <MenuItem key={key} value={value.prefectureCd}>{value.label}</MenuItem>
                        ))
                      }
                    </Select>
                  </FormControl>
                </Box>
              </Grid>
              <Grid item xs={6}>
                <Box sx={{ margin: 1 }}>
                  <FormControl
                    variant="standard"
                    fullWidth
                  >
                    <InputLabel id="appearance_rate_cd_label">出現率コード *</InputLabel>
                    <Select
                      labelId="appearance_rate_cd"
                      id="appearance_rate_cd_list"
                      value={stationData.appearanceRateCd}
                      onChange={(event, value) => {
                        setStationData({
                          ...stationData,
                          appearanceRateCd: event.target.value as string,
                        });
                      }}
                    >
                      <MenuItem key={1} value={"1"}>{"1 (N)"}</MenuItem>
                      <MenuItem key={2} value={"2"}>{"2 (HN)"}</MenuItem>
                      <MenuItem key={3} value={"3"}>{"3 (R)"}</MenuItem>
                      <MenuItem key={4} value={"4"}>{"4 (SR)"}</MenuItem>
                      <MenuItem key={5} value={"5"}>{"5 (SSR)"}</MenuItem>
                    </Select>
                  </FormControl>
                </Box>
              </Grid>
              <Grid item xs={12}>
                <Box sx={{ margin: 1 }}>
                  <TextField
                    name='stationInformationText'
                    label='駅情報テキスト'
                    type='text'
                    multiline
                    rows={4}
                    fullWidth
                    value={stationData.stationInformationText}
                    inputProps={{ maxLength: 1000 }}
                    onChange={handleChangeText}
                    sx={{
                      margin: 0,
                    }}
                  />
                </Box>
              </Grid>
            </Grid>
            <Box
              sx={{
                display: 'block',
                padding: 1,
              }}
            >
              <AppButton kind="primary"
                onClick={handleClickRegist}
              >
                {isNew ? "登録" : "更新"}
              </AppButton>
            </Box>
          </Box>
        </DialogContent>
        <ActionButtonRow
          sx={{
            justifyContent: 'flex-end',
            padding: 1,
            borderTop: 1,
          }}>
          <AppButton kind="secondary"
            onClick={handleClose}
          >
            閉じる
          </AppButton>
        </ActionButtonRow>
      </Dialog >
    </>
  );
}

export default StationMasterEditDialog;
