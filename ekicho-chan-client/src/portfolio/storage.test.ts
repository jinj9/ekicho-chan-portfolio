import {
  clearPortfolioCollection,
  readPortfolioCollection,
  recordPortfolioObtained,
} from './storage';

beforeEach(() => localStorage.clear());

test('one station is stored once and repeated acquisition updates the same record', () => {
  expect(recordPortfolioObtained(10, 'GACHA')).toBe(false);
  expect(recordPortfolioObtained(10, 'LOCATION')).toBe(true);

  const records = readPortfolioCollection();
  expect(records).toHaveLength(1);
  expect(records[0]).toMatchObject({
    stationId: 10,
    obtainedCount: 2,
    lastObtainedMethod: 'LOCATION',
  });
});

test('collection can be cleared', () => {
  recordPortfolioObtained(10, 'GACHA');
  clearPortfolioCollection();
  expect(readPortfolioCollection()).toEqual([]);
});
