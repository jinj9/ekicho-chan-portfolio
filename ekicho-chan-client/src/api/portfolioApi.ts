import apiClient from './apiClient';
import { ObtainedMethod } from '../portfolio/storage';

export type PortfolioStation = {
  id: number;
  stationCd: string;
  stationName: string;
  stationEnglishName: string;
  latitude: number | undefined;
  longitude: number | undefined;
  locationName: string;
  imageData: string;
  appearanceRateCd: string;
  stationInformationText: string;
  isObtained: boolean;
};

export const drawPortfolioStation = async () =>
  (await apiClient.get<PortfolioStation>('/api/v1/portfolio/stations/gacha')).data;

export const obtainPortfolioStationAtLocation = async (
  stationCd: string,
  latitude: number | null,
  longitude: number | null,
) => (await apiClient.post<PortfolioStation>('/api/v1/portfolio/stations/location-get', {
  stationCd, latitude, longitude,
})).data;

export const getPortfolioStation = async (stationId: number, method: ObtainedMethod) =>
  (await apiClient.get<PortfolioStation>(`/api/v1/portfolio/stations/${stationId}`, {
    params: { method },
  })).data;
