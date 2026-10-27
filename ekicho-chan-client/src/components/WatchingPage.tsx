import React from 'react';
import AppButton from './AppButton';
import ActionButtonRow from './ActionButtonRow';
import { useMediaQuery } from 'react-responsive';
import { Alert, Box, Card, FormControl, Grid, InputLabel, MenuItem, Select, TextField, Typography } from '@mui/material';
import '../App.css';
import WatchingImageDialog from './WatchingImageDialog';
import Loading from './Loading';
import apiClient from '../api/apiClient';
import { PREFECTURE_LIST } from './Const';

type StationMaster = {
  id: number | undefined;
  stationName: string;
  stationEnglishName: string;
  latitude: number | undefined;
  longitude: number | undefined;
  locationName: string;
  imageData: string;
  appearanceRateCd: string;
  stationInformationText: string;
  gettingDate: string;
};

type ObtainedPage = {
  content: StationMaster[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
};

const emptyStation: StationMaster = {
  id: undefined, stationName: '', stationEnglishName: '', latitude: undefined,
  longitude: undefined, locationName: '', imageData: '', appearanceRateCd: '',
  stationInformationText: '', gettingDate: '',
};

const WatchingPage = () => {
  const isDesktop = useMediaQuery({ minWidth: 600 });
  const [isDialogOpen, setIsDialogOpen] = React.useState(false);
  const [isLoading, setIsLoading] = React.useState(false);
  const [isSearched, setIsSearched] = React.useState(false);
  const [stations, setStations] = React.useState<StationMaster[]>([]);
  const [selectedStation, setSelectedStation] = React.useState<StationMaster>(emptyStation);
  const [page, setPage] = React.useState(0);
  const [totalPages, setTotalPages] = React.useState(0);
  const [totalElements, setTotalElements] = React.useState(0);
  const [prefectureCd, setPrefectureCd] = React.useState('0');
  const [stationName, setStationName] = React.useState('');
  const [appearanceRateCd, setAppearanceRateCd] = React.useState('');
  const [sort, setSort] = React.useState('newest');

  const search = async (targetPage = 0) => {
    setIsLoading(true);
    try {
      const response = await apiClient.get<ObtainedPage>('/api/v1/web/stations/obtained', {
        params: { page: targetPage, size: 12, prefectureCd, stationName: stationName.trim(), appearanceRateCd, sort },
      });
      setStations(response.data.content);
      setPage(response.data.page);
      setTotalPages(response.data.totalPages);
      setTotalElements(response.data.totalElements);
      setIsSearched(true);
    } catch {
      alert('獲得したえきちょーちゃんを取得できませんでした。');
    } finally {
      setIsLoading(false);
    }
  };

  const reset = () => {
    setPrefectureCd('0'); setStationName(''); setAppearanceRateCd(''); setSort('newest');
    setStations([]); setPage(0); setTotalPages(0); setTotalElements(0); setIsSearched(false);
  };

  const openImage = (station: StationMaster) => {
    setSelectedStation(station);
    setIsDialogOpen(true);
  };

  return (
    <Box sx={{ minHeight: '100vh', pb: 8 }}>
      <Box sx={{ maxWidth: 1100, mx: 'auto', mt: 2, p: 2, bgcolor: '#fff', border: 1, borderColor: '#C0C0C0', borderRadius: 1 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={6} md={2}>
            <FormControl fullWidth size="small">
              <InputLabel>都道府県</InputLabel>
              <Select value={prefectureCd} label="都道府県" onChange={e => setPrefectureCd(e.target.value)}>
                <MenuItem value="0">すべて</MenuItem>
                {PREFECTURE_LIST.map(value => <MenuItem key={value.prefectureCd} value={String(value.prefectureCd)}>{value.label}</MenuItem>)}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={6} md={4}>
            <TextField fullWidth size="small" label="駅名" value={stationName} inputProps={{ maxLength: 100 }} onChange={e => setStationName(e.target.value)} />
          </Grid>
          <Grid item xs={12} sm={4} md={2}>
            <FormControl fullWidth size="small">
              <InputLabel>レアリティ</InputLabel>
              <Select value={appearanceRateCd} label="レアリティ" onChange={e => setAppearanceRateCd(e.target.value)}>
                <MenuItem value="">すべて</MenuItem>
                {['N', 'HN', 'R', 'SR', 'SSR'].map((name, index) => <MenuItem key={name} value={String(index + 1)}>{name}</MenuItem>)}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={8} md={4}>
            <FormControl fullWidth size="small">
              <InputLabel>並び順</InputLabel>
              <Select value={sort} label="並び順" onChange={e => setSort(e.target.value)}>
                <MenuItem value="newest">獲得日が新しい順</MenuItem>
                <MenuItem value="oldest">獲得日が古い順</MenuItem>
                <MenuItem value="station">駅名順</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12}>
            <ActionButtonRow sx={{ justifyContent: 'center', flexWrap: 'nowrap' }}>
              <AppButton kind="primary" onClick={() => search(0)}>検索</AppButton>
              <AppButton kind="secondary" onClick={reset}>リセット</AppButton>
            </ActionButtonRow>
          </Grid>
        </Grid>
      </Box>

      {isLoading && <Loading />}

      {isSearched && stations.length === 0 && (
        <Alert severity="info" sx={{ maxWidth: 600, mx: 'auto', mt: 3 }}>条件に一致する、獲得済みのえきちょーちゃんはいません。</Alert>
      )}

      {stations.length > 0 && (
        <Box sx={{ p: 3 }}>
          <Typography textAlign="center" sx={{ mb: 2 }}>全{totalElements}件</Typography>
          <Grid container spacing={1}>
            {stations.map(value => (
              <Grid item xs={6} sm={3} md={2} key={`${value.id}-${value.appearanceRateCd}`}>
                <Card sx={{ height: '100%', textAlign: 'center' }}>
                  <Box sx={{ m: 1 }}>
                    <Box className="card_image">
                      <img src={`data:image/png;base64,${value.imageData}`} width="100%" height="auto" alt={`${value.stationName}駅`} onClick={() => openImage(value)} />
                    </Box>
                    <div className="card_image_text">{value.stationName}駅</div>
                    <Typography variant="caption" color="text.secondary">GET日：{value.gettingDate}</Typography>
                  </Box>
                </Card>
              </Grid>
            ))}
          </Grid>
          <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 2, mt: 3 }}>
            <AppButton kind="text" disabled={page === 0} onClick={() => search(page - 1)}>前へ</AppButton>
            <Typography>{page + 1} / {totalPages}ページ</Typography>
            <AppButton kind="text" disabled={page + 1 >= totalPages} onClick={() => search(page + 1)}>次へ</AppButton>
          </Box>
        </Box>
      )}

      <WatchingImageDialog isOpen={isDialogOpen} isDialogClose={setIsDialogOpen} isDesktop={isDesktop} stationData={selectedStation} />
    </Box>
  );
};

export default WatchingPage;
