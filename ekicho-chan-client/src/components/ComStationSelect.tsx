import { Autocomplete, Box, TextField } from '@mui/material';
import React from 'react';
import { API_PATH } from './Const';
import axios, { AxiosResponse } from 'axios';
import Loading from './Loading';

/**
 * 駅リストの共通コンポーネント
 * オートコンプリート
 */

type stationList = [{
  id: number | null,
  stationCd: string,
  stationName: string
}]

type Props = {
  prefectureCd: string,
  value: string,
  onChange: any,
  isRequired: boolean
}

const ComStationSelect: React.FC<Props> = (props) => {

  const [isLoading, setIsLoading] = React.useState(false);
  const [stationList, setStationList] = React.useState<stationList>([{
    id: null,
    stationCd: "",
    stationName: ""
  }]);

  React.useEffect(() => {
    const getStationDataList = async () => {
      setIsLoading(true);
      try {
        const response: AxiosResponse<stationList> = await axios.get(
          `${API_PATH}/api/stationMaster/store/stationList/${props.prefectureCd ? props.prefectureCd : "99"}`
        );
        setStationList(response.data);
      } catch (e) {
        alert("サーバ通信エラーです。");
      } finally {
        setIsLoading(false);
      }
    }
    getStationDataList();
  }, [props.prefectureCd]);

  if (isLoading) {
    return (
      <div>
        <Loading />
      </div>
    );
  }

  return (
    <>
      <Autocomplete
        id="station_list"
        options={stationList}
        isOptionEqualToValue={(option, v) => option.id === v.id}
        getOptionLabel={(option) => option.stationName}
        onChange={props.onChange}
        // value={locationGetParam.stationCd}
        // inputValue={locationGetParam.stationCd}
        noOptionsText="候補がありません"
        renderOption={(props, option) => {
          return (
            <Box component="li" {...props} key={option.id}>
              {option.stationName}
            </Box>
          )
        }}
        renderInput={(params) => <TextField {...params} label={props.isRequired ? "駅の選択*" : "駅の選択"} />}
        sx={{
          display: 'inline-flex',
          width: '220px'
        }}
      />
    </>
  );
}

export default ComStationSelect;