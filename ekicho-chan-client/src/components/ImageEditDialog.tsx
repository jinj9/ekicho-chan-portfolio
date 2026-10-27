import React from "react";
import AppButton from './AppButton';
import ActionButtonRow from './ActionButtonRow';
import {
  Box,
  Dialog,
  DialogContent,
  DialogTitle,
  Grid,
} from '@mui/material';
import Loading from "./Loading";
import { AxiosResponse } from "axios";
import apiClient from '../api/apiClient';

/** 
 * えきちょーちゃん画像データ登録・編集ダイアログ
 */

type Props = {
  isOpen: boolean;
  isDialogClose: (add: boolean) => void;
  selectedData: stationMasterImage;
};

type stationMasterImage = {
  id: number | undefined,
  stationCd: string,
  stationName: string,
  imageData: string,
  imageType: string,
  imageDataSP: string,
  imageTypeSP: string
}

const initStationMaster: stationMasterImage = {
  id: undefined,
  stationCd: "",
  stationName: "",
  imageData: "",
  imageType: "",
  imageDataSP: "",
  imageTypeSP: ""
};

const ImageEditDialog: React.FC<Props> = ({
  isOpen,
  isDialogClose,
  selectedData,
}) => {
  const [isLoading, setIsLoading] = React.useState(false);
  const [stationDataImage, setStationDataImage] = React.useState<stationMasterImage>(initStationMaster);

  const fileInputRef = React.useRef<HTMLInputElement>(null);
  const [selectImageURL, setSelectImageURL] = React.useState<any>(null);
  const [selectImageURLSP, setSelectImageURLSP] = React.useState<any>(null);

  // ダイアログ初期化イベント(Enter->Entered)
  const handleDialogEnter = () => {
    setStationDataImage(initStationMaster);
  };

  const handleDialogEntered = () => {
    setStationDataImage({
      ...selectedData,
      stationName: selectedData.stationName,
      imageData: selectedData.imageData ? selectedData.imageData : "",
      imageType: selectedData.imageType ? selectedData.imageType : "",
      imageDataSP: selectedData.imageDataSP ? selectedData.imageDataSP : "",
      imageTypeSP: selectedData.imageTypeSP ? selectedData.imageTypeSP : "",
    });
    setSelectImageURL(null);
    setSelectImageURLSP(null);
  };

  // 画像選択イベント
  const onChangeFileImage = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.currentTarget?.files && e.currentTarget.files[0]) {
      const targetFile = e.currentTarget.files[0];
      if (!['image/png', 'image/jpeg'].includes(targetFile.type)) {
        alert('PNGまたはJPEG画像を選択してください。');
        e.currentTarget.value = '';
        return;
      }
      if (targetFile.size > 5 * 1024 * 1024) {
        alert('画像ファイルは5MB以下にしてください。');
        e.currentTarget.value = '';
        return;
      }
      readFile(targetFile, e.target.name);
    }
  }

  // 更新ボタンのクリックイベント
  const handleClickRegist = () => {
    if (!confirm("更新します。よろしいですか？")) return;
    updateStationImageData();
  }

  // 登録処理のAPI
  const updateStationImageData = async () => {
    setIsLoading(true);
    try {
      const response: AxiosResponse<number> = await apiClient.post(
        '/api/v1/web/admin/stations/image', {
        stationCd: stationDataImage.stationCd,
        imageData: stationDataImage.imageData,
        imageType: stationDataImage.imageType,
        imageDataSP: stationDataImage.imageDataSP,
        imageTypeSP: stationDataImage.imageTypeSP,
      });
      // 登録成功
      if (response.data) {
        alert("更新しました。");
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

  // ダイアログクローズ
  const handleClose = () => {
    if (confirm("編集中のデータは保存されません。よろしいですか？")) {
      isDialogClose(false)
    }
  };

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

  // 選択した画像の読み込み処理
  function readFile(file: File, imageName: string) {
    const fr = new FileReader();
    fr.onload = (e) => {
      const resultData = e?.target?.result as string;
      const fileData = resultData.split(',')[1];
      if (!fileData) {
        alert("選択ファイルに誤りがあります。");
        return;
      }
      // 拡張子取得
      const mimeType = file.name.split('.').pop()?.toLowerCase() || '';
      console.log("mimeType:", mimeType);
      const allowExtensions = '(jpeg|jpg|png)$'; // 許可する拡張子
      if (!mimeType.match(allowExtensions)) {
        alert("選択ファイルの拡張子に誤りがあります。");
        return;
      }
      if (imageName === "imageData") {
        if (selectImageURL) {
          URL.revokeObjectURL(selectImageURL);
        }
        setSelectImageURL(resultData);
        setStationDataImage({
          ...stationDataImage,
          imageData: fileData,
          imageType: mimeType
        })
      } else if (imageName === "imageDataSP") {
        if (selectImageURLSP) {
          URL.revokeObjectURL(selectImageURLSP);
        }
        setSelectImageURLSP(resultData);
        setStationDataImage({
          ...stationDataImage,
          imageDataSP: fileData,
          imageTypeSP: mimeType
        })
      }

    };
    fr.readAsDataURL(file);
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
      maxWidth="md"
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
          {"画像データ編集（" + selectedData.stationName + "駅）"}
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
            <Grid item xs={6} border={1} borderColor={"#C0C0C0"} borderRadius={2}>
              <p>＜通常画像＞</p>
              <Box sx={{ margin: 1, border: 3 }}>
                {
                  selectImageURL ? (
                    <div className=''>
                      <img
                        src={selectImageURL}
                        width={'100%'}
                        height={'100%'}
                      />
                    </div>
                  ) : stationDataImage.imageData ? (
                    <div className=''>
                      <img
                        src={getImage(stationDataImage.imageData)}
                        width={'100%'}
                        height={'100%'}
                      />
                    </div>
                  ) : (
                    <div className='' style={{ width: 'auto', height: '380px' }}>
                      <p>画像が未登録です。</p>
                    </div>
                  )
                }
              </Box>
              <Box sx={{ margin: 1 }}>
                <input
                  name="imageData"
                  type="file"
                  accept="image/png,image/jpeg"
                  ref={fileInputRef}
                  onChange={onChangeFileImage}
                />
                <AppButton kind="secondary"
                  onClick={() => {
                    setSelectImageURL(null);
                    setStationDataImage({
                      ...stationDataImage,
                      imageData: "",
                      imageType: ""
                    });
                    URL.revokeObjectURL(selectImageURL);
                  }}
                >
                  リセット
                </AppButton>
              </Box>
            </Grid>
            <Grid item xs={6} border={1} borderColor={"#C0C0C0"} borderRadius={2}>
              <p>＜スペシャル画像＞</p>
              <Box sx={{ margin: 1, border: 3 }}>
                {
                  selectImageURLSP ? (
                    <div className=''>
                      <img
                        src={selectImageURLSP}
                        width={'100%'}
                        height={'100%'}
                      />
                    </div>
                  ) : stationDataImage.imageDataSP ? (
                    <div className=''>
                      <img
                        src={getImage(stationDataImage.imageDataSP)}
                        width={'100%'}
                        height={'100%'}
                      />
                    </div>
                  ) : (
                    <div className='' style={{ width: 'auto', height: '380px' }}>
                      <p>画像が未登録です。</p>
                    </div>
                  )
                }
              </Box>
              <Box sx={{ margin: 1 }}>
                <input
                  name="imageDataSP"
                  type="file"
                  accept="image/png,image/jpeg"
                  onChange={onChangeFileImage}
                />
                <AppButton kind="secondary"
                  onClick={() => {
                    setSelectImageURLSP(null);
                    setStationDataImage({
                      ...stationDataImage,
                      imageDataSP: "",
                      imageTypeSP: ""
                    });
                    URL.revokeObjectURL(selectImageURLSP);
                  }}
                >
                  リセット
                </AppButton>
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
              更新
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
  );
}

export default ImageEditDialog;
