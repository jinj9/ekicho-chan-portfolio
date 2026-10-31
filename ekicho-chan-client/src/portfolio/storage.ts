export type ObtainedMethod = 'GACHA' | 'LOCATION';

export type PortfolioCollectionRecord = {
  stationId: number;
  firstObtainedAt: string;
  lastObtainedAt: string;
  obtainedCount: number;
  lastObtainedMethod: ObtainedMethod;
};

const STORAGE_KEY = 'ekicho-chan.portfolio.collection.v1';

const isRecord = (value: unknown): value is PortfolioCollectionRecord => {
  if (!value || typeof value !== 'object') return false;
  const record = value as Partial<PortfolioCollectionRecord>;
  return Number.isInteger(record.stationId)
    && typeof record.firstObtainedAt === 'string'
    && typeof record.lastObtainedAt === 'string'
    && Number.isInteger(record.obtainedCount)
    && (record.lastObtainedMethod === 'GACHA' || record.lastObtainedMethod === 'LOCATION');
};

export const readPortfolioCollection = (): PortfolioCollectionRecord[] => {
  try {
    const parsed: unknown = JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]');
    return Array.isArray(parsed) ? parsed.filter(isRecord) : [];
  } catch {
    return [];
  }
};

export const recordPortfolioObtained = (stationId: number, method: ObtainedMethod): boolean => {
  const records = readPortfolioCollection();
  const existing = records.find(record => record.stationId === stationId);
  const now = new Date().toISOString();
  if (existing) {
    existing.lastObtainedAt = now;
    existing.obtainedCount += 1;
    existing.lastObtainedMethod = method;
  } else {
    records.push({
      stationId,
      firstObtainedAt: now,
      lastObtainedAt: now,
      obtainedCount: 1,
      lastObtainedMethod: method,
    });
  }
  localStorage.setItem(STORAGE_KEY, JSON.stringify(records));
  return Boolean(existing);
};

export const clearPortfolioCollection = () => localStorage.removeItem(STORAGE_KEY);
