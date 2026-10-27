
/**
 * 共通使用する定数を定義する
 */


// APIパス。AWSではCloudFrontの同一オリジンから/apiをALBへ転送するため空文字にする。
// 別ドメインのAPIを使う場合だけ、末尾のスラッシュなしでHTTPS URLを設定する。
const configuredApiPath = (process.env.REACT_APP_API_PATH || '').trim().replace(/\/$/, '');
if (process.env.NODE_ENV === 'production'
  && configuredApiPath !== ''
  && !configuredApiPath.startsWith('https://')) {
  throw new Error('Production API path must be empty (same origin) or use HTTPS.');
}
export const API_PATH = configuredApiPath;

/** 共通カラー */
export const COLOR_THEME = {
  gold: "#CC9933",
  nayvy: "#000033",
  red: "#AA0000"
};

// 管理者ページ表示フラグ(true→管理者ページを表示する)
export const isAdminPageView = false;

// テストユーザID
export const TEST_USER_ID = "test-user";

// ゲストユーザID
export const GUEST_USER_ID = "guest-user";

// テスト用緯度（博多駅付近）
export const TEST_LATITUDE = 33.5893588;

// テスト用経度（博多駅付近）
export const TEST_LONGITUDE = 130.4207079;

// 都道府県リスト
export const PREFECTURE_LIST = [
  { prefectureCd: 40, label: "福岡県" },
  { prefectureCd: 41, label: "佐賀県" },
  { prefectureCd: 42, label: "長崎県" },
  { prefectureCd: 43, label: "熊本県" },
  { prefectureCd: 44, label: "大分県" },
  { prefectureCd: 45, label: "宮崎県" },
  { prefectureCd: 46, label: "鹿児島県" },
];

// TODO サンプル駅リスト
export const STATION_LIST = [
  { id: 1, stationCd: "1", label: "博多" },
  { id: 2, stationCd: "2", label: "小倉" },
  { id: 3, stationCd: "3", label: "熊本" },
  { id: 4, stationCd: "4", label: "長崎" },
  { id: 5, stationCd: "5", label: "佐賀" },
  { id: 6, stationCd: "6", label: "大分" },
  { id: 7, stationCd: "7", label: "宮崎" },
  { id: 8, stationCd: "8", label: "鹿児島中央" },
  { id: 9, stationCd: "9", label: "二日市" },
  { id: 10, stationCd: "10", label: "都府楼南" },
  { id: 11, stationCd: "11", label: "教育大前" },
  { id: 12, stationCd: "12", label: "新飯塚" },
  { id: 13, stationCd: "13", label: "九郎原" },
  { id: 14, stationCd: "14", label: "田川後藤寺" },
  { id: 15, stationCd: "15", label: "門司港" },
  { id: 16, stationCd: "16", label: "山本" },
  { id: 17, stationCd: "17", label: "新大村" },
  { id: 18, stationCd: "18", label: "ハウステンボス" },
  { id: 19, stationCd: "19", label: "由布院" },
  // { id: 20, stationCd: "99", label: "スペースワールド" },
];
